package com.paragon464.gameserver.model.shop;

import com.paragon464.gameserver.model.item.ShopItem;

public class Shop {

    private int id;
    private String name;
    private String currency;
    private boolean generalShop;
    private boolean buysBack;
    private ShopItem[] stock;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency(int item) {
        switch (id) {
            case 9://fish shop
                if (item >= 113258 && item <= 113261) {
                    return "FISH_GUILD_POINTS";
                }
                break;
        }
        return currency;
    }

    public boolean isGeneralShop() {
        return generalShop;
    }

    public void setGeneralShop(boolean generalShop) {
        this.generalShop = generalShop;
    }

    public boolean buysBack() {
        return buysBack;
    }

    public void setBuysBack(boolean buysBack) {
        this.buysBack = buysBack;
    }

    public ShopItem[] getStock() {
        return stock;
    }

    public void setStock(ShopItem[] stock) {
        this.stock = stock;
    }

    public void addToStock(ShopItem item) {
        int free_index = item.getPosition();
        if (free_index == -1 || free_index >= 40) {
            return;
        }
        this.stock[free_index] = item;
    }

    public void shift() {
        ShopItem[] old = stock;
        stock = new ShopItem[stock.length];
        int newIndex = 0;
        for (int i = 0; i < stock.length; i++) {
            if (old[i] != null) {
                stock[newIndex] = old[i];
                newIndex++;
            }
        }
    }
}
