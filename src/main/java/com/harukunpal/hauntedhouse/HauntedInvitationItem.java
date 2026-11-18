package com.harukunpal.hauntedhouse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Right-click a block with this invitation to build a small haunted mansion
 * and summon a vanilla Vex as the first ghost prototype.
 */
public final class HauntedInvitationItem extends Item {
    public HauntedInvitationItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos origin = context.getClickedPos().relative(context.getClickedFace());
        buildMansion(level, origin);

        EntityType<?> vexType = BuiltInRegistries.ENTITY_TYPE.get(
                Identifier.fromNamespaceAndPath("minecraft", "vex")).orElseThrow().value();
        Entity ghost = vexType.create(level, EntitySpawnReason.MOB_SUMMONED);
        if (ghost != null) {
            ghost.setPos(origin.getX() + 4.5, origin.getY() + 2.0, origin.getZ() + 4.5);
            ghost.setCustomName(Component.translatable("entity.haunted_house.ghost"));
            ghost.setCustomNameVisible(true);
            level.addFreshEntity(ghost);
        }

        level.playSound(null, origin, SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, 1.0F, 0.55F);

        if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private static void buildMansion(Level level, BlockPos origin) {
        BlockState floor = Blocks.DARK_OAK_PLANKS.defaultBlockState();
        BlockState wall = Blocks.DARK_OAK_LOG.defaultBlockState();
        BlockState roof = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        BlockState glass = Blocks.GLASS.defaultBlockState();

        // 9 x 9 floor and roof.
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                set(level, origin.offset(x, 0, z), floor);
                set(level, origin.offset(x, 6, z), roof);
            }
        }

        // Walls, windows and a doorway.
        for (int y = 1; y <= 5; y++) {
            for (int i = 0; i < 9; i++) {
                if (y != 2 || i < 3 || i > 5) {
                    set(level, origin.offset(i, y, 0), wall);
                    set(level, origin.offset(i, y, 8), wall);
                }
                set(level, origin.offset(0, y, i), wall);
                set(level, origin.offset(8, y, i), wall);
            }
        }
        for (int x : new int[]{2, 6}) {
            for (int y = 2; y <= 3; y++) {
                set(level, origin.offset(x, y, 0), glass);
                set(level, origin.offset(x, y, 8), glass);
            }
        }

        // Interior pillars, cobwebs and eerie lighting.
        for (int x : new int[]{2, 6}) {
            for (int z : new int[]{2, 6}) {
                for (int y = 1; y <= 5; y++) {
                    set(level, origin.offset(x, y, z), wall);
                }
            }
        }
        set(level, origin.offset(4, 1, 4), Blocks.SOUL_LANTERN.defaultBlockState());
        set(level, origin.offset(1, 4, 1), Blocks.COBWEB.defaultBlockState());
        set(level, origin.offset(7, 4, 7), Blocks.COBWEB.defaultBlockState());
        set(level, origin.offset(4, 1, 0), Blocks.AIR.defaultBlockState());
        set(level, origin.offset(4, 2, 0), Blocks.AIR.defaultBlockState());
        set(level, origin.offset(4, 3, 0), Blocks.AIR.defaultBlockState());
    }

    private static void set(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 3);
    }
}
