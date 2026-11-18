package com.harukunpal.hauntedhouse;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Registers the invitation item and its dedicated creative tab. */
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

    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY =
            ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(),
                    Identifier.fromNamespaceAndPath(HauntedHouseMod.MOD_ID, "items"));

    public static final CreativeModeTab CREATIVE_TAB = FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup.haunted_house"))
            .icon(() -> new ItemStack(INVITATION))
            .displayItems((parameters, output) -> output.accept(INVITATION))
            .build();

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_KEY, CREATIVE_TAB);
        HauntedHouseMod.LOGGER.info("Registered haunted house invitation and creative tab.");
    }
}
