package me.kteq.hiddenarmor.handler;

public interface PacketHandler {
    void init();
    ArmorUpdateHandler getArmorUpdater();
}