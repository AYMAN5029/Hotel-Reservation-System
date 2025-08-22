# Hotel Reservation System - Microservices Architecture

A comprehensive hotel reservation system built with **Spring Boot Microservices Architecture**, featuring distributed services with **Netflix Eureka** service discovery, **Spring Cloud Gateway**, and **mock payment processing**. The system provides complete end-to-end functionality for hotel booking, reservation management, and administrative operations.

<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 48 26 PM" src="https://github.com/user-attachments/assets/7a097104-86f4-4ad3-94a0-9cdab8ad005f" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 48 35 PM" src="https://github.com/user-attachments/assets/187dfa01-366a-4c5d-8b30-c4685eddec33" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 48 44 PM" src="https://github.com/user-attachments/assets/48b2da1b-55e8-4734-98ea-ba296e713878" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 49 21 PM" src="https://github.com/user-attachments/assets/5ca6f6f9-05d6-4014-b1a6-19b5198c6db5" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 49 36 PM" src="https://github.com/user-attachments/assets/10490720-e507-466a-a721-ec85bd8d61d6" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 49 50 PM" src="https://github.com/user-attachments/assets/051dabef-41a0-416f-aa21-418e8c97a71d" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 50 05 PM" src="https://github.com/user-attachments/assets/1aba8ac9-be3d-4bf5-9e8b-7bfd689a6ddc" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 50 15 PM" src="https://github.com/user-attachments/assets/5322f76d-cd5c-4de7-a024-a13a4a7a08d9" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 50 39 PM" src="https://github.com/user-attachments/assets/dd69e543-5b44-4d65-b5d6-5081af9c92f4" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 50 47 PM" src="https://github.com/user-attachments/assets/74662b72-9dd3-431d-8a56-e2a5028ca2ec" />
<img width="1552" height="987" alt="Screenshot 2025-08-22 at 1 51 00 PM" src="https://github.com/user-attachments/assets/cb4489ea-1002-4ef7-8ac7-db1b287a515f" />


## ✅ System Status
**FULLY OPERATIONAL** - All microservices are integrated and working end-to-end:
- ✅ User authentication and registration
- ✅ Hotel management with room inventory system
- ✅ Advanced room management (AC/Non-AC room allocation)
- ✅ Real-time room availability tracking and display
- ✅ Reservation workflow with PENDING → CONFIRMED status flow
- ✅ Mock payment processing with automatic reservation confirmation
- ✅ Multi-room booking with dynamic cost calculation
- ✅ Admin dashboard with comprehensive reservation and room management
- ✅ Enhanced search interface with room availability indicators
- ✅ Currency display in Indian Rupees (₹)
- ✅ Frontend integration with all backend services

## 🏗️ Architecture Overview

This system follows a **microservices architecture** pattern with the following services:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Eureka Server │    │   API Gateway   │    │   User Service  │
│   Port: 8761    │    │   Port: 8080    │    │   Port: 8081    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                |
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Hotel Service  │    │Reservation Svc  │    │ Payment Service │
│   Port: 8082    │    │   Port: 8083    │    │   Port: 8084    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 Services Overview

### 1. **Eureka Server** (Port: 8761)
- **Service Discovery & Registration**
- Health monitoring and load balancing
- Dashboard: `http://localhost:8761`

### 2. **API Gateway** (Port: 8080)
- **Single Entry Point** for all client requests
- Request routing and load balancing
- Centralized authentication and security
- Cross-cutting concerns (CORS, rate limiting)

### 3. **User Service** (Port: 8081)
- **Authentication & Authorization**
- User registration and profile management
- Role-based access control (ADMIN/CUSTOMER)
- Data initialization with default admin and customer users
- Database: `user_service_db`

### 4. **Hotel Service** (Port: 8082)
- **Hotel Management & Room Inventory**
- Complete hotel CRUD operations with room management
- Room inventory tracking (AC/Non-AC rooms)
- Real-time room availability management
- Search functionality with advanced filtering
- Room availability checking and updating
- Database: `hotel_service_db`

