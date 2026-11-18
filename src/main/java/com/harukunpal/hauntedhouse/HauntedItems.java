package com.harukunpal.hauntedhouse;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/** Registers the invitation item. */
public final class HauntedItems {
    private HauntedItems() {}

    public static final ResourceKey<Item> INVITATION_KEY =
            ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(HauntedHouseMod.MOD_ID, "invitation"));

    public static final Item INVITATION = Registry.register(
            BuiltInRegistries.ITEM,
            INVITATION_KEY,
            new HauntedInvitationItem(new Item.Properties().setId(INVITATION_KEY))
    );

    public static void initialize() {
        HauntedHouseMod.LOGGER.info("Registered haunted house invitation.");
    }
}
