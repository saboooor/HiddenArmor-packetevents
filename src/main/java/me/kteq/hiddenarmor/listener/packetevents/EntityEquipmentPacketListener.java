package me.kteq.hiddenarmor.listener.packetevents;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.manager.PlayerManager;
import me.kteq.hiddenarmor.util.ItemUtil;
import me.kteq.hiddenarmor.util.ConfigHolder;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class EntityEquipmentPacketListener implements PacketListener, ConfigHolder {

    private final PlayerManager playerManager;
    private final HiddenArmor plugin;

    private boolean ignoreLeatherArmor;
    private boolean ignoreTurtleHelmet;
    private boolean ignoreElytra;
    private List<String> ignoreWorlds;

    public EntityEquipmentPacketListener(HiddenArmor plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
        plugin.addConfigHolder(this);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);

        Player packetPlayer = getPlayerByEntityId(packet.getEntityId());
        if (packetPlayer == null) return;
        if (playerManager.isArmorVisible(packetPlayer)) return;

        List<Equipment> pairList = packet.getEquipment();

        for (Equipment pair : pairList) {
            ItemStack item = pair.getItem();
            if (item == null) continue;

            ItemType material = item.getType();

            if (material == ItemTypes.ELYTRA
                && (packetPlayer.isGliding() || ignoreElytra)
                && !packetPlayer.isInvisible()
            ) {
                pair.setItem(item);
            } else if (!shouldIgnore(item, packetPlayer.getWorld())) {
                ItemStack Air = ItemStack.builder().type(ItemTypes.AIR).build();
                pair.setItem(Air);
            }
        }

        packet.setEquipment(pairList);
    }

    private boolean shouldIgnore(ItemStack itemStack, World world) {
        ItemType material = itemStack.getType();

        return (ignoreWorlds.contains(world.getName())
            || (ignoreLeatherArmor && material.toString().startsWith("LEATHER"))
            || (ignoreTurtleHelmet && material == ItemTypes.TURTLE_HELMET)
            || (!ItemUtil.isArmor(
                SpigotConversionUtil.toBukkitItemStack(itemStack)
            ) && material != ItemTypes.ELYTRA)
            || (ignoreElytra && material == ItemTypes.ELYTRA));
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.ignoreLeatherArmor = config.getBoolean("ignore.leather-armor");
        this.ignoreTurtleHelmet = config.getBoolean("ignore.turtle-helmet");
        this.ignoreElytra = config.getBoolean("ignore.elytra");
        this.ignoreWorlds = config.getStringList("ignore.worlds");
    }

    private Player getPlayerByEntityId(int entityId) {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.getEntityId() == entityId) return p;
        }
        return null;
    }
}