🏫 Campus Lost & Found Marketplace 📱

🔍 An Android-based application designed to help students and campus staff report, search, manage, and recover lost and found items within a college campus.

🎯 Project Goal
To create a centralized digital platform that replaces informal lost-and-found communication through WhatsApp groups, notice boards, and word-of-mouth.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

💡 1. IDEA

🏫 Campus Lost & Found Marketplace

A mobile application where students can:
🔴 Report lost items
🟢 Report found items
🔎 Search for items
📋 Browse reported items
📄 View item details
📞 Contact the owner/finder

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

❓ 2. PROBLEM IDENTIFICATION

Students frequently lose:

📱 Mobile Phones
💻 Laptops
🎧 Earphones
🎒 Bags
👛 Wallets
🪪 ID Cards
📚 Books
🔑 Keys
📄 Documents

Currently, lost/found information is often shared through:

💬 WhatsApp Groups
📢 Notice Boards
👥 Friends & Classmates
📣 Classroom Announcements

❌ Problems:
• Information is scattered
• Difficult to search
• No centralized database
• Difficult to track item status
• Owner and finder may not connect easily

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📋 3. REQUIREMENT ANALYSIS

🔹 Functional Requirements

🔐 User Registration
🔑 User Login
🔴 Report Lost Item
🟢 Report Found Item
📋 View Lost Items
📋 View Found Items
🔎 Search Items
🏷️ Category Filtering
📄 View Item Details
📍 Store Location
📅 Store Date
💾 Store Item Information
📞 Contact Owner/Finder

🔹 Non-Functional Requirements

⚡ Fast Response
🎨 User-Friendly Interface
🔒 Secure Authentication
💾 Reliable Data Storage
📱 Mobile Responsive UI
🛠️ Maintainable Code
🚀 Scalable Architecture

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📄 4. SRS DOCUMENT

The Software Requirements Specification contains:

📌 Project Overview
📌 Problem Statement
📌 Objectives
📌 Scope
📌 Functional Requirements
📌 Non-Functional Requirements
📌 User Requirements
📌 Hardware Requirements
📌 Software Requirements
📌 System Constraints

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔬 5. FEASIBILITY STUDY

💻 Technical Feasibility

Technologies used/planned:

• Kotlin
• Android Studio
• XML
• Room Database
• Spring Boot
• REST API
• MySQL/PostgreSQL
• Firebase

💰 Economic Feasibility

The project can be developed using commonly available development tools and open-source technologies.

👥 Operational Feasibility

Students and campus staff can use the application through a simple mobile interface.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🏗️ 6. SYSTEM DESIGN

👤 User
   ↓
🔐 Login / Register
   ↓
🏠 Home Dashboard
   ↓
┌──────────────┬──────────────┬──────────────┐
↓              ↓              ↓
🔴 Lost       🟢 Found       📝 Post Item
↓              ↓              ↓
📋 Items       📋 Items       💾 Database
└──────────────┴──────────────┘
                 ↓
            📄 Item Details
                 ↓
             📞 Contact

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🎨 7. UI/UX DESIGN

📱 Application Screens:

🚀 Splash Screen
🔐 Login Screen
📝 Registration Screen
🏠 Home Dashboard
🔴 Lost Items
🟢 Found Items
📝 Post Item
📄 Item Details

🎯 UI focuses on:
• Simple navigation
• Clear buttons
• Easy item reporting
• Organized item listings
• Mobile-friendly layouts

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🗄️ 8. DATABASE DESIGN

💾 Room Database

Table: items

┌─────────────────────────┐
│         ITEMS           │
├─────────────────────────┤
│ id                      │
│ name                    │
│ description             │
│ category                │
│ type                    │
│ location                │
│ date                    │
│ status                  │
└─────────────────────────┘

Example:

📱 Name: Black Phone
📝 Description: Samsung black smartphone
🏷️ Category: Electronics
🔴 Type: LOST
📍 Location: Central Library
📅 Date: 17-09-2026
📌 Status: LOST

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

⚙️ 9. PROJECT SETUP

🛠️ Development Environment

💻 IDE: Android Studio
💙 Language: Kotlin
🎨 UI: XML
📦 Build System: Gradle
💾 Database: Room
📱 Platform: Android
🔢 Minimum SDK: API 24
🔢 Compile SDK: API 37
🌐 Version Control: Git & GitHub

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📱 10. ANDROID DEVELOPMENT

