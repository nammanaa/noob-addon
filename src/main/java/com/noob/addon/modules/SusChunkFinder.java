package com.noob.addon.modules;

import com.noob.addon.NoobCategory;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SusChunkFinder extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
        .name("radius")
        .description("Bán kính quét tính theo chunk")
        .defaultValue(4)
        .min(1)
        .max(16)
        .sliderMax(10)
        .build()
    );

    private final Setting<Integer> fillAlpha = sgGeneral.add(new IntSetting.Builder()
        .name("fill-alpha")
        .description("Độ trong suốt vùng tô màu")
        .defaultValue(40)
        .min(0)
        .max(255)
        .build()
    );

    private final Setting<Integer> lineAlpha = sgGeneral.add(new IntSetting.Builder()
        .name("line-alpha")
        .description("Độ đậm viền chunk")
        .defaultValue(200)
        .min(0)
        .max(255)
        .build()
    );

    private final Setting<Boolean> scanOnTick = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-scan")
        .description("Tự quét lại mỗi 40 tick")
        .defaultValue(true)
        .build()
    );

    private static final Set<Block> SUS_BLOCKS = new HashSet<>(Set.of(
        Blocks.VINE,
        Blocks.CAVE_VINES,
        Blocks.CAVE_VINES_PLANT,
        Blocks.GLOW_LICHEN,
        Blocks.BEEHIVE,
        Blocks.BEE_NEST,
        Blocks.KELP,
        Blocks.KELP_PLANT,
        Blocks.AMETHYST_CLUSTER,
        Blocks.BUDDING_AMETHYST,
        Blocks.SMALL_AMETHYST_BUD,
        Blocks.MEDIUM_AMETHYST_BUD,
        Blocks.LARGE_AMETHYST_BUD,
        Blocks.BAMBOO,
        Blocks.AZALEA,
        Blocks.FLOWERING_AZALEA,
        Blocks.SPORE_BLOSSOM,
        Blocks.HANGING_ROOTS,
        Blocks.ROOTED_DIRT,
        Blocks.MOSS_BLOCK,
        Blocks.MOSS_CARPET
    ));

    private final List<ChunkPos> susChunks = new ArrayList<>();
    private int tickCounter = 0;

    public SusChunkFinder() {
        super(NoobCategory.NOOB, "sus-chunk-finder",
            "Tô đỏ chunk có block tự nhiên (vines, kelp, amethyst...)");
    }

    @Override
    public void onActivate() {
        susChunks.clear();
        scan();
    }

    @Override
    public void onDeactivate() {
        susChunks.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!scanOnTick.get()) return;
        if (++tickCounter >= 40) {
            tickCounter = 0;
            scan();
        }
    }

    private void scan() {
        if (mc.world == null || mc.player == null) return;
        susChunks.clear();

        ChunkPos center = mc.player.getChunkPos();
        int r = radius.get();

        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                ChunkPos cp = new ChunkPos(center.x + dx, center.z + dz);
                WorldChunk chunk = mc.world.getChunk(cp.x, cp.z);
                int minY = mc.world.getBottomY();
                int maxY = mc.world.getTopY();
                boolean found = false;

                outer:
                for (int x = cp.getStartX(); x <= cp.getEndX(); x++) {
                    for (int z = cp.getStartZ(); z <= cp.getEndZ(); z++) {
                        for (int y = minY; y < maxY; y++) {
                            if (SUS_BLOCKS.contains(
                                chunk.getBlockState(new BlockPos(x, y, z)).getBlock())) {
                                found = true;
                                break outer;
                            }
                        }
                    }
                }

                if (found) susChunks.add(cp);
            }
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (susChunks.isEmpty()) return;

        Color fill = new Color(255, 0, 0, fillAlpha.get());
        Color line = new Color(255, 0, 0, lineAlpha.get());

        for (ChunkPos cp : susChunks) {
            event.renderer.box(
                cp.getStartX(), mc.world.getBottomY(), cp.getStartZ(),
                cp.getEndX() + 1, mc.world.getTopY(), cp.getEndZ() + 1,
                fill, line, ShapeMode.Both, 0
            );
        }
    }
}