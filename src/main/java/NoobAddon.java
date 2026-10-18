package com.noob.addon;

import com.noob.addon.modules.SusChunkFinder;
import com.noob.addon.modules.TridentCooldown;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class NoobAddon extends MeteorAddon {

    @Override
    public void onInitialize() {
        Modules.get().add(new SusChunkFinder());
        Modules.get().add(new TridentCooldown());
    }

    @Override
    public String getPackage() {
        return "com.noob.addon";
    }
}