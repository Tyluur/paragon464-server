package com.paragon464.gameserver.model;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.region.Position;

public class Projectiles {

    /**
     * The id.
     */
    private int id;

    /**
     * The delay.
     */
    private int startSpeed;

    /**
     * The angle.
     */
    private int angle;

    /**
     * The speed.
     */
    private int speed;

    /**
     * The start height.
     */
    private int startHeight;

    /**
     * The end height.
     */
    private int endHeight;

    /**
     * The lockon.
     */
    private Mob lockon;

    /**
     * The starting position
     */
    private Position start;

    /**
     * The finishing position
     */
    private Position finish;

    private int slope;

    private int radius;

    private int hitDelay;

    /**
     * @param start       where it begins
     * @param finish      where it finishes
     * @param lockOn      the mob its going to follow
     * @param id          - the projectile gfx id
     * @param startSpeed  - start speed
     * @param speed       - speed it will travel at
     * @param angle       - the angle
     * @param startHeight - start height
     * @param endHeight   - end height
     */
    public Projectiles(Position start, Position finish, Mob lockOn, int id, int startSpeed, int speed, int angle,
                       int startHeight, int endHeight) {
        this.start = start;
        setLockon(lockOn);
        if (lockOn == null) {
            this.finish = finish;
        }
        this.id = id;
        this.startSpeed = startSpeed;
        this.angle = angle;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.speed = speed;
    }

    public Projectiles(Position start, Position finish, Mob lockOn, int id, int startSpeed, int speed, int angle,
                       int startHeight, int endHeight, int slope, int radius) {
        this.start = start;
        setLockon(lockOn);
        if (lockOn == null) {
            this.finish = finish;
        }
        this.id = id;
        this.startSpeed = startSpeed;
        this.angle = angle;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.speed = speed;
        this.slope = slope;
        this.radius = radius;
    }

    /**
     * Creates a new projectile
     *
     * @param start
     * @param finish
     * @param lockOn
     * @param id
     * @param startSpeed
     * @param speed
     * @param angle
     * @param startHeight
     * @param endHeight
     * @return
     */
    public static Projectiles create(Position start, Position finish, Mob lockOn, int id, int startSpeed, int speed,
                                     int angle, int startHeight, int endHeight) {
        return new Projectiles(start, finish, lockOn, id, startSpeed, speed, angle, startHeight, endHeight);
    }

    public static Projectiles create(Position start, Position finish, Mob lockOn, int id, int startSpeed, int speed,
                                     int angle, int startHeight, int endHeight, int slope, int radius) {
        return new Projectiles(start, finish, lockOn, id, startSpeed, speed, angle, startHeight, endHeight, slope,
            radius);
    }

    public void setMagicSpeed(Mob cast, Mob source) {
        this.start = cast.getPosition();
        this.finish = source.getPosition();
        int gfxDelay;
        if (cast.getPosition().isWithinRadius(source.getPosition(), 1)) {
            speed = 30;
        } else if (cast.getPosition().isWithinRadius(source.getPosition(), 5)) {
            speed = 40;
        } else if (cast.getPosition().isWithinRadius(source.getPosition(), 8)) {
            speed = 45;
        } else {
            speed = 55;
        }
        gfxDelay = speed + 20;
        setHitDelay((gfxDelay / 20) - 1);
    }

    public void setSpeedRange(Mob attacker, Mob victim) {
        this.start = attacker.getPosition();
        this.finish = victim.getPosition();
        int gfxDelay;
        if (attacker.getPosition().isWithinRadius(victim.getPosition(), 1)) {
            speed = 20;
        } else if (attacker.getPosition().isWithinRadius(victim.getPosition(), 3)) {
            speed = 25;
        } else if (attacker.getPosition().isWithinRadius(victim.getPosition(), 8)) {
            speed = 30;
        } else {
            speed = 40;
        }
        gfxDelay = speed + 20;
        hitDelay = (gfxDelay / 20) - 2;
    }

    public void setSpeedRange(Mob attacker, Mob victim, boolean second) {
        this.start = attacker.getPosition();
        this.finish = victim.getPosition();
        int gfxDelay;
        if (attacker.getPosition().isWithinRadius(victim.getPosition(), 1)) {
            speed = 50;
        } else if (attacker.getPosition().isWithinRadius(victim.getPosition(), 3)) {
            speed = 50;
        } else if (attacker.getPosition().isWithinRadius(victim.getPosition(), 8)) {
            speed = 60;
        } else {
            speed = 65;
        }
        if (second) {
            speed += 15;
        }
        gfxDelay = speed + 20;
        hitDelay = (gfxDelay / 20) - 2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartSpeed() {
        return startSpeed;
    }

    public void setStartSpeed(int startSpeed) {
        this.startSpeed = startSpeed;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public void setStartHeight(int startHeight) {
        this.startHeight = startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public void setEndHeight(int endHeight) {
        this.endHeight = endHeight;
    }

    public Mob getLockon() {
        return lockon;
    }

    /**
     * Sets the lockon mob.
     *
     * @param mob The lockon.
     */
    public void setLockon(Mob mob) {
        if (mob == null) {
            return;
        }
        this.lockon = mob;
        this.finish = mob.getPosition();
    }

    public Position getStart() {
        return start;
    }

    public void setStart(final Position start) {
        this.start = start;
    }

    public Position getFinish() {
        return finish;
    }

    public void setFinish(final Position finish) {
        this.finish = finish;
    }

    public int getHitDelay() {
        return hitDelay;
    }

    public void setHitDelay(int hitDelay) {
        this.hitDelay = hitDelay;
    }

    public int getSlope() {
        return slope;
    }

    public void setSlope(int slope) {
        this.slope = slope;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
