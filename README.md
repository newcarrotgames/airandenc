# Random Encounters - AI-Driven Storytelling for RLCraft Dregora

An advanced Minecraft Forge mod that brings AI-powered dynamic storytelling and persistent narrative arcs to RLCraft Dregora. Track your journey through the wasteland as the mod remembers your choices, manages ongoing story threads, and creates contextually aware encounters that respond to your actions.

## Features

- **Persistent Story State** - Your choices and encounters are remembered across play sessions
- **Dynamic Story Threads** - Narrative arcs that evolve based on your actions (up to 10 concurrent threads)
- **Faction Reputation System** - Track relationships with 9 Dregora factions:
  - Dregorian Colonists
  - Wasteland Nomads
  - Scavenger Clans
  - Blight Cultists
  - Pre-War Military Remnants
  - Trading Guilds
  - Techno-Scavengers
  - Mutant Tribes
  - Independent Survivors
- **Encounter History** - Maintains a detailed log of your last 100 encounters
- **AI Integration Ready** - Prepared for OpenAI GPT-4 and Anthropic Claude integration
- **Automatic Story Management** - Threads progress, become dormant, or resolve based on your gameplay
- **Thread Priority System** - Important story arcs receive priority for continuation

## Requirements

- **Minecraft Version**: 1.12.2
- **Forge Version**: 14.23.5.2768 or compatible
- **Java**: Java 8 or higher
- **Recommended**: RLCraft Dregora modpack (designed specifically for this environment)

## Installation

### For Players

1. **Download the Mod**
   - Locate the built JAR file: `RandomEncounters-1.12.2-0.1.0.jar`
   - Or build from source (see Building from Source below)

2. **Install to Minecraft**
   ```
   - Navigate to your Minecraft installation directory
   - Open the `mods` folder
   - Copy `RandomEncounters-1.12.2-0.1.0.jar` into the mods folder
   ```

3. **Launch Minecraft**
   - Start Minecraft with Forge 1.12.2
   - The mod will initialize automatically on world load

4. **Verify Installation**
   - Check the mod list in the Minecraft main menu
   - Look for "Random Encounters v0.1.0" in the mod list
   - In-game logs should show:
     ```
     [RandomEncounters] Random Encounters - Pre-initialization
     [RandomEncounters] Random Encounters - Initialization
     [RandomEncounters] Story State Manager initialized with world directory: ...
     ```

### File Locations

After first launch, the mod creates the following directory structure:

```
<world_save>/
└── data/
    └── story_encounters/
        └── players/
            └── <player_uuid>.json    # Individual player story states
```

**Important**: These JSON files contain your persistent story data. Back them up to preserve your narrative progress!

## Building from Source

### Prerequisites

- **Git** (to clone the repository)
- **Java JDK 8** or higher
- **Gradle** (included via Gradle Wrapper)

### Build Steps

1. **Clone or Download the Repository**
   ```bash
   cd d:\gamedev\dregoraserver\airandenc
   ```

2. **Build with Gradle**
   ```bash
   # On Windows
   gradlew.bat build

   # On Linux/Mac
   ./gradlew build
   ```

3. **Locate Built Files**
   - Main JAR: `build/libs/RandomEncounters-1.12.2-0.1.0.jar`
   - Sources JAR: `build/libs/RandomEncounters-1.12.2-0.1.0-sources.jar`

4. **Clean Build (if needed)**
   ```bash
   # Windows
   gradlew.bat clean build

   # Linux/Mac
   ./gradlew clean build
   ```

### Development Setup

For mod development:

```bash
# Setup development workspace
gradlew setupDecompWorkspace

# Setup IDE (choose one)
gradlew idea      # IntelliJ IDEA
gradlew eclipse   # Eclipse
```

## Usage

### Current Features (Phase 1 - Foundation)

The mod currently implements the complete story state management system:

#### Story State Tracking
- Automatically creates a story state for each player on first login
- Tracks up to 100 recent encounters per player
- Manages up to 10 concurrent story threads
- Stores faction reputation for all 9 Dregora factions
- Records player traits and notable items

#### Story Thread Lifecycle
- Threads start at ACTIVE status with MEDIUM priority
- Progress from level 1-10 based on encounters
- Automatically promoted to HIGH/URGENT priority as they near completion
- Become DORMANT after 7 days without updates
- Marked as RESOLVED or FAILED when concluded
- Automatically cleaned up (30 days for resolved, 14 days for failed)

#### Data Persistence
- All story state saved to JSON files
- Auto-save on player logout
- Cached in memory for performance
- Graceful handling of corrupted files (creates new state if needed)

### Future Features (In Development)

The following features are planned for future releases:

- **Phase 2**: Context enrichment (inventory scanning, structure detection)
- **Phase 3**: AI prompt building with Dregora lore integration
- **Phase 4**: AI service integration (OpenAI, Anthropic)
- **Phase 5**: Automatic encounter outcome tracking
- **Phase 6**: In-game commands (`/encounter story`, `/encounter threads`, etc.)
- **Phase 7**: Full configuration system

## Configuration

Configuration files will be added in Phase 7. Planned settings include:

```java
// Story System
enableStorySystem = true
maxActiveThreadsPerPlayer = 10
maxEncounterHistorySize = 100

// Dregora Lore
enableDregoraLore = true
defaultTone = "grimdark_with_hope"
emphasizeBlight = true
```

## Troubleshooting

### Mod Doesn't Load

1. **Check Forge Version**
   - Ensure you're running Forge 14.23.5.2768 or compatible
   - Verify Minecraft version is 1.12.2

