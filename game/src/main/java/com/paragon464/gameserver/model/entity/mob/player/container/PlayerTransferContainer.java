package com.paragon464.gameserver.model.entity.mob.player.container;

import com.paragon464.gameserver.model.entity.mob.player.Player;

public class PlayerTransferContainer implements PlayerTransferContainerInterface {

    public Player player, other;
    public STAGE stage = STAGE.NUETRAL_1;
    public boolean modified;

    @Override
    public void open() {
        // TODO Auto-generated method stub

    }

    @Override
    public void secondScreen() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleClicking(int option, int interfaceId, int button, int amount, int slot) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deposit(int sentId, int amount, int invSlot) {
        // TODO Auto-generated method stub

    }

    @Override
    public void withdraw(int amount, int containerSlot) {
        // TODO Auto-generated method stub

    }

    @Override
    public void closed() {
        // TODO Auto-generated method stub

    }

    public boolean hasAccepted() {
        return (this.stage == STAGE.ACCEPTED_1 || this.stage == STAGE.ACCEPTED_2);
    }

    public boolean allowedToDepositOrWithdraw() {
        return (this.stage.ordinal() < 2);
    }

    public STAGE getStage() {
        return this.stage;
    }

    public void setStage(STAGE stage) {
        this.stage = stage;
    }

    public enum STAGE {
        NUETRAL_1, ACCEPTED_1, NUETRAL_2, ACCEPTED_2, FINISHED
    }
}