### 5. **Reservation Service** (Port: 8083)
- **Booking Management & Room Allocation**
- Multi-room reservation support
- Automatic room availability validation
- Integration with hotel service for room management
- Reservation status management (PENDING/CONFIRMED/CANCELLED)
- Room count updates on booking/cancellation
- Database: `reservation_service_db`

### 6. **Payment Service** (Port: 8084)
- **Payment Processing & Reservation Confirmation**
- Mock payment processing with validation
- Automatic reservation status updates after payment
- Support for multiple payment methods
- Transaction tracking and management
- Integration with reservation service for status updates
- Database: `payment_service_db`

**Note**: Security is disabled for testing purposes - all endpoints are accessible without authentication.

## ✨ Key Features

### Customer Features
- **Hotel Search & Discovery** - Advanced search capabilities with room availability:
  - Search by location (city, state, country)
  - Search by hotel name with auto-suggestions
  - Filter by room rates (AC/Non-AC price ranges in ₹)
  - **Real-time room availability display** for each hotel
  - Visual indicators for fully booked hotels
  - Interactive hotel cards with comprehensive details and room status
  - Disabled booking for hotels with no available rooms

- **Multi-Room Reservation System** - Enhanced booking process:
  - **Select number of rooms** (1-5 rooms per booking)
  - Easy date selection with calendar interface
  - Guest count specification with validation
  - Room type selection (AC/Non-AC) with availability check
  - **Dynamic cost calculation** (nights × room_cost × number_of_rooms)
  - Real-time room availability validation during booking
  - Booking confirmation with detailed multi-room summary
  - View and manage personal reservations with room details
  - Cancel bookings with automatic room restoration

- **Enhanced Payment Experience**:
  - Multiple payment method support (Credit Card, Debit Card, UPI, Net Banking)
  - Secure payment processing with validation
  - **Automatic reservation confirmation** after successful payment
  - Real-time status updates from PENDING to CONFIRMED
  - Transaction tracking and payment history

### Admin Features
- **Hotel Management** - Complete CRUD operations for hotels with:
  - Detailed hotel information including long descriptions
  - Location management (city, state, country)
  - Room pricing (AC and Non-AC rates in ₹)
  - **Room Inventory Management** - Set total and available AC/Non-AC rooms
  - Hotel ratings and customer feedback integration
  - Real-time room availability tracking
  - Bulk operations and data export capabilities

- **Room Inventory Control** - Advanced room management system:
  - Set total AC and Non-AC rooms for each hotel
  - Monitor available rooms in real-time
  - Automatic room count updates with bookings
  - Room availability validation and overbooking prevention
  - Visual room status indicators in admin interface

- **Reservation Monitoring** - Complete reservation management system with:
  - View all reservations with search and filtering
  - Multi-room booking support and tracking
  - Detailed reservation information with room counts
  - Cancel reservations with automatic room restoration
  - Export reservation data to CSV
  - Real-time status updates (PENDING → CONFIRMED)
  - Real-time status tracking

### System Features
- **Microservices Architecture** - Scalable and maintainable service separation
- **Service Discovery** - Automatic service registration and discovery with Eureka
- **API Gateway** - Centralized routing and request handling
- **Database Persistence** - MySQL databases for each service
- **Data Initialization** - Automatic sample data creation on startup
- **Extended Data Support** - Hotel descriptions up to 1000 characters
- **Mock Payment Integration** - Complete payment flow without real payment processing

## 🛠️ Technology Stack

### Backend Technologies
- **Spring Boot 3.2.0** - Microservices framework
- **Spring Cloud 2023.0.0** - Cloud-native patterns
- **Netflix Eureka** - Service discovery
- **Spring Cloud Gateway** - API gateway
- **Spring Security** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Spring Data JPA** - Data persistence
- **OpenFeign** - Inter-service communication
- **MySQL 8.0** - Database (separate DB per service)
- **Maven** - Build and dependency management
- **Java 17** - Programming language

