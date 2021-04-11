package com.paragon464.gameserver.model.content.miniquests;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.dialogue.impl.BrainRobberyDialogue;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.controller.Controller;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.MapBuilder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.SizedPosition;
import com.paragon464.gameserver.tickable.Tickable;

public class BattleController extends Controller {

	protected SizedPosition baseLocation = null;
	protected int endDistance = 20;
	protected NPC npc;
    
    public BattleController(NPC npc) {
    	this.npc = npc;
    	if (npc.getId() >= 907 && npc.getId() <= 911) {//magearena
    		Position north_east = new Position(3108, 3946, 0);
            Position south_west = new Position(3090, 3921, 0);
            int width = (north_east.getX() - south_west.getX()) / 4;
            int height = (north_east.getY() - south_west.getY()) / 2;
            int[] newCoords = MapBuilder.findEmptyChunkBound(width, height);
            MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1],
                width, height);
            this.baseLocation = new SizedPosition(newCoords[0] << 3, newCoords[1] << 3, 0, width, height);
    	} else if (npc.getId() >= 3491 && npc.getId() <= 3497) {//rfd
    		Position north_east = new Position(1910, 5366, 2);
            Position south_west = new Position(1889, 5345, 2);
            final int width = (north_east.getX() - south_west.getX()) / 4;
            final int height = (north_east.getY() - south_west.getY()) / 4;
            final int[] newCoords = MapBuilder.findEmptyChunkBound(width, height);
            MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1],
                width, height);
            this.baseLocation = new SizedPosition(newCoords[0] << 3, newCoords[1] << 3, 2, width, height);
    	} else if (npc.getId() >= 5902 && npc.getId() <= 5905) {//lunars
    		Position north_east = new Position(1843, 5169, 2);
            Position south_west = new Position(1805, 5141, 2);
            int width = (north_east.getX() - south_west.getX()) / 8 + 3;
            int height = (north_east.getY() - south_west.getY()) / 8 + 2;
            int[] newCoords = MapBuilder.findEmptyChunkBound(width, height);
            MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1],
                width, height);
            this.baseLocation = new SizedPosition(newCoords[0] << 3, newCoords[1] << 3, 2, width, height);
    	}
    	Position north_east = null, south_west = null;
    	int width = 0, height = 0;
    	int[] newCoords = null;
    	switch (npc.getId()) {
    	case 1813://mountainbear
    		north_east = new Position(2793, 10088, 0);
            south_west = new Position(2772, 10060, 0);
            width = (north_east.getX() - south_west.getX()) / 8 + 3;
            height = (north_east.getY() - south_west.getY()) / 8 + 2;
            newCoords = MapBuilder.findEmptyChunkBound(width, height);
            MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1],
                width, height);
            baseLocation = new SizedPosition(newCoords[0] << 3, newCoords[1] << 3, 0, width, height);
            break;
    	case 5666://barrelchest
			north_east = new Position(3820, 2853, 0);
			south_west = new Position(3805, 2833, 0);
			width = (north_east.getX() - south_west.getX()) / 4;
			height = (north_east.getY() - south_west.getY()) / 6;
			newCoords = MapBuilder.findEmptyChunkBound(width, height);
			MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1], width,
					height);
			this.baseLocation = new SizedPosition(newCoords[0] << 3, newCoords[1] << 3, 0, width, height);
    		break;
    	}
    }
    
    @Override
    public void process() {
		if (player.isDestroyed()) {
			end(false);
			return;
		}
		if (!npc.isDestroyed()) {
			if (!TileControl.isWithinRadius(player, npc, endDistance)) {
				end(false);
				return;
			}
		}
    }
    
	@Override
	public void start() {
		// TODO Auto-generated method stub
		player.teleport(playerStart());
        World.getWorld().submit(new Tickable(1) {
            @Override
            public void execute() {
                this.stop();
                spawn();
            }
        });
	}

	@Override
	public boolean processDeath() {
		end(false);
    	return false;
	}
	
	@Override
	public boolean processMobDeath(Mob mob) {
		end(false);
		if (mob.isNPC()) {
			NPC npc = (NPC) mob;
			switch (npc.getId()) {
			case 3497://dagg mother
				player.getAttributes().set("hfd_stage", 3);
				break;
			case 1913://kamil
				if (!player.getInventory().addItem(4671)) {
	                GroundItemManager.registerGroundItem(new GroundItem(new Item(4671, 1), player));
	            } else {
	                player.getInventory().refresh();
	            }
				break;
			case 1977://Fareed
				if (!player.getInventory().addItem(4672)) {
	                GroundItemManager.registerGroundItem(new GroundItem(new Item(4672, 1), player));
	            } else {
	                player.getInventory().refresh();
	            }
				break;
			case 1914://dessous
				if (!player.getInventory().addItem(4670)) {
	                GroundItemManager.registerGroundItem(new GroundItem(new Item(4670, 1), player));
	            } else {
	                player.getInventory().refresh();
	            }
				break;
			case 1975://damis
				if (!player.getInventory().addItem(4673)) {
	                GroundItemManager.registerGroundItem(new GroundItem(new Item(4673, 1), player));
	            } else {
	                player.getInventory().refresh();
	            }
				break;
			case 1813://mountain bear
				if (player.getInventory().addItem(4488)) {
		            player.getInventory().refresh();
		        } else {
		            GroundItemManager.registerGroundItem(new GroundItem(new Item(4488, 1), player));
		        }
				break;
			case 5666://barrelchest
				if (player.getInventory().freeSlots() >= 2) {
		            player.getInventory().addItem(10888);
		            player.getInventory().addItem(4199);
		            player.getAttributes().set("brain_robbery_stage", 3);
		            player.getAttributes().set("dialogue_session", new BrainRobberyDialogue(null, player, 37));
		        } else {
		            GroundItemManager.registerGroundItem(new GroundItem(new Item(10888), player));
		            GroundItemManager.registerGroundItem(new GroundItem(new Item(4199), player));
		        }
				break;
			}
		}
		return false;
	}
	
	@Override
	public boolean logout() {
		end(true);
		return true;
	}
	
	@Override
	public boolean processButtons(int interfaceId, int button, int button2, int itemId) {
		if (interfaceId == 271 || interfaceId == 597 || (interfaceId == 548 && button == 145)) {
			player.getFrames().sendMessage("You can't use prayers in here.");
			player.getPrayers().deactivateAllPrayers();
			return false;
		}
		return true;
	}
	
	public void end(boolean forceStop) {
		World.getWorld().unregister(npc);
        player.getFrames().sendHintArrow(null);
        player.teleport(playerFinish());
        player.resetVariables();
        player.playAnimation(-1, AnimationPriority.HIGH);
        if (baseLocation != null) {
            World.getWorld().submit(new Tickable(1) {
                @Override
                public void execute() {
                    MapBuilder.destroyMap(baseLocation.getZoneX(), baseLocation.getZoneY(), baseLocation.getWidth(),
                        baseLocation.getHeight());
                    this.stop();
                }
            });
        }
        if (forceStop) {
        	player.getControllerManager().forceStop();
        }
	}
	
	public void spawn() {
		if (npc.getId() == 3497) {//dagg mother
			npc.setTransformationId(3497);
		}
		npc.getAttributes().set("force_aggressive", true);
        npc.setPosition(npcStart());
        npc.setLastKnownRegion(npcStart());
        World.getWorld().addNPC(npc);
        player.getFrames().sendHintArrow(npc);
        CombatAction.beginCombat(npc, player);
    }
	
	private Position playerFinish() {
		if (npc.getId() >= 907 && npc.getId() <= 911) {//magearena
			return new Position(2539, 4719, 0);
		}
		if (npc.getId() >= 5902 && npc.getId() <= 5905) {
			return new Position(2341, 3680, 0);
		}
		if (npc.getId() >= 3491 && npc.getId() <= 3497) {//rfd
			return new Position(3207, 3216, 0);
		}
		switch (npc.getId()) {
		case 3497://dagg mother
			return new Position(2515, 4625, 1);
		case 1913://kamil
			return new Position(2341, 3680, 0);
		case 1977://fareed
			return new Position(2341, 3680, 0);
		case 1914://dessous
			return new Position(2341, 3680, 0);
		case 1974://damis
		case 1975://damis
			return new Position(2341, 3680, 0);
		case 1813://mountainbear
			return new Position(2808, 3704, 0);
		case 5666://barrelchest
			return new Position(3804, 2844, 0);
		}
		return null;
	}
	private Position playerStart() {
		if (npc.getId() >= 907 && npc.getId() <= 911) {//magearena
			return new Position(this.baseLocation.getX() + 11, this.baseLocation.getY() + 14, 0);
		}
		if (npc.getId() >= 5902 && npc.getId() <= 5905) {
			return new Position(this.baseLocation.getX() + 17, this.baseLocation.getY() + 4, 2);
		}
		if (npc.getId() >= 3491 && npc.getId() <= 3497) {//rfd
			return new Position(this.baseLocation.getX() + 9, this.baseLocation.getY() + 14, 2);
		}
		switch (npc.getId()) {
		case 3497://dagg mother
			return new Position(2515, 4632, 0);
		case 1913://kamil
			return new Position(2848, 3809, 2);
		case 1977://fareed
			return new Position(3307, 9376, 0);
		case 1914://dessous
			return new Position(3569, 3409, 0);
		case 1974://damis
		case 1975://damis
			return new Position(2739, 5075, 0);
		case 1813://mountainbear
			return new Position(baseLocation.getX() + 20, baseLocation.getY() + 20, 0);
		case 5666://barrelchest
			return new Position(this.baseLocation.getX() + 7, this.baseLocation.getY() + 12, 0);
		}
		return null;
	}
	
	private Position npcStart() {
		if (npc.getId() >= 907 && npc.getId() <= 911) {//magearena
			return new Position(player.getPosition().getX() + 7, player.getPosition().getY(), 0);
		}
		if (npc.getId() >= 5902 && npc.getId() <= 5905) {
			return new Position(player.getPosition().getX() - 1, player.getPosition().getY() + 12, 2);
		}
		if (npc.getId() >= 3491 && npc.getId() <= 3497) {//rfd
			return new Position(player.getPosition().getX() - 1, player.getPosition().getY() - 8, 2);
		}
		switch (npc.getId()) {
		case 3497://dagg mother
			return new Position(2517, 4648, 0);
		case 1913://kamil
			return new Position(2834, 3810, 2);
		case 1977://fareed
			return new Position(3315, 9376, 0);
		case 1914://dessous
			return new Position(3569, 3405, 0);
		case 1974://damis
		case 1975://damis
			return new Position(2739, 5086, 0);
		case 1813://mountainbear
			return new Position(player.getPosition().getX() - 2, player.getPosition().getY() + 10, 0);
		case 5666://barrelchest
			return new Position(player.getPosition().getX() + 7, player.getPosition().getY(), 0);
		}
		return null;
	}
}
