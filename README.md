# 🏢 ApartmentAI - Smart Apartment Management System

ApartmentAI is a comprehensive solution designed to digitize apartment management processes while integrating an AI virtual assistant to enhance the resident experience and optimize the operations of the building management team. The project consists of a robust Spring Boot Backend and a modern ReactJS Frontend.

## ✨ Key Features

### 🤖 AI Virtual Assistant (Chatbot)

* Provides 24/7 support for residents' inquiries.
* Integrated directly into the user interface.

### 👥 Resident & Apartment Management

* Manages resident and household information.
* Manages apartment status (Vacant, Occupied, Under Maintenance).

### 💰 Financial & Fee Management

* **Invoices:** Creates and manages monthly electricity, water, and service invoices.
* **Management Fees:** Tracks fee payments for each household.
* **Statistics:** Provides visual charts for revenue and outstanding debt.

### 🚗 Vehicle & Parking Management

* Registers and manages residents' vehicles.
* Manages parking slots intelligently.

### 📢 Communication & Interaction

* **Announcements:** Sends announcements from the building management team to residents.
* **Feedback/Complaints:** Allows residents to submit feedback or complaints for the building management team to receive and process.
* **Suggestions:** Provides a channel for collecting residents' suggestions to improve the apartment community.

### 🛡️ Authorization & Security

* Secure login and registration system.
* Detailed role-based access control for Admins (Building Management) and Users (Residents).

---

## 🛠️ Technologies Used

### Backend (backend-main)

* **Language:** Java 21
* **Framework:** Spring Boot 3.2.0
* **Database:** MySQL
* **Security:** Spring Security, OAuth2 Resource Server
* **Utilities:** Apache POI, Docx4j (Document Processing), Lombok

### Frontend (frontend-main)

* **Framework:** React 19
* **Styling:** Bootstrap 5, Ant Design, Sass (SCSS)
* **Charts:** D3.js, Flot, Chart.js (Statistical Charts)
* **Routing:** React Router Dom

---

## 🚀 Project Installation & Execution Guide

### Prerequisites

* **Java Development Kit (JDK):** Version 21 or later
* **Node.js:** Version 18 or later (recommended)
* **MySQL:** Database
* **Maven:** Java build tool

### 1. Database Setup

1. Create a new MySQL database (e.g., `bluemoon_db`).
2. (Optional) Import the sample SQL file, if available.

### 2. Backend Configuration & Execution

1. Navigate to the backend directory:

   ```bash
   cd backend-main
   ```
2. Open `src/main/resources/application.yaml` and configure the database connection and mail server:

   ```yaml
   server:
     port: 22986
     servlet:
       context-path: /demo # The base URL will be /demo

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
3. Run the application:

   ```bash
   mvn spring-boot:run
   ```

   The Backend will run at: `http://localhost:22986/demo`

### 3. Frontend Installation & Execution

1. Navigate to the frontend directory:

   ```bash
   cd frontend-main
   ```
2. Install the required dependencies:

   ```bash
   npm install
   ```

   *Note: If you encounter dependency errors, try running `npm install --legacy-peer-deps`.*
3. Run the web application:

   ```bash
   npm start
   ```

   The Frontend will open at: `http://localhost:3000`

---

## 📂 Project Structure

```
ApartmentAI/
├── backend-main/         # Backend source code (Spring Boot)
│   ├── src/main/java     # Java source code (Controllers, Services, Repositories)
│   └── src/main/resources# Configuration files, static resources
├── frontend-main/        # Frontend source code (ReactJS)
│   ├── src/components    # Reusable components (Chatbot, Navbar...)
│   ├── src/pages         # Main pages (Home, Login, Admin Dashboard...)
│   └── public            # Static assets
└── README.md             # Project documentation
```

---

## 🤝 Contribution

The project was developed by the **BlueMoonProject** team. All contributions are welcome. Please create a Pull Request or submit an Issue.
