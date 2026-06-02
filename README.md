# Social Engineering Training App

A JavaFX desktop application for training users to recognize social engineering attacks through interactive scenarios and a knowledge quiz.

## Features

- Interactive scenarios covering phishing, smishing, tech support scams, social media impersonation, spear phishing, pretexting, and baiting
- Branching decision flow with immediate feedback
- Bonus phishing awareness exercise
- 10-question knowledge quiz
- Performance summary screen

## Requirements

- Java 21+
- Maven

## Setup

1. Clone the repository
2. Open in IntelliJ IDEA or your preferred IDE
3. Run `mvn install` to install dependencies
4. Run `Main.java` to start the application

The database (`app.db`) is included; no setup required.

## Database

`schema.sql` and `seed.sql` document the database structure and content. They are not used at runtime but can be used to recreate `app.db` if needed using any SQLite tool.
