package me.kteq.hiddenarmor.listener.packetevents;

import me.kteq.hiddenarmor.HiddenArmor;
import me.kteq.hiddenarmor.handler.ArmorPlaceholderHandler;
import me.kteq.hiddenarmor.manager.PlayerManager;
import org.bukkit.entity.Player;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;

import java.util.List;

public class WindowItemsPacketListener implements PacketListener {

    private final PlayerManager playerManager;
    private final ArmorPlaceholderHandler placeholderHandler;

    public WindowItemsPacketListener(HiddenArmor plugin) {
        this.playerManager = plugin.getPlayerManager();
        this.placeholderHandler = plugin.getArmorPlaceholderHandler();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.WINDOW_ITEMS) return;

        Player player = event.getPlayer();
        if (playerManager.isArmorVisible(player)) return;

        WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);

        // Only handle window id 0 (player inventory)
        if (packet.getWindowId() != 0) return;

        List<ItemStack> items = packet.getItems();
        for (int i = 5; i < 9; i++) {
            ItemStack itemStack = items.get(i);
            if (itemStack != null) {
                ItemStack placeholder = SpigotConversionUtil.fromBukkitItemStack(
                    placeholderHandler.buildItemPlaceholder(
                        SpigotConversionUtil.toBukkitItemStack(itemStack)
                    )
                );
                items.set(i, placeholder);
            }
        }
    }
}
