# 🏢 ApartmentAI - Hệ thống Quản lý Chung cư Thông minh


ApartmentAI là một giải pháp toàn diện giúp số hóa quy trình quản lý chung cư, kết hợp với trợ lý ảo AI để nâng cao trải nghiệm cư dân và tối ưu hóa công việc của ban quản lý. Dự án bao gồm Backend mạnh mẽ với Spring Boot và Frontend hiện đại với ReactJS.

## ✨ Tính năng Nổi bật

### 🤖 Trợ lý ảo AI (Chatbot)
- Hỗ trợ giải đáp thắc mắc cư dân 24/7.
- Tích hợp trực tiếp vào giao diện người dùng.

### 👥 Quản lý Cư dân & Căn hộ
- Quản lý thông tin cư dân, hộ gia đình.
- Quản lý trạng thái phòng (Trống, Đang ở, Đang bảo trì).

### 💰 Quản lý Tài chính & Phí
- **Hóa đơn:** Tạo và quản lý hóa đơn điện, nước, dịch vụ hàng tháng.
- **Phí quản lý:** Theo dõi đóng phí của từng hộ.
- **Thống kê:** Biểu đồ trực quan về doanh thu và công nợ.

### 🚗 Quản lý Xe & Bãi đỗ
- Đăng ký và quản lý phương tiện cư dân.
- Quản lý slot bãi đỗ xe thông minh.

### 📢 Truyền thông & Tương tác
- **Thông báo:** Gửi thông báo từ ban quản lý đến cư dân.
- **Phản ánh/Khiếu nại:** Cư dân gửi ý kiến, BQL tiếp nhận và xử lý.
- **Đóng góp ý kiến:** Kênh thu thập ý kiến đóng góp xây dựng chung cư.

### 🛡️ Phân quyền & Bảo mật
- Hệ thống đăng nhập/đăng ký an toàn.
- Phân quyền chi tiết cho Admin (Ban quản lý) và User (Cư dân).

---

## 🛠️ Công nghệ Sử dụng

### Backend (backend-main)
- **Language:** Java 21
- **Framework:** Spring Boot 3.2.0
- **Database:** MySQL
- **Security:** Spring Security, OAuth2 Resource Server
- **Utilities:** Apache POI, Docx4j (Xử lý tài liệu), Lombok

### Frontend (frontend-main)
- **Framework:** React 19
- **Styling:** Bootstrap 5, Ant Design, Sass (SCSS)
- **Charts:** D3.js, Flot, Chart.js (Biểu đồ thống kê)
- **Routing:** React Router Dom

---

## 🚀 Hướng dẫn Cài đặt & Chạy Dự án

### Yêu cầu Tiên quyết
- **Java Development Kit (JDK):** Phiên bản 21 trở lên
- **Node.js:** Phiên bản 18 trở lên (khuyến nghị)
- **MySQL:** Cơ sở dữ liệu
- **Maven:** Công cụ build cho Java

### 1. Cài đặt Database
1. Tạo database MySQL mới (ví dụ: `bluemoon_db`).
2. (Tùy chọn) Import file SQL mẫu nếu có.

### 2. Cấu hình & Chạy Backend
1. Di chuyển vào thư mục backend:
   ```bash
   cd backend-main
   ```
2. Mở file `src/main/resources/application.yaml` và cấu hình kết nối database & mail server:
   ```yaml
   server:
     port: 22986
     servlet:
       context-path: /demo # Base URL sẽ là /demo

   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/YOUR_DB_NAME
       username: YOUR_USERNAME
       password: YOUR_PASSWORD
     mail:
        host: smtp.gmail.com
        username: YOUR_EMAIL
        password: YOUR_APP_PASSWORD
   ```
3. Chạy ứng dụng:
   ```bash
   mvn spring-boot:run
   ```
   Backend sẽ chạy tại: `http://localhost:22986/demo`

### 3. Cài đặt & Chạy Frontend
1. Di chuyển vào thư mục frontend:
   ```bash
   cd frontend-main
   ```
2. Cài đặt các thư viện phụ thuộc:
   ```bash
   npm install
   ```
   *Lưu ý: Nếu gặp lỗi dependency, hãy thử `npm install --legacy-peer-deps`*
3. Chạy ứng dụng web:
   ```bash
   npm start
   ```
   Frontend sẽ mở tại: `http://localhost:3000`

---

## 📂 Cấu trúc Dự án

```
ApartmentAI/
├── backend-main/         # Source code Backend (Spring Boot)
│   ├── src/main/java     # Java source code (Controllers, Services, Repositories)
│   └── src/main/resources# Config files, Static resources
├── frontend-main/        # Source code Frontend (ReactJS)
│   ├── src/components    # Các component tái sử dụng (Chatbot, Navbar...)
│   ├── src/pages         # Các trang chính (Home, Login, Admin Dashboard...)
│   └── public            # Static assets
└── README.md             # Tài liệu dự án
```

---

## 🤝 Đóng góp
Dự án được xây dựng bởi nhóm **BlueMoonProject**. Mọi đóng góp xin vui lòng tạo Pull Request hoặc gửi Issue.


