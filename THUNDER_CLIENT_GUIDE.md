# ⚡ SkillVerse — Thunder Client API Testing Guide

This guide contains all endpoints, URLs, and sample request payloads for testing the **SkillVerse Backend** using the **Thunder Client** extension in VS Code or IntelliJ.

---

## 📌 How Data is Stored (No Database)
> **Explanation for Viva / Presentation**:
> There is **no database** attached to this project. All data is stored in **RAM (In-Memory `ArrayList`)** directly inside each Java Controller class (e.g., `userList` in `UserController.java`).
> When the Spring Boot application starts, the constructor initializes starter objects into the list. Any new items saved via `POST` or deleted via `DELETE` remain in memory as long as the server is running.

---

## 🚀 Base URL
```
http://localhost:8080/api
```

---

## 1. 👤 Users Controller (`/api/users`)

### A. Get All Users (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/users`
- **URL with Filter**: `http://localhost:8080/api/users?role=CUSTOMER`

### B. Get User by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/users/1`

### C. Create New User (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/users`
- **Header**: `Content-Type: application/json`
- **Body (JSON)**:
```json
{
  "name": "Sabbir Hossain",
  "email": "sabbir@gmail.com",
  "phone": "01799887766",
  "role": "CUSTOMER",
  "nidNumber": "1995123456789",
  "address": "Banani, Dhaka"
}
```

### D. Upload Profile Picture (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/users/1/upload?profilePictureUrl=https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150`

### E. Delete User (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/users/1`

---

## 2. 🛠️ Worker Profiles (`/api/worker-profiles`)

### A. Get All Worker Profiles (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/worker-profiles`
- **URL with Filter**: `http://localhost:8080/api/worker-profiles?serviceArea=Uttara`

### B. Get Worker Profile by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/worker-profiles/1`

### C. Create Worker Profile (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/worker-profiles`
- **Body (JSON)**:
```json
{
  "userId": 2,
  "skills": "Plumbing, Pipe Fitting, Water Pump",
  "experienceYears": 4,
  "serviceArea": "Dhanmondi, Dhaka",
  "careerLevel": "Silver",
  "hourlyRate": 400.0,
  "basePrice": 300.0
}
```

### D. Update Service Area (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/worker-profiles/1/upload?serviceArea=Gulshan, Dhaka`

### E. Delete Worker Profile (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/worker-profiles/1`

---

## 3. 💰 Worker Wallet (`/api/worker-wallets`)

### A. Get All Wallets (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/worker-wallets`
- **URL with Filter**: `http://localhost:8080/api/worker-wallets?workerId=2`

### B. Get Wallet by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/worker-wallets/1`

### C. Create Worker Wallet (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/worker-wallets`
- **Body (JSON)**:
```json
{
  "workerId": 3,
  "balance": 5000.0,
  "totalEarnings": 12000.0
}
```

### D. Deposit Amount (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/worker-wallets/1/upload?depositAmount=1500.0`

### E. Delete Wallet (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/worker-wallets/1`

---

## 4. 📝 Problem Posts (`/api/problem-posts`)

### A. Get All Problem Posts (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/problem-posts`
- **URL with Filter**: `http://localhost:8080/api/problem-posts?serviceCategory=Electrical`

### B. Get Problem Post by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/problem-posts/1`

### C. Create Problem Post (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/problem-posts`
- **Body (JSON)**:
```json
{
  "customerId": 1,
  "serviceCategory": "HVAC",
  "title": "AC Not Cooling",
  "description": "General 1.5 Ton AC blowing normal room air.",
  "applianceInfo": "General Inverter 1.5 Ton",
  "preferredDate": "2026-10-05",
  "preferredTime": "10:00 AM",
  "address": "Sector 11, Uttara, Dhaka",
  "budgetPrice": 1200.0
}
```

### D. Upload Problem Photo (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/problem-posts/1/upload?photoUrl=https://images.unsplash.com/photo-1581094288338-2314dddb7ecc?w=300`

### E. Delete Problem Post (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/problem-posts/1`

---

## 5. 🤝 Problem Offers (`/api/problem-offers`)

### A. Get All Problem Offers (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/problem-offers`
- **URL with Filter**: `http://localhost:8080/api/problem-offers?problemPostId=1`

### B. Get Problem Offer by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/problem-offers/1`

### C. Create Problem Offer (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/problem-offers`
- **Body (JSON)**:
```json
{
  "problemPostId": 1,
  "workerId": 2,
  "proposedPrice": 1100.0,
  "message": "I can reach your location in 25 minutes with full diagnostic tools.",
  "estimatedArrival": "25 mins"
}
```

### D. Update Offer Status (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/problem-offers/1/upload?status=ACCEPTED`

