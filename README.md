# 🛠️ SkillVerse — AOOP Project (Update 1)

**SkillVerse** is an on-demand service and skills platform. This repository contains the backend and frontend prepared for the **AOOP (Advanced Object-Oriented Programming)** course 1st update.

---

## 📌 Architecture & Design Overview

- **Architecture**: In-Memory OOP architecture without database dependencies.
- **Backend**: Java Spring Boot (`REST Controllers`, `Request DTOs`, `Encapsulated POJOs`).
- **Frontend**: React + Vite UI.

---

## 🗂️ Core ER Diagram Entities & Controllers

The backend directly implements the 9 entities from the submitted ER diagram:

1. **`User`** (`/api/users`)
2. **`WorkerProfile`** (`/api/worker-profiles`)
3. **`WorkerWallet`** (`/api/worker-wallets`)
4. **`ProblemPost`** (`/api/problem-posts`)
5. **`ProblemOffer`** (`/api/problem-offers`)
6. **`ServiceBooking`** (`/api/service-bookings`)
7. **`StoreOrder`** (`/api/store-orders`)
8. **`Course`** (`/api/courses`)
9. **`CourseEnrollment`** (`/api/course-enrollments`)

---

## 🎯 Controller Implementation Guidelines

Each controller implements the required methods demonstrating Spring Boot annotations:

1. **`getById(@PathVariable Long id)`** — Retrieves an entity by its path ID using **`@PathVariable`**.
2. **`save(@RequestBody <Entity>Request request)`** — Creates a new record using **`@RequestBody`** and dedicated **Request Objects (DTOs)**.
3. **`upload(@RequestParam ...)`** — Handles file/metadata parameters using **`@RequestParam`**.
4. **`delete(@PathVariable Long id)`** — Removes an entity by its path ID using **`@PathVariable`**.
5. **`getAll(@RequestParam(required = false) ...)`** — Lists and filters data using **`@RequestParam`**.

---

## 🚀 How to Run

### Backend (Spring Boot)
```bash
cd backend
./gradlew bootRun
```
*Backend runs on `http://localhost:8081`.*

### Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
*Frontend runs on `http://localhost:5173`.*
