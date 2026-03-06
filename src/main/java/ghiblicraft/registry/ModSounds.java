package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
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

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, new Identifier(GhibliCraft.MOD_ID, "forest_ambience"), FOREST_AMBIENCE);
        Registry.register(Registries.SOUND_EVENT, new Identifier(GhibliCraft.MOD_ID, "village_bells"), VILLAGE_BELLS);
        Registry.register(Registries.SOUND_EVENT, new Identifier(GhibliCraft.MOD_ID, "wind_grass"), WIND_GRASS);
        Registry.register(Registries.SOUND_EVENT, new Identifier(GhibliCraft.MOD_ID, "river_flow"), RIVER_FLOW);
        Registry.register(Registries.SOUND_EVENT, new Identifier(GhibliCraft.MOD_ID, "kodama_click"), KODAMA_CLICK);
        Registry.register(Registries.SOUND_EVENT, new Identifier(GhibliCraft.MOD_ID, "spirit_whisper"), SPIRIT_WHISPER);

        GhibliCraft.LOGGER.info("Registered GhibliCraft sounds.");
    }
}
