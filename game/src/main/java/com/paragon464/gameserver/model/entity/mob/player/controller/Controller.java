package com.paragon464.gameserver.model.entity.mob.player.controller;

import com.paragon464.gameserver.model.content.Foods.Food;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;

public abstract class Controller {

	protected Player player;
	
	public void setPlayer(Player p) {
		this.player = p;
	}
	
	
	public boolean continueCombating(Mob other) {
		return true;
	}
	
	public boolean startAttack(Mob other) {
		return true;
	}
	
	public boolean processItemTeleport(Position pos) {
		return true;
	}
	
	public boolean processMagicTeleport(Position pos) {
		return true;
	}
	
	public boolean processObjectTeleport(GameObject object, Position pos) {
		return true;
	}
	
	public boolean processButtons(int interfaceId, int button, int button2, int itemId) {
		return true;
	}
	
	public boolean processDeath() {
		return true;
	}
	
	public boolean processMobDeath(Mob mob) {
		return true;
	}
	
	public boolean processItemOnNPC(NPC npc, Item item) {
		return true;
	}
	
	//TODO
	public boolean processItemOnPlayer(Player other, int item) {
		return true;
	}
	
	public boolean processItemOnObject(GameObject object, Item item) {
		return true;
	}
	
	public boolean processItemOnItem(Item itemUsed, Item usedWith) {
		return true;
	}
	
	public boolean processDropItem(Item item) {
		return true;
	}
	
	public boolean summonFamiliar() {
		return true;
	}
	
	public boolean processNPCInteract(NPC npc, int option) {
		return true;
	}
	
	public boolean processObjectInteract(GameObject object, int option) {
		return true;
	}
	
	public boolean processPlayerInteract(Player other, int option) {
		return true;
	}
	
	public boolean processFood(Food food) {
		return true;
	}

	public boolean processPotion(Item item) {
		return true;
	}
	
	public boolean processItemEquip(int itemId, int slotId) {
		return true;
	}

	public boolean login() {
		return true;
	}
	public boolean logout() {
		return true;
	}
	public void process() {}
	public void moved() {}
	
	public final Object[] getArguments() {
		return player.getControllerManager().getLastControllerArguments();
	}

	public final void setArguments(Object[] objects) {
		player.getControllerManager().setLastControllerArguments(objects);
	}

	public final void removeController() {
		player.getControllerManager().removeControllerWithoutCheck();
	}

	public abstract void start();
	
	public void forceClose() {
	}
}
