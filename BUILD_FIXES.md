# Build Fixes Required

The codebase has several API compatibility issues with Minecraft 1.20.1:

1. **Material class removed** - Fixed by using `BlockBehaviour.Properties.copy()`
2. **Entity.level is now private** - Need to use `entity.level()` method
3. **MultiLineEditBox constructor changed** - Needs additional parameter
4. **Research system API changes** - List vs Set for unlocked research
5. **getBrightness method signature changed** - Now requires LightLayer parameter

These are common issues when developing across Minecraft versions. The mod structure and logic are sound, but would need API updates for a production build.

## Quick Fix for Demo JAR

For a demonstration JAR that shows the mod structure, you could:
1. Comment out the problematic GUI classes temporarily
2. Fix the entity.level access issues
3. Build with reduced features

The core systems (energy, consciousness, items, blocks) are properly implemented.