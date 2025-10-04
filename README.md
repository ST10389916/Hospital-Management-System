# Care Plus  

## 📖 Introduction  
As healthcare systems evolve, the integration of digital tools has become essential for enhancing patient–provider interactions, appointment management, and access to medical records (ScienceDirect, 2024).  

However, many healthcare applications fall short in **low-resource environments** or fail to accommodate the specific needs of different user roles. Addressing these gaps, **Care Plus** is a **Kotlin-based Android application** that offers a streamlined, **offline-capable**, and **role-driven** approach to healthcare appointment and record management.  

Care Plus caters to **three distinct user groups** — Doctors, Admins, and Patients — each presented with a tailored interface and specific features relevant to their responsibilities. By simplifying functionality and reinforcing data security, Care Plus enhances operational efficiency while ensuring an intuitive experience across all user types.  

---

- ### URl:
- Video URL: https://www.youtube.com/watch?v=v9RvlfQzrhg
- Source Code URL: [https://github.com/ST10389916/Hospital-Management-System

### Group Leader:
- Justice Ngwenya - ST100389916
 
###  Name and student numbers of team:
- Blessings Manganye – ST10362704
- Justin Mohale – ST10335113
- Mpho Ndou – ST10362019.

---

## 📌 Key Features  

### 🚑 Role-Specific User Experience  
- **Doctors**: Manage appointments, update patient records, and earn achievement badges.  
- **Admins**: Onboard doctors, manage user records, oversee system workflows, and use the AI assistant.  
- **Patients**: Book appointments, edit profiles, view upcoming consultations, and access curated health news.  

### 🌍 Offline-First Operation  
- Data caching and background synchronization ensure critical features remain available even without constant internet access.  

### 📰 Health News API Integration  
- Patients receive reliable health content from external APIs, improving health awareness in regions with limited education outreach.  

### 🔔 Multi-Channel Notifications  
- Appointment reminders and alerts via **SMS, email, and in-app messages**.  

### 🔒 Secure & Compliant Infrastructure  
- Built on **Firebase** with real-time syncing, scalable backend services, and compliance with **GDPR** and **HIPAA**.  

### 🤖 AI-Assisted Admin Support  
- A unique **Ask AI page** enables Admins to query system operations or healthcare workflows using natural language, powered by **ChatGPT**.  

---

## ⚙️ Functional Requirements  

### 👨‍⚕️ Doctor Menu  
- View & manage appointments.  
- Access & update patient records.  
- Earn badges via light gamification.  

### 🛠️ Admin Menu  
- **Register Doctors**: Onboard and verify doctor credentials.  
- **View Patients**: Browse and manage patient records.  
- **View Doctors**: Access and monitor registered doctors.  
- **View Appointments**: Maintain visibility over scheduled appointments.  
- **Ask AI Page**: Use ChatGPT integration for administrative guidance.  

### 👩‍🦰 Patient Menu  
- **View Health News**: Curated from external APIs with offline caching.  
- **Edit Profile**: Update personal and emergency contact details.  
- **Book & View Appointments**: Schedule consultations and review upcoming/past appointments.  

---

## 🎨 User Interface Design  

### Design Principles  
- **Role-Based Views**: Each role sees only relevant options to reduce clutter.  
- **Accessibility**: High-contrast text, clear labeling, and touch-friendly controls.  
- **Intuitive Layouts**: Simplified navigation for doctors, admins, and patients.  

---

## 🚀 Strategic Aims  
- **Operational Efficiency**: Streamlined workflows reduce administrative burden.  
- **Enhanced Patient Care**: Improved access to records and appointment scheduling.  
- **Inclusive Access**: Offline-first design for rural and bandwidth-limited areas.  
- **Data Privacy**: Enforced through secure infrastructure and role-based permissions.  

---

## 📚 References  
- *ScienceDirect, 2024 – Digital tools in healthcare*  
- *PMC, 2023 – Health education & outreach*  
- *Generative and Agentic AI, 2024 – AI in administration*  

---

## 📱 Tech Stack  
- **Frontend**: Kotlin (Android)  
- **Backend**: Firebase (Authentication, Firestore, Notifications)  
- **APIs**: Health News API, ChatGPT API  
- **Security**: GDPR & HIPAA compliant  

---

## 🏁 Getting Started  

### Prerequisites  
- Android Studio (latest version)  
- Firebase project setup  
- API keys for Health News API and OpenAI (ChatGPT)  

### Installation  
```bash
# Clone the repository
git clone https://github.com/your-username/care-plus.git

# Open project in Android Studio
# Sync Gradle and build
