package com.paragon464.gameserver.model.entity.mob.player.container;

public interface PlayerTransferContainerInterface {

    void open();

    void secondScreen();

    void refresh();

    void handleClicking(final int option, final int interfaceId, final int button, final int amount,
                        final int slot);

    void deposit(int sentId, int amount, int invSlot);

    void withdraw(int amount, int containerSlot);

    void closed();
}
