package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.quests.QuestTab;

public class TutorialDialogue extends DialogueHandler {

    private int npc = 945;

    public TutorialDialogue(Player player, boolean send) {
        super(player, send);
    }

    @Override
    public void sendDialogue() {
        int npc = 945;
        switch (this.stage) {
            case 0:
                this.npc(npc, "Hello " + player.getDetails().getName() + ", Welcome to " + Config.SERVER_NAME + ".");
                break;
            case 1:
                this.npc(npc, "Would you like a quick run-through of " + Config.SERVER_NAME + "?");
                break;
            case 2:
                this.options("Choose An Option", "Yes please.", "No thanks.");
                break;
            case 3:
                if (this.optionClicked == 1) {
                    this.player("Yes please.");
                } else if (this.optionClicked == 2) {
                    this.player("No thanks.");
                    this.stage = 1000;
                }
                break;
            case 4:
                this.npc(npc, "Okay. Let's start off with our game tabs.");
                break;
            case 5:
                this.npc(npc, "The Game Information will show you specific details", "about RuneNova & your personal account. Clicking the", "green tab in the top right-hand will open available quests", "in RuneNova.");
                player.getFrames().forceSendTab(2);
                player.getFrames().sendTab(player.getSettings().isInResizable() ? 67 : 88, 274);
                break;
            case 6:
                this.npc(npc, "There are currently 5 available Quests in RuneNova.");
                player.getAttributes().set("quest_tab_viewing", true);
                QuestTab.sendQuests(player);
                player.getFrames().sendTab(player.getSettings().isInResizable() ? 67 : 88, 610);
                break;
            case 7:
                this.npc(npc, "Here we have the options tab. Available here is", "options for all client-related features. Check em out!");
                player.getFrames().forceSendTab(11);
                player.getFrames().sendTab(player.getSettings().isInResizable() ? 76 : 97, 261);
                break;
            case 8:
                this.npc(npc, "Now let's get into the game.");
                player.getFrames().forceSendTab(3);
                player.getFrames().sendTab(player.getSettings().isInResizable() ? 68 : 89, 149);
                break;
            case 9:
                this.npc(npc, "This is RuneNova's Mainland. It can be re-visited via", "the Home Teleport spell in your Magic book.");
                player.getFrames().forceSendTab(6);
                player.getFrames().sendTab(player.getSettings().isInResizable() ? 71 : 92, player.getSettings().getMagicType() == 1 ? 192 : player.getSettings().getMagicType() == 2 ? 193 : 430);
                break;
            case 10:
                this.npc(npc, "Just south of here is where Shopping is done.");
                break;
            case 11:
                this.npc(npc, "Each NPC has their own unique styled shop.", "You may sell any items you'd like to the Shopkeeper.");
                player.teleport(2329, 3669, 0);
                break;
            case 12:
                this.npc(npc, "Just West of here, you have your MISC NPCs.");
                break;
            case 13:
                this.npc(npc, "In here we have the Make-over mage, which you will", "be able to design your characters looks. Vannaka is your", "go-to Slayer Master, and Combat master who will sell,", "trim skillcapes.");
                player.teleport(2318, 3669, 0);
                break;
            case 14:
                this.npc(npc, "North east of here, you have the bank area.");
                break;
            case 15:
                this.npc(npc, "You can set bank-pins with the 1st click option on", "the bank booth. The 2nd option will open your bank.");
                player.teleport(2329, 3689, 0);
                break;
            case 16:
                this.npc(npc, "Here we have the Sailor of the Mainland.", "Talking to him will bring a screen of all locations you're", "able to travel to in RuneNova.");
                player.teleport(2317, 3687, 0);
                break;
            case 17:
                this.npc(npc, "This ladder here has a list of dungeons available", "all around RuneNova.");
                player.teleport(2335, 3664, 0);
                break;
            case 18:
                this.npc(npc, "I've showed you around. Now it's time to get", "out there and conquer RuneNova!");
                break;
            default:
                player.getVariables().getTutorial().end();
                end();
                break;
        }
    }
}