Main Activities:

🚀 SplashActivity
🔐 LoginActivity
📝 RegisterActivity
🏠 MainActivity
🔴 LostItemsActivity
🟢 FoundItemsActivity
📝 PostItemActivity
📄 ItemDetailsActivity

📂 Package Structure

com.example.campuslostfound
│
├── 📁 adapter
│   └── ItemAdapter.kt
│
├── 📁 database
│   ├── AppDatabase.kt
│   ├── ItemDao.kt
│   ├── ItemEntity.kt
│   └── ItemRepository.kt
│
├── 📁 model
│   └── Item.kt
│
├── SplashActivity.kt
├── LoginActivity.kt
├── RegisterActivity.kt
├── MainActivity.kt
├── LostItemsActivity.kt
├── FoundItemsActivity.kt
├── PostItemActivity.kt
└── ItemDetailsActivity.kt

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔄 11. APPLICATION WORKFLOW

🚀 Splash
   ↓
🔐 Login
   ↓
🏠 Home
   ↓
┌─────────────┬─────────────┬──────────────┐
↓             ↓             ↓
🔴 LOST      🟢 FOUND      📝 POST
↓             ↓             ↓
📋 List       📋 List       📝 Form
↓             ↓             ↓
📄 Details   📄 Details    💾 Database
└─────────────┴─────────────┴──────────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🗃️ 12. ROOM DATABASE

Room provides local persistence for item information.

DAO Operations:

➕ Insert Item
📋 Get All Items
🔴 Get Lost Items
🟢 Get Found Items
🗑️ Delete Item

Architecture:

Kotlin Application
       ↓
Repository
       ↓
DAO
       ↓
Room Database
       ↓
SQLite

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔌 13. BACKEND / API INTEGRATION

🌐 Planned Architecture:

📱 Android Application
        ↓
🌐 REST API
        ↓
☕ Spring Boot Backend
        ↓
🗄️ MySQL / PostgreSQL

Possible backend technologies:

☕ Java
🌱 Spring Boot
🔗 Spring Data JPA
🌐 REST API
🐬 MySQL
🐘 PostgreSQL

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔐 14. AUTHENTICATION

Current:

🔐 Login UI
📝 Registration UI

Planned:

🔥 Firebase Authentication
🔑 JWT Authentication
🛡️ Spring Security

Workflow:

📝 Register
   ↓
🔐 Login
   ↓
🛡️ Authentication
   ↓
🏠 User Dashboard

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔎 15. SEARCH & FILTERING

Users will be able to search using:

🔍 Item Name
🏷️ Category
📍 Location
📌 Lost / Found
📅 Date

Example:

Search: 📱 Phone

        ↓

📱 Black Phone
📱 Samsung Phone
📱 iPhone
📱 Red Phone

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🖼️ 16. IMAGE UPLOAD

Future functionality:

📸 Upload Item Image
        ↓
💾 Store Image
        ↓
📋 Display Image
        ↓
🔎 Easier Item Identification

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🗺️ 17. LOCATION & MAPS

Future integration:

📍 Lost Location
📍 Found Location
🗺️ Campus Map
📌 Pickup Location

Example:

Item Found
   ↓
Central Library
   ↓
🗺️ Map Location

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔔 18. NOTIFICATIONS

Future notifications can inform users about:

🔔 Matching Items
📢 New Found Items
📌 Status Updates
💬 Responses
🎯 Possible Matches

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🧪 19. TESTING

🔹 Functional Testing

✅ Registration
✅ Login
✅ Navigation
✅ Post Item
✅ Lost Items
✅ Found Items
✅ Item Details
✅ Database Operations

🔹 UI Testing

✅ Buttons
✅ Text Fields
✅ Scrolling
✅ RecyclerView
✅ Different Screen Sizes

🔹 Database Testing

➕ Insert
   ↓
📖 Retrieve
   ↓
📱 Display
   ↓
🗑️ Delete

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🐛 20. DEBUGGING

Tools:

🔍 Android Studio Logcat
🐞 Debugger
🔴 Breakpoints
⚙️ Gradle Build Output
📱 Android Emulator

Process:

❌ Error
 ↓
🔍 Identify
 ↓
🧠 Analyze
 ↓
🔧 Fix
 ↓
🧪 Test Again

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📦 21. APK / AAB GENERATION

Development Build:

💻 Source Code
      ↓
