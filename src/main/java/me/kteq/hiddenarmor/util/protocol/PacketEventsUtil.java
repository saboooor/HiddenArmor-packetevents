package me.kteq.hiddenarmor.util.protocol;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PacketEventsUtil {

    public static void broadcastPlayerPacket(Object packet, Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();
        int viewRadius = Bukkit.getViewDistance() * 16;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!(p.getWorld().equals(world)
                    && p.getLocation().distance(loc) < viewRadius
                    && !p.equals(player))) {
                continue;
            }

            PacketEvents.getAPI().getPlayerManager()
                .sendPacket(p, packet);
        }
    }

    public static boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.BOOTS ||
               slot == EquipmentSlot.LEGGINGS ||
               slot == EquipmentSlot.CHEST_PLATE ||
               slot == EquipmentSlot.HELMET;
    }

    public enum ArmorType {
        HELMET(5), CHEST(6), LEGGS(7), BOOTS(8);

        private final int value;

        public static ArmorType getType(int value){
            for (ArmorType type : values()) {
                if (type.value == value) return type;
            }
            return null;
        }

        public int getValue(){
            return value;
        }

        ArmorType(int i){
            this.value = i;
        }
    }

    public static ItemStack getArmor(ArmorType type, PlayerInventory inv) {
        switch (type) {
            case HELMET: if(inv.getHelmet()!=null) return inv.getHelmet().clone();
                break;
            case CHEST: if(inv.getChestplate()!=null) return inv.getChestplate().clone();
                break;
            case LEGGS: if(inv.getLeggings()!=null) return inv.getLeggings().clone();
                break;
            case BOOTS: if(inv.getBoots()!=null) return inv.getBoots().clone();
                break;
        }
        return new ItemStack(Material.AIR);
    }
}