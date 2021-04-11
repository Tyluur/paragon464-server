package com.paragon464.gameserver.model.entity.mob.player.container;

public interface TransferContainerInterface {

    int containerSize();

    void open();

    void refresh();

    void handleClicking(final int option, final int interfaceId, final int button, final int slot);

    void deposit(int amount, int invSlot);

    void withdraw(int amount, int containerSlot);

    void closed();
}
