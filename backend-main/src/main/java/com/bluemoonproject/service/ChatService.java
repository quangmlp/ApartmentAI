package com.bluemoonproject.service;

import com.bluemoonproject.dto.request.ChatRequestDto;
import com.bluemoonproject.dto.response.ChatResponse;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.repository.RoomRepository;
import com.bluemoonproject.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.EntityType;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Add this import
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@SessionScope // Giữ lịch sử chat cho từng phiên người dùng
public class ChatService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    // Remove the hardcoded API_KEY
    @Value("${groq.api.key}")
    private String apiKey;
    
    private static final String MODEL_NAME = "openai/gpt-oss-120b";
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final OkHttpClient client;
    private final Gson gson;

    // Schema Context: Chứa thông tin chính xác về Bảng và Cột trong DB
    private String schemaContext = "";
    
    // Lịch sử hội thoại (Context Window)
    private final List<JsonObject> conversationHistory = new ArrayList<>();

    public ChatService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    @PostConstruct
    public void initSchema() {
        StringBuilder sb = new StringBuilder();
        sb.append("### DATABASE SCHEMA (PHYSICAL TABLES & COLUMNS):\n");
        
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            Class<?> javaType = entity.getJavaType();
            
            // 1. Lấy tên bảng thực tế (Physical Table Name)
            String tableName = getPhysicalTableName(javaType);
            sb.append("- Table `").append(tableName).append("`:\n");

            // 2. Lấy tên cột thực tế (Physical Column Name)
            for (Field field : getAllFields(javaType)) {
                // Bỏ qua các trường static hoặc transient
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(Transient.class)) continue;
                
                // Bỏ qua các quan hệ OneToMany/ManyToMany (thường không có cột trong bảng chính)
                if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) continue;

                String colName = getPhysicalColumnName(field);
                String typeInfo = field.getType().getSimpleName();

                // Nếu là Enum, liệt kê các giá trị để AI map đúng (VD: 'xe máy' -> MOTORBIKE)
                if (field.getType().isEnum()) {
                    String enumValues = Arrays.toString(field.getType().getEnumConstants());
                    typeInfo += " (ENUM Values: " + enumValues + ")";
                }

                sb.append("  + `").append(colName).append("` (").append(typeInfo).append(")\n");
            }
            
            // 3. Xử lý các bảng phụ (ElementCollection)
            for (Field field : getAllFields(javaType)) {
                if (field.isAnnotationPresent(ElementCollection.class)) {
                    CollectionTable ct = field.getAnnotation(CollectionTable.class);
                    String subTable = (ct != null && !ct.name().isEmpty()) ? ct.name() : tableName + "_" + camelToSnake(field.getName());
                    sb.append("- Table `").append(subTable).append("` (Linked to ").append(tableName).append(")\n");
                }
            }
        }
        this.schemaContext = sb.toString();
        // System.out.println(this.schemaContext); // Debug schema nếu cần
    }

    public ChatResponse processMessage(ChatRequestDto request) {
        String userMsg = request.getMessage().trim();
        addToHistory("user", userMsg);

        // --- AUTHORIZATION CHECK ---
        String restrictionContext = null;
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
                String username = authentication.getName();
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN"));

                if (!isAdmin) {
                    List<Room> rooms = roomRepository.findRoomsByUserId(user.getId());
                    if (rooms.isEmpty()) {
                        restrictionContext = "RESTRICTION: The user is NOT an ADMIN and does NOT own any apartment. You MUST NOT answer any questions related to specific apartments or residents. Return 'NOT_AUTHORIZED' if they ask about apartment data.";
                    } else {
                        String roomNumbers = rooms.stream().map(Room::getRoomNumber).collect(Collectors.joining(", "));
                        restrictionContext = "RESTRICTION: The user is a resident of Room(s): [" + roomNumbers + "]. You MUST restrict all queries to these rooms. If the user asks about another room, return 'NOT_AUTHORIZED'. You MUST add `WHERE room_number IN ('" + roomNumbers.replace(", ", "', '") + "')` to all queries involving rooms.";
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Auth check failed: " + e.getMessage());
        }
        // ---------------------------

        try {
            // 1. Phân loại Intent (Hỏi dữ liệu hay Chém gió)
            if (isChitChat(userMsg)) {
                String reply = generateChitChatResponse();
                addToHistory("assistant", reply);
                return new ChatResponse(reply);
            }

            // 2. Sinh SQL với cơ chế Tự Sửa Lỗi (Self-Correction)
            String sql = generateSqlWithRetry(userMsg, restrictionContext);
            
            if (sql.contains("NOT_SQL")) {
                String reply = "Xin lỗi, câu hỏi này không liên quan đến dữ liệu hệ thống.";
                addToHistory("assistant", reply);
                return new ChatResponse(reply);
            }

            if (sql.contains("NOT_AUTHORIZED")) {
                String reply = "Bạn không có quyền truy cập thông tin của căn hộ này.";
                addToHistory("assistant", reply);
                return new ChatResponse(reply);
            }

            // 3. Thực thi SQL & Tóm tắt kết quả
            List<Object[]> results = executeSql(sql);
            String finalResponse = summarizeResults(userMsg, results);
            addToHistory("assistant", finalResponse);
            return new ChatResponse(finalResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // --- CORE LOGIC ---

    private String generateSqlWithRetry(String question, String restrictionContext) throws IOException {
        // Lần 1: Sinh SQL bằng Decomposer
        String sql = decomposeAndGenerateSql(question, restrictionContext);
        
        // Thử chạy SQL (Dry Run)
        try {
            if (sql.contains("NOT_AUTHORIZED") || sql.contains("NOT_SQL")) return sql;
            executeSql(sql); 
            return sql; // Nếu chạy OK thì trả về
        } catch (Exception e) {
            System.out.println("SQL Error (Attempt 1): " + e.getMessage());
            
            // Lần 2: Refiner (Sửa lỗi)
            String fixedSql = refineSql(question, sql, e.getMessage(), e.getClass().getSimpleName(), restrictionContext);
            System.out.println("Fixed SQL: " + fixedSql);
            return fixedSql;
        }
    }

    private String decomposeAndGenerateSql(String question, String restrictionContext) throws IOException {
        String template = "Given a 【Database schema】 description, a knowledge 【Evidence】 and the 【Question】, you need to use valid MySQL and understand the database and knowledge, and then decompose the question into subquestions for text-to-SQL generation.\n" +
                "When generating SQL, we should always consider constraints:\n" +
                "【Constraints】\n" +
                "- In `SELECT <column>`, just select needed columns in the 【Question】 without any unnecessary column or value\n" +
                "- In `FROM <table>` or `JOIN <table>`, do not include unnecessary table\n" +
                "- If use max or min func, `JOIN <table>` FIRST, THEN use `SELECT MAX(<column>)` or `SELECT MIN(<column>)`\n" +
                "- If [Value examples] of <column> has 'None' or None, use `JOIN <table>` or `WHERE <column> is NOT NULL` is better\n" +
                "- If use `ORDER BY <column> ASC|DESC`, add `GROUP BY <column>` before to select distinct values\n" +
                "\n" +
                "==========\n" +
                "\n" +
                "【Database schema】\n" +
                "{desc_str}\n" +
                "【Foreign keys】\n" +
                "(See schema)\n" +
                "【Question】\n" +
                "{query}\n" +
                "【Evidence】\n" +
                "{evidence}\n" +
                "\n" +
                "Decompose the question into sub questions, considering 【Constraints】, and generate the SQL after thinking step by step.\n" +
                "Example format:\n" +
                "Sub question 1: ...\n" +
                "SQL\n" +
                "```sql\n" +
                "...\n" +
                "```\n" +
                "Sub question 2: ...\n" +
                "SQL\n" +
                "```sql\n" +
                "...\n" +
                "```\n";

        String evidence = "### SEMANTIC MAPPING RULES (Vietnamese -> DB):\n" +
                "- 'xe máy' -> `type` = 'MOTORBIKE' (in `vehicles` table)\n" +
                "- 'ô tô' -> `type` = 'CAR'\n" +
                "- 'số người' -> `people_count` (in `rooms` table)\n" +
                "- 'số phòng' -> `room_number`\n" +
                "- 'phí' -> `fees` table\n" +
                "- 'đóng góp' -> `contributions` table\n";
        
        if (restrictionContext != null) {
            evidence += "\n" + restrictionContext;
        }

        String prompt = template.replace("{desc_str}", this.schemaContext)
                                .replace("{query}", question)
                                .replace("{evidence}", evidence);

        JsonArray messages = new JsonArray();
        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", "You are a MySQL Expert. Decompose the question and generate SQL. Return ONLY raw SQL in the final block.");
        messages.add(sys);

        messages.addAll(getHistoryAsJsonArray());

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", prompt);
        messages.add(user);

        String response = callGroqApi(messages, 0.1);
        return extractLastSqlBlock(response);
    }

    private String refineSql(String question, String oldSql, String errorMsg, String errorType, String restrictionContext) throws IOException {
        String template = "【Instruction】\n" +
                "When generating SQL, we should always consider constraints:\n" +
                "【Constraints】\n" +
                "- In `SELECT <column>`, just select needed columns in the 【Question】 without any unnecessary column or value\n" +
                "- In `FROM <table>` or `JOIN <table>`, do not include unnecessary table\n" +
                "- If use max or min func, `JOIN <table>` FIRST, THEN use `SELECT MAX(<column>)` or `SELECT MIN(<column>)`\n" +
                "- If [Value examples] of <column> has 'None' or None, use `JOIN <table>` or `WHERE <column> is NOT NULL` is better\n" +
                "- If use `ORDER BY <column> ASC|DESC`, add `GROUP BY <column>` before to select distinct values\n" +
                "\n" +
                "==========\n" +
                "\n" +
                "【Database schema】\n" +
                "{desc_str}\n" +
                "【Foreign keys】\n" +
                "(See schema)\n" +
                "【Question】\n" +
                "{query}\n" +
                "【Evidence】\n" +
                "{evidence}\n" +
                "【Old SQL】\n" +
                "{sql}\n" +
                "【MySQL Error】\n" +
                "{sqlite_error}\n" +
                "【Exception Class】\n" +
                "{exception_class}\n" +
                "\n" +
                "Based on the 【Database schema】, 【Question】, 【Evidence】 and 【MySQL Error】, you need to fix the 【Old SQL】 and generate a new SQL.\n" +
                "If the error is about unknown column, check the schema again.\n" +
                "Return ONLY raw SQL. No Markdown. No explanation.";

         String evidence = "### SEMANTIC MAPPING RULES (Vietnamese -> DB):\n" +
                "- 'xe máy' -> `type` = 'MOTORBIKE' (in `vehicles` table)\n" +
                "- 'ô tô' -> `type` = 'CAR'\n" +
                "- 'số người' -> `people_count` (in `rooms` table)\n" +
                "- 'số phòng' -> `room_number`\n" +
                "- 'phí' -> `fees` table\n" +
                "- 'đóng góp' -> `contributions` table\n";

        if (restrictionContext != null) {
            evidence += "\n" + restrictionContext;
        }

        String prompt = template.replace("{desc_str}", this.schemaContext)
                                .replace("{query}", question)
                                .replace("{evidence}", evidence)
                                .replace("{sql}", oldSql)
                                .replace("{sqlite_error}", errorMsg)
                                .replace("{exception_class}", errorType);

        JsonArray messages = new JsonArray();
        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", "You are a MySQL Expert. Fix the SQL error.");
        messages.add(sys);

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", prompt);
        messages.add(user);

        return callGroqApi(messages, 0.1).replaceAll("```sql", "").replaceAll("```", "").trim();
    }

    private String extractLastSqlBlock(String text) {
        int lastIndex = text.lastIndexOf("```sql");
        if (lastIndex == -1) {
             lastIndex = text.lastIndexOf("```");
        }
        if (lastIndex != -1) {
            String sub = text.substring(lastIndex);
            sub = sub.replaceFirst("```(sql)?", "");
            int closingIndex = sub.indexOf("```");
            if (closingIndex != -1) {
                sub = sub.substring(0, closingIndex);
            }
            return sub.trim();
        }
        return text.replaceAll("```sql", "").replaceAll("```", "").trim();
    }

    private boolean isChitChat(String msg) throws IOException {
        // Dùng AI để phân loại nhanh
        String prompt = "Classify intent: '" + msg + "'. Return 'TRUE' if it is greeting, thanks, apology, or feedback. Return 'FALSE' if it asks for data/info. Return ONLY the label.";
        JsonArray msgs = new JsonArray();
        JsonObject m = new JsonObject();
        m.addProperty("role", "user");
        m.addProperty("content", prompt);
        msgs.add(m);
        return callGroqApi(msgs, 0.1).trim().equalsIgnoreCase("TRUE");
    }

    private String generateChitChatResponse() throws IOException {
        JsonArray msgs = new JsonArray();
        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", "You are a helpful Apartment Assistant. Answer naturally in Vietnamese.");
        msgs.add(sys);
        msgs.addAll(getHistoryAsJsonArray());
        return callGroqApi(msgs, 0.7);
    }

    private String summarizeResults(String question, List<Object[]> data) throws IOException {
        if (data.isEmpty()) return "Không tìm thấy dữ liệu nào.";
        String dataStr = data.stream().limit(10).map(Arrays::toString).collect(Collectors.joining("\n"));
        
        JsonArray msgs = new JsonArray();
        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", "Summarize the data in Vietnamese naturally. Be concise.");
        msgs.add(sys);
        
        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", "Question: " + question + "\nData: " + dataStr);
        msgs.add(user);
        
        return callGroqApi(msgs, 0.5);
    }

    // --- HELPER METHODS ---

    private void addToHistory(String role, String content) {
        JsonObject msg = new JsonObject();
        msg.addProperty("role", role);
        msg.addProperty("content", content);
        conversationHistory.add(msg);
        if (conversationHistory.size() > 10) conversationHistory.remove(0);
    }

    private JsonArray getHistoryAsJsonArray() {
        JsonArray arr = new JsonArray();
        conversationHistory.forEach(arr::add);
        return arr;
    }

    private String getPhysicalTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        return (table != null && !table.name().isEmpty()) ? table.name() : camelToSnake(clazz.getSimpleName());
    }

    private String getPhysicalColumnName(Field field) {
        Column col = field.getAnnotation(Column.class);
        if (col != null && !col.name().isEmpty()) return col.name();
        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn jc = field.getAnnotation(JoinColumn.class);
            return (jc != null && !jc.name().isEmpty()) ? jc.name() : camelToSnake(field.getName()) + "_id";
        }
        return camelToSnake(field.getName());
    }

    private String camelToSnake(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) fields.addAll(Arrays.asList(c.getDeclaredFields()));
        return fields;
    }

    private List<Object[]> executeSql(String sql) {
        if (!sql.trim().toUpperCase().startsWith("SELECT")) throw new IllegalArgumentException("Unauthorized SQL");
        Query query = entityManager.createNativeQuery(sql);
        List<?> raw = query.getResultList();
        if (raw.isEmpty()) return new ArrayList<>();
        if (raw.get(0) instanceof Object[]) return (List<Object[]>) raw;
        return raw.stream().map(o -> new Object[]{o}).collect(Collectors.toList());
    }

    private String callGroqApi(JsonArray messages, double temp) throws IOException {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", MODEL_NAME);
        jsonBody.addProperty("temperature", temp);
        jsonBody.add("messages", messages);

        RequestBody body = RequestBody.create(gson.toJson(jsonBody), MediaType.get("application/json"));
        Request request = new Request.Builder().url(API_URL).addHeader("Authorization", "Bearer " + this.apiKey).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resStr = response.body().string();
            JsonObject resJson = gson.fromJson(resStr, JsonObject.class);
            return resJson.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
        }
    }
}