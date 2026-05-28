# College Students Community Platform

A modern microservices-based community platform designed for college students, featuring social networking, content sharing, and AI-powered interactions.

## 🏗️ Architecture

This project adopts a microservices architecture built with Spring Cloud and Vue.js, consisting of the following core services:

### Backend Services (Java 17 + Spring Boot 3.1)

| Service | Description | Port |
|---------|-------------|------|
| **api-gateway** | API Gateway with JWT authentication and routing | 8080 |
| **user-server** | User management, authentication, email verification | 8081 |
| **community-server** | Core community features: posts, comments, likes, favorites, friendships | 8082 |
| **agent-server** | AI agent integration with Dify platform | 8083 |
| **notification-server** | Push notifications, email alerts, WebSocket messaging | 8084 |

### Frontend (Vue 3 + Vite)

| Module | Description |
|--------|-------------|
| **feed** | Home feed with post browsing and filtering |
| **publish** | Content creation and publishing |
| **detail** | Post detail view with comments |
| **messages** | Private messaging and friend requests |
| **shopping** | Shopping module |
| **auth** | User authentication and profile management |

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.1 + Spring Cloud 2022.0.4
- **Service Discovery**: Nacos 2.3.2
- **Message Queue**: RabbitMQ 3.13
- **Databases**: MySQL 8.0, MongoDB 7, Redis 7.2
- **ORM**: MyBatis 3.0.4
- **Security**: JWT Authentication
- **Resilience**: Resilience4j 2.1.0
- **AI Integration**: Dify Platform

### Frontend
- **Framework**: Vue 3.4 + Vue Router 4.3
- **State Management**: Pinia 2.1
- **UI Components**: Vant 4.8
- **Build Tool**: Vite 5.2
- **Styling**: SCSS

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **Service Mesh**: Spring Cloud Alibaba
- **API Gateway**: Custom JWT-based gateway

## 📦 Features

- **User Management**: Registration, login, email verification, password reset
- **Social Features**: Posts, comments, likes, favorites, shares
- **Friend System**: Friend requests, notifications, private messaging
- **Content Publishing**: Rich text posts, images, videos, carousels
- **AI Integration**: AI-powered content generation and assistance
- **Real-time Notifications**: WebSocket-based push notifications
- **Email Notifications**: Automated email alerts for important events
- **File Upload**: OSS-based file storage and management
- **Content Moderation**: Review workflow with fallback mechanisms
- **Mobile-First UI**: Responsive design optimized for mobile devices

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+

### Using Docker Compose (Recommended)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Manual Setup

#### Backend

```bash
# Build all modules
mvn clean install

# Run individual services
cd user-server && mvn spring-boot:run
cd community-server && mvn spring-boot:run
cd agent-server && mvn spring-boot:run
cd notification-server && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

#### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build
```

## 📁 Project Structure

```
college_students_project/
├── api-gateway/          # API Gateway service
├── user-server/          # User management service
├── community-server/     # Core community service
├── agent-server/         # AI agent service
├── notification-server/  # Notification service
├── common/               # Shared utilities and DTOs
├── frontend/             # Vue.js frontend application
├── docker-compose.yml    # Docker orchestration
├── database_schema.sql   # MySQL schema
└── mongodb_init.js       # MongoDB initialization
```

## 🔧 Configuration

All services use YAML configuration files located in `src/main/resources/`. Key configurations include:

- Database connections (MySQL, MongoDB, Redis)
- RabbitMQ message broker settings
- Nacos service discovery
- JWT authentication secrets
- Email SMTP settings
- Aliyun OSS storage

> **Note**: Sensitive configurations should be managed via environment variables or a configuration server in production.

## 📊 Infrastructure Services

| Service | Port | Purpose |
|---------|------|---------|
| MySQL | 3307 | Relational data storage |
| Redis | 6379 | Caching and session management |
| MongoDB | 27017 | Document storage for posts and notifications |
| RabbitMQ | 5672/15672 | Message queue for async operations |
| Nacos | 8848/9848 | Service discovery and configuration |

## 📝 API Documentation

API endpoints are available through the API Gateway at `http://localhost:8080`. JWT tokens are required for authenticated endpoints.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is developed for educational purposes.
