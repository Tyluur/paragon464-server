package com.paragon464.gameserver.model.content.dialogue;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCDefinition;
import com.paragon464.gameserver.model.entity.mob.player.Player;

public abstract class DialogueHandler {

    protected Player player;
    protected NPC npc;
    protected int stage = -1, optionClicked = -1;
    protected int expression = DialogueExpressions.JUST_LISTEN;

    public DialogueHandler(NPC npc, Player player, int default_stage) {
        this.npc = npc;
        this.player = player;
        continueStages();
        this.stage = default_stage;
        sendDialogue();
    }

    public void continueStages() {
        this.stage++;
    }

    public abstract void sendDialogue();

    public DialogueHandler(NPC npc, Player player) {
        this.npc = npc;
        this.player = player;
        continueStages();
        //XXX - default stages
        switch (npc.getId()) {
            case 6026:
            case 6043:
            case 6028:
            case 6034:
                this.stage = 12;
                break;
        }
        sendDialogue();
    }

    public DialogueHandler(Player player, boolean send) {
        this.player = player;
        continueStages();
        if (send) {
            sendDialogue();
        }
    }

    public void end() {
        this.stage = -1;
        player.getAttributes().remove("dialogue_session");
        player.getInterfaceSettings().closeInterfaces(false);
    }

    public void handle(Player player, int interfaceId, int button) {
        boolean options = (interfaceId == 228 || interfaceId == 230 || interfaceId == 232 || interfaceId == 234);
        if (options) {
            continueStages();
            this.optionClicked = button;
        } else if (!options) {
            if (button >= 3 && button <= 6) {// starts at 3, ends at 6
                continueStages();
            }
        }
        sendDialogue();
        // this.optionClicked = -1;
    }

    public void npc(int npc, String... text) {
        if (text.length > 4 || text.length < 1) {
            return;
        }
        int interfaceId = 240 + text.length;
        if (interfaceId <= 240) {
            interfaceId = 241;
        }
        NPCDefinition definition = NPCDefinition.forId(npc);
        if (definition == null) {
            return;
        }
        String name = definition.getName();
        player.getFrames().sendNPCHead(npc, interfaceId, 0);
        player.getFrames().modifyText(name, interfaceId, 1);
        for (int i = 0; i < text.length; i++) {
            player.getFrames().modifyText(text[i], interfaceId, 2 + i);
        }
        player.getFrames().animateInterface(expression, interfaceId, 0);
        player.getFrames().sendChatboxInterface(interfaceId);
    }

    public void npc(String... text) {
        if (text.length > 4 || text.length < 1) {
            return;
        }
        int interfaceId = 240 + text.length;
        if (interfaceId <= 240) {
            interfaceId = 241;
        }
        player.getFrames().sendNPCHead(npc.getId(), interfaceId, 0);
        player.getFrames().modifyText(npc.getDefinition().getName(), interfaceId, 1);
        for (int i = 0; i < text.length; i++) {
            player.getFrames().modifyText(text[i], interfaceId, 2 + i);
        }
        player.getFrames().animateInterface(expression, interfaceId, 0);
        player.getFrames().sendChatboxInterface(interfaceId);
    }

    public void player(String... text) {
        if (text.length > 4 || text.length < 1) {
            return;
        }
        int interfaceId = 63 + text.length;
        if (interfaceId <= 63) {
            interfaceId = 64;
        }
        player.getFrames().sendPlayerHead(interfaceId, 0);
        player.getFrames().modifyText(player.getDetails().getName(), interfaceId, 1);
        for (int i = 0; i < text.length; i++) {
            player.getFrames().modifyText(text[i], interfaceId, 2 + i);
        }
        if (player.getDetails().getClientMode() == 464) {
            expression = DialogueExpressions.Emotes.HAPPY.getId();
        }
        player.getFrames().animateInterface(expression, interfaceId, 0);
        player.getFrames().sendChatboxInterface(interfaceId);
    }

    public void options(String title, String... text) {
        if (text.length > 5 || text.length < 2) {
            return;
        }
        int interfaceId = 224 + (text.length * 2);
        player.getFrames().modifyText(title, interfaceId, 0);
        for (int i = 0; i < text.length; i++) {
            player.getFrames().modifyText(text[i], interfaceId, i + 1);
        }
        player.getFrames().sendChatboxInterface(interfaceId);
    }
}
