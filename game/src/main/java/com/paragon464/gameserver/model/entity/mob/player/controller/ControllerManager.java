package com.paragon464.gameserver.model.entity.mob.player.controller;

import com.paragon464.gameserver.model.content.Foods.Food;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;

public final class ControllerManager {

	private Player player;
	private Controller controller;
	private boolean inited;
	
	private Object[] lastControllerArguments;
	private String lastController;
	
	public ControllerManager() {
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	public Controller getController() {
		return controller;
	}
	
	public void startController(Object key, Object... parameters) {
		if (controller != null)
			forceStop();
		controller = (Controller) (key instanceof Controller ? key : ControllerHandler.getController(key));
		if (controller == null)
			return;
		lastControllerArguments = parameters;
		if (key instanceof String) {
			lastController = (String) key.toString();
		} else {
			lastController = key.toString();
		}
		controller.setPlayer(player);
		controller.start();
		inited = true;
	}
	
	public boolean continueCombating(Mob other) {
		if (controller == null || !inited)
			return true;
		return controller.continueCombating(other);
	}
	
	public boolean startAttack(Mob other) {
		if (controller == null || !inited)
			return true;
		return controller.startAttack(other);
	}
	
	public boolean processItemTeleport(Position pos) {
		if (controller == null || !inited)
			return true;
		return controller.processItemTeleport(pos);
	}
	
	public boolean processMagicTeleport(Position pos) {
		if (controller == null || !inited)
			return true;
		return controller.processMagicTeleport(pos);
	}
	
	public boolean processObjectTeleport(GameObject object, Position pos) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectTeleport(object, pos);
	}
	
	public boolean processObjectTeleport(GameObject object, int x, int y, int z) {
		if (controller == null || !inited)
			return true;
		return processObjectTeleport(object, new Position(x, y, z));
	}
	
	public boolean processButtons(int interfaceId, int button, int button2, int itemId) {
		if (controller == null || !inited)
			return true;
		return controller.processButtons(interfaceId, button, button2, itemId);
	}
	
	public boolean processDeath() {
		if (controller == null || !inited)
			return true;
		return controller.processDeath();
	}
	
	/*public boolean processMobDeath(Mob mob) {
		if (controller == null || !inited)
			return true;
		return controller.processMobDeath(mob);
	}*/
	
	public void login() {
		if (lastController == null)
			return;
		controller = ControllerHandler.getController(lastController);
		if (controller == null) {
			forceStop();
			return;
		}
		if (controller.login())
			forceStop();
		else
			inited = true;
	}

	public void logout() {
		if (controller == null)
			return;
		if (controller.logout())
			forceStop();
	}
	
	public boolean processItemEquip(int itemId, int slotId) {
		if (controller == null || !inited)
			return true;
		return controller.processItemEquip(itemId, slotId);
	}
	
	public boolean processItemOnItem(Item itemUsed, Item usedWith) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnItem(itemUsed, usedWith);
	}
	
	public boolean processPlayerInteract(Player target, int option) {
		if (controller == null || !inited)
			return true;
		return controller.processPlayerInteract(target, option);
	}
	
	public void moved() {
		if (controller == null || !inited)
			return;
		controller.moved();
	}
	
	public void process() {
		if (controller == null || !inited)
			return;
		controller.process();
	}
	
	public boolean processFood(Food food) {
		if (controller == null || !inited)
			return true;
		return controller.processFood(food);
	}

	public boolean processPotion(Item pot) {
		if (controller == null || !inited)
			return true;
		return controller.processPotion(pot);
	}
	
	public boolean processObjectInteract(GameObject object, int option) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectInteract(object, option);
	}
	
	public boolean processNPCInteract(NPC npc, int option) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCInteract(npc, option);
	}
	
	public boolean summonFamiliar() {
		if (controller == null || !inited)
			return true;
		return controller.summonFamiliar();
	}
	
	public boolean processItemOnNPC(NPC npc, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnNPC(npc, item);
	}
	
	public boolean processItemOnPlayer(Player player, int itemId) {
		if (controller == null | !inited)
			return true;
		return controller.processItemOnPlayer(player, itemId);
	}
	
    public boolean processItemOnObject(GameObject object, Item item) {
		if (controller == null || !inited)
		    return true;
		return controller.processItemOnObject(object, item);
    }
    
	public boolean processDropItem(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processDropItem(item);
	}
	
	public void forceStop() {
		if (controller != null) {
			controller.forceClose();
			controller = null;
		}
		lastControllerArguments = null;
		lastController = null;
		inited = false;
	}

	public void removeControllerWithoutCheck() {
		controller = null;
		lastControllerArguments = null;
		lastController = null;
		inited = false;
	}

	public Object[] getLastControllerArguments() {
		return lastControllerArguments;
	}

	public void setLastControllerArguments(Object[] lastControllerArguments) {
		this.lastControllerArguments = lastControllerArguments;
	}
	
	public boolean isMinigameOrMiniquest() {
		if (controller == null || !inited)
		    return false;
		if (controller instanceof BattleController)
			return true;
		return false;
	}
}
