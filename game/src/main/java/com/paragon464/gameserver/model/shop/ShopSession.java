package com.paragon464.gameserver.model.shop;

import com.paragon464.gameserver.io.database.pool.impl.ConnectionPool;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.entity.mob.player.container.ContainerInterface;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Inventory;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.shop.impl.SkillcapeShopSession;
import com.paragon464.gameserver.net.Packet;
import com.paragon464.gameserver.net.PacketBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.String.format;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public abstract class ShopSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopSession.class);

    protected Player player;
    protected Shop shop;
    private ShopItem[] stock;

    public ShopSession(Player player, Shop shop) {
        this.player = player;
        this.shop = shop;
        if (this.shop.getCurrency() == null) {
            this.shop.setCurrency("COINS");
        }
        this.open();
    }

    public void open() {
        this.stock = shop.getStock();
        player.getFrames().modifyText(shop.getName(), 614, 22);
        player.getInterfaceSettings().openInterface(614);
        player.getFrames().displayInventoryInterface(615);
        player.getInterfaceSettings().addListener(player.getInventory(), new ContainerInterface(-1, 0, 93));
        if (shop.getId() == 19 || this instanceof SkillcapeShopSession) {
            refresh();
        } else {
            player.getFrames().sendItems(-1, 64271, 31, stock);
        }
        Object[] invparams = new Object[]{"", "", "", "", "Sell 50", "Sell 10", "Sell 5", "Sell 1", "Value", -1, 0, 7, 4, 93, 615 << 16};
        player.getFrames().sendClientScript(150, invparams, "IviiiIsssssssss");
        player.getFrames().sendClickMask(0, 27, 615, 0, 1278);
        player.getFrames().sendClickMask(0, 27, 615, 0, 2360446);
        Object[] shopparams = new Object[]{"", "", "", "", "Buy 50", "Buy 10", "Buy 5", "Buy 1", "Value", -1, 0, 4, 10, 31, (614 << 16) + 23};//TODO - 23 main tab, 24 secondary tab
        player.getFrames().sendClientScript(150, shopparams, "IviiiIsssssssss");
        player.getFrames().sendInterfaceVisibility(614, 34, true);
        player.getInventory().refresh();
        handleTabs(true);
        switch (shop.getId()) {
            case 10://Teamcapes
            case 11://
                player.getFrames().sendInterfaceVisibility(614, 26, true);
                break;
            default:
                player.getFrames().sendInterfaceVisibility(614, 26, false);
                break;
        }
        switch (shop.getCurrency()) {
            case "NIGHTMARE_POINTS":
                player.getFrames().modifyText("Nightmare Zone points: " + player.getAttributes().getInt("nightmare_points"), 614, 28);
                break;
            case "ZOMBIES_SHOP_CREDITS":
                player.getFrames().modifyText("Zombies Shop Credits: " + player.getAttributes().getInt("zombies_shop_credits"), 614, 28);
                break;
            case "RUNE_CRAFT":
                player.getFrames().modifyText("Runecrafting Points: " + player.getAttributes().getInt("rc_points"), 614, 28);
                break;
            case "CREDITS":
                final int credits = player.getAttributes().getInt("credits");
                player.getFrames().modifyText("Credits: " + (credits > 0 ? "<col=00FF00>" : "<col=ff0000>") + credits + "</col>. You may buy more at \"https://runenova.com/bank/get-more\".", 614, 28);
                break;
            case "VOTE_POINTS":
                player.getFrames().modifyText("Voting Points: " + player.getAttributes().getInt("vote_points"), 614, 28);
                break;
            case "WC_GUILD_POINTS":
                player.getFrames().modifyText("Woodcutting Guild Points: " + player.getAttributes().getInt("wc_guild_points"), 614, 28);
                break;
            case "MINE_GUILD_POINTS":
                player.getFrames().modifyText("Woodcutting Guild Points: " + player.getAttributes().getInt("mine_guild_points"), 614, 28);
                break;
            default:
                player.getFrames().modifyText("", 614, 28);
                switch (shop.getId()) {
                    case 9:
                        player.getFrames().modifyText("Fishing Guild Points: " + player.getAttributes().getInt("fish_guild_points"), 614, 28);
                        break;
                }
                break;
        }
        // player.getFrames().sendTBC(168, 3);
    }

    void refresh() {
        PacketBuilder bldr = new PacketBuilder(92, Packet.Type.VARIABLE_SHORT);
        bldr.putInt(-1 << 16 | 64271);
        bldr.putShort(31);
        bldr.putShort(shop.getStock().length);
        for (Item item : stock) {
            if (item != null) {
                if (shop.getId() == 19) {
                    if (item.getId() == 7456 || item.getId() == 7457) {
                        if (player.getAttributes().getInt("rfd_stage") < 6) {
                            bldr.putByteC((byte) 0);
                            bldr.putInt(0);
                            continue;
                        }
                    } else if (item.getId() == 7458) {
                        if (player.getAttributes().getInt("rfd_stage") < 7) {
                            bldr.putByteC((byte) 0);
                            bldr.putInt(0);
                            continue;
                        }
                    } else if (item.getId() == 7459) {
                        if (player.getAttributes().getInt("rfd_stage") < 8) {
                            bldr.putByteC((byte) 0);
                            bldr.putInt(0);
                            continue;
                        }
                    } else if (item.getId() == 7460) {
                        if (player.getAttributes().getInt("rfd_stage") < 9) {
                            bldr.putByteC((byte) 0);
                            bldr.putInt(0);
                            continue;
                        }
                    } else if (item.getId() == 7461) {
                        if (player.getAttributes().getInt("rfd_stage") < 10) {
                            bldr.putByteC((byte) 0);
                            bldr.putInt(0);
                            continue;
                        }
                    } else if (item.getId() == 7462) {
                        if (player.getAttributes().getInt("rfd_stage") < 11) {
                            bldr.putByteC((byte) 0);
                            bldr.putInt(0);
                            continue;
                        }
                    }
                } else if (this instanceof SkillcapeShopSession) {
                    int skill = SkillcapeShopSession.getSlotForHoodId(item.getId());
                    int lvl = player.getSkills().getLevel(SkillType.fromId(skill));
                    if (lvl < 99) {
                        bldr.putByteC((byte) 0);
                        bldr.putInt(0);
                        continue;
                    }
                }
                int count = item.getAmount();
                if (count > 254) {
                    bldr.putByteC((byte) 255);
                    bldr.putInt(count);
                } else {
                    bldr.putByteC((byte) count);
                }
                bldr.putInt(item.getId() + 1);
            } else {
                bldr.putByteC((byte) 0);
                bldr.putInt(0);
            }
        }
        player.write(bldr.toPacket());
    }

    void handleTabs(boolean main) {
        if (main) {
            player.getFrames().sendInterfaceVisibility(614, 29, true);
            player.getFrames().sendInterfaceVisibility(614, 25, false);
            player.getFrames().sendInterfaceVisibility(614, 27, false);
            player.getFrames().sendClickMask(0, 40, 614, 23, 1278);
        } else {
            player.getFrames().sendInterfaceVisibility(614, 25, true);
            player.getFrames().sendInterfaceVisibility(614, 29, false);
            player.getFrames().sendInterfaceVisibility(614, 27, true);
            player.getFrames().sendClickMask(0, 40, 614, 24, 1278);
        }
    }

    public void value(final Item item, final int interfaceId) {
        if (item == null || item.getDefinition() == null) {
            return;
        }

        final ShopItem shopItem = Arrays.stream(stock).filter(stockItem -> stockItem != null && stockItem.getId() == item.getId())
            .findAny().orElse(null);
        if (shopItem != null) {
            if (shop.getCurrency().equalsIgnoreCase("free")) {
                player.getFrames().sendMessage("This item has no cost.");
                return;
            }

            final StringBuilder valueMessage = new StringBuilder(item.getDefinition().getName());
            if (interfaceId == 614) {
                valueMessage.append(" currently costs: ").append(NumberFormat.getInstance().format(shopItem.getBuyPrice())).append(" ");
            } else {
                valueMessage.append(" currently sells for: ").append(NumberFormat.getInstance().format(shopItem.getSellPrice())).append(" ");
            }
            valueMessage.append(getCurrencyName(item)).append(".");
            player.getFrames().sendMessage(valueMessage.toString());
        } else {
            if (!isSellable(shop, item)) {
                player.getFrames().sendMessage("You can't sell that item here.");
            } else {
                player.getFrames()
                    .sendMessage("" + item.getDefinition().getName() + " currently sells for "
                        + NumberFormat.getInstance().format(item.getDefinition().getHighAlch()) + " "
                        + getCurrencyName(item) + ".");
            }
        }
        LOGGER.debug("Shop item: {}", item.toString());
    }

    public String getCurrencyName(Item item) {
        if (item.getDefinition().getName().equalsIgnoreCase("Slayer helmet")) {
            return "Slayer points";
        }
        switch (shop.getCurrency(item.getId())) {
            case "NIGHTMARE_POINTS":
                return "Nightmare points";
            case "FREE":
                return "Free";
            case "COINS":
                return "Coins";
            case "TZHAAR":
                return "Tokkul";
            case "PEST_CONTROL":
                return "Pest control points";
            case "SLAYER":
                return "Slayer points";
            case "VOTE_POINTS":
                return "Vote points";
            case "RUNE_CRAFT":
                return "Runecraft points";
            case "CREDITS":
                return "Credits";
            case "ZOMBIES_SHOP_CREDITS":
                return "Zombies shop credits";
            case "WC_GUILD_POINTS":
                return "Guild points";
            case "MINE_GUILD_POINTS":
                return "Guild points";
            case "FISH_GUILD_POINTS":
                return "Guild points";
        }
        return "NOTHING";
    }

    /**
     * Checks if the specified item is sellable to a specific shop.
     *
     * @param shop The shop that is to be checked against.
     * @param item The item in question.
     * @return {@code true} if the shop is a general store and the item is not a currency item
     * or if the shop buys back and the item is stocked. In both cases, the item needs to be tradable.
     * Else, {@code false}.
     */
    private boolean isSellable(Shop shop, Item item) {
        if (!item.getDefinition().isTradable() || item.getDefinition().getName().matches("([cC]oins|[tT]okkul)"))
            return false;

        if (!shop.isGeneralShop()) {
            if (shop.buysBack()) {
                for (ShopItem stock : stock) {
                    if (stock == null) continue;
                    if (item.getId() == stock.getId()) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    public boolean purchaseItem(final int slot, final int initAmount) {
        if (slot < 0 || slot > 40) {
            return false;
        }
        ShopItem shopItem = getSlot(slot);
        if (shopItem == null || shopItem.getDefinition() == null) {
            return false;
        }
        int price = shopItem.getBuyPrice();
        if (price <= 0 && !shop.getCurrency().equalsIgnoreCase("free")) {
            player.getFrames().sendMessage("This item doesn't belong?!");
            return false;
        }
        // check if we even have enough for just one
        if (!playerHasEnoughCurrency(shopItem, price)) {
            player.getFrames().sendMessage("You don't have enough " + getCurrencyName(shopItem) + " to purchase this.");
            return false;
        }

        final int freeSlots = player.getInventory().freeSlots(shopItem.getId());
        final int amountToPurchase = freeSlots > initAmount ? initAmount : freeSlots;
        final int totalMoney = getPlayerCurrencyAmount(shopItem);
        final int purchasedAmount = totalMoney > amountToPurchase * price ? amountToPurchase : (int) Math.floor(totalMoney / price);
        final int finalCost = amountToPurchase * price;

        if (purchasedAmount < amountToPurchase) {
            player.getFrames().sendMessage(format("You didn't have enough %s to purchase the full amount.", getCurrencyName(shopItem)));
        }

        player.getInventory().addItem(new Item(shopItem.getId(), purchasedAmount));
        deductCurrencyFromPlayer(shopItem, finalCost);
        ShopManager.refreshShopping(player);
        return true;
    }

    public ShopItem getSlot(int slot) {
        return stock[slot];
    }

    public boolean playerHasEnoughCurrency(Item item, int var) {
        if (item.getDefinition().getName().equalsIgnoreCase("Slayer helmet")) {
            return player.getAttributes().getInt("slayer_points") >= var;
        }
        switch (shop.getCurrency(item.getId())) {
            case "NIGHTMARE_POINTS":
                return player.getAttributes().getInt("nightmare_points") >= var;
            case "FREE":
                return true;
            case "COINS":
                return player.getInventory().hasItemAmount(995, var);
            case "TZHAAR":
                return player.getInventory().hasItemAmount(6529, var);
            case "PEST_CONTROL":
                return player.getAttributes().getInt("zombies_points") >= var;
            case "SLAYER":
                return player.getAttributes().getInt("slayer_points") >= var;
            case "RUNE_CRAFT":
                return player.getAttributes().getInt("rc_points") >= var;
            case "VOTE_POINTS":
                return player.getAttributes().getInt("vote_points") >= var;
            case "CREDITS":
                return player.getAttributes().getInt("credits") >= var;
            case "ZOMBIES_SHOP_CREDITS":
                return player.getAttributes().getInt("zombies_shop_credits") >= var;
            case "WC_GUILD_POINTS":
                return player.getAttributes().getInt("wc_guild_points") >= var;
            case "MINE_GUILD_POINTS":
                return player.getAttributes().getInt("mine_guild_points") >= var;
            case "FISH_GUILD_POINTS":
                return player.getAttributes().getInt("fish_guild_points") >= var;
        }
        return false;
    }

    public int getPlayerCurrencyAmount(Item item) {
        if (item.getDefinition().getName().equalsIgnoreCase("Slayer helmet")) {
            return player.getAttributes().getInt("slayer_points");
        }
        switch (shop.getCurrency(item.getId())) {
            case "NIGHTMARE_POINTS":
                return player.getAttributes().getInt("nightmare_points");
            case "FREE":
                return 999999999;
            case "COINS":
                return player.getInventory().getItemAmount(995);
            case "TZHAAR":
                return player.getInventory().getItemAmount(6529);
            case "PEST_CONTROL":
                return player.getAttributes().getInt("zombies_points");
            case "SLAYER":
                return player.getAttributes().getInt("slayer_points");
            case "RUNE_CRAFT":
                return player.getAttributes().getInt("rc_points");
            case "VOTE_POINTS":
                return player.getAttributes().getInt("vote_points");
            case "CREDITS":
                return player.getAttributes().getInt("credits");
            case "ZOMBIES_SHOP_CREDITS":
                return player.getAttributes().getInt("zombies_shop_credits");
            case "WC_GUILD_POINTS":
                return player.getAttributes().getInt("wc_guild_points");
            case "MINE_GUILD_POINTS":
                return player.getAttributes().getInt("mine_guild_points");
            case "FISH_GUILD_POINTS":
                return player.getAttributes().getInt("fish_guild_points");
        }
        return 0;
    }

    public void deductCurrencyFromPlayer(Item item, int var) {
        if (item.getDefinition().getName().equalsIgnoreCase("Slayer helmet")) {
            player.getAttributes().subtractInt("slayer_points", var);
            return;
        }
        switch (shop.getCurrency(item.getId())) {
            case "COINS":
                player.getInventory().deleteItem(995, var);
                break;
            case "TZHAAR":
                player.getInventory().deleteItem(6529, var);
                break;
            case "PEST_CONTROL":
                player.getAttributes().subtractInt("zombies_points", var);
                break;
            case "SLAYER":
                player.getAttributes().subtractInt("slayer_points", var);
                break;
            case "RUNE_CRAFT":
                player.getAttributes().subtractInt("rc_points", var);
                break;
            case "VOTE_POINTS":
                player.getAttributes().subtractInt("vote_points", var);
                break;
            case "ZOMBIES_SHOP_CREDITS":
                player.getAttributes().subtractInt("zombies_shop_credits", var);
                break;
            case "NIGHTMARE_POINTS":
                player.getAttributes().subtractInt("nightmare_points", var);
                break;
            case "WC_GUILD_POINTS":
                player.getAttributes().subtractInt("wc_guild_points", var);
                break;
            case "MINE_GUILD_POINTS":
                player.getAttributes().subtractInt("mine_guild_points", var);
                break;
            case "FISH_GUILD_POINTS":
                player.getAttributes().subtractInt("fish_guild_points", var);
                break;
            case "CREDITS":
                LOGGER.warn("Player {} purchased an item using credits. This shouldn't happen.", player.getDetails().getName());
                // TODO: Uncomment when reimplemented.
                /*ConnectionPool.execute(() -> {
                    try (Connection connection = ConnectionPool.getForumPool().getConnection();
                         PreparedStatement statement = connection.prepareStatement("UPDATE xf_user SET bdbank_money = bdbank_money - ? WHERE user_id = ?;")) {
                        statement.setDouble(1, var);
                        statement.setInt(2, player.getDetails().getUserId());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        LOGGER.error("Failed to deduct credits from player [id={}]!", player.getDetails().getUserId(), e);
                    }
                });*/
                player.getAttributes().subtractInt("credits", var);
                break;
        }
    }

    public void sellItem(final int slot, final int initAmount) {
        if (slot < 0 || slot > Inventory.SIZE) {
            return;
        }
        Item Item = player.getInventory().get(slot);
        if (Item == null) {
            return;
        }
        if (!isSellable(shop, Item)) {
            player.getFrames().sendMessage("You can't sell that item here.");
            return;
        }

        final boolean stackable = Item.getDefinition().isStackable();
        final int sellPrice = Item.getDefinition().getHighAlch();
        final int amount = player.getInventory().getItemAmount(Item.getId());
        final int amountToSell = amount >= initAmount ? initAmount : amount;

        if (stackable) {
            player.getInventory().deleteItem(Item.getId(), amountToSell, slot);
        } else {
            IntStream.generate(() -> 1).limit(amountToSell).forEach(item -> player.getInventory().deleteItem(Item.getId()));
        }
        addCurrencyToPlayer(Item, amountToSell * sellPrice);
        ShopManager.refreshShopping(player);
    }

    public void addCurrencyToPlayer(Item item, int var) {
        switch (shop.getCurrency()) {
            case "NIGHTMARE_POINTS":
                player.getAttributes().addInt("nightmare_points", var);
                break;
            case "COINS":
                player.getInventory().addItem(995, var);
                break;
            case "TZHAAR":
                player.getInventory().addItem(6529, var);
                break;
            case "PEST_CONTROL":
                player.getAttributes().addInt("zombies_points", var);
                break;
            case "SLAYER":
                player.getAttributes().addInt("slayer_points", var);
                break;
            case "RUNE_CRAFT":
                player.getAttributes().addInt("rc_points", var);
                break;
            case "VOTE_POINTS":
                player.getAttributes().addInt("vote_points", var);
                break;
            case "CREDITS":
                player.getAttributes().addInt("credits", var);
                break;
            case "ZOMBIES_SHOP_CREDITS":
                player.getAttributes().addInt("zombies_shop_credits", var);
                break;
            case "WC_GUILD_POINTS":
                player.getAttributes().addInt("wc_guild_points", var);
                break;
            case "MINE_GUILD_POINTS":
                player.getAttributes().addInt("mine_guild_points", var);
                break;
            case "FISH_GUILD_POINTS":
                player.getAttributes().addInt("fish_guild_points", var);
                break;
        }
    }

    public ShopItem getStockItem(int slot) {
        return stock[slot];
    }

    public int getItemInSlot(int slot) {
        return stock[slot].getId();
    }

    public int getStockCount() {
        return stock.length;
    }

    public int getItemSlot(int id) {
        for (int i = 0; i < stock.length; i++) {
            if (stock[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public boolean containsItem(int itemId) {
        for (int i = 0; i < stock.length; i++) {
            if (stock[i].getId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public int findFreeSlot() {
        for (int i = 0; i < stock.length; i++) {
            if (stock[i] == null || stock[i].getId() <= 0)
                return i;
        }
        return -1;
    }

    public void addToStock(ShopItem item) {
        int free_index = item.getPosition();
        if (free_index == -1 || free_index >= 40) {
            return;
        }
        this.stock[free_index] = item;
    }

    public void setStockItem(ShopItem item, int slot) {
        this.stock[slot] = null;
    }

    public ShopItem[] getStock() {
        return stock;
    }

    public void setStock(ShopItem[] stock) {
        this.stock = stock;
    }
}
