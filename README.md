# Graphical-Password-Auth-Module

A Java Spring Boot-based web application implementing graphical password authentication, where users log in by selecting a sequence of images instead of entering traditional alphanumeric passwords.

## 🚀 Features

- 🔐 Secure authentication using a sequence of user-chosen images
- 📧 Email integration for notifications and password recovery
- 🗃️ MySQL-based persistent user storage
- 📄 Javadoc generated for code documentation
- 📦 Built using Spring Boot, JPA, and Maven

## 📁 Project Structure
GraphicalPasswordAuth
│
├── src/
│ └── main/
│ └── java/
│ └── com/
│ └── GraphicalPasswordAuth/
│ ├── controller/
│ ├── services/
│ ├── repository/
│ ├── model/
│ └── GraphicalPasswordAuthApplication.java
│
├── resources/
│ ├── static/
│ ├── templates/
│ └── application.properties
│
├── doc/ # Generated Javadoc
├── pom.xml # Maven configuration
└── README.md


## 🛠️ Technologies Used

- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- Spring Mail
- MySQL
- Maven

## ⚙️ Setup & Run

### Prerequisites

- Java 21+
- Maven
- MySQL (running and accessible)
- Git

### Clone & Build

```bash
git clone https://github.com/divya-gadekar28/Graphical-Password-Auth-Module.git
cd Graphical-Password-Auth-Module
mvn clean install

###Configure Database

Update src/main/resources/application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

###Run the App
mvn spring-boot:run
Navigate to http://localhost:8080 in your browser.

###🧪 Running Tests
mvn test

###📚 Generate Javadoc
mvn javadoc:javadoc
Docs will be available in target/site/apidocs/ or your configured directory.

🌐 Hosting
Frontend/static content can be hosted via GitHub Pages.

Backend can be deployed to platforms like Heroku, Render, or AWS Elastic Beanstalk.

📬 Contact
Made with ❤️ by Divya Gadekar



