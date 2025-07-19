# PokeText Adventure

## A Text-Based Pokemon Game Implementation

## Project Overview

This project is a Java-based implementation of a text-driven Pokemon adventure game, developed as part of my bachelor's thesis. The application demonstrates the integration of traditional text-based gameplay with modern JavaFX graphical interfaces, showcasing object-oriented programming principles and software architecture design.

The game recreates core mechanics from the classic Pokemon series, including Pokemon battles, catching mechanics, world exploration, and persistent data management through a command-line interface enhanced with graphical components.

### Building the Project

```bash
git clone https://github.com/RemiSpin/PoketextAdventure.git
cd PoketextAdventure
mvn clean compile
```

### Running the Application

**Development Mode:**

```bash
mvn javafx:run
```

**Production Build:**

```bash
mvn clean package
java -jar target/PokeTextAdventure-1.0-SNAPSHOT.jar
```

## Technologies and Frameworks

**Programming Language:** Java 17  
**GUI Framework:** JavaFX for graphical interface components  
**Build Management:** Apache Maven for dependency management and compilation  
**Data Format:** JSON for external data storage and configuration  
