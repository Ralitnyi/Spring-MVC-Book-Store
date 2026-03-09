# Spring MVC Book Store

## Project Overview
The Spring MVC Book Store is an online platform that allows users to browse, search, and purchase a wide variety of books. Built using the Spring Framework, it employs the Model-View-Controller (MVC) architecture to promote a clean separation of concerns throughout the application.

## Features
- User registration and authentication
- Browse and search functionality for books
- Shopping cart management
- Secure payment processing
- User reviews and ratings
- Admin panel for managing book inventory and orders

## Tech Stack
- **Backend:** Java, Spring MVC, Spring Boot
- **Database:** MySQL
- **Frontend:** HTML, CSS, JavaScript, Thymeleaf
- **Build Tool:** Maven

## Setup Instructions
1. Clone the repository:
2. 
   ```bash
   git clone https://github.com/Ralitnyi/Spring-MVC-Book-Store.git
   cd Spring-MVC-Book-Store

3. Install the dependencies:

  ```bash
  mvn install
  ```

3. Set up the database:

Create a new MySQL database and update the application.properties file with your database credentials.
Run the application:

4. Run the application:
   
  ```bash
  mvn spring-boot:run
  ```

## Project Structure

Spring-MVC-Book-Store
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   └── webapp
├── pom.xml
└── README.md
Running the Application
After starting the application, navigate to http://localhost:8080 in your web browser to access the Book Store.
