package me.kteq.hiddenarmor.handler;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.listener.protocollib.EntityEquipmentPacketListener;
import me.kteq.hiddenarmor.listener.protocollib.SetSlotPacketListener;
import me.kteq.hiddenarmor.listener.protocollib.WindowItemsPacketListener;
import me.kteq.hiddenarmor.util.protocol.PacketIndexMapper;

public class ProtocolLibHandler implements PacketHandler {
    private final HiddenArmor plugin;
    private final ProtocolManager manager;
    private final PacketIndexMapper packetIndexMapper;
    private ProtocolLibArmorUpdateHandler armorUpdateHandler;

    public ProtocolLibHandler(HiddenArmor plugin) {
        this.packetIndexMapper = new PacketIndexMapper(
            plugin.getServer().getBukkitVersion().split("-")[0]
        );
        this.plugin = plugin;
        this.manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void init() {
        armorUpdateHandler = new ProtocolLibArmorUpdateHandler(manager, packetIndexMapper);

        manager.addPacketListener(
            new SetSlotPacketListener(plugin, packetIndexMapper)
        );
        manager.addPacketListener(
            new WindowItemsPacketListener(plugin, packetIndexMapper)
        );
        manager.addPacketListener(
            new EntityEquipmentPacketListener(plugin, packetIndexMapper, manager)
        );
    }

    public ArmorUpdateHandler getArmorUpdater() {
        return armorUpdateHandler;
    }
}