### E. Delete Problem Offer (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/problem-offers/1`

---

## 6. 📅 Service Bookings (`/api/service-bookings`)

### A. Get All Bookings (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/service-bookings`
- **URL with Filter**: `http://localhost:8080/api/service-bookings?status=CONFIRMED`

### B. Get Booking by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/service-bookings/1`

### C. Create Service Booking (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/service-bookings`
- **Body (JSON)**:
```json
{
  "customerId": 1,
  "workerId": 2,
  "serviceType": "Ceiling Fan & Switch Repair",
  "preferredDate": "2026-10-02",
  "preferredTime": "03:00 PM",
  "address": "Dhanmondi, Dhaka",
  "description": "Switch spark and fan regulator replacement.",
  "estimatedCost": 650.0
}
```

### D. Upload Before Photo (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/service-bookings/1/upload?beforePhoto=https://images.unsplash.com/photo-1505797149-43b0069ec26b?w=300`

### E. Delete Service Booking (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/service-bookings/1`

---

## 7. 🛒 Store Orders (`/api/store-orders`)

### A. Get All Store Orders (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/store-orders`
- **URL with Filter**: `http://localhost:8080/api/store-orders?orderStatus=ORDER_PLACED`

### B. Get Store Order by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/store-orders/1`

### C. Create Store Order (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/store-orders`
- **Body (JSON)**:
```json
{
  "orderNumber": "ORD-2026-9901",
  "userId": 1,
  "totalAmount": 2300.0,
  "customerName": "Rahim Ahmed",
  "phone": "01711112233",
  "address": "Dhanmondi, Dhaka",
  "paymentMethod": "BKASH",
  "orderItemsSummary": "1x Heavy Duty Pipe Wrench, 1x Insulation Tape Pack"
}
```

### D. Upload Delivery Receipt (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/store-orders/1/upload?deliveryReceiptUrl=https://example.com/receipt-9901.pdf`

### E. Delete Store Order (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/store-orders/1`

---

## 8. 🎓 Courses (`/api/courses`)

### A. Get All Courses (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/courses`
- **URL with Filter**: `http://localhost:8080/api/courses?category=Electrical`

### B. Get Course by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/courses/1`

### C. Create Course (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/courses`
- **Body (JSON)**:
```json
{
  "title": "Smart Home IoT Automation & Security",
  "description": "Learn smart switches, CCTV IP cameras, and voice assistant integration.",
  "instructor": "Engr. Mahmudul Hasan",
  "category": "Smart Home",
  "level": "Intermediate",
  "duration": "12 hours",
  "lessonsCount": 18,
  "price": 2500.0,
  "isFree": false
}
```

### D. Upload Syllabus Document (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/courses/1/upload?syllabusDocumentUrl=https://example.com/syllabus.pdf`

### E. Delete Course (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/courses/1`

---

## 9. 📜 Course Enrollments (`/api/course-enrollments`)

### A. Get All Course Enrollments (`@RequestParam`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/course-enrollments`
- **URL with Filter**: `http://localhost:8080/api/course-enrollments?userId=1`

### B. Get Course Enrollment by ID (`@PathVariable`)
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/course-enrollments/1`

### C. Create Course Enrollment (`@RequestBody`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/course-enrollments`
- **Body (JSON)**:
```json
{
  "userId": 1,
  "courseId": 2,
  "paymentStatus": "SUCCESSFUL",
  "paymentMethod": "NAGAD",
  "transactionId": "TRX88291038",
  "amountPaid": 2200.0
}
```

### D. Upload Certificate (`@RequestParam`)
- **Method**: `POST`
- **URL**: `http://localhost:8080/api/course-enrollments/1/upload?certificateUrl=https://example.com/cert-user1-course2.pdf`

### E. Delete Course Enrollment (`@PathVariable`)
- **Method**: `DELETE`
- **URL**: `http://localhost:8080/api/course-enrollments/1`

---

## 📋 Summary of Teacher's Criteria Satisfied

| Annotation | Method Example | How It's Used |
|---|---|---|
| **`@PathVariable`** | `getById(@PathVariable Long id)` | Extracts ID from the URL path: `/api/users/{id}` |
| **`@RequestBody`** | `save(@RequestBody UserRequest request)` | Reads incoming JSON data mapped to a Request DTO class |
| **`@RequestParam`** | `upload(@RequestParam String url)` | Reads query parameters or form values: `?profilePictureUrl=...` |
| **`@PathVariable`** | `delete(@PathVariable Long id)` | Deletes the item by ID from in-memory list: `/api/users/{id}` |
| **`@RequestParam`** | `getAll(@RequestParam(required = false) String filter)` | Filters items by criteria: `/api/users?role=CUSTOMER` |
