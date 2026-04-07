package me.kteq.hiddenarmor.listener.packetevents;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorPlaceholderHandler;
import me.kteq.hiddenarmor.manager.PlayerManager;

import org.bukkit.entity.Player;

public class SetSlotPacketListener implements PacketListener {

    private final PlayerManager playerManager;
    private final ArmorPlaceholderHandler placeholderHandler;

    public SetSlotPacketListener(HiddenArmor plugin) {
        this.playerManager = plugin.getPlayerManager();
        this.placeholderHandler = plugin.getArmorPlaceholderHandler();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SET_SLOT) return;

        Player player = event.getPlayer();
        if (playerManager.isArmorVisible(player)) return;

        WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);

        // Only handle window id 0 (player inventory)
        if (packet.getWindowId() != 0) return;

        int slot = packet.getSlot();
        if (slot < 5 || slot > 8) return; // armor slots

        ItemStack itemStack = packet.getItem();
        if (itemStack != null) {
            ItemStack placeholder = SpigotConversionUtil.fromBukkitItemStack(
                placeholderHandler.buildItemPlaceholder(
                    SpigotConversionUtil.toBukkitItemStack(itemStack)
                )
            );
            packet.setItem(placeholder);
        }
    }
}