2. **Check Java Version**
   ```bash
   java -version
   # Should show Java 8 or higher
   ```

3. **Review Logs**
   - Check `logs/latest.log` for error messages
   - Look for lines containing `[RandomEncounters]`

### Build Failures

1. **Clean and Rebuild**
   ```bash
   gradlew clean build
   ```

2. **Java Version Issues**
   - If using Java 16+, the gradle.properties already includes necessary JVM arguments
   - Check that JAVA_HOME points to a valid JDK

3. **Gradle Cache Issues**
   ```bash
   # Clear Gradle cache
   gradlew clean --refresh-dependencies
   ```

### Story State Issues

1. **Corrupted JSON Files**
   - The mod will automatically create a new state file if corruption is detected
   - Backup files are recommended

2. **Permission Errors**
   - Ensure the server has write permissions to `<world>/data/story_encounters/`

3. **Memory Usage**
   - Monitor memory if tracking many players
   - Automatic cleanup helps manage memory over time

## Development

### Project Structure

```
src/main/java/ai/torchlite/randomencounters/
├── RandomEncounters.java              # Main mod class
├── proxy/
│   ├── CommonProxy.java               # Shared logic
│   ├── ClientProxy.java               # Client-side only
│   └── ServerProxy.java               # Server-side only
├── config/
│   └── ConfigHandler.java             # Mod configuration
├── command/
│   └── EncounterCommand.java          # In-game commands (/encounter)
├── story/
│   ├── ThreadStatus.java              # Thread lifecycle states
│   ├── ThreadPriority.java            # Priority levels
│   ├── EncounterSummary.java          # Compressed encounter records
│   ├── StoryThread.java               # Narrative arc data
│   ├── StoryThreadUpdate.java         # Thread modification data
│   ├── PlayerStoryState.java          # Complete player state
│   ├── StoryStateManager.java         # Persistence manager
│   ├── StoryThreadManager.java        # Thread lifecycle logic
│   └── StorytellingResponse.java      # AI response wrapper
├── ai/
│   ├── AIEncounterRequest.java        # Base AI request
│   ├── StorytellingRequest.java       # Extended AI request
│   ├── IAIStorytellingService.java    # Service interface
│   ├── OpenAIStorytellingService.java # OpenAI GPT-4 implementation
│   ├── AnthropicStorytellingService.java # Anthropic Claude implementation
│   ├── AIStorytellingEngine.java      # Main orchestrator
│   └── NarrativePromptBuilder.java    # Dregora prompt builder
└── context/
    └── ContextEnrichmentEngine.java   # Game context gathering
```

### API Usage Example

```java
// Get player story state
StoryStateManager manager = StoryStateManager.getInstance();
PlayerStoryState state = manager.getOrCreateState(player);

// Create a new story thread
StoryThreadManager threadManager = new StoryThreadManager();
StoryThread thread = threadManager.createThread(
    "thread_unique_id",
    "The Scavenger's Revenge",
    "Scavengers seek vengeance for your interference",
    "You helped nomads escape a scavenger raid..."
);
state.addThread(thread);

// Add encounter to history
EncounterSummary encounter = new EncounterSummary(
    "encounter_id",
    "combat",
    "Ambushed by scavengers",
    "victory"
);
state.addEncounterSummary(encounter);

// Save state
manager.saveState(state);
```

See [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md) for detailed implementation guide.

## Credits

- **Developed by**: torchlite.ai
- **Designed for**: RLCraft Dregora
- **Lore Guide**: See [DREGORA_LORE_GUIDE.md](DREGORA_LORE_GUIDE.md)
- **Design Document**: See [AI_STORYTELLING_DESIGN.md](AI_STORYTELLING_DESIGN.md)

## License

[Add your license here]

## Contributing

Contributions are welcome! Please ensure:
- Code follows existing style conventions
- New features include appropriate documentation
- Test your changes with RLCraft Dregora

## Support

For issues, questions, or feature requests:
- Check [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md) for current development status
- Review [AI_STORYTELLING_DESIGN.md](AI_STORYTELLING_DESIGN.md) for design philosophy
- Submit issues with detailed logs and reproduction steps

## Version History

### v0.1.0 (Current)
- ✅ Complete story state management system
- ✅ Persistent player narratives with JSON storage
- ✅ Story thread lifecycle management
- ✅ Faction reputation tracking (9 Dregora factions)
- ✅ Encounter history (last 100 encounters)
- ✅ Automatic thread priority and dormancy
- ✅ Thread cleanup system
- ✅ AI integration framework (OpenAI GPT-4, Anthropic Claude)
- ✅ Context enrichment engine (game state gathering)
- ✅ Narrative prompt builder with Dregora lore
- ✅ In-game commands (/encounter)
- ✅ Full configuration system
- ✅ AI service fallback chain

### Planned
- 🚧 Automatic encounter outcome tracking
- 🚧 Encounter spawning/execution
- 🚧 Additional AI service integrations
- 🚧 Enhanced structure detection
- 🚧 Named location tracking

### Commands Available

- `/encounter story` - View your complete story state and faction reputation
- `/encounter threads` - List all active story threads with progress
- `/encounter history [count]` - View recent encounters (default: 5)
- `/encounter generate` - Manually trigger AI encounter generation
- `/encounter services` - Check which AI services are configured
- `/encounter reload` - Reload configuration (requires op permissions)

---

**Current Status**: Phases 1-4 Complete - Ready for AI-powered encounters!
**Last Updated**: 2025-11-30
