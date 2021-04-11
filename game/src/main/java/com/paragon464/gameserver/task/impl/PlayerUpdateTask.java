package com.paragon464.gameserver.task.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Appearance;
import com.paragon464.gameserver.model.entity.mob.masks.ChatMessage;
import com.paragon464.gameserver.model.entity.mob.masks.ForceMovement;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.data.ClientModes;
import com.paragon464.gameserver.model.item.EquipmentSlot;
import com.paragon464.gameserver.model.item.EquipmentType;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.net.Packet;
import com.paragon464.gameserver.net.PacketBuilder;
import com.paragon464.gameserver.util.ProtocolUtils;

import java.util.Iterator;

/**
 * A task which creates and sends the player update block.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class PlayerUpdateTask {

    private Player player;

    public PlayerUpdateTask(Player player) {
        this.player = player;
    }

    public void execute() {
        /*
         * If the map region changed send the new one. We do this immediately as
         * the client can begin loading it before the actual packet is received.
         */
        if (player.isMapRegionChanging()) {
            player.loadMapRegions();
        }

        /*
         * The update block packet holds update blocks and is send after the
         * main packet.
         */
        PacketBuilder updateBlock = new PacketBuilder();

        /*
         * The main packet is written in bits instead of bytes and holds
         * information about the local list, players to add and remove, movement
         * and which updates are required.
         */
        PacketBuilder packet = new PacketBuilder(90, Packet.Type.VARIABLE_SHORT);
        packet.startBitAccess();

        /*
         * Updates this player.
         */
        updateThisPlayerMovement(packet);
        updatePlayer(updateBlock, player, false);

        /*
         * Write the current size of the player list.
         */
        packet.putBits(8, player.getLocalPlayers().size());

        updateLocalPlayers(updateBlock, packet);

        addLocalPlayers(updateBlock, packet);

        /*
         * Check if the update block is not empty.
         */
        if (!updateBlock.isEmpty()) {
            /*
             * Write a magic id indicating an update block follows.
             */
            packet.putBits(11, 2047);
            packet.finishBitAccess();

            /*
             * Add the update block at the end of this packet.
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

    private void addLocalPlayers(PacketBuilder updateBlock, PacketBuilder packet) {
        for (Player otherPlayer : World.getSurroundingPlayers(player.getPosition())) {
            if (player.getLocalPlayers().size() >= 255) {
                break;
            }
            if (otherPlayer == player || player.getLocalPlayers().contains(otherPlayer)
                || !otherPlayer.getPosition().isVisibleFrom(player.getPosition())
                || !otherPlayer.isVisible()) {
                continue;
            }
            player.getLocalPlayers().add(otherPlayer);
            addNewPlayer(packet, otherPlayer);
            updatePlayer(updateBlock, otherPlayer, true);
        }
    }

    private void updateLocalPlayers(PacketBuilder updateBlock, PacketBuilder packet) {
        /*
         * Iterate through the local player list.
         */
        for (Iterator<Player> it$ = player.getLocalPlayers().iterator(); it$.hasNext(); ) {
            /*
             * Get the next player.
             */
            Player otherPlayer = it$.next();

            /*
             * If the player should still be in our list.
             */
            if (World.getWorld().containsPlayer(otherPlayer) != -1 && !otherPlayer.isTeleporting()
                && otherPlayer.getPosition().isVisibleFrom(player.getPosition())
                && otherPlayer.isVisible()) {
                /*
                 * Update the movement.
                 */
                updatePlayerMovement(packet, otherPlayer);

                /*
                 * Check if an update is required, and if so, send the update.
                 */
                if (otherPlayer.getUpdateFlags().isUpdateRequired()) {
                    updatePlayer(updateBlock, otherPlayer, false);
                }
            } else {
                /*
                 * Otherwise, remove the player from the list.
                 */
                it$.remove();

                /*
                 * Tell the client to remove the player from the list.
                 */
                packet.putBits(1, 1);
                packet.putBits(2, 3);
            }
        }
    }

    /**
     * Updates a non-this player's movement.
     *
     * @param packet      The packet.
     * @param otherPlayer The player.
     */
    public void updatePlayerMovement(PacketBuilder packet, Player otherPlayer) {
        /*
         * Check which type of movement took place.
         */
        if (otherPlayer.getSprites().getPrimarySprite() == -1) {
            /*
             * If no movement did, check if an update is required.
             */
            if (otherPlayer.getUpdateFlags().isUpdateRequired()) {
                /*
                 * Signify that an update happened.
                 */
                packet.putBits(1, 1);

                /*
                 * Signify that there was no movement.
                 */
                packet.putBits(2, 0);
            } else {
                /*
                 * Signify that nothing changed.
                 */
                packet.putBits(1, 0);
            }
        } else if (otherPlayer.getSprites().getSecondarySprite() == -1) {
            /*
             * The player moved but didn't run. Signify that an update is
             * required.
             */
            packet.putBits(1, 1);

            /*
             * Signify we moved one tile.
             */
            packet.putBits(2, 1);

            /*
             * Write the primary sprite (i.e. walk direction).
             */
            packet.putBits(3, otherPlayer.getSprites().getPrimarySprite());

            /*
             * Write a flag indicating if a block update happened.
             */
            packet.putBits(1, otherPlayer.getUpdateFlags().isUpdateRequired() ? 1 : 0);
        } else {
            /*
             * The player ran. Signify that an update happened.
             */
            packet.putBits(1, 1);

            /*
             * Signify that we moved two tiles.
             */
            packet.putBits(2, 2);

            /*
             * Write the primary sprite (i.e. walk direction).
             */
            packet.putBits(3, otherPlayer.getSprites().getPrimarySprite());

            /*
             * Write the secondary sprite (i.e. run direction).
             */
            packet.putBits(3, otherPlayer.getSprites().getSecondarySprite());

            /*
             * Write a flag indicating if a block update happened.
             */
            packet.putBits(1, otherPlayer.getUpdateFlags().isUpdateRequired() ? 1 : 0);
        }
    }

    /**
     * Adds a new player.
     *
     * @param packet      The packet.
     * @param otherPlayer The player.
     */
    public void addNewPlayer(PacketBuilder packet, Player otherPlayer) {
        /*
         * Write the player index.
         */
        packet.putBits(11, otherPlayer.getIndex());
        /*
         * Calculate the x and y offsets.
         */
        int yPos = otherPlayer.getPosition().getY() - player.getPosition().getY();
        int xPos = otherPlayer.getPosition().getX() - player.getPosition().getX();
        packet.putBits(5, yPos);
        packet.putBits(3, 6);// dir
        packet.putBits(1, 1);
        packet.putBits(1, 1);
        packet.putBits(5, xPos);
    }

    /**
     * Updates this player's movement.
     *
     * @param packet The packet.
     */
    private void updateThisPlayerMovement(PacketBuilder packet) {
        /*
         * Check if the player is teleporting.
         */
        if (player.isTeleporting() || player.isMapRegionChanging()) {
            packet.putBits(1, 1);
            packet.putBits(2, 3);
            packet.putBits(2, player.getPosition().getZ());
            packet.putBits(7, player.getPosition().getLocalX(player.getLastKnownRegion()));
            packet.putBits(1, (player.isTeleporting()) ? 1 : 0);
            packet.putBits(7, player.getPosition().getLocalY(player.getLastKnownRegion()));
            packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
        } else {
            /*
             * Otherwise, check if the player moved.
             */
            if (player.getSprites().getPrimarySprite() == -1) {
                /*
                 * The player didn't move. Check if an update is required.
                 */
                if (player.getUpdateFlags().isUpdateRequired()) {
                    /*
                     * Signifies an update is required.
                     */
                    packet.putBits(1, 1);

                    /*
                     * But signifies that we didn't move.
                     */
                    packet.putBits(2, 0);
                } else {
                    /*
                     * Signifies that nothing changed.
                     */
                    packet.putBits(1, 0);
                }
            } else {
                /*
                 * Check if the player was running.
                 */
                if (player.getSprites().getSecondarySprite() == -1) {
                    /*
                     * The player walked, an update is required.
                     */
                    packet.putBits(1, 1);

                    /*
                     * This indicates the player only walked.
                     */
                    packet.putBits(2, 1);

                    /*
                     * This is the player's walking direction.
                     */
                    packet.putBits(3, player.getSprites().getPrimarySprite());

                    /*
                     * This flag indicates an update block is appended.
                     */
                    packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
                } else {
                    /*
                     * The player ran, so an update is required.
                     */
                    packet.putBits(1, 1);

                    /*
                     * This indicates the player ran.
                     */
                    packet.putBits(2, 2);

                    /*
                     * This is the walking direction.
                     */
                    packet.putBits(3, player.getSprites().getPrimarySprite());

                    /*
                     * And this is the running direction.
                     */
                    packet.putBits(3, player.getSprites().getSecondarySprite());

                    /*
                     * And this flag indicates an update block is appended.
                     */
                    packet.putBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
                }
            }
        }
    }

    /**
     * Updates a player.
     *
     * @param packet          The packet.
     * @param otherPlayer     The other player.
     * @param forceAppearance The force appearance flag.
     */
    public void updatePlayer(PacketBuilder packet, Player otherPlayer, boolean forceAppearance) {
        if (!otherPlayer.getUpdateFlags().isUpdateRequired()
            && !forceAppearance) {
            return;
        }
        synchronized (otherPlayer) {
            if (otherPlayer.hasCachedUpdateBlock() && otherPlayer != player && !forceAppearance) {
                packet.put(otherPlayer.getCachedUpdateBlock().getPayload().flip());
                return;
            }
            /*
             * We have to construct and cache our own block.
             */
            PacketBuilder block = new PacketBuilder();

            /*
             * Calculate the bitmask.
             */
            int mask = 0;
            final UpdateFlags flags = otherPlayer.getUpdateFlags();
            if (flags.get(UpdateFlags.UpdateFlag.HIT)) {
                mask |= 0x100;
            }
            if (flags.get(UpdateFlags.UpdateFlag.GRAPHICS)) {
                mask |= 0x200;
            }
            if (flags.get(UpdateFlags.UpdateFlag.FACE_ENTITY)) {
                mask |= 0x8;
            }
            if (flags.get(UpdateFlags.UpdateFlag.FACE_COORDINATE)) {
                mask |= 0x4;
            }
            if (flags.get(UpdateFlags.UpdateFlag.FORCE_MOVEMENT)) {
                mask |= 0x400;
            }
            if (flags.get(UpdateFlags.UpdateFlag.FORCED_CHAT)) {
                mask |= 0x80;
            }
            if (flags.get(UpdateFlags.UpdateFlag.ANIMATION)) {
                mask |= 0x20;
            }
            if (flags.get(UpdateFlags.UpdateFlag.CHAT)) {
                mask |= 0x1;
            }
            if (flags.get(UpdateFlags.UpdateFlag.HIT_2)) {
                mask |= 0x2;
            }
            if (flags.get(UpdateFlags.UpdateFlag.APPEARANCE) || forceAppearance) {
                mask |= 0x40;
            }
            /*
             * Check if the bitmask would overflow a byte.
             */
            if (mask >= 0x100) {
                /*
                 * Write it as a short and indicate we have done so.
                 */
                mask |= 0x10;
                block.put((byte) (mask & 0xff));
                block.put((byte) (mask >> 8));
            } else {
                /*
                 * Write it as a byte.
                 */
                block.put((byte) (mask & 0xFF));
            }

            /*
             * Append the appropriate updates.
             */
            if (flags.get(UpdateFlags.UpdateFlag.HIT)) {
                appendHitUpdate(otherPlayer, block);
            }
            if (flags.get(UpdateFlags.UpdateFlag.GRAPHICS)) {
                appendGraphicsUpdate(block, otherPlayer);
            }
            if (flags.get(UpdateFlags.UpdateFlag.FACE_ENTITY)) {
                appendFaceEntityUpdate(otherPlayer, block);
            }
            if (flags.get(UpdateFlags.UpdateFlag.FACE_COORDINATE)) {
                appendFaceLocationUpdate(otherPlayer, block);
            }
            if (flags.get(UpdateFlags.UpdateFlag.FORCE_MOVEMENT)) {
                appendForceMovement(block, otherPlayer);
            }
            if (flags.get(UpdateFlags.UpdateFlag.FORCED_CHAT)) {
                appendForceTextUpdate(otherPlayer, block);
            }
            if (flags.get(UpdateFlags.UpdateFlag.ANIMATION)) {
                appendAnimationUpdate(block, otherPlayer);
            }
            if (flags.get(UpdateFlags.UpdateFlag.CHAT)) {
                appendChatUpdate(block, otherPlayer);
            }
            if (flags.get(UpdateFlags.UpdateFlag.HIT_2)) {
                appendHit2Update(otherPlayer, block);
            }
            if (flags.get(UpdateFlags.UpdateFlag.APPEARANCE) || forceAppearance) {
                appendPlayerAppearanceUpdate(block, otherPlayer, forceAppearance);
            }

            /*
             * Convert the block builder to a packet.
             */
            Packet blockPacket = block.toPacket();

            /*
             * Now it is over, cache the block if we can.
             */
            if (otherPlayer != player && !forceAppearance) {
                otherPlayer.setCachedUpdateBlock(blockPacket);
            }

            /*
             * And finally append the block at the end.
             */
            packet.put(blockPacket.getPayload());
        }
    }

    private void appendHit2Update(final Player player, final PacketBuilder updateBlock) {
        Hits.Hit secondary = player.getSecondaryHit();
        updateBlock.putByteA((byte) secondary.getDamage());
        updateBlock.putByteS((byte) secondary.getType().getType());
        updateBlock.putByteS((byte) player.getHp());
        updateBlock.putByteS((byte) player.getMaxHp());
    }

    private void appendHitUpdate(final Player player, final PacketBuilder updateBlock) {
        Hits.Hit primary = player.getPrimaryHit();
        updateBlock.putByteS((byte) primary.getDamage());
        updateBlock.putByteS((byte) primary.getType().getType());
        updateBlock.putByteA((byte) player.getHp());
        updateBlock.putByteA((byte) player.getMaxHp());
    }

    private void appendFaceEntityUpdate(Player otherPlayer, PacketBuilder updateBlock) {
        Mob interacting = otherPlayer.getInteractingMob();
        updateBlock.putLEShort(interacting == null ? -1 : interacting.getClientIndex());
    }

    private void appendForceTextUpdate(Player otherPlayer, PacketBuilder updateBlock) {
        updateBlock.putRS2String(otherPlayer.getForcedChat());
    }

    private void appendFaceLocationUpdate(Player otherPlayer, PacketBuilder updateBlock) {
        int x = otherPlayer.getFaceLocation().getX();
        int y = otherPlayer.getFaceLocation().getY();
        updateBlock.putLEShortA(2 * x + 1);
        updateBlock.putLEShortA(2 * y + 1);
    }

    /**
     * Appends an animation update.
     *
     * @param block       The update block.
     * @param otherPlayer The player.
     */
    private void appendAnimationUpdate(PacketBuilder block, Player otherPlayer) {
        Animation currentAnimation = otherPlayer.getCurrentAnimation();
        int animToSend = ClientModes.getFixedAnimations(otherPlayer, currentAnimation.getId());
        block.putInt(animToSend);
        block.putByteC((byte) otherPlayer.getCurrentAnimation().getDelay());
    }

    /**
     * Appends a graphics update.
     *
     * @param block       The update block.
     * @param otherPlayer The player.
     */
    private void appendGraphicsUpdate(PacketBuilder block, Player otherPlayer) {
        int gfxToSend = ClientModes.getFixedGfxIds(otherPlayer, otherPlayer.getCurrentGraphic().getId());
        block.putInt(gfxToSend);
        block.putInt2(otherPlayer.getCurrentGraphic().getDelay() | otherPlayer.getCurrentGraphic().getHeight() << 16);
    }

    /**
     * Appends a chat text update.
     *
     * @param packet      The packet.
     * @param otherPlayer The player.
     */
    private void appendChatUpdate(PacketBuilder packet, Player otherPlayer) {
        ChatMessage cm = otherPlayer.getCurrentChatMessage();
        packet.putLEShortA((cm.getColour() & 0xFF) << 8 | cm.getEffect() & 0xFF);
        packet.putByteA((byte) otherPlayer.getDetails().getRights());
        byte[] chatStr = new byte[256];
        chatStr[0] = (byte) cm.getText().length();
        int offset = 1 + ProtocolUtils.encryptPlayerChat(chatStr, 0, 1, cm.getText().length(),
            cm.getText().getBytes());
        packet.put((byte) offset);
        packet.putReverse(chatStr, 0, offset);
    }

    /**
     * Moves the player.
     *
     * @param packet
     * @param p
     */
    private void appendForceMovement(PacketBuilder packet, final Player p) {
        ForceMovement forceMovement = p.getVariables().getNextForceMovement();
        Position lastRegion = player.getLastKnownRegion();
        Position position = p.getPosition();
        int firstSpeed = (forceMovement.getFirstTickDelay());
        int secondSpeed = (forceMovement.getSecondTickDelay());
        int dir = forceMovement.getDirection();
        int firstX = forceMovement.getFirstTile().getX() - position.getX();
        int firstY = forceMovement.getFirstTile().getY() - position.getY();
        int secondX = forceMovement.getSecondTile().getX() - position.getX();
        int secondY = forceMovement.getSecondTile().getY() - position.getY();
        packet.putByteS((byte) (position.getLocalX(lastRegion) + firstX)); // first
        packet.putByteS((byte) (position.getLocalY(lastRegion) + firstY)); // first
        packet.putByteS((byte) (position.getLocalX(lastRegion) + secondX)); // second
        packet.putByteA((byte) (position.getLocalY(lastRegion) + secondY)); // second
        packet.putShortA(firstSpeed); // speed going to
        packet.putShort(secondSpeed); // speed returning to
        packet.putByteC((byte) dir); // direction
    }

    /**
     * Appends an appearance update.
     *
     * @param packet      The packet.
     * @param otherPlayer The player.
     */
    private void appendPlayerAppearanceUpdate(PacketBuilder packet, Player otherPlayer, boolean forceAppearance) {
        PacketBuilder playerProps = new PacketBuilder();
        Appearance app = otherPlayer.getAppearance();
        int bitPacked = app.getGender();
        if (app.isNpc()) {
            bitPacked |= (otherPlayer.getSize() - 1) << 3;
        }
        playerProps.put((byte) bitPacked);
        playerProps.put((byte) otherPlayer.getPrayers().getPkIcon());
        playerProps.put((byte) otherPlayer.getPrayers().getHeadIcon());
        if (!app.isInvisible()) {
            if (!app.isNpc()) {
                for (int i = 0; i < 4; i++) {
                    int id = otherPlayer.getEquipment().getItemInSlot(i);
                    if (id != -1) {
                        playerProps.putInt(512 + id);
                    } else {
                        playerProps.putInt(0);
                    }
                }
                Item chest = otherPlayer.getEquipment().get(EquipmentSlot.TORSO.getSlotId());
                if (chest != null) {
                    playerProps.putInt(512 + chest.getId());
                } else {
                    playerProps.putInt(0x100 + app.getLook(2));
                }
                int shieldId = otherPlayer.getEquipment().getItemInSlot(EquipmentSlot.OFF_HAND.getSlotId());
                if (shieldId != -1) {
                    playerProps.putInt(512 + shieldId);
                } else {
                    playerProps.putInt(0);
                }
                if (chest != null && chest.getDefinition().equipmentDefinition != null) {
                    if (!chest.getDefinition().equipmentDefinition.matchesEquipmentType(EquipmentType.FULL_BODY)) {
                        playerProps.putInt(0x100 + app.getLook(3));
                    } else {
                        playerProps.putInt(0);
                    }
                } else {
                    playerProps.putInt(0x100 + app.getLook(3));
                }
                int slot_legs = otherPlayer.getEquipment().getItemInSlot(EquipmentSlot.LEGS.getSlotId());
                if (slot_legs != -1) {
                    playerProps.putInt(512 + slot_legs);
                } else {
                    playerProps.putInt(0x100 + app.getLook(5));
                }
                Item slot_helmet = otherPlayer.getEquipment().get(EquipmentSlot.HEAD.getSlotId());
                if (slot_helmet != null && slot_helmet.getDefinition().equipmentDefinition != null) {
                    if (!slot_helmet.getDefinition().equipmentDefinition.matchesEquipmentType(EquipmentType.MED_HELM) && !slot_helmet.getDefinition().equipmentDefinition.matchesEquipmentType(EquipmentType.MASK)) {
                        playerProps.putInt(0x100 + app.getLook(0));//shows char head
                    } else {
                        playerProps.putInt(0);//shows item
                    }
                } else {
                    playerProps.putInt(0x100 + app.getLook(0));
                }
                int slot_hands = otherPlayer.getEquipment().getItemInSlot(EquipmentSlot.HANDS.getSlotId());
                if (slot_hands != -1) {
                    playerProps.putInt(512 + slot_hands);
                } else {
                    playerProps.putInt(0x100 + app.getLook(4));
                }
                int slot_feet = otherPlayer.getEquipment().getItemInSlot(EquipmentSlot.FEET.getSlotId());
                if (slot_feet != -1) {
                    playerProps.putInt(512 + slot_feet);
                } else {
                    playerProps.putInt(0x100 + app.getLook(6));
                }
                if (slot_helmet != null && slot_helmet.getDefinition().equipmentDefinition != null) {
                    if (!slot_helmet.getDefinition().equipmentDefinition.matchesEquipmentType(EquipmentType.FULL_MASK) && !slot_helmet.getDefinition().equipmentDefinition.matchesEquipmentType(EquipmentType.MASK)) {
                        playerProps.putInt(0x100 + app.getLook(1));
                    } else {
                        playerProps.putInt(0);
                    }
                } else {
                    playerProps.putInt(0x100 + app.getLook(1));
                }
            } else {
                playerProps.putInt(-1);
                playerProps.putInt(app.getNpcId());
            }
        } else {
            for (int i = 0; i < 12; i++) {
                playerProps.put((byte) 0);
            }
        }
        for (int colour : app.getColoursArray()) {
            playerProps.put((byte) colour);
        }
        int animToSend = ClientModes.getFixedAnimations(player, otherPlayer.getVariables().getStandAnimation());
        playerProps.putInt(animToSend);// stand
        animToSend = ClientModes.getFixedAnimations(player, otherPlayer.getVariables().getTurnAnimation());
        playerProps.putInt(animToSend);// stand turn
        animToSend = ClientModes.getFixedAnimations(player, otherPlayer.getVariables().getWalkAnimation());
        playerProps.putInt(animToSend);// walk
        animToSend = ClientModes.getFixedAnimations(player, otherPlayer.getVariables().getTurn180Animation());
        playerProps.putInt(animToSend);// turn 180
        animToSend = ClientModes.getFixedAnimations(player, otherPlayer.getVariables().getTurn90Clockwise());
        playerProps.putInt(animToSend);// turn 90 cw
        animToSend = ClientModes.getFixedAnimations(player, otherPlayer.getVariables().getTurn90CounterClockwise());
        playerProps.putInt(animToSend);// turn 90 ccw
        animToSend = ClientModes.getFixedAnimations(player, otherPlayer.getVariables().getRunAnimation());
        playerProps.putInt(animToSend);// run
        playerProps.putRS2String(otherPlayer.getDetails().getName());
        playerProps.put((byte) otherPlayer.getSkills().getCombatLevel());                                        // level
        playerProps.putShort(0);
        Packet propsPacket = playerProps.toPacket();
        packet.putByteA((byte) propsPacket.getLength());
        packet.put(propsPacket.getPayload());
    }
}
