# Java 2D RPG Game Engine

A comprehensive Java-based RPG demonstrating advanced object-oriented programming principles and enterprise-level software architecture. This project showcases a complete game development framework with sophisticated mechanics, GUI integration, and scalable design patterns.

## 🎯 What It Does

This is a fully functional 2D RPG game featuring:
- **Character Creation & Progression**: Six-attribute system (Strength, Dexterity, Intelligence, Luck, Constitution, Charisma) with dynamic stat calculation
- **Combat System**: Turn-based battles with hit chance, dodge, parry, critical hits, and asymptotic armor mitigation
- **Equipment & Inventory**: Complete item management with weapons, armor, and crafting materials
- **Enemy System**: Multiple enemy types (Wolf, Slime, Snake) with rarity tiers and level scaling
- **Battle Loops**: Sequential combat encounters with all-or-nothing reward mechanics
- **Save/Load System**: Complete game state persistence using Java serialization
- **GUI Framework**: Swing-based interface with multiple game panels and screen management

## 🏗️ OOP Fundamentals Implemented

### Core OOP Principles
- **Inheritance**: `Enemy` abstract class extended by `Wolf`, `Slime` ; `EquipableItem` extended by `Weapon`, `Armor`
- **Polymorphism**: `CharactersGeneralMethods` interface implemented by `Player` and `Enemy` classes
- **Encapsulation**: Private fields with public getter/setter methods across all classes
- **Abstraction**: Abstract `Enemy` and `EquipableItem` classes defining common behavior

### Advanced Design Patterns
- **Strategy Pattern**: `CombatCalculator` with interchangeable combat algorithms
- **Template Method**: Abstract enemy class with customizable stat generation
- **Singleton Pattern**: Centralized `GameSaver` utility class
- **Observer Pattern**: Automatic stat recalculation when equipment changes
- **Factory Pattern**: Dynamic enemy generation with configurable parameters

### Enterprise Java Features
- **Serialization**: Complete save/load functionality 
- **Exception Handling**: Robust error management with try-catch blocks and graceful degradation
- **Generics & Collections**: Type-safe `ArrayList`, `HashMap` usage throughout
- **Enum Types**: `EnemyType`, `EnemyRarity`, `EquipmentSlot` for type safety
- **Interface Segregation**: Focused interfaces for specific behaviors
- **Composition**: `Player` contains `Inventory`, `GameWindow` manages multiple panels

## 🛠️ Technical Architecture

\`\`\`
├── Core Game Logic
│   ├── Handler.java - Central game coordinator
│   ├── LoopHandler.java - Battle sequence management  
│   ├── CombatCalculator.java - Mathematical combat engine
│   └── GameSaver.java - Serialization utilities
├── Character System
│   ├── Player.java - Complete player with stats & inventory
│   ├── Enemy.java - Abstract enemy base class
│   └── Inventory.java - Item management system
├── Equipment Framework
│   ├── EquipableItem.java - Abstract equipment base
│   ├── Weapon.java / Armor.java - Concrete implementations
│   └── CraftItem.java - Crafting materials // still not done 
└── GUI System
    ├── GameWindow.java - Main JFrame container
    ├── GamePanel.java - CardLayout screen manager
    └── Multiple UI panels for different game screens
\`\`\`

## 🎮 Key Technical Features

- **Mathematical Game Balance**: Asymptotic formulas preventing stat inflation, probability-based enemy generation
- **Complex State Management**: Multi-layered game state with automatic persistence
- **Event-Driven Architecture**: Handler pattern coordinating game systems
- **Modular Design**: Loosely coupled components enabling easy expansion
- **Performance Optimization**: Efficient algorithms with O(1) lookups 

## 💼 Professional Skills Demonstrated

- **Advanced OOP Design**: Proper use of inheritance hierarchies, interfaces, and design patterns
- **Enterprise Architecture**: Scalable, maintainable code structure suitable for team development
- **Mathematical Modeling**: Complex probability systems and game balance algorithms  
- **GUI Development**: Professional Swing application with multiple screens and state management
- **Data Persistence**: Robust serialization system with error handling
- **Code Quality**: Comprehensive documentation, consistent naming, and professional structure


## Note that Pngs are not organized but not big deal i hope (:
## Project not finished but is running fine!
---

*This project demonstrates senior-level Java development skills including advanced OOP principles, enterprise design patterns, mathematical problem-solving, and complex system architecture - directly applicable to software engineering roles requiring sophisticated technical expertise.*
