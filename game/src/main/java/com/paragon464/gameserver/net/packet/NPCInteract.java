package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.io.database.table.log.PacketTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCDefinition;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.combat.MagicAction;
import com.paragon464.gameserver.model.content.combat.data.MagicData;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.pathfinders.DumbPathFinder;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.net.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NPC clicking packets.
 *
 * @author Luke132
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Joker
 */
public class NPCInteract implements PacketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NPCInteract.class);

    private static final int ATTACK = 129;
    private static final int FIRST_CLICK = 156;
    private static final int SECOND_CLICK = 19;
    private static final int THIRD_CLICK = 33;
    private static final int FOURTH_CLICK = 51;
    private static final int MAGIC_ON_NPC = 69;
    private static final int ITEM_ON_NPC = 187;
    private static final int EXAMINE_NPC = 72;

    @Override
    public void handle(Player player, Packet packet) {
        if (packet.getOpcode() != EXAMINE_NPC) {
            player.resetActionAttributes();
        }
        if (!canExecute(player, packet) && (player.getAttributes().get("dialogue_session") == null || player.getVariables().getTutorial() != null)) {
            return;
        }
        switch (packet.getOpcode()) {
            case EXAMINE_NPC:
                handleExamineNPC(player, packet);
                break;
            default:
                handleActions(player, packet);
                break;
        }
    }

    private void handleExamineNPC(Player player, Packet packet) {
        int npcIndex = packet.getShort();
        NPCDefinition def = NPCDefinition.forId(npcIndex);
        if (def == null) {
            return;
        }

        String message = "It's an NPC.";
        if (def.getExamine() != null) {
            message = def.getExamine();
        }

        player.getFrames().sendMessage(message);
    }

    private void handleActions(Player player, Packet packet) {
        int npcIndex = -1;
        int type = -1;
        final var interactType = new StringBuilder("npcInteract[");
        switch (packet.getOpcode()) {
            case ATTACK:
                npcIndex = packet.getLEShort() & 0xFFFF;
                interactType.append("click-attack]");
                break;
            case MAGIC_ON_NPC:
                npcIndex = packet.getShortA();
                interactType.append("magic-attack]");
                break;
            case ITEM_ON_NPC:
                npcIndex = packet.getLEShort();
                interactType.append("item-on]");
                type = -1;
                break;
            case FIRST_CLICK:
                npcIndex = packet.getShortA() & 0xFFFF;
                interactType.append("click-1]");
                type = 1;
                break;
            case SECOND_CLICK:
                npcIndex = packet.getLEShortA() & 0xFFFF;
                interactType.append("click-2]");
                type = 2;
                break;
            case THIRD_CLICK:
                npcIndex = packet.getLEShortA();
                interactType.append("click-3]");
                type = 3;
                return;
            case FOURTH_CLICK:
                npcIndex = packet.getShortA() & 0xFFFF;
                interactType.append("click-4]");
                type = 4;
                break;
        }
        if (npcIndex < 0 || npcIndex > Config.NPC_LIMIT) {
            return;
        }
        final NPC npc = World.getWorld().getNPC(npcIndex);
        if (npc == null || npc.isDestroyed()) {
            return;
        }
        if (World.getRegion(player, npc.getPosition().getRegionId()) == null) {
            return;
        }
        player.setInteractingMob(npc);
        if (packet.getOpcode() == ATTACK) {
            player.getFollowing().setFollowing(npc, true);
            CombatAction.beginCombat(player, npc);
            LOGGER.debug("Attacking NPC: {}, spawned at: {}", npc.getId(), npc.getSpawnPosition());
        } else if (packet.getOpcode() == MAGIC_ON_NPC) {
            packet.getShort();
            int interfaceHash = packet.getInt2();
            int childId = interfaceHash & 0xFFFF;
            MagicAction.setNextSpell(player, childId, true);
            CombatAction.beginCombat(player, npc);
            String spellInfo = "Spell[serverIndex: " + MagicData.getSpellIndex(player, childId) + ", clientIndex: " + childId + ", interfaceId: " + (interfaceHash >> 16) + "]";
            PacketTable.save(player, interactType.append(": NPC").append(npc.logString()).append(", PLAYER").append(player.logString()).append(", ").append(spellInfo).append(".").toString());
        } else {
            if (packet.getOpcode() == ITEM_ON_NPC) {
                int interfaceBitPacked = packet.getLEInt();
                int slot = packet.getShort();
                int itemId = packet.getInt();
                int interfaceId = interfaceBitPacked >> 16;
                Item item = player.getInventory().getSlot(slot);
                if (item == null) {
                    return;
                }
                if (itemId != item.getId()) {
                    return;
                }
                if (!player.getInventory().hasItem(itemId)) {
                    return;
                }
                player.getAttributes().set("packet_item", item);
                String itemInfo = "ITEM" + item.toString() + ", slot: " + slot + ", interface[" + interfaceId + "]";
                PacketTable.save(player, interactType.append(": NPC").append(npc.logString()).append(", PLAYER").append(player.logString()).append(", ").append(itemInfo).append(", ").toString());
            } else {
                PacketTable.save(player, interactType.append(": NPC").append(npc.logString()).append(", PLAYER").append(player.logString()).append(".").toString());
            }
            int x = npc.getPosition().getX();
            int y = npc.getPosition().getY();
            if (player.getPosition().equals(npc.getPosition())) {
                DumbPathFinder.generateMovement(player);
                player.getAttributes().set("packet_interaction_type", type);
                player.getAttributes().set("npc_interact", true);
                player.getAttributes().set("packet_npc", npc);
                return;
            }
            PathState pathState = player.executeVariablePath(npc, -1, 0, 0, x, y);
            if (!pathState.isRouteFound()) {
                player.getFrames().sendMessage("I can't reach that!");
                player.getWalkingQueue().reset();
                player.resetActionAttributes();
                return;
            }
            player.getAttributes().set("packet_interaction_type", type);
            player.getAttributes().set("npc_interact", true);
            player.getAttributes().set("packet_npc", npc);
        }
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        if (player.getAttributes().isSet("stopActions")) {
            return false;
        }
        return !player.getCombatState().isDead();
    }
}
