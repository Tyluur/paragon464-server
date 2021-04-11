package com.paragon464.gameserver.task.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.data.ClientModes;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.net.Packet;
import com.paragon464.gameserver.net.PacketBuilder;

import java.util.Iterator;

/**
 * A task which creates and sends the NPC update block.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class NPCUpdateTask {

    private Player player;

    public NPCUpdateTask(Player player) {
        this.player = player;
    }

    public void execute() {
        /*
         * The update block holds the update masks and data, and is written
         * after the main block.
         */
        PacketBuilder updateBlock = new PacketBuilder();

        /*
         * The main packet holds information about adding, moving and removing
         * NPCs.
         */
        PacketBuilder packet = new PacketBuilder(174, Packet.Type.VARIABLE_SHORT);
        packet.startBitAccess();

        /*
         * Write the current size of the npc list.
         */
        packet.putBits(8, player.getLocalNPCs().size());

        /*
         * Iterate through the local npc list.
         */
        for (Iterator<NPC> it$ = player.getLocalNPCs().iterator(); it$.hasNext(); ) {
            /*
             * Get the next NPC.
             */
            NPC npc = it$.next();
            /*
             * If the NPC should still be in our list.
             */
            if (World.getWorld().containsNPC(npc) != -1 && !npc.isTeleporting()
                && npc.getPosition().isVisibleFrom(player.getPosition()) && npc.isVisible()) {
                /*
                 * Update the movement.
                 */
                updateNPCMovement(packet, npc);

                /*
                 * Check if an update is required, and if so, send the update.
                 */
                if (npc.getUpdateFlags().isUpdateRequired()) {
                    updateNPC(updateBlock, npc);
                }
            } else {
                /*
                 * Otherwise, remove the NPC from the list.
                 */
                it$.remove();

                /*
                 * Tell the client to remove the NPC from the list.
                 */
                packet.putBits(1, 1);
                packet.putBits(2, 3);
            }
        }
        for (NPC npc : World.getSurroundingNPCS(player.getPosition())) {
            if (player.getLocalNPCs().size() >= 255) {
                break;
            }
            if (npc == null) continue;
            if (player.getLocalNPCs().contains(npc) || !npc.isVisible()) {
                continue;
            }
            player.getLocalNPCs().add(npc);
            addNewNPC(packet, npc);
            if (npc.getUpdateFlags().isUpdateRequired()) {
                updateNPC(updateBlock, npc);
            }
        }

        /*
         * Check if the update block isn't empty.
         */
        if (!updateBlock.isEmpty()) {
            /*
             * If so, put a flag indicating that an update block follows.
             */
            packet.putBits(15, 32767);
            packet.finishBitAccess();

            /*
             * And append the update block.
             */
            packet.put(updateBlock.toPacket().getPayload());
        } else {
            /*
             * Terminate the packet normally.
             */
            packet.finishBitAccess();
        }

        /*
         * Write the packet.
         */
        player.write(packet.toPacket());
    }

    /**
     * Update an NPC's movement.
     *
     * @param packet The main packet.
     * @param npc    The npc.
     */
    private void updateNPCMovement(PacketBuilder packet, NPC npc) {
        /*
         * Check if the NPC is running.
         */
        if (npc.getSprites().getSecondarySprite() == -1) {
            /*
             * They are not, so check if they are walking.
             */
            if (npc.getSprites().getPrimarySprite() == -1) {
                /*
                 * They are not walking, check if the NPC needs an update.
                 */
                if (npc.getUpdateFlags().isUpdateRequired()) {
                    /*
                     * Indicate an update is required.
                     */
                    packet.putBits(1, 1);

                    /*
                     * Indicate we didn't move.
                     */
                    packet.putBits(2, 0);
                } else {
                    /*
                     * Indicate no update or movement is required.
                     */
                    packet.putBits(1, 0);
                }
            } else {
                /*
                 * They are walking, so indicate an update is required.
                 */
                packet.putBits(1, 1);

                /*
                 * Indicate the NPC is walking 1 tile.
                 */
                packet.putBits(2, 1);

                /*
                 * And write the direction.
                 */
                packet.putBits(3, npc.getSprites().getPrimarySprite());

                /*
                 * And write the update flag.
                 */
                packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
            }
        } else {
            /*
             * They are running, so indicate an update is required.
             */
            packet.putBits(1, 1);

            /*
             * Indicate the NPC is running 2 tiles.
             */
            packet.putBits(2, 2);

            // packet.putBits(1, 1);

            /*
             * And write the directions.
             */
            packet.putBits(3, npc.getSprites().getPrimarySprite());
            packet.putBits(3, npc.getSprites().getSecondarySprite());

            /*
             * And write the update flag.
             */
            packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
        }
    }

    /**
     * Update an NPC.
     *
     * @param packet The update block.
     * @param npc    The npc.
     */
    private void updateNPC(PacketBuilder updateBlock, NPC npc) {
        /*
         * Calculate the mask.
         */
        int mask = 0;
        final UpdateFlags flags = npc.getUpdateFlags();
        if (flags.get(UpdateFlags.UpdateFlag.ANIMATION)) {
            mask |= 0x40;
        }
        if (flags.get(UpdateFlags.UpdateFlag.HIT)) {
            mask |= 0x10;
        }
        if (flags.get(UpdateFlags.UpdateFlag.GRAPHICS)) {
            mask |= 0x8;
        }
        if (flags.get(UpdateFlags.UpdateFlag.FACE_ENTITY)) {
            mask |= 0x4;
        }
        if (flags.get(UpdateFlags.UpdateFlag.FORCED_CHAT)) {
            mask |= 0x80;
        }
        if (flags.get(UpdateFlags.UpdateFlag.HIT_2)) {
            mask |= 0x20;
        }
        if (flags.get(UpdateFlags.UpdateFlag.TRANSFORM)) {
            mask |= 0x2;
        }
        if (flags.get(UpdateFlags.UpdateFlag.FACE_COORDINATE)) {
            mask |= 0x1;
        }
        // if(mask >= 0x100) {
        // mask |= 0x1;
        // updateBlock.put((byte) (mask & 0xFF));
        // updateBlock.put((byte) (mask >> 8));
        // } else {
        updateBlock.put((byte) (mask));
        // }
        if (flags.get(UpdateFlags.UpdateFlag.ANIMATION)) {
            appendAnimationUpdate(npc, updateBlock);
        }
        if (flags.get(UpdateFlags.UpdateFlag.HIT)) {
            appendHitUpdate(npc, updateBlock);
        }
        if (flags.get(UpdateFlags.UpdateFlag.GRAPHICS)) {
            appendGraphicsUpdate(npc, updateBlock);
        }
        if (flags.get(UpdateFlags.UpdateFlag.FACE_ENTITY)) {
            appendEntityFocusUdate(npc, updateBlock);
        }
        if (flags.get(UpdateFlags.UpdateFlag.FORCED_CHAT)) {
            appendForceTextUpdate(npc, updateBlock);
        }
        if (flags.get(UpdateFlags.UpdateFlag.HIT_2)) {
            appendHit2Update(npc, updateBlock);
        }
        if (flags.get(UpdateFlags.UpdateFlag.TRANSFORM)) {
            appendTransformationUpdate(npc, updateBlock);
        }
        if (flags.get(UpdateFlags.UpdateFlag.FACE_COORDINATE)) {
            appendFaceLocationUpdate(npc, updateBlock);
        }
    }

    /**
     * Adds a new NPC.
     *
     * @param packet The main packet.
     * @param npc    The npc to add.
     */
    private void addNewNPC(PacketBuilder packet, NPC npc) {
        /*
         * Write the NPC's index.
         */
        packet.putBits(15, npc.getIndex());
        /*
         * Calculate the x and y offsets.
         */
        int yPos = npc.getPosition().getY() - player.getPosition().getY();
        int xPos = npc.getPosition().getX() - player.getPosition().getX();
        /*
         * And write them.
         */
        packet.putBits(5, yPos);

        /*
         * We now write the NPC type id.
         */
        packet.putBits(17, npc.getId());

        /*
         * And indicate if an update is required.
         */
        packet.putBits(1, npc.getUpdateFlags().isUpdateRequired() ? 1 : 0);
        /*
         * TODO unsure, but probably discards the client-side walk queue.
         */
        packet.putBits(1, 0);

        packet.putBits(3, npc.getDirection());
        packet.putBits(5, xPos);
    }

    private void appendAnimationUpdate(NPC npc, PacketBuilder updateBlock) {
        Animation currentAnimation = npc.getCurrentAnimation();
        int sendId = ClientModes.getFixedAnimations(player, currentAnimation.getId());
        updateBlock.putInt(sendId);
        updateBlock.putByteA((byte) npc.getCurrentAnimation().getDelay());
    }

    private void appendHitUpdate(NPC npc, PacketBuilder updateBlock) {
        double max = npc.getSkills().getMaxHitpoints();
        double hp = npc.getHp();
        Hits.Hit hit = npc.getPrimaryHit();
        updateBlock.putByteS((byte) hit.getDamage());
        updateBlock.putByteA((byte) hit.getType().getType());
        updateBlock.putByteA((byte) hp);
        updateBlock.putByteS((byte) max);
    }

    private void appendGraphicsUpdate(NPC npc, PacketBuilder updateBlock) {
        int gfxToSend = ClientModes.getFixedGfxIds(player, npc.getCurrentGraphic().getId());
        updateBlock.putInt(gfxToSend);
        updateBlock.putInt1(npc.getCurrentGraphic().getDelay() | npc.getCurrentGraphic().getHeight() << 16);
    }

    private void appendEntityFocusUdate(NPC npc, PacketBuilder updateBlock) {
        Mob mob = npc.getInteractingMob();
        updateBlock.putShort(mob == null ? -1 : mob.getClientIndex());
    }

    private void appendForceTextUpdate(NPC npc, PacketBuilder updateBlock) {
        updateBlock.putRS2String(npc.getForcedChat());
    }

    private void appendHit2Update(NPC npc, PacketBuilder updateBlock) {
        double max = npc.getSkills().getMaxHitpoints();
        double hp = npc.getHp();
        Hits.Hit hit = npc.getSecondaryHit();
        updateBlock.put((byte) hit.getDamage());
        updateBlock.putByteA((byte) hit.getType().getType());
        updateBlock.put((byte) hp);
        updateBlock.put((byte) max);
    }

    private void appendTransformationUpdate(NPC npc, PacketBuilder updateBlock) {
        int transformationId = npc.getTransformationId();
        if (transformationId == -1) {
            transformationId = npc.getId();
        }
        updateBlock.putInt(transformationId);
    }

    private void appendFaceLocationUpdate(NPC npc, PacketBuilder updateBlock) {
        Position loc = npc.getFaceLocation();
        int x = loc.getX();
        int y = loc.getY();
        updateBlock.putShortA(x = 2 * x + 1);
        updateBlock.putShort(y = 2 * y + 1);
    }
}