⚙️ Build
      ↓
🧪 Testing
      ↓
🐛 Debugging
      ↓
📦 Release Build
      ↓
📱 APK / AAB

APK → Direct Android installation

AAB → Google Play Store distribution

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📚 22. DOCUMENTATION

Project documentation includes:

📖 Introduction
📋 Problem Statement
🎯 Objectives
📄 SRS
🔬 Feasibility Study
🏗️ System Design
🎨 UI/UX Design
🗄️ Database Design
💻 Implementation
🧪 Testing
🐛 Debugging
📊 Results
🚀 Future Scope
📝 Conclusion

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🎓 23. FINAL PRESENTATION & VIVA

Presentation Topics:

📌 Project Introduction
📌 Problem Statement
📌 Objectives
📌 Existing System
📌 Proposed System
📌 System Architecture
📌 Technologies Used
📌 Database Design
📌 Application Screens
📌 Demonstration
📌 Testing
📌 Future Scope
📌 Conclusion

🎤 Viva Demonstration:

🚀 Splash
 ↓
🔐 Login
 ↓
🏠 Dashboard
 ↓
🔴 Lost / 🟢 Found
 ↓
📝 Post Item
 ↓
💾 Database
 ↓
📄 Item Details

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🛠️ 24. TECHNOLOGY STACK

💙 Kotlin
📱 Android Studio
🎨 XML
📋 RecyclerView
💾 Room Database
🌱 Spring Boot
🌐 REST API
🔥 Firebase
🐬 MySQL / PostgreSQL
🔧 Git
🐙 GitHub

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🚀 25. FUTURE ENHANCEMENTS

🔐 Firebase Authentication
🔑 JWT Authentication
☁️ Cloud Database
🖼️ Image Upload
🔎 Advanced Search
🗺️ Google Maps
📍 Location Services
🔔 Push Notifications
💬 Real-Time Chat
👨‍💼 Admin Dashboard
🤖 AI-Based Item Matching
🧠 Image-Based Item Recognition

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📊 26. PROJECT STATUS

✅ Project Setup
✅ Splash Screen
✅ Login
✅ Registration
✅ Home Dashboard
✅ Lost Items
✅ Found Items
✅ Item Details
✅ Post Item UI
✅ RecyclerView
🔨 Room Database Integration
⏳ MVVM Architecture
⏳ Search & Filtering
⏳ Firebase Authentication
⏳ Spring Boot API
⏳ Image Upload
⏳ Notifications
⏳ Maps
⏳ Complete Testing
⏳ APK/AAB
⏳ Final Documentation

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

👥 27. GITHUB COLLABORATION

This project is developed as a collaborative GitHub project.

👨‍💻 Team Members
       ↓
🌿 Individual Branches
       ↓
💻 Code Development
       ↓
📤 Push Changes
       ↓
🔄 Pull Request
       ↓
👀 Code Review
       ↓
🔀 Merge
       ↓
🌳 Main Branch

GitHub is used for:

📦 Source Code Management
🌿 Branch Management
👥 Team Collaboration
🔄 Pull Requests
📝 Version History

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🎯 28. EXPECTED OUTCOME

The final application will provide a centralized platform where students can:

🔐 Register / Login
🔴 Report Lost Items
🟢 Report Found Items
🔎 Search Items
📋 Browse Items
📄 View Details
📍 Check Locations
📞 Contact Owner/Finder
♻️ Recover Lost Belongings

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🏆 29. PROJECT GOAL

The Campus Lost & Found Marketplace aims to make the campus lost-and-found process:

⚡ Faster
🔎 Easier to Search
📋 Better Organized
📱 Digitally Accessible
💾 Centrally Managed
🤝 Easier for Students to Collaborate

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📌 30. CONCLUSION

🏫 Campus Lost & Found Marketplace provides a centralized digital solution for managing lost and found items within a college campus.

The project demonstrates practical implementation of:

💙 Kotlin
📱 Android Development
🎨 XML UI Design
📋 RecyclerView
💾 Room Database
🏗️ Software Engineering
🔧 Git & GitHub
🌐 API Integration
🧪 Testing
🐛 Debugging

The application can be further expanded with cloud services, authentication, REST APIs, notifications, maps, image recognition, and AI-powered item matching.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

💡 FIND IT. 🔎 REPORT IT. 🤝 RETURN IT.

⭐ Campus Lost & Found Marketplace ⭐
