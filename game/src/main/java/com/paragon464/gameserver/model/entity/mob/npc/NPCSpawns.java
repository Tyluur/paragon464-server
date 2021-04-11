package com.paragon464.gameserver.model.entity.mob.npc;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.pathfinders.Directions.NormalDirection;
import com.paragon464.gameserver.model.region.Position;

import java.util.ArrayList;
import java.util.List;

public class NPCSpawns {

    public static List<NPCSpawns> definitions = new ArrayList<>();
    public int id;
    public int x, y, z, radius;
    public NormalDirection direction;

    public static void load(NPC dead) {
        NPCSpawns spawn = dead.getSpawn();
        if (spawn == null)
            return;
        final NPC npc = new NPC(dead.getId());
        Position position = new Position(spawn.getX(), spawn.getY(), spawn.z);
        npc.setPosition(position);
        npc.setLastKnownRegion(position);
        npc.setSpawnPosition(position);
        npc.setRandomWalking(spawn.radius > 0);
        npc.setDirection(spawn.direction.intValue());
        npc.setSpawn(spawn);
        World.getWorld().addNPC(npc);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static NPCSpawns forId(int id) {
        NPCSpawns def = null;
        for (NPCSpawns defs : definitions) {
            if (defs.id == id) {
                def = defs;
            }
        }
        if (def == null) {
            def = new NPCSpawns();
        }
        return def;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public NormalDirection getDirection() {
        return direction;
    }

    public void setDirection(NormalDirection direction) {
        this.direction = direction;
    }

    public Position getMaximum() {
        return new Position(x + radius, y + radius, z);
    }

    public Position getMinimum() {
        return new Position(x - radius, y - radius, z);
    }
}
