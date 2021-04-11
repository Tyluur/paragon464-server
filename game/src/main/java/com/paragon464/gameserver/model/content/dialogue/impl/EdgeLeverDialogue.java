package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.region.Position;

public class EdgeLeverDialogue extends DialogueHandler {

    private GameObject lever;

    public EdgeLeverDialogue(Player player, GameObject obj) {
        super(player, true);
        this.lever = obj;
    }

    @Override
    public void sendDialogue() {
        switch (this.stage) {
            case 0:
                this.options("<col=FF0000>Dangerous PVP</col>", "Castle", "Graveyard", "Hill giants", "Ardy Lever");
                break;
            default:
                if (optionClicked == 1) {
                    World.getWorld().getGlobalObjects().getLevers().pull(player, lever, new Position(3003, 3633, 0));
                } else if (optionClicked == 2) {
                    World.getWorld().getGlobalObjects().getLevers().pull(player, lever, new Position(3149, 3671, 0));
                } else if (optionClicked == 3) {
                    World.getWorld().getGlobalObjects().getLevers().pull(player, lever, new Position(3302, 3653, 0));
                } else if (optionClicked == 4) {
                    World.getWorld().getGlobalObjects().getLevers().pull(player, lever, new Position(3153, 3923, 0));
                }
                end();
                break;
        }
    }
}
