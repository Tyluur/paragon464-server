package com.paragon464.gameserver.model.item;

import com.google.common.base.Objects;

public final class ShopItem extends Item {
    private final int position;
    private final int buyPrice;
    private final int sellPrice;

    public ShopItem(int id, int amount, int position, int buyPrice, int sellPrice) {
        super(id, amount);
        this.position = position;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public ShopItem(int id, int amount, int position, int buyPrice) {
        super(id, amount);
        this.position = position;
        this.buyPrice = buyPrice;
        this.sellPrice = -1;
    }

    public ShopItem(int id, int amount, int position) {
        super(id, amount);
        this.position = position;
        this.buyPrice = -1;
        this.sellPrice = -1;
    }

    public ShopItem(int id, int amount) {
        super(id, amount);
        this.position = -1;
        this.buyPrice = -1;
        this.sellPrice = -1;
    }

    public ShopItem(int id) {
        super(id, -1);
        this.position = -1;
        this.buyPrice = -1;
        this.sellPrice = -1;
    }

    public int getPosition() {
        return position;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getAmount(), position, buyPrice, sellPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopItem shopItem = (ShopItem) o;
        return getId() == shopItem.getId() &&
            getAmount() == shopItem.getAmount() &&
            position == shopItem.position &&
            buyPrice == shopItem.buyPrice &&
            sellPrice == shopItem.sellPrice;
    }

    @Override
    public String toString() {
        return "ShopItem{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", position=" + position +
            ", buyPrice=" + buyPrice +
            ", sellPrice=" + sellPrice +
            '}';
    }
}
