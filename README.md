# GhibliCraft

### *Where Studio Ghibli meets Minecraft — and AI breathes life into the spirits*

---

## Bringing Ghibli to Life with Claude Agent SDK

GhibliCraft isn't just a mod — it's a living, breathing world. And we're building toward something unprecedented: **AI-powered spirits that think, speak, and remember you.**

Using the [Claude Agent SDK](https://github.com/anthropics/claude-agent-sdk-python), GhibliCraft can evolve beyond static game logic into a world where every spirit has a mind of its own.

### The Vision: AI-Driven Spirit Companions

Imagine a Kodama that doesn't just click and wander — it *observes* you. It notices you planted trees near its grove. It remembers the time you defended the forest from corruption. And when you approach, it speaks:

> *"You returned. The saplings you planted last moon have grown tall. The forest remembers your kindness."*

This is what the Claude Agent SDK enables:

```
┌─────────────────────────────────────────────────────┐
│                  GhibliCraft World                   │
│                                                     │
│   Player ←──→ Spirit NPC (Kodama, No-Face, etc.)   │
│                     │                               │
│              ┌──────┴──────┐                        │
│              │ Agent SDK   │                        │
│              │  ┌────────┐ │                        │
│              │  │ Memory │ │  Remembers past        │
│              │  └────────┘ │  interactions           │
│              │  ┌────────┐ │                        │
│              │  │ Tools  │ │  Reads world state,    │
│              │  └────────┘ │  inventory, karma       │
│              │  ┌────────┐ │                        │
│              │  │ Reason │ │  Generates quests,     │
│              │  └────────┘ │  dialogue, decisions    │
│              └─────────────┘                        │
└─────────────────────────────────────────────────────┘
```

### How It Works

**1. Spirit Dialogue System** — Each spirit type gets its own agent personality:
- **Kodama Spirits** — Shy, cryptic, speak in nature metaphors
- **No-Face** — Communicates through gifts and emotional gestures
- **Forest Guardian** — Wise protector, gives lore about the world
- **Cat Spirit** — Playful, mischievous, drops hints as riddles

**2. Dynamic Quest Generation** — The agent reads your karma, inventory, season, and biome to craft quests that feel personal:
- Low karma? The spirits send you on a redemption arc
- Found a rare item? A spirit notices and weaves it into a storyline
- Festival night? Special one-time quests with unique rewards

**3. World Memory** — Using the SDK's multi-turn conversation and persistence:
- Spirits remember what you've done across sessions
- Your reputation shapes how the entire spirit world treats you
- Emergent storylines that no two players experience the same way

**4. Custom Agent Tools** — Minecraft-specific tools the agent can call:
- `check_player_karma` — Read the Spirit Reputation System
- `get_forest_health` — Query the Forest Corruption state
- `get_season` — Check current Ghibli season
- `get_nearby_spirits` — Sense spirits in range
- `grant_quest` — Assign a generated quest to the player
- `speak_to_player` — Send dialogue with Ghibli-style formatting

### Getting Started with Agent Integration

```python
from claude_agent_sdk import Agent

# Define Minecraft-specific tools
tools = [
    {"name": "check_player_karma", "description": "Get player's spirit reputation"},
    {"name": "get_forest_health", "description": "Query forest corruption level"},
    {"name": "get_season", "description": "Current Ghibli season"},
    {"name": "speak_to_player", "description": "Send in-game dialogue"},
    {"name": "grant_quest", "description": "Assign a quest to the player"},
]

# Create a spirit agent with personality
kodama_agent = Agent(
    model="claude-opus-4-6",
    system="""You are a Kodama spirit in a Ghibli-inspired Minecraft world.
    You are shy, ancient, and deeply connected to the forest.
    You speak in short, poetic phrases. You notice everything about nature.
    You remember every interaction with this player.""",
    tools=tools,
)

# When player interacts with a Kodama in-game:
response = kodama_agent.run(
    f"Player {player_name} approaches. Karma: {karma}. "
    f"Season: {season}. Forest health: {health}. "
    f"They are holding: {held_item}."
)
```

### Architecture: Companion Server

The integration runs as a lightweight sidecar process alongside the Minecraft server:

```
Minecraft Server (Java)
    │
    ├── GhibliCraft Mod (Fabric)
    │       │
    │       └── HTTP Client ──→ Agent Companion Server (Python/TS)
    │                                   │
    │                                   ├── Kodama Agent
    │                                   ├── No-Face Agent
    │                                   ├── Forest Guardian Agent
    │                                   ├── Quest Master Agent
    │                                   └── World Narrator Agent
    │
    └── World State (PersistentState)
```

Each spirit type runs its own agent with distinct personality, memory, and tool access. The Quest Master agent orchestrates multi-step storylines across all spirits. The World Narrator agent provides atmospheric text when entering new biomes or during events.

**This is the future of modding** — not just scripted interactions, but spirits that genuinely *think* about who you are and what you've done.

---

## The Mod

GhibliCraft transforms Minecraft into a peaceful, magical world inspired by the films of Studio Ghibli — *My Neighbor Totoro*, *Spirited Away*, *Princess Mononoke*, *Howl's Moving Castle*, *Kiki's Delivery Service*, and *Nausicaa of the Valley of the Wind*.

### Features

**Spirits & Creatures**
- Kodama Spirits — forest tree spirits that click and glow
- Soot Sprites — tiny workers found in dark places
- Forest Guardians — ancient protectors of the wilderness
- Cat Spirits — mysterious feline companions
- No-Face — enigmatic spirit that follows and gives gifts
- Catbus — rideable mount that leaps over terrain
- Haku Dragon — majestic flying dragon mount
- Turnip Head — loyal scarecrow companion

**Living World Systems**
- Spirit Reputation System — karma that shapes how spirits treat you (persisted)
- Forest Corruption & Healing — your actions affect the health of the land (persisted)
- Seasonal Cycle — Spring, Summer, Autumn, Winter with visual effects and buffs
- Lantern Festival — magical festival every 7th night with floating lanterns
- Wind Currents — dynamic wind for glider flight (Nausicaa-inspired)
- Bus Stop Network — Totoro-style teleportation between stops (persisted)

**Biomes**
- Totoro Forest — dense, magical woodland
- Flower Valley — rolling hills of wildflowers
- Bamboo Hills — towering bamboo groves
- Windmill Fields — pastoral farmland with gentle winds
- Spirit Lake — ethereal lakeside with spirit activity
- Cherry Blossom Highlands, Terraced Rice Valley, Shrine Mountain, and more

**Music & Atmosphere**
- 10 original biome and event music themes
- Biome-aware atmospheric sounds (forest ambience, wind, river, bells)
- Seasonal particles (cherry blossoms, fireflies, snowfall, pollen)
- Morning mist, falling leaves, frost breath effects

**Blocks & Items**
- Calcifer Furnace — smelts 2x faster with 50% fuel bonus
- Enchanted Music Box — plays melodies that grow flowers and attract spirits
- Spirit Bathhouse — multi-block healing structure
- Spell Inscription Table — magical crafting
- Soot Sprite Workshop — automated crafting with soot sprites
- Japanese building blocks: Tatami, Shoji Screens, Paper Lanterns, Stone Lanterns, Wind Chimes
- Rice Paddies — growable crop with 4 growth stages
- 12+ Japanese foods: Onigiri, Ramen, Mochi, Dango, Bento Box, and more
- Wind Rider Glider — soar on wind currents
- Magical Broomstick — Kiki-style flight
- Spirit Portal Key — access the Spirit Realm dimension

**Quests & Progression**
- Dynamic quest system with spirit-given tasks
- Karma tiers: Cursed, Wary, Neutral, Trusted, Revered
- Purification rituals to heal corrupted forests
- Spirit Realm dimension with unique exploration

### Technical Details

- **Platform**: Fabric Mod Loader
- **Minecraft**: 1.20.1
- **Java**: 17+
- **Architecture**: 82 Java source files across 28 packages
- **Persistence**: PersistentState for karma, forest health, and bus stop networks
- **Multiplayer-safe**: Per-player state tracking, no global mutable statics
- **Client-server sync**: DataTracker for entity state (emotions, saddled mounts)
- **Extensible**: BlockTags for tree/flower detection, EntityType-based spirit identification

### Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.20.1
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Drop `ghiblicraft.jar` into your `mods/` folder
4. Launch Minecraft and explore

### Configuration

Edit `config/ghiblicraft-config.json`:
- Spawn rates for each spirit type
- Biome generation weights
- Particle density multipliers
- Toggle music, atmospheric sounds, and comfort buffs

### Building from Source

```bash
git clone https://github.com/SaharBarak/ghiblicraft.git
cd ghiblicraft
./gradlew build
```

The built jar will be in `build/libs/`.

---

*The wind rises. The spirits are watching. The forest remembers.*

*Built with love for Ghibli, Minecraft, and the magic between them.*
