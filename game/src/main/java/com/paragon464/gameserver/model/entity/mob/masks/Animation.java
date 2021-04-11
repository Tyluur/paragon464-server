package com.paragon464.gameserver.model.entity.mob.masks;

/**
 * Represents a single animation request.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class Animation {

    /**
     * Different animation constants.
     */
    public final static Animation YES_EMOTE = create(855, AnimationPriority.HIGH);
    public final static Animation NO_EMOTE = create(856, AnimationPriority.HIGH);
    public final static Animation THINKING = create(857, AnimationPriority.HIGH);
    public final static Animation BOW = create(858, AnimationPriority.HIGH);
    public final static Animation ANGRY = create(859, AnimationPriority.HIGH);
    public final static Animation CRY = create(860, AnimationPriority.HIGH);
    public final static Animation LAUGH = create(861, AnimationPriority.HIGH);
    public final static Animation CHEER = create(862, AnimationPriority.HIGH);
    public final static Animation WAVE = create(863, AnimationPriority.HIGH);
    public final static Animation BECKON = create(864, AnimationPriority.HIGH);
    public final static Animation CLAP = create(865, AnimationPriority.HIGH);
    public final static Animation DANCE = create(866, AnimationPriority.HIGH);
    public final static Animation PANIC = create(2105, AnimationPriority.HIGH);
    public final static Animation JIG = create(2106, AnimationPriority.HIGH);
    public final static Animation SPIN = create(2107, AnimationPriority.HIGH);
    public final static Animation HEADBANG = create(2108, AnimationPriority.HIGH);
    public final static Animation JOYJUMP = create(2109, AnimationPriority.HIGH);
    public final static Animation RASPBERRY = create(2110, AnimationPriority.HIGH);
    public final static Animation YAWN = create(2111, AnimationPriority.HIGH);
    public final static Animation SALUTE = create(2112, AnimationPriority.HIGH);
    public final static Animation SHRUG = create(2113, AnimationPriority.HIGH);
    public final static Animation BLOW_KISS = create(1368, AnimationPriority.HIGH);
    public final static Animation GLASS_WALL = create(1128, AnimationPriority.HIGH);
    public final static Animation LEAN = create(1129, AnimationPriority.HIGH);
    public final static Animation CLIMB_ROPE = create(1130, AnimationPriority.HIGH);
    public final static Animation GLASS_BOX = create(1131, AnimationPriority.HIGH);
    public final static Animation GOBLIN_BOW = create(2127, AnimationPriority.HIGH);
    public final static Animation GOBLIN_DANCE = create(2128, AnimationPriority.HIGH);
    /**
     * The id.
     */
    private int id;
    private AnimationPriority priority;
    /**
     * The delay.
     */
    private int delay;

    /**
     * Creates an animation.
     *
     * @param id    The id.
     * @param delay The delay.
     */
    public Animation(int id, int delay, AnimationPriority prior) {
        this.id = id;
        this.delay = delay;
        this.priority = prior;
    }

    /**
     * Creates an animation with no delay.
     *
     * @param id The id.
     * @return The new animation object.
     */
    public static Animation create(int id, AnimationPriority priority) {
        return create(id, 0, priority);
    }

    /**
     * Creates an animation.
     *
     * @param id    The id.
     * @param delay The delay.
     * @return The new animation object.
     */
    public static Animation create(int id, int delay, AnimationPriority prior) {
        return new Animation(id, delay, prior);
    }

    /**
     * Gets the id.
     *
     * @return The id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the delay.
     *
     * @return The delay.
     */
    public int getDelay() {
        return delay;
    }

    public AnimationPriority getPriority() {
        return priority;
    }

    public void setPriority(AnimationPriority priority) {
        this.priority = priority;
    }

    public enum AnimationPriority {
        HIGH, MED, LOW
    }
}
