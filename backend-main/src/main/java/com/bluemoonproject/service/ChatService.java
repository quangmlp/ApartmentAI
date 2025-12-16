package com.bluemoonproject.service;

import com.bluemoonproject.dto.request.ChatRequestDto;
import com.bluemoonproject.dto.response.ChatResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.EntityType;
import okhttp3.*;
import org.springframework.stereotype.Service;
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

    // API Key Groq (Miễn phí & Nhanh)
    private static final String API_KEY = "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";
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

        try {
            // 1. Phân loại Intent (Hỏi dữ liệu hay Chém gió)
            if (isChitChat(userMsg)) {
                String reply = generateChitChatResponse();
                addToHistory("assistant", reply);
                return new ChatResponse(reply);
            }

            // 2. Sinh SQL với cơ chế Tự Sửa Lỗi (Self-Correction)
            String sql = generateSqlWithRetry(userMsg);
            
            if (sql.contains("NOT_SQL")) {
                String reply = "Xin lỗi, câu hỏi này không liên quan đến dữ liệu hệ thống.";
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

    private String generateSqlWithRetry(String question) throws IOException {
        // Lần 1: Sinh SQL
        String sql = generateSql(question, null);
        
        // Thử chạy SQL (Dry Run)
        try {
            executeSql(sql); 
            return sql; // Nếu chạy OK thì trả về
        } catch (Exception e) {
            System.out.println("SQL Error (Attempt 1): " + e.getMessage());
            
            // Lần 2: Gửi lỗi cho AI để nó tự sửa (Self-Correction)
            String errorMsg = e.getMessage();
            String retryPrompt = "The SQL query failed with error: \"" + errorMsg + "\".\n" +
                                 "Please FIX the SQL based on the Schema provided above.\n" +
                                 "Pay attention to exact table names and column names (e.g. 'room_number' vs 'roomNumber').";
            
            String fixedSql = generateSql(question, retryPrompt);
            System.out.println("Fixed SQL: " + fixedSql);
            return fixedSql;
        }
    }

    private String generateSql(String question, String errorContext) throws IOException {
        JsonArray messages = new JsonArray();
        
        // System Prompt cực kỳ chi tiết
        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", 
            "You are a MySQL Expert. Generate a SELECT query for the User Question.\n" +
            "\n" +
            this.schemaContext + "\n" +
            "\n" +
            "### SEMANTIC MAPPING RULES (Vietnamese -> DB):\n" +
            "- 'xe máy' -> `type` = 'MOTORBIKE' (in `vehicles` table)\n" +
            "- 'ô tô' -> `type` = 'CAR'\n" +
            "- 'số người' -> `people_count` (in `rooms` table)\n" +
            "- 'số phòng' -> `room_number`\n" +
            "- 'phí' -> `fees` table\n" +
            "- 'đóng góp' -> `contributions` table\n" +
            "\n" +
            "### INSTRUCTIONS:\n" +
            "1. Use ONLY the physical table/column names listed in Schema.\n" +
            "2. Return ONLY raw SQL. No Markdown. No explanation.\n" +
            "3. If question is irrelevant to DB, return 'NOT_SQL'.");
        messages.add(sys);

        // Thêm lịch sử chat để hiểu ngữ cảnh
        messages.addAll(getHistoryAsJsonArray());

        // Nếu đang sửa lỗi, thêm thông tin lỗi
        if (errorContext != null) {
            JsonObject err = new JsonObject();
            err.addProperty("role", "user");
            err.addProperty("content", errorContext);
            messages.add(err);
        }

        return callGroqApi(messages, 0.1).replaceAll("```sql", "").replaceAll("```", "").trim();
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
        Request request = new Request.Builder().url(API_URL).addHeader("Authorization", "Bearer " + API_KEY).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("API Error: " + response.code());
            String resStr = response.body().string();
            JsonObject resJson = gson.fromJson(resStr, JsonObject.class);
            return resJson.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
        }
    }
}