# PokeText Adventure

## A Text-Based Pokemon Game Implementation

## Project Overview

This project is a Java-based implementation of a text-driven Pokemon adventure game, developed as part of my bachelor's thesis. The application demonstrates the integration of traditional text-based gameplay with modern JavaFX graphical interfaces, showcasing object-oriented programming principles and software architecture design.

The game recreates core mechanics from the classic Pokemon series, including Pokemon battles, catching mechanics, world exploration, and persistent data management through a command-line interface enhanced with graphical components.

## Academic Objectives

This implementation serves to demonstrate:

- **Object-Oriented Design**: Comprehensive use of inheritance, polymorphism, and encapsulation
- **Software Architecture**: Modular design with clear separation of concerns
- **Data Management**: JSON-based data persistence and game state management
- **User Interface Integration**: Hybrid text/graphical interface design
- **Game Logic Implementation**: Complex battle systems and game mechanics

## Technical Implementation

### Architecture Overview

The project follows a layered architecture pattern with distinct modules:

```
BattleLogic/     - Combat system and move calculations
Overworld/       - World representation and location management
PlayerRelated/   - Player data and save/load functionality
PokemonLogic/    - Pokemon entities and stat management
WindowThings/    - JavaFX GUI components and window management
```

### Core Features Implemented

**Game Mechanics:**

- Pokemon battle system with type effectiveness and status effects
- Wild Pokemon encounter and capture mechanics
- Trainer progression and Pokemon party management
- World exploration through text commands

**Technical Features:**

- JSON-based data serialization for Pokemon statistics and movesets
- JavaFX integration for enhanced user interface elements
- Save/load system with persistent game state
- Modular code structure enabling easy feature expansion

**User Interface:**

- Command-line interface for primary game interaction
- JavaFX windows for Pokemon statistics and Pokedex viewing
- Real-time battle animations and visual feedback

## System Requirements

- **Java Development Kit**: Version 17 or higher
- **Build Tool**: Apache Maven 3.6+
- **JavaFX**: Included in JDK 17+ distributions

## Installation and Execution

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

## Usage Instructions

### Command Interface

The game operates through a text-based command system:

- `help` - Display available commands and gameplay instructions
- `explore` - Examine current location and available actions
- `pokemon` - View current Pokemon party status
- `pokedex` - Access Pokedex interface (opens JavaFX window)
- `save` - Persist current game state to file
- `pokemon [nickname]` - View detailed Pokemon statistics

### Battle System

Combat utilizes an interactive button interface within JavaFX windows:

- **Fight**: Select and execute Pokemon moves
- **Catch**: Attempt Pokemon capture using available Pokeballs
- **Switch**: Change active Pokemon during battle
- **Run**: Attempt to flee from wild Pokemon encounters

## Project Structure and Design Patterns

```
src/main/java/
├── BattleLogic/          # Strategy pattern for move execution
├── Overworld/            # Composite pattern for location hierarchy
├── PlayerRelated/        # Singleton pattern for player state
├── PokemonLogic/         # Factory pattern for Pokemon creation
├── Utils/                # Utility classes and helper functions
└── WindowThings/         # Observer pattern for UI updates

src/main/resources/       # External data files and assets
├── pokemon.json          # Pokemon base statistics and data
├── moves.json            # Move database with effects and power
├── movesets.json         # Pokemon-specific available moves
└── [multimedia assets]   # Sprites, music, and UI resources
```

## Technologies and Frameworks

**Programming Language:** Java 17  
**GUI Framework:** JavaFX for graphical interface components  
**Build Management:** Apache Maven for dependency management and compilation  
**Data Format:** JSON for external data storage and configuration  
**Architecture Pattern:** Model-View-Controller (MVC) with modular component design

## Key Learning Outcomes

This project demonstrates several important computer science concepts:

1. **Software Engineering Principles**

   - Modular design and code organization
   - Separation of concerns between game logic and presentation
   - Use of established design patterns (Factory, Strategy, Observer)

2. **Data Structures and Algorithms**

   - Efficient Pokemon data management using appropriate collections
   - Battle calculation algorithms with type effectiveness matrices
   - Save/load serialization and deserialization processes

3. **User Interface Design**
   - Integration of command-line and graphical interfaces
   - Event-driven programming with JavaFX
   - User experience considerations for game flow

## Current Implementation Status

**Completed Features:**

- ✅ **Advanced Battle Engine**: Complex turn-based combat system with type effectiveness matrices, critical hit calculations, status effect management, and real-time animated transitions using JavaFX Timeline API
- ✅ **Dynamic Pokemon Management System**: Comprehensive party and PC storage implementation with stat calculation algorithms, experience tracking, and automated level progression mechanics
- ✅ **Sophisticated Data Persistence Architecture**: Multi-layered save system supporting player progress, Pokemon states, inventory management, and world exploration data through JSON serialization
- ✅ **Interactive Pokedex Implementation**: Database-driven Pokemon encyclopedia with dynamic sprite loading, caught/seen tracking algorithms, and detailed statistical analysis displays
- ✅ **Modular World Architecture**: Object-oriented location system implementing the Composite pattern for seamless navigation between interconnected towns, routes, and building interiors

**Future Development:**

- Complete Kanto region expansion with all gym leader implementations

## Testing and Quality Assurance

The project includes comprehensive testing of core functionality:

- Unit tests for battle calculation accuracy
- Integration tests for save/load operations
- User interface testing for JavaFX components
- Game flow validation through complete playthroughs

## Conclusion

This project successfully demonstrates the application of object-oriented programming principles in creating a functional game application. The hybrid interface design showcases modern software development practices while maintaining the nostalgic appeal of classic text-based gaming.

The modular architecture enables future expansion and modification, making it suitable for continued development beyond the scope of this thesis project.
