package io.github.pikachu920;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class FunctionSectionAddon extends JavaPlugin {

    @Override
    public void onEnable() {
        SkriptAddon thisAddon = Skript.registerAddon(this);
        try {
            thisAddon.loadClasses("io.github.pikachu920", "skript");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