### Key Features
- **Microservices Architecture** with service discovery
- **Distributed Authentication** with JWT
- **Database per Service** pattern
- **Inter-service Communication** via Feign clients
- **Centralized API Gateway** for routing
- **Comprehensive Exception Handling**
- **Role-based Security** (ADMIN/CUSTOMER)
- **Health Monitoring** and service registration

## 🗄️ Database Architecture

Each microservice has its own dedicated MySQL database:

- **user_service_db** - User authentication and profile data
- **hotel_service_db** - Hotel information and room details
- **reservation_service_db** - Booking and reservation data
- **payment_service_db** - Payment transactions and refunds

## 🔌 API Endpoints

**All requests go through the API Gateway (Port: 8080)**

### Authentication Endpoints
```
POST /api/auth/login          # User login
POST /api/auth/register       # User registration
```

### Hotel Endpoints
```
GET  /api/hotels               # Get all hotels
GET  /api/hotels/{id}          # Get hotel by ID
GET  /api/hotels/search        # Search hotels with filters
POST /api/hotels               # Create hotel (Admin only)
PUT  /api/hotels/{id}         # Update hotel (Admin only)
DELETE /api/hotels/{id}       # Delete hotel (Admin only)

# Room Management Endpoints
GET  /api/hotels/{id}/room-availability    # Check room availability
POST /api/hotels/{id}/update-room-availability # Update room availability

# Bulk Operations
DELETE /api/reservations/hotel/{hotelId}   # Delete all reservations for a hotel
```

### Reservation Endpoints
```
POST /api/reservations        # Create reservation (Customer/Admin)
GET  /api/reservations/{id}   # Get reservation (Customer/Admin)
PUT  /api/reservations/{id}   # Update reservation (Customer/Admin)
PUT  /api/reservations/{id}/cancel # Cancel reservation (Customer/Admin)
```

### Payment Endpoints
```
POST /api/payments            # Process payment (Customer/Admin)
GET  /api/payments/{id}       # Get payment details (Customer/Admin)
PUT  /api/payments/{id}/refund # Process refund (Customer/Admin)
```

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **MySQL 8.0+**
- **Maven 3.6+**
- **Git**

### 1. Clone the Repository
```bash
git clone <repository-url>
cd project-4
```

### 2. Database Setup
Create the following MySQL databases:
```sql
CREATE DATABASE user_service_db;
CREATE DATABASE hotel_service_db;
CREATE DATABASE reservation_service_db;
CREATE DATABASE payment_service_db;
```

### 3. Configure Database Connection
Update the database credentials in each service's `application.yml` file:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/{database_name}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_mysql_password
```

### 4. Add MySQL Dependency
Ensure each service's `pom.xml` includes the MySQL connector:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 🔌 API Endpoints

### User Management
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login
- `GET /api/users/{userId}` - Get user by ID
- `PUT /api/users/{userId}` - Update user
- `DELETE /api/users/{userId}` - Delete user

### Hotel Management
- `POST /api/hotels` - Add new hotel (Admin only)
- `GET /api/hotels` - Get all hotels
- `GET /api/hotels/{hotelId}` - Get hotel by ID
- `PUT /api/hotels/{hotelId}` - Update hotel (Admin only)
- `DELETE /api/hotels/{hotelId}` - Delete hotel (Admin only)
- `GET /api/hotels/search/city/{city}` - Search hotels by city
- `GET /api/hotels/search/state/{state}` - Search hotels by state

### Reservation Management
- `POST /api/reservations` - Create reservation
- `GET /api/reservations/{reservationId}` - Get reservation by ID
- `GET /api/reservations/user/{userId}` - Get user reservations
- `PUT /api/reservations/{reservationId}` - Update reservation
- `PUT /api/reservations/{reservationId}/cancel` - Cancel reservation

### Payment Management
- `POST /api/payments` - Process payment
- `GET /api/payments/{paymentId}` - Get payment by ID
- `GET /api/payments/reservation/{reservationId}` - Get payment by reservation
- `PUT /api/payments/{paymentId}/refund` - Process refund

## Testing with Postman

### 1. Register a User
```json
POST /api/auth/register
{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "fullName": "John Doe",
    "phoneNumber": "+91-9876543210",
    "role": "CUSTOMER"
}
```

### 2. Login
```json
POST /api/auth/login
{
    "username": "john_doe",
    "password": "password123"
}
```

### 3. Add a Hotel (Admin)
```json
POST /api/hotels
{
  "hotelName": "JW Marriott Hotel Bengaluru",
  "address": "24/1 Vittal Mallya Road, Shanthala Nagar, Ashok Nagar",
  "city": "Bengaluru",
  "state": "Karnataka",
  "country": "India",
  "description": "A 5-star luxury hotel located in Bengaluru’s central business district, adjacent to Cubbon Park and UB City. The hotel features 281 air-conditioned rooms over 14–19 floors, with amenities including a full-service spa, outdoor pool with lazy river, rooftop terrace, fitness center, 5 restaurants, 2 bars/lounges, extensive event spaces, and spa/sauna/steam facilities.",
  "avgRatingByCustomers": 4.9,
  "acRoomCost": 9000,
  "nonAcRoomCost": 7500,
  "totalAcRooms": 30,
  "availableAcRooms": 30,
  "totalNonAcRooms": 15,
  "availableNonAcRooms": 15
}

