package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    // Ambient sounds
    public static final SoundEvent FOREST_AMBIENCE = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "forest_ambience"));
    public static final SoundEvent VILLAGE_BELLS = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "village_bells"));
    public static final SoundEvent WIND_GRASS = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "wind_grass"));
    public static final SoundEvent RIVER_FLOW = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "river_flow"));
    public static final SoundEvent KODAMA_CLICK = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "kodama_click"));
    public static final SoundEvent SPIRIT_WHISPER = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "spirit_whisper"));

    // Music tracks — Ghibli-inspired biome and event themes
    public static final SoundEvent MUSIC_TOTORO_FOREST = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.totoro_forest"));
    public static final SoundEvent MUSIC_FLOWER_VALLEY = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.flower_valley"));
    public static final SoundEvent MUSIC_WINDMILL_FIELDS = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.windmill_fields"));
    public static final SoundEvent MUSIC_SPIRIT_LAKE = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.spirit_lake"));
    public static final SoundEvent MUSIC_BAMBOO_HILLS = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.bamboo_hills"));
    public static final SoundEvent MUSIC_LANTERN_FESTIVAL = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.lantern_festival"));
    public static final SoundEvent MUSIC_SPIRIT_REALM = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.spirit_realm"));
    public static final SoundEvent MUSIC_NIGHT_SERENADE = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.night_serenade"));
    public static final SoundEvent MUSIC_DAWN_CHORUS = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.dawn_chorus"));
    public static final SoundEvent MUSIC_BOX_MELODY = SoundEvent.of(
            new Identifier(GhibliCraft.MOD_ID, "music.music_box"));

    public static void register() {
        // Ambient
        reg("forest_ambience", FOREST_AMBIENCE);
        reg("village_bells", VILLAGE_BELLS);
        reg("wind_grass", WIND_GRASS);
        reg("river_flow", RIVER_FLOW);
        reg("kodama_click", KODAMA_CLICK);
        reg("spirit_whisper", SPIRIT_WHISPER);

        // Music
        reg("music.totoro_forest", MUSIC_TOTORO_FOREST);
        reg("music.flower_valley", MUSIC_FLOWER_VALLEY);
        reg("music.windmill_fields", MUSIC_WINDMILL_FIELDS);
        reg("music.spirit_lake", MUSIC_SPIRIT_LAKE);
        reg("music.bamboo_hills", MUSIC_BAMBOO_HILLS);
        reg("music.lantern_festival", MUSIC_LANTERN_FESTIVAL);
        reg("music.spirit_realm", MUSIC_SPIRIT_REALM);
        reg("music.night_serenade", MUSIC_NIGHT_SERENADE);
        reg("music.dawn_chorus", MUSIC_DAWN_CHORUS);
        reg("music.music_box", MUSIC_BOX_MELODY);

        GhibliCraft.LOGGER.info("Registered GhibliCraft sounds.");
    }

    private static void reg(String name, SoundEvent event) {
        Registry.register(Registries.SOUND_EVENT, new Identifier(GhibliCraft.MOD_ID, name), event);
    }
}
