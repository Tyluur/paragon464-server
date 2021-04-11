package com.paragon464.gameserver.model.entity.mob.player.controller;

import java.util.HashMap;

import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.util.Logger;

public class ControllerHandler {

	private static final HashMap<Object, Class<Controller>> handledControlers = new HashMap<Object, Class<Controller>>();

	public static final void init() {
		try {
			Class<Controller> value1 = (Class<Controller>) Class
					.forName(BattleController.class.getCanonicalName());
			handledControlers.put("BattleController", value1);
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}
	
	public static final void reload() {
		handledControlers.clear();
		init();
	}

	public static final Controller getController(Object key) {
		if (key instanceof Controller)
			return (Controller) key;
		Class<Controller> classC = handledControlers.get(key);
		if (classC == null)
			return null;
		try {
			return classC.newInstance();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}
}
