package com.paragon464.gameserver.model.entity.mob.masks;

/**
 * Represents a single graphic request.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class Graphic {

    /**
     * The id.
     */
    private int id;
    /**
     * The delay.
     */
    private int delay;
    /**
     * The height.
     */
    private int height;

    /**
     * Creates a graphic.
     *
     * @param id    The id.
     * @param delay The delay.
     */
    public Graphic(int id, int delay, int height) {
        this.id = id;
        this.delay = delay;
        this.height = height;
    }

    /**
     * Creates an graphic with no delay.
     *
     * @param id The id.
     * @return The new graphic object.
     */
    public static Graphic create(int id) {
        return create(id, 0, 0);
    }

    /**
     * Creates a graphic.
     *
     * @param id     The id.
     * @param delay  The delay.
     * @param height The height.
     * @return The new graphic object.
     */
    public static Graphic create(int id, int delay, int height) {
        return new Graphic(id, delay, height);
    }

    /**
     * Creates a graphic.
     *
     * @param id    The id.
     * @param delay The delay.
     * @return The new graphic object.
     */
    public static Graphic create(int id, int delay) {
        return new Graphic(id, delay, 0);
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

    /**
     * Gets the height.
     *
     * @return The height.
     */
    public int getHeight() {
        return height;
    }
}
