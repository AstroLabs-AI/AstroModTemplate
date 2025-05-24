# IMPORTANT: About the JAR Files

## The Issue
The JAR file `arcanecodex-1.20.1-0.3.0-source.jar` is a **source code archive**, not a compiled mod. It cannot be loaded by Minecraft Forge because it contains `.java` files instead of compiled `.class` files.

## Error You're Seeing
```
[FATAL]: The Mod File has mods that were not found
```
This happens because Forge is looking for compiled class files, specifically `com.astrolabs.arcanecodex.ArcaneCodex.class`.

## How to Build a Working Mod

### Option 1: Use Forge MDK (Recommended)
1. Download Minecraft Forge MDK for 1.20.1
2. Copy the `src` folder from this project
3. Fix the compilation errors (see BUILD_FIXES.md)
4. Run `./gradlew build`

### Option 2: Quick Fix for Testing
1. The mod has API compatibility issues with MC 1.20.1
2. Main issues:
   - `Material` class removed (use `BlockBehaviour.Properties.copy()`)
   - `entity.level` is now `entity.level()`
   - GUI constructors changed
   - Various other API changes

## What's In This JAR
- Complete source code for all 3 phases
- 41+ Java source files
- All recipes, lang files, and resources
- Full implementation of:
  - Quantum Energy System
  - Consciousness System
  - Reality Programming Language
  - Quantum Mechanics
  - All blocks, items, and GUIs

## For Developers
This source code demonstrates advanced Minecraft modding concepts:
- Custom energy systems
- Capability systems
- Complex GUIs
- Particle effects
- Network packets
- Multi-block structures

The code is well-structured and can serve as a reference for implementing similar systems.