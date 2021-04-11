package com.paragon464.gameserver.model.entity.mob.masks;

import com.paragon464.gameserver.model.entity.mob.Mob;

/**
 * Handles player hits.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class Hits {

    private Hit hit1;
    private Hit hit2;

    public Hits() {
        hit1 = null;
        hit2 = null;
    }

    public void setHit1(Hit hit) {
        this.hit1 = hit;
    }

    public void setHit2(Hit hit) {
        this.hit2 = hit;
    }

    public int getHitDamage1() {
        if (hit1 == null) {
            return 0;
        }
        return hit1.damage;
    }

    public int getHitDamage2() {
        if (hit2 == null) {
            return 0;
        }
        return hit2.damage;
    }

    public int getHitType1() {
        if (hit1 == null) {
            return HitType.NO_DAMAGE.getType();
        }
        return hit1.type.getType();
    }

    public int getHitType2() {
        if (hit2 == null) {
            return HitType.NO_DAMAGE.getType();
        }
        return hit2.type.getType();
    }

    public void clear() {
        hit1 = null;
        hit2 = null;
    }

    public enum HitType {
        NO_DAMAGE(0), // blue
        NORMAL_DAMAGE(1), // red
        POISON_DAMAGE(2), // green
        DISEASE_DAMAGE(3); // orange

        private final int type;

        HitType(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }
    }

    /**
     * Holds the hit priority types.
     */
    public enum HitPriority {

        /**
         * Low priority means that when the next loop is called that checks the
         * hit queue, if the hit is not picked out, it is never displayed, used
         * for hits such as Ring of Recoil.
         */
        LOW_PRIORITY,

        /**
         * High priority means that the hit will wait in the queue until it's
         * displayed, used for hits such as special attacks.
         */
        HIGH_PRIORITY

    }

    public static class Hit {

        private Mob owner, target;
        private HitType type;
        private int damage;
        private int delay;
        private boolean potDelay;
        private HitPriority hitPriority;

        public Hit(Mob owner, int damage) {
            this(HitType.NORMAL_DAMAGE, damage, HitPriority.HIGH_PRIORITY, 0);
            this.owner = owner;
        }

        /**
         * Creates a hit.
         *
         * @param type   The hit type.
         * @param damage The damage.
         */
        public Hit(HitType type, int damage, HitPriority hitPriority, int delay) {
            this.type = type;
            this.damage = damage;
            this.delay = delay;
            this.hitPriority = hitPriority;
        }

        public Mob getOwner() {
            return owner;
        }

        public HitType getType() {
            return type;
        }

        public void setType(HitType type) {
            this.type = type;
        }

        public int getDamage() {
            return damage;
        }

        public void setDamage(int dmg) {
            this.damage = dmg;
        }

        public HitPriority getHitPriority() {
            return hitPriority;
        }

        public int getDelay() {
            return delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public void deductDamage(int var) {
            this.damage -= var;
        }

        public void setPriority(HitPriority prior) {
            this.hitPriority = prior;
        }

        public boolean isPotDelaying() {
            return potDelay;
        }

        public void setPotDelay(boolean potDelay) {
            this.potDelay = potDelay;
        }

        public Mob getTarget() {
            if (target == null) {
                target = owner;
            }
            return target;
        }

        public void setTarget(Mob target) {
            this.target = target;
        }
    }
}