```

### 4. Create a Reservation
```json
POST /api/reservations
{
    "userId": 1,
    "hotelId": 6,
    "checkInDate": "2025-09-15",
    "checkOutDate": "2025-09-18",
    "roomType": "AC",
    "numberOfRooms": 2,
    "numberOfGuests": 4,
    "totalCost": 45000.0
}
```

### 5. Process Payment
```json
POST /api/payments
{
    "reservationId": 1,
    "amount": 45000.0,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4532-1234-5678-9012",
    "cardHolderName": "John Doe",
    "expiryDate": "12/26",
    "cvv": "123"
}
```

### 6. Update User Profile
```json
PUT /api/users/{userId}
{
    "username": "john_doe_updated",
    "email": "john.updated@example.com",
    "fullName": "John Doe Updated",
    "phoneNumber": "+1234567891"
}
```

### 7. Update Hotel Details (Admin)
```json
PUT /api/hotels/{hotelId}
{
    "hotelName": "The Ritz-Carlton Bangalore - Premium",
    "address": "Residency Road, Shanti Nagar, Premium Wing",
    "city": "Bengaluru",
    "state": "Karnataka",
    "country": "India",
    "description": "The first Ritz-Carlton in India, a 16-storey luxury hotel spread over 3 acres with 277 rooms, spa by ESPA, six upscale F&B outlets and a grand ballroom. Now featuring premium amenities.",
    "avgRatingByCustomers": 4.8,
    "acRoomCost": 8500.0,
    "nonAcRoomCost": 6000.0,
    "totalAcRooms": 160,
    "availableAcRooms": 145,
    "totalNonAcRooms": 117,
    "availableNonAcRooms": 110
}
```

### 8. Update Reservation
```json
PUT /api/reservations/{reservationId}
{
    "checkInDate": "2025-09-16",
    "checkOutDate": "2025-09-20",
    "roomType": "NON_AC",
    "numberOfRooms": 3,
    "numberOfGuests": 6,
    "totalCost": 60000.0
}
```

### 9. Cancel Reservation
```json
PUT /api/reservations/{reservationId}/cancel
```

### 10. Process Refund
```json
PUT /api/payments/{paymentId}/refund
{
    "refundReason": "Customer requested cancellation"
}
```

### 11. Delete User Account (Admin)
```json
DELETE /api/users/{userId}
```

### 12. Delete Hotel (Admin)
```json
DELETE /api/hotels/{hotelId}
```

### 13. Delete Reservation (Admin)
```json
DELETE /api/reservations/{reservationId}
```

### 14. Get User by ID
```json
GET /api/users/{userId}
```

### 15. Search Hotels by City
```json
GET /api/hotels/search?city=Bengaluru
```

### 16. Search Hotels by Name
```json
GET /api/hotels/search?hotelName=Ritz-Carlton
```

### 17. Get All User Reservations
```json
GET /api/reservations/user/{userId}
```

### 18. Get Reservation by ID
```json
GET /api/reservations/{reservationId}
```

### 19. Get Payment Details
```json
GET /api/payments/{paymentId}
```

### 20. Get Payment by Reservation
```json
GET /api/payments/reservation/{reservationId}
```

## API Response Examples

### Successful User Registration Response
```json
{
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890",
    "createdAt": "2024-03-15T10:30:00Z"
}
```

### Successful Hotel Creation Response
```json
{
    "hotelId": 6,
    "hotelName": "The Ritz-Carlton Bangalore",
    "address": "Residency Road, Shanti Nagar",
    "city": "Bengaluru",
    "state": "Karnataka",
    "country": "India",
    "description": "The first Ritz-Carlton in India, a 16-storey luxury hotel spread over 3 acres with 277 rooms, spa by ESPA, six upscale F&B outlets and a grand ballroom.",
    "avgRatingByCustomers": 0.0,
    "acRoomCost": 7500.0,
    "nonAcRoomCost": 5000.0,
    "totalAcRooms": 150,
    "availableAcRooms": 150,
    "totalNonAcRooms": 127,
    "availableNonAcRooms": 127,
    "createdAt": "2025-08-15T10:35:00Z"
}
```

### Successful Reservation Response
```json
{
    "reservationId": 1,
    "userId": 1,
    "hotelId": 6,
    "checkInDate": "2025-09-15",
    "checkOutDate": "2025-09-18",
    "roomType": "AC",
    "numberOfRooms": 2,
    "numberOfGuests": 4,
    "totalCost": 45000.0,
    "status": "CONFIRMED",
    "createdAt": "2025-08-15T10:40:00Z",
    "updatedAt": "2025-08-15T10:40:00Z"
}
```

### Error Response Example
```json
{
    "timestamp": "2025-08-15T10:45:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "Hotel not found with id: 999",
    "path": "/api/hotels/999"
}
```

## Refund Policy

- **7+ days before check-in**: 100% refund
- **3-6 days before check-in**: 75% refund
- **1-2 days before check-in**: 50% refund
- **Same day**: No refund

## 🧪 Testing

The project includes comprehensive unit tests using **JUnit 5** and **Mockito** for all microservices.

### Test Coverage

#### **Hotel Service Tests** (`HotelServiceImplTest.java`)
- **21 test cases** covering:
  - CRUD operations (add, get, update, delete hotels)
  - Search functionality (by city, name, state, country)
  - Room availability management (AC/Non-AC rooms)
  - Image upload validation and error handling
  - Edge cases and error scenarios

#### **Reservation Service Tests** (`ReservationServiceImplTest.java`)
- **20 test cases** covering:
  - Reservation creation with room availability validation
  - Cancellation with refund calculations (100%, 75%, 50%, 0%)
  - Status management (PENDING → CONFIRMED)
  - Bulk operations (delete by hotel ID)
  - Inter-service communication via Feign clients

#### **User Service Tests** (`AuthControllerTest.java`)
- **7 test cases** covering:
  - User registration (success, duplicate validation)
  - Authentication (login success/failure scenarios)
  - Health endpoint monitoring
  - Security and validation testing

### Running Tests

#### Run All Tests for a Service
```bash
# Hotel Service Tests
cd hotel-service
mvn test

# Reservation Service Tests  
cd reservation-service
mvn test

# User Service Tests
cd user-service
mvn test
```

#### Run Specific Test Classes
```bash
# Run specific test class
mvn test -Dtest=HotelServiceImplTest
mvn test -Dtest=ReservationServiceImplTest
mvn test -Dtest=AuthControllerTest
```

#### Test Results Summary
- **Hotel Service**: ✅ 21/21 tests passing
- **Reservation Service**: ✅ 20/20 tests passing
- **User Service**: ✅ 7/7 tests passing

### Test Features
- **Mocking**: Uses Mockito for repository and service mocking
- **Business Logic Testing**: Validates refund calculations, room availability
- **Error Scenarios**: Tests exception handling and edge cases
- **Integration Points**: Tests inter-service communication
- **Data Validation**: Ensures proper input validation and constraints

## 🖥️ Frontend Setup and Startup

The project includes a **React.js frontend** that provides a modern web interface for the hotel reservation system.

### Prerequisites for Frontend
- **Node.js 16+**
- **npm** or **yarn**

### Frontend Features
- **User Authentication** (Login/Register)
- **Hotel Search and Booking**
- **Admin Panel** for hotel management
- **Reservation Management**
- **Payment Processing**
- **Responsive Design** with Bootstrap

### 🚀 Starting the Frontend

#### 1. Navigate to Frontend Directory
```bash
cd frontend
```

#### 2. Install Dependencies
```bash
npm install
# or
yarn install
```

#### 3. Start the Development Server
```bash
npm start
# or
yarn start
```

The frontend will start on **http://localhost:3000** and automatically proxy API requests to the backend services running on port 8080.

#### 4. Build for Production (Optional)
```bash
npm run build
# or
yarn build
```

### Frontend Structure
```
frontend/
├── public/               # Static assets
├── src/
│   ├── components/       # Reusable React components
│   │   ├── common/       # Common components (Navbar, Footer)
│   │   └── forms/        # Form components
│   ├── pages/            # Page components
│   │   ├── admin/        # Admin-specific pages
│   │   ├── auth/         # Authentication pages
│   │   └── customer/     # Customer-specific pages
│   ├── services/         # API service functions
│   ├── contexts/         # React contexts (Auth, etc.)
│   ├── utils/            # Utility functions
│   └── styles/           # CSS and styling
├── package.json          # Dependencies and scripts
└── README.md            # Frontend-specific documentation
```

### 🔗 Frontend-Backend Integration

The frontend is configured to work seamlessly with the microservices:

- **API Base URL**: `http://localhost:8080/api` (via API Gateway)
- **Authentication**: JWT tokens stored in localStorage
- **CORS**: Configured for cross-origin requests
- **Proxy**: Development server proxies API calls to backend

