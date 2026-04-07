package me.kteq.hiddenarmor.handler;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.listener.packetevents.EntityEquipmentPacketListener;
import me.kteq.hiddenarmor.listener.packetevents.SetSlotPacketListener;
import me.kteq.hiddenarmor.listener.packetevents.WindowItemsPacketListener;

import java.util.ArrayList;
import java.util.List;

public class PacketEventsHandler implements PacketHandler {
    private final HiddenArmor plugin;

    private final List<PacketListener> listeners = new ArrayList<>();
    private PacketEventsArmorUpdateHandler armorUpdateHandler;

    public PacketEventsHandler(HiddenArmor plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        armorUpdateHandler = new PacketEventsArmorUpdateHandler();

        // Create listeners
        PacketListener setSlot = new SetSlotPacketListener(plugin);
        PacketListener windowItems = new WindowItemsPacketListener(plugin);
        PacketListener entityEquipment = new EntityEquipmentPacketListener(plugin);

        // Store them for cleanup
        listeners.add(setSlot);
        listeners.add(windowItems);
        listeners.add(entityEquipment);

        // Register them
        EventManager events = PacketEvents.getAPI().getEventManager();
        for (PacketListener listener : listeners) {
            events.registerListener(listener, PacketListenerPriority.NORMAL);
        }
    }

    public ArmorUpdateHandler getArmorUpdater() {
        return armorUpdateHandler;
    }
}