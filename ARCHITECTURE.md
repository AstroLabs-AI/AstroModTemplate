# Arcane Codex Architecture Overview

## Core Design Principles

1. **Modularity**: Each system operates independently but synergizes with others
2. **Data-Driven**: Content defined through JSON/datapacks where possible
3. **Performance-First**: Heavy calculations offloaded to worker threads
4. **Multiplayer-Ready**: All systems designed with synchronization in mind
5. **Extensibility**: Public API for addon developers

## System Architecture

### Energy System (Quantum Flux)
```
IQuantumEnergy (API)
├── QuantumEnergyStorage (Implementation)
├── EnergyNetworkManager (Graph-based routing)
├── ResonanceCalculator (Energy interactions)
└── FluxEvolutionHandler (Living energy behavior)
```

**Key Features:**
- Six energy types with unique properties
- Energy evolves based on usage patterns
- Resonance cascades between compatible types
- Phase states affect transmission efficiency

### Consciousness System
```
IConsciousness (API)
├── PlayerConsciousnessCapability
├── NeuralNetworkManager
├── ResearchTreeHandler
└── AugmentationRegistry
```

**Key Features:**
- 3D holographic research visualization
- Synaptic linking between discoveries
- Memory fragment puzzle system
- Consciousness level progression

### Reality Manipulation
```
IRealityManipulator (API)
├── RPLInterpreter (Custom language)
├── RealityEffectRegistry
├── GlitchGenerator
└── TemporalMechanicsHandler
```

**RPL (Reality Programming Language) Example:**
```javascript
reality.manifest({
  type: "gravitational_anomaly",
  position: player.lookingAt(),
  strength: 9.8 * amplifier,
  duration: quantum.cycles(100)
});
```

### Dimension System
```
IDimensionalAnchor (API)
├── DimensionCompiler
├── ProceduralDimensionGenerator
├── StabilityManager
└── DimensionDataStorage
```

**Dimension Types:**
- Data Stream: Information mining
- Probability Garden: Quantum superposition
- Time Echo: Temporal mechanics
- Null Space: Void storage

## Technical Implementation Details

### Networking
- Custom packet system for quantum energy updates
- Delta compression for research tree sync
- Predictive client-side effects
- Server authority for reality manipulation

### Performance Optimizations
- Quantum calculations use CompletableFuture
- Energy networks cache valid paths
- LOD system for holographic renders
- Chunk-based reality effect processing

### Data Storage
- NBT for item/block data
- Capabilities for player progression
- WorldSavedData for global systems
- JSON for configuration/recipes

## Content Pipeline

### Blocks
1. Machines extend `QuantumMachineBlock`
2. Multiblocks use structure validation
3. Energy connections via capability
4. Custom rendering for holograms

### Items
1. Tools extend `QuantumTool`
2. Augments implement `IAugment`
3. Components tagged for recipes
4. Memory fragments store data

### Entities
1. Constructs use goal-based AI
2. Anomalies have glitch effects
3. Projections are client-side only
4. All extend quantum base classes

## Progression Flow

```
Tier 0: Discovery
├── Find Neural Interface
├── Craft Quantum Scanner
└── Harvest first energy

Tier 1: Foundation
├── Build energy network
├── Begin research tree
└── Install first augment

Tier 2: Manipulation
├── Learn RPL basics
├── Create multiblocks
└── Advanced augments

Tier 3: Creation
├── Compile dimensions
├── Quantum entanglement
└── Temporal mechanics

Tier 4: Mastery
├── Reality hacking
├── Consciousness upload
└── Server events

Tier 5: Transcendence
├── Pure energy form
├── Reality control
└── Legacy systems
```

## Integration Points

### Forge Systems
- Capabilities for all major systems
- Events for quantum interactions
- Tags for material compatibility
- Loot modifiers for memory fragments

### External Mods
- Create: Quantum-powered kinetics
- CC: Tweaked: RPL computer access
- AE2: Quantum storage cells
- JEI: Custom recipe categories

## Development Guidelines

1. **Energy First**: All features should interact with quantum energy
2. **Player Agency**: Multiple solutions to problems
3. **Emergent Gameplay**: Systems create unexpected interactions
4. **Visual Feedback**: Every action has quantum effects
5. **Progression Gates**: Natural skill/knowledge barriers

## Future Expansion Areas

- Quantum creature taming
- Reality virus system
- Consciousness trading
- Temporal paradox events
- Cross-server dimensions
- Quantum PvP arenas
- Collective consciousness raids
- Reality stock market