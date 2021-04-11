package com.paragon464.gameserver.model.entity.mob.player.container;

import com.paragon464.gameserver.model.entity.mob.player.Player;

public class TransferContainer implements TransferContainerInterface {

    public Player player;

    @Override
    public int containerSize() {
        return 0;
    }

    @Override
    public void open() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleClicking(final int option, final int interfaceId, final int button, final int slot) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deposit(int button, int invSlot) {
        // TODO Auto-generated method stub

    }

    @Override
    public void withdraw(int button, int containerSlot) {
        // TODO Auto-generated method stub

    }

    @Override
    public void closed() {
        // TODO Auto-generated method stub

    }
}
