package com.paragon464.gameserver.model.region;

import com.google.common.base.Objects;

public final class SizedPosition extends Position {

    private final int width;
    private final int height;

    public SizedPosition(final int x, final int y, final int z, final int width, final int height) {
        super(x, y, z);
        this.width = width;
        this.height = height;
    }

    public SizedPosition(final int x, final int y, final int width, final int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public SizedPosition transform(final int diffX, final int diffY, final int diffZ) {
        return new SizedPosition(getX() + diffX, getY() + diffY, getZ() + diffZ, width, height);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SizedPosition that = (SizedPosition) o;
        return width == that.width &&
            height == that.height;
    }

    @Override
    public String toString() {
        return "Position{" +
            "x=" + getX() +
            ", y=" + getY() +
            ", z=" + getZ() +
            ", regionId=" + getRegionId() +
            ", width=" + width +
            ", height=" + height +
            '}';
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
