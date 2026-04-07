package me.kteq.hiddenarmor;

import me.kteq.hiddenarmor.command.HiddenArmorTabCompleter;
import me.kteq.hiddenarmor.command.HiddenArmorCommand;
import me.kteq.hiddenarmor.command.ToggleArmorCommand;
import me.kteq.hiddenarmor.handler.ArmorPlaceholderHandler;
import me.kteq.hiddenarmor.handler.ArmorUpdateHandler;
import me.kteq.hiddenarmor.handler.MessageHandler;
import me.kteq.hiddenarmor.handler.PacketEventsHandler;
import me.kteq.hiddenarmor.handler.PacketHandler;
import me.kteq.hiddenarmor.handler.ProtocolLibHandler;
import me.kteq.hiddenarmor.util.ConfigHolder;
import me.kteq.hiddenarmor.util.Metrics;
import me.kteq.hiddenarmor.listener.EntityToggleGlideListener;
import me.kteq.hiddenarmor.listener.GameModeListener;
import me.kteq.hiddenarmor.listener.PotionEffectListener;
import me.kteq.hiddenarmor.listener.InventoryShiftClickListener;
import me.kteq.hiddenarmor.manager.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class HiddenArmor extends JavaPlugin {
    private PlayerManager playerManager;
    private ArmorUpdateHandler armorUpdater;
    private ArmorPlaceholderHandler armorPlaceholderHandler;
    private MessageHandler messageHandler;

    private List<ConfigHolder> configHolders;

    private PacketHandler packetHandler;

    @Override
    public void onEnable() {
        // Default config file
        this.saveDefaultConfig();
        checkConfig();

        // Load ProtocolLib or PacketEvents
        if (isPluginEnabled("packetevents")) {
            this.packetHandler = new PacketEventsHandler(this);
            this.armorUpdater = this.packetHandler.getArmorUpdater();
        } else if (isPluginEnabled("ProtocolLib")) {
            this.packetHandler = new ProtocolLibHandler(this);
            this.armorUpdater = this.packetHandler.getArmorUpdater();
        } else {
            getLogger().warning("No packet library found! Please install either packetevents or ProtocolLib");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Instantiate members
        this.messageHandler = new MessageHandler(this, "&c[&fHiddenArmor&c] &f");
        this.playerManager = new PlayerManager(this);
        this.armorPlaceholderHandler = new ArmorPlaceholderHandler(this);

        // Enable commands
        new ToggleArmorCommand(this, "togglearmor")
                .setPermission("hiddenarmor")
                .setPermissionRequired(false);
        new HiddenArmorCommand(this, "hiddenarmor")
                .setPermission("hiddenarmor")
                .setPermissionRequired(false)
                .setTabCompleter(new HiddenArmorTabCompleter(this));

        // Register packet listeners
        packetHandler.init();

        // Register event listeners
        new InventoryShiftClickListener(this);
        new GameModeListener(this);
        new PotionEffectListener(this);
        new EntityToggleGlideListener(this);

        //getCommand("hiddenarmor").setTabCompleter(new HiddenArmorTabCompleter(this));
        reloadConfig();

        // Metrics
        new Metrics(this, 14419);
    }

    @Override
    public void onDisable() {
        playerManager.saveCurrentEnabledPlayers();
    }

    private void checkConfig() {
        reloadConfig();
        if(getConfig().getInt("config-version") >= getConfig().getDefaults().getInt("config-version"))
            return;
        getLogger().log(Level.WARNING, "Your HiddenArmor configuration file is outdated!");
        getLogger().log(Level.WARNING, "Please regenerate the 'config.yml' file when possible.");
    }

    private boolean isPluginEnabled(String name) {
        return getServer().getPluginManager().isPluginEnabled(name);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (configHolders == null)
            configHolders = new ArrayList<>();
        configHolders.forEach(c -> c.loadConfig(getConfig()));
    }

    public void addConfigHolder(ConfigHolder configHolder) {
        configHolders.add(configHolder);
    }


    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ArmorUpdateHandler getArmorUpdater() {
        return armorUpdater;
    }

    public ArmorPlaceholderHandler getArmorPlaceholderHandler() {
        return armorPlaceholderHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }
}
