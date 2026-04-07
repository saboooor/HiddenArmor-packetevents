package me.kteq.hiddenarmor.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.kteq.hiddenarmor.util.protocol.PacketEventsUtil;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;

public class PacketEventsArmorUpdateHandler implements ArmorUpdateHandler {

    @Override
    public void updatePlayer(Player player) {
        updateSelf(player);
        updateToOthers(player);
    }

    @Override
    public void updateSelf(Player player) {
    PlayerInventory inv = player.getInventory();

    for (int i = 5; i <= 8; i++) {
        org.bukkit.inventory.ItemStack bukkitArmor = PacketEventsUtil.getArmor(
            PacketEventsUtil.ArmorType.getType(i), inv
        );

        com.github.retrooper.packetevents.protocol.item.ItemStack peArmor =
            SpigotConversionUtil.fromBukkitItemStack(bukkitArmor);

        // windowId=0 (player inventory), stateId=0, slot=i
        WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(0, 0, i, peArmor);

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
    }

    private void updateToOthers(Player player) {
        PlayerInventory inv = player.getInventory();

        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment(
            EquipmentSlot.HELMET,
            SpigotConversionUtil.fromBukkitItemStack(
                PacketEventsUtil.getArmor(PacketEventsUtil.ArmorType.HELMET, inv)
            )
        ));
        equipment.add(new Equipment(
            EquipmentSlot.CHEST_PLATE,
            SpigotConversionUtil.fromBukkitItemStack(
                PacketEventsUtil.getArmor(PacketEventsUtil.ArmorType.CHEST, inv)
            )
        ));
        equipment.add(new Equipment(
            EquipmentSlot.LEGGINGS,
            SpigotConversionUtil.fromBukkitItemStack(
                PacketEventsUtil.getArmor(PacketEventsUtil.ArmorType.LEGGS, inv)
            )
        ));
        equipment.add(new Equipment(
            EquipmentSlot.BOOTS,
            SpigotConversionUtil.fromBukkitItemStack(
                PacketEventsUtil.getArmor(PacketEventsUtil.ArmorType.BOOTS, inv)
            )
        ));
        equipment.add(new Equipment(
            EquipmentSlot.MAIN_HAND,
            SpigotConversionUtil.fromBukkitItemStack(player.getInventory().getItemInMainHand())
        ));
        equipment.add(new Equipment(
            EquipmentSlot.OFF_HAND,
            SpigotConversionUtil.fromBukkitItemStack(player.getInventory().getItemInOffHand())
        ));

        WrapperPlayServerEntityEquipment packet =
            new WrapperPlayServerEntityEquipment(player.getEntityId(), equipment);

        for (Player viewer : player.getWorld().getPlayers()) {
            if (viewer.equals(player)) continue;
            PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet);
        }
    }
}