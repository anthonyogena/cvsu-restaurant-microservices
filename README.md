# CVSU Restaurant Microservices System

A microservices-based food ordering system built with Spring Boot, MySQL, and Spring Cloud Gateway.

## 🏗 Architecture
This project consists of three microservices:
1.  **API Gateway (Port 8080):** The entry point. Routes traffic to Menu or Order services.
2.  **Menu Service (Port 8081):** Manages food items.
3.  **Order Service (Port 8082):** Handles customer orders and calculates prices by communicating with the Menu Service.

## 🚀 Prerequisites
*   Java 21 or 25
*   Maven
*   MySQL Server

## ⚙️ Setup Instructions

### 1. Database Setup
Open MySQL Workbench and run:
```sql
CREATE DATABASE restaurant_system;
