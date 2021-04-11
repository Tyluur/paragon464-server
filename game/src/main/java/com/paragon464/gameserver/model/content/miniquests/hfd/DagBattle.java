package com.paragon464.gameserver.model.content.miniquests.hfd;

import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;

public class DagBattle extends BattleController {

    public DagBattle(NPC npc) {
		super(npc);
		// TODO Auto-generated constructor stub
	}

	private int transformCounter = 15;
    private int transformIndex = 0;

    @Override
    public void process() {
        if (transformCounter > 0) {
            transformCounter--;
        } else {
            transformCounter = 15;
            transformIndex++;
            if (transformIndex == 5) {
                transformIndex = 0;
            }
            npc.setTransformationId(3497 + transformIndex);
        }
    }
}