### 📱 Available Routes

#### Public Routes
- `/` - Home page
- `/login` - User login
- `/register` - User registration
- `/hotels` - Browse hotels
- `/hotels/:id` - Hotel details

#### Customer Routes (Authenticated)
- `/dashboard` - Customer dashboard
- `/reservations` - My reservations
- `/profile` - User profile
- `/booking/:hotelId` - Hotel booking

#### Admin Routes (Admin Only)
- `/admin` - Admin dashboard
- `/admin/hotels` - Manage hotels
- `/admin/hotels/add` - Add new hotel
- `/admin/hotels/edit/:id` - Edit hotel
- `/admin/reservations` - Manage all reservations
- `/admin/users` - Manage users

### 🎨 UI Components

The frontend uses **React Bootstrap** for styling and includes:

- **Responsive Navigation Bar**
- **Authentication Forms**
- **Hotel Search and Filters**
- **Booking Forms**
- **Admin Management Tables**
- **Payment Forms**
- **Toast Notifications**
- **Loading Spinners**
- **Modal Dialogs**

## Project Structure

### Backend Structure
```
src/main/java/com/cognizant/hotelmanagement/
├── controllers/           # REST Controllers
├── model/
│   ├── pojo/             # Entity classes
│   ├── dao/
│   │   ├── repositories/ # JPA Repositories
│   │   ├── services/     # Service interfaces
│   │   └── serviceimplementations/ # Service implementations
│   └── exceptions/       # Custom exceptions
└── config/              # Configuration classes
```

### Microservices Structure
```
├── eureka-server/        # Service Discovery
├── api-gateway/          # API Gateway
├── user-service/         # User Management
├── hotel-service/        # Hotel Management
├── reservation-service/  # Reservation Management
├── payment-service/      # Payment Processing
└── frontend/             # React Frontend
```
