# Care Plus  

**Current Version:** 1.0.0  
**Release Date:** 02 October 2026

## 📝 Release Notes & History  

| Version | Release Date | Changes / Features                                           |
|---------|--------------|--------------------------------------------------------------|
| 1.0.0   | 02 Oct 2026  | - Initial release with role-specific interfaces for Doctors, Admins, and Patients.<br>- Appointment booking, management, and notifications implemented.<br>- Health News API integration for patients.<br>- Offline-first functionality with caching and background sync.<br>- AI-assisted Admin support via ChatGPT.<br>- Built with Kotlin (Android) and Firebase backend.<br>- GDPR & HIPAA compliant security. |
| 1.1.0   | TBD          | - Multi-language support.<br>- Enhanced AI query suggestions for Admins.<br>- Improved offline data sync reliability.<br>- Minor UI/UX enhancements. |
| 1.2.0   | TBD          | - Integration with additional external health APIs.<br>- Push notification customization per user role.<br>- Bug fixes and performance optimizations.<br>- Accessibility improvements. |
| 2.0.0   | TBD          | - Major update: Redesigned UI for improved usability.<br>- Support for analytics dashboards for Admins and Doctors.<br>- Expanded patient health record features.<br>- Enhanced security protocols and audit logging. |

---

## 📖 Introduction  
As healthcare systems evolve, the integration of digital tools has become essential for enhancing patient–provider interactions, appointment management, and access to medical records (ScienceDirect, 2024).  

However, many healthcare applications fall short in **low-resource environments** or fail to accommodate the specific needs of different user roles. Addressing these gaps, **Care Plus** is a **Kotlin-based Android application** that offers a streamlined, **offline-capable**, and **role-driven** approach to healthcare appointment and record management.  

Care Plus caters to **three distinct user groups** — Doctors, Admins, and Patients — each presented with a tailored interface and specific features relevant to their responsibilities. By simplifying functionality and reinforcing data security, Care Plus enhances operational efficiency while ensuring an intuitive experience across all user types.  

---

### Group Leader:
- Justice Ngwenya - ST100389916

### Name and student numbers of team:
- Leonard Langa – ST10125760 (Software Developer)
- Millennium Msomi – ST10539382 (Business Analyst/ Researcher)
- Josh Solomon – ST10501615 (UI/UX Designer)

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
