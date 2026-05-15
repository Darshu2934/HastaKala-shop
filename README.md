# Hasta-Kala Shop
A smart Android application built using *GenAI-powered analytics* to help local artisans and small craft businesses manage sales, inventory, and product performance efficiently.

---

# Live Demo
Try the application directly in your browser:
https://appetize.io/app/b_bnbr43qo54r3obpgjcwq26wonu

## Overview

Hasta-Kala Shop is designed for artisans who create handmade products such as:

- Bamboo bags
- Keychains
- Handcrafted accessories
- Decorative items

The app helps users track:

- Sales and income
- Best-selling products
- Inventory stock
- Product performance insights

This project was developed as part of the **MindMatrix VTU Internship Program**.

---

##  Features

### 🧾 Quick Billing
- Add product sales instantly
- Select product and variant/color
- Save transaction quickly

### 📊 Sales Analytics
- Track total income
- Monitor number of orders
- View best-performing products
- Visual sales insights

### 📦 Inventory Management
- Stock availability tracking
- Low stock alerts
- Product inventory monitoring

### 🤖 AI Assistance
- Smart suggestions for product trends
- Sales improvement recommendations
- Analytics-based insights

### 📈 Dashboard
- Daily business overview
- Income summary
- Product performance cards

---

## 🛠️ Tech Stack

### Frontend
- Android Studio
- Kotlin / Java
- XML UI Design

### Backend & Database
- Room Database / SQLite

### Libraries & Tools
- MPAndroidChart
- Material Design Components
- RecyclerView
- ViewModel & LiveData

---
## 🎯 Problem Statement

Many artisans struggle with:

- Unsold inventory
- No sales tracking
- Difficulty identifying popular products
- Lack of business analytics tools

Hasta-Kala Shop solves this problem by providing a simple and intelligent sales analytics platform for small-scale handmade product businesses.

---

## 📸 App Screenshots
<img width="1080" height="2340" alt="image" src="https://github.com/user-attachments/assets/f7a1899e-f8fe-432c-9896-eb3d83ed53af" />
<img width="540" height="1170" alt="image" src="https://github.com/user-attachments/assets/de9369cd-dd9c-4453-abca-2de2917a95e5" />
<img width="540" height="1170" alt="image" src="https://github.com/user-attachments/assets/f6a673a1-31eb-4817-81d1-6bba834bf6f9" />
<img width="540" height="1170" alt="image" src="https://github.com/user-attachments/assets/815d8a17-4eb3-4469-9a56-7a7ffd21a658" />
<img width="540" height="1170" alt="image" src="https://github.com/user-attachments/assets/82bf7d70-502c-444d-905e-bc20294b9ece" />
<img width="1080" height="2340" alt="image" src="https://github.com/user-attachments/assets/8b5118da-acc1-46a6-bc9f-a18c8afd7a64" />
<img width="540" height="1170" alt="image" src="https://github.com/user-attachments/assets/98697aef-8802-449c-9158-8f60bb256eb2" />



### 🏠 Home Dashboard

Features shown in the dashboard:

- Total income tracking
- Orders overview
- Best-selling products
- Inventory status

---
### Open in Android Studio

1. Open Android Studio
2. Select **Open Project**
3. Choose the cloned folder
4. Sync Gradle
5. Run the app

---

## 🔑 Requirements

- Android Studio Hedgehog or above
- Minimum SDK: 24
- Gradle 8+
- Kotlin support enabled

---

## 📈 Future Enhancements

- Cloud database integration
- Multi-user artisan support
- AI demand prediction
- Online payment support
- QR-based billing
- Customer management system

---

## 👨‍💻 Developed By

**Darshan K**

MindMatrix VTU Internship Program

---

## 📄 License

This project is for educational and internship purposes.

---
## 🙌 Acknowledgements

- MindMatrix VTU Internship Program
- Android Developers Documentation
- Open-source Android libraries

  ## 📂 Project Structure

```bash
HastaKalaShop/
│
├── app/
│   ├── java/
│   ├── res/
│   ├── manifests/
│
├── assets/
├── screenshots/
├── README.md

App Architecture Diagram
┌─────────────────────────────────────────┐
│           UI Layer (Activities/Fragments)│
│  - HomeActivity                          │
│  - DashboardFragment                     │
│  - IncomeLogFragment                     │
│  - StockAlertFragment                    │
│  - ProductManagementActivity             │
└─────────────┬───────────────────────────┘
              │
              ↓
┌─────────────────────────────────────────┐
│         ViewModel Layer                  │
│  - SalesViewModel                        │
│  - DashboardViewModel                    │
│  - IncomeViewModel                       │
│  - ProductViewModel                      │
└─────────────┬───────────────────────────┘
              │
              ↓
┌─────────────────────────────────────────┐
│         Repository Layer                 │
│  - SalesRepository                       │
│  - ProductRepository                     │
│  - AnalyticsRepository                   │
└─────────────┬───────────────────────────┘
              │
              ↓
┌─────────────────────────────────────────┐
│         Data Layer (Room Database)       │
│  - AppDatabase                           │
│  - ProductDao                            │
│  - SaleRecordDao                         │
│  - Entities (Product, Variant, Sale)     │
└─────────────────────────────────────────┘


