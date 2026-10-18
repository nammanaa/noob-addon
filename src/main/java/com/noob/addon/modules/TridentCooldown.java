package com.noob.addon.modules;

import com.noob.addon.NoobCategory;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;

public class TridentCooldown extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> cooldownSeconds = sgGeneral.add(new DoubleSetting.Builder()
        .name("cooldown-seconds")
        .description("Cooldown trident (0 = không cooldown)")
        .defaultValue(0.0)
        .min(0.0)
        .max(5.0)
        .sliderMax(3.0)
        .build()
    );

    private final Setting<Boolean> removeOnZero = sgGeneral.add(new BoolSetting.Builder()
        .name("remove-when-zero")
        .description("Xóa hoàn toàn cooldown khi giá trị là 0")
        .defaultValue(true)
        .build()
    );

    public TridentCooldown() {
        super(NoobCategory.NOOB, "trident-cooldown",
            "Điều chỉnh hoặc xóa cooldown của trident");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        var manager = mc.player.getItemCooldownManager();

        if (removeOnZero.get() && cooldownSeconds.get() == 0.0) {
            manager.remove(Items.TRIDENT);
            return;
        }

        if (manager.isCoolingDown(Items.TRIDENT)) {
            float progress = manager.getCooldownProgress(Items.TRIDENT, 0f);
            float remainingSeconds = progress * 2.0f;
            if (remainingSeconds <= cooldownSeconds.get()) {
                manager.remove(Items.TRIDENT);
            }
        }
    }
}