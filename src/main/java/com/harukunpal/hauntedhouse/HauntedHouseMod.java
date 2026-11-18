package com.harukunpal.hauntedhouse;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main entry point for Haunted House MOD. */
public final class HauntedHouseMod implements ModInitializer {
    public static final String MOD_ID = "haunted_house";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HauntedItems.initialize();
        HauntedWorldGeneration.initialize();
        LOGGER.info("Haunted House MOD is loading...");
        LOGGER.info("The mansion is waiting in the dark.");
    }
}
