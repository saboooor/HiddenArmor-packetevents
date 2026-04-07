package me.kteq.hiddenarmor.handler;

import org.bukkit.entity.Player;

public interface ArmorUpdateHandler {
    void updatePlayer(Player player);
    void updateSelf(Player player);
}
