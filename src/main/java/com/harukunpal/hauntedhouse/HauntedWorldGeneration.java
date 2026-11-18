package com.harukunpal.hauntedhouse;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Lightweight natural generation prototype.
 * A small mansion can appear in roughly one out of every 128 newly explored chunks.
 */
public final class HauntedWorldGeneration {
    private static final int CHANCE_DENOMINATOR = 128;

    private HauntedWorldGeneration() {}

    public static void initialize() {
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            ChunkPos chunkPos = chunk.getPos();
            int chunkX = chunkPos.x;
            int chunkZ = chunkPos.z;

            // Stable per-chunk roll: loading the same chunk again does not reroll it.
            int roll = Math.floorMod((chunkX * 73428767) ^ (chunkZ * 912931) ^ 0x5F3759DF,
                    CHANCE_DENOMINATOR);
            if (roll != 0) {
                return;
            }

            int x = chunkX * 16 + 3;
            int z = chunkZ * 16 + 3;
            int y = world.getHeight(Heightmap.Types.WORLD_SURFACE, x + 4, z + 4);
            BlockPos origin = new BlockPos(x, y, z);
            BlockPos marker = origin.below();

            // A hidden marker prevents rebuilding the same house every time its chunk loads.
            if (!world.getBlockState(marker).isAir()) {
                return;
            }

            // Avoid placing the structure underwater or in very cramped terrain.
            if (!world.getBlockState(origin).isAir()
                    || !world.getBlockState(origin.above()).isAir()
                    || !world.getBlockState(origin.above(6)).isAir()) {
                return;
            }

            buildMansion(world, origin);
            world.setBlock(marker, Blocks.BARRIER.defaultBlockState(), 3);
            summonGhost(world, origin);
            HauntedHouseMod.LOGGER.info("Generated a haunted mansion at {}, {}, {}",
                    x, y, z);
        });
    }

    private static void buildMansion(ServerLevel world, BlockPos origin) {
        BlockState floor = Blocks.DARK_OAK_PLANKS.defaultBlockState();
        BlockState wall = Blocks.DARK_OAK_LOG.defaultBlockState();
        BlockState roof = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        BlockState glass = Blocks.GLASS.defaultBlockState();

        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                world.setBlock(origin.offset(x, 0, z), floor, 3);
                world.setBlock(origin.offset(x, 6, z), roof, 3);
            }
        }

        for (int y = 1; y <= 5; y++) {
            for (int i = 0; i < 9; i++) {
                if (y != 2 || i < 3 || i > 5) {
                    world.setBlock(origin.offset(i, y, 0), wall, 3);
                    world.setBlock(origin.offset(i, y, 8), wall, 3);
                }
                world.setBlock(origin.offset(0, y, i), wall, 3);
                world.setBlock(origin.offset(8, y, i), wall, 3);
            }
        }

        for (int x : new int[]{2, 6}) {
            for (int y = 2; y <= 3; y++) {
                world.setBlock(origin.offset(x, y, 0), glass, 3);
                world.setBlock(origin.offset(x, y, 8), glass, 3);
            }
        }

        for (int x : new int[]{2, 6}) {
            for (int z : new int[]{2, 6}) {
                for (int y = 1; y <= 5; y++) {
                    world.setBlock(origin.offset(x, y, z), wall, 3);
                }
            }
        }

        world.setBlock(origin.offset(4, 1, 4), Blocks.SOUL_LANTERN.defaultBlockState(), 3);
        world.setBlock(origin.offset(1, 4, 1), Blocks.COBWEB.defaultBlockState(), 3);
        world.setBlock(origin.offset(7, 4, 7), Blocks.COBWEB.defaultBlockState(), 3);
        world.setBlock(origin.offset(4, 1, 0), Blocks.AIR.defaultBlockState(), 3);
        world.setBlock(origin.offset(4, 2, 0), Blocks.AIR.defaultBlockState(), 3);
        world.setBlock(origin.offset(4, 3, 0), Blocks.AIR.defaultBlockState(), 3);
    }

    private static void summonGhost(ServerLevel world, BlockPos origin) {
        EntityType<?> vexType = BuiltInRegistries.ENTITY_TYPE
                .get(Identifier.fromNamespaceAndPath("minecraft", "vex"))
                .orElseThrow().value();
        Entity ghost = vexType.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (ghost != null) {
            ghost.setPos(origin.getX() + 4.5, origin.getY() + 2.0, origin.getZ() + 4.5);
            ghost.setCustomName(net.minecraft.network.chat.Component.translatable(
                    "entity.haunted_house.ghost"));
            ghost.setCustomNameVisible(true);
            world.addFreshEntity(ghost);
        }
    }
}
