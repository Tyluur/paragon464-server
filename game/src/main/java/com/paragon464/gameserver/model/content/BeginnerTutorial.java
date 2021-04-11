package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.impl.TutorialDialogue;

public class BeginnerTutorial {

    private Player player;
    private int stage;

    public BeginnerTutorial(Player player) {
        this.player = player;
        this.stage = 0;
        this.begin();
    }

    public void begin() {
        player.getAttributes().set("stopActions", true);
        player.setVisible(false);
        player.getAttributes().set("dialogue_session", new TutorialDialogue(player, true));
    }

    public void end() {
        player.getAttributes().remove("stopActions");
        player.getAttributes().remove("new_account_verify");
        player.setVisible(true);
        player.getAttributes().set("quest_tab_viewing", false);
        player.getAttributes().set("tutorial_completed", true);
        player.getFrames().refreshContainerInterfaces();
        player.getVariables().giveStarter();
        player.getVariables().setTutorial(null);
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public void increaseStage(int amt) {
        this.stage += amt;
    }
}
