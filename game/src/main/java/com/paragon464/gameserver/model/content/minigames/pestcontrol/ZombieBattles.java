package com.paragon464.gameserver.model.content.minigames.pestcontrol;

import com.paragon464.gameserver.GameEngine;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.NPCFollowing;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCCombatDefinition;
import com.paragon464.gameserver.model.entity.mob.npc.NPCSkills;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Inventory;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.combat.npcs.PestZombie;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ShopItem;
import com.paragon464.gameserver.model.pathfinders.Directions;
import com.paragon464.gameserver.model.pathfinders.PrimitivePathFinder;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.MapBuilder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.model.shop.Shop;
import com.paragon464.gameserver.model.shop.ShopSession;
import com.paragon464.gameserver.model.shop.impl.DefaultShopSession;
import com.paragon464.gameserver.model.shop.impl.PestShopSession;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class ZombieBattles extends GameConstants {

    private final List<NPC> npcs = new LinkedList<>();
    private List<Player> players;
    private NPC knight = null, shopKeeper = null;

    private Position baseLocation = null;

    private int round = 0, pausedTimer = (round == 0 ? 1 : 20);

    private Map<Position, NPC> barricadeLocations = new HashMap<>();

    private boolean running = false, nextRound = true;

    private Tickable ticker = null;

    public ZombieBattles(List<Player> team) {
        this.players = team;
        this.running = true;
        this.init_gamearea();
        this.init_gamehandler();
    }

    public void init_gamearea() {
        int[] newCoords = MapBuilder.findEmptyChunkBound(8, 8);
        this.baseLocation = new Position((newCoords[0] << 3), (newCoords[1] << 3), 0);
        MapBuilder.copyAllPlanesMap(3474 >> 3, 3195 >> 3, newCoords[0], newCoords[1], 8, 8);
        final Position instancedWellPosition = new Position(baseLocation.getX() + 10, baseLocation.getY() + 42, 0);

        World.getWorld().submit(new Tickable(0) {
            @Override
            public void execute() {
                this.stop();
                final GameObject well = World.getObjectWithId(instancedWellPosition, 12897);
                if (well != null) {
                    World.removeObject(well, false);
                }
            }
        });
    }

    private void init_gamehandler() {
        World.getWorld().submit(this.ticker = new Tickable(1) {
            @Override
            public void execute() {
                if (!gameHandler()) {
                    destroySession();
                    this.stop();
                }
            }
        });
    }

    public boolean gameHandler() {
        if (!running)
            return false;
        for (Player player : players) {
            if (player != null) {
                refreshScreens();
            }
        }
        for (NPC npc : npcs) {
            if (npc != null) {
                aggressive(npc);
            }
        }
        if (nextRound && pausedTimer > 0) {
            pausedTimer--;
        } else if (nextRound && pausedTimer == 0) {
            initNextWave();
        }
        handleVoidShouts();
        if (knight.getAttributes().isSet("void_powerup")) {
            knight.playGraphic(247);
            int amount = knight.getAttributes().getInt("void_powerup");
            if (amount > 0) {
                knight.getAttributes().subtractInt("void_powerup", 1);
            } else {
                knight.getAttributes().remove("void_powerup");
                knight.getSkills().defence -= 25;
            }
        }
        return true;
    }

    public void destroySession() {
        destroyArea();
        final List<Player> team = new LinkedList<>();
        team.addAll(this.players);
        this.players.clear();
        for (Player player : team) {
            player.teleport(2657, 2639, 0);
            player.getAttributes().addInt("zombies_points", points());
            player.getInterfaceSettings().closeOverlay();
            player.resetVariables();
            player.playAnimation(-1, Animation.AnimationPriority.HIGH);
            player.setPestGameSession(null);
            player.getAttributes().remove("force_multi");
            player.getFrames().sendHintArrow(null);
            removeZombiesItems(player);
            player.getInterfaceSettings().closeInterfaces(true);
        }
        final List<NPC> npcsCopy = new LinkedList<>();
        npcsCopy.addAll(this.npcs);
        for (NPC npcsLeft : npcsCopy) {
            if (World.getWorld().containsNPC(npcsLeft) != -1)
                World.getWorld().unregister(npcsLeft);
        }
        World.getWorld().unregister(this.shopKeeper);
    }

    public void refreshScreens() {
        for (Player player : players) {
            if (player != null) {
                int health = knight.getSkills().getLevel(3);
                if (knight.getHp() <= health * 0.20) {
                    player.getFrames().modifyText("<col=FF0000>Knights health: " + knight.getHp(), 407, 1);
                } else {
                    player.getFrames().modifyText("<col=52D017>Knights health: " + knight.getHp(), 407, 1);
                }
                player.getFrames().modifyText("<col=52D017>Zombies remaining: " + zombieCount(), 407, 2);
                if (round == 0) {
                    player.getFrames().modifyText("<col=52D017>Game will begin soon..", 407, 3);
                } else {
                    player.getFrames().modifyText("<col=52D017>Round: " + round, 407, 3);
                }
                if (pausedTimer > 0 && nextRound) {
                    player.getFrames().modifyText("<col=52D017>Next round in: " + pausedTimer, 407, 4);
                } else if (pausedTimer == 0 && nextRound) {
                    player.getFrames().modifyText("", 407, 4);
                }
            }
        }
    }

    private boolean aggressive(final NPC npc) {
        if (npc.getCombatState().isDead()) return true;
        if (npc.getId() == knight.getId() || npc.getId() == 1532)
            return true;
        if (npc.getId() == shopKeeper.getId())
            return true;
        if (npc.getCombatState().previouslyAttackedInSecs() != 0) {//npc already under attack
            return true;
        }
        if (npc.getCombatState().previouslyHitInSecs() != 0) {//npc already hitting an mob
            return true;
        }
        int roll = NumberUtils.random(1);
        int closestDist = 14;
        Mob mainTarget = knight;//main target
        //check for baricades in the way
        Directions.NormalDirection dir = Directions.directionFor(npc.getPosition(), mainTarget.getPosition());
        NPC closestNPC = closestNPC(npc, dir);
        if (closestNPC != null && closestNPC.getId() == 1532) {
            mainTarget = closestNPC;
        } else {//no baricades
            if (roll == 0) {
                //choose a random player to attack
                List<Player> areaPlayers = new LinkedList<>();
                for (Player victims : players) {
                    if (victims.getCombatState().isDead()) continue;
                    areaPlayers.add(victims);
                }
                if (areaPlayers.size() > 0) {
                    for (Player p : areaPlayers) {
                        if (p.getCombatState().isDead()) continue;
                        int targetDist = npc.getPosition().getDistanceFrom(p.getPosition());
                        if (targetDist < closestDist) {
                            closestDist = targetDist;
                            mainTarget = p;
                        }
                    }
                }
            }
        }
        CombatAction.beginCombat(npc, mainTarget);
        return true;
    }

    private void initNextWave() {
        for (Player player : players) {
            player.getAttributes().set("zombies_shop_credits", player.getAttributes().getInt("zombies_shop_credits") + 1);
        }

        pausedTimer = 0;
        nextRound = false;
        round++;
        // final List<NPC> npcsCopy = new LinkedList<NPC>();
        // npcsCopy.addAll(this.npcs);
        /*
         * for (NPC barricade : npcsCopy) { if (barricade != null) { if
         * (barricade.getId() == 1532) { barricadeLocations.remove(barricade);
         * unregister(barricade); World.getWorld().unregister(barricade); } } }
         */
        initZombies();
    }

    private void handleVoidShouts() {
        if (NumberUtils.random(4) == 0) {
            int health = knight.getSkills().getLevel(3);
            if (knight.getHp() <= health * 0.20) {
                String knightMessage = VOID_HELP_SHOUTS[NumberUtils.random(VOID_HELP_SHOUTS.length - 1)];
                knight.playForcedChat(knightMessage);
                /*
                 * for (Player player : players) {
                 * player.getFrames().sendMessage("Void Knight: <col=FF0000>" +
                 * knightMessage); }
                 */
            } else {
                String knightMessage = VOID_BATTLE_SHOUTS[NumberUtils.random(VOID_BATTLE_SHOUTS.length - 1)];
                knight.playForcedChat(knightMessage);
            }
        }
    }

    public void destroyArea() {
        this.ticker = null;
        final Position instancedWellPosition = new Position(baseLocation.getX() + 10, baseLocation.getY() + 42, 0);
        final Position barricadeTableLoc = new Position(instancedWellPosition.getX() - 1,
            instancedWellPosition.getY() - 9, 0);
        final GameObject barricadeTable = World.getObjectWithId(barricadeTableLoc, 36582);
        if (barricadeTable != null) {
            World.removeObject(barricadeTable, false);
        }
        GameEngine.slowExecutor.schedule(() -> MapBuilder.destroyMap(baseLocation.getZoneX(), baseLocation.getZoneY(), 0,
            0), 1200, TimeUnit.MILLISECONDS);
    }

    public int points() {
        int points = 0;
        for (int i = 1; i < round + 1; i++) {
            points += i;
        }
        return points;
    }

    private void removeZombiesItems(final Player player) {
        for (int i = 0; i < Inventory.SIZE; i++) {
            Item item = player.getInventory().get(i);
            if (item == null) continue;
            if (item.getId() == 4053 || item.getId() == EXCALIBUR || item.getId() >= 10542 && item.getId() <= 10545) {
                player.getInventory().deleteItem(item);
            }
        }
        player.getInventory().refresh();
    }

    public int zombieCount() {
        int count = 0;
        for (NPC zombie : getNPCS()) {
            if (zombie.getId() == 73 || zombie.getId() == 750) {// TODO - change
                // wen adding
                // new zombies
                count++;
            }
        }
        return count;
    }

    public NPC closestNPC(NPC npc, Directions.NormalDirection dir) {
        return TileControl.npcInPath(npc.getPosition(), npc, dir, npc.getSize());
    }

    public void initZombies() {
        int amountBase = 6 + (round * 2);
        int mageZombieAmount = round / 5;
        Region region = World.getRegion(baseLocation.getRegionId(), false);
        if (mageZombieAmount > 0) {
            for (int amount = 0; amount < mageZombieAmount; amount++) {
                NPC mageZombie = new NPC(750);
                mageZombie.setAttackLayout(new PestZombie());
                mageZombie.setHp(120);
                mageZombie.getSkills().attack = 150;
                mageZombie.getSkills().magic = 150;
                mageZombie.getSkills().defence = 200;
                final Position base = new Position(baseLocation.getX() + 10, baseLocation.getY() + 50, 0);
                Position placement = new Position((baseLocation.getX() + 10) + NumberUtils.random(10),
                    (baseLocation.getY() + 50) - NumberUtils.random(15), 0);
                boolean projectileBlocked = ProjectilePathFinder.hasLineOfSight(region, base, placement, false);
                boolean npcOnTile = (TileControl.getSingleton().locationOccupied(mageZombie, placement.getX(),
                    placement.getY(), 0) || barricadeLocations.get(placement) != null);
                while (npcOnTile || !projectileBlocked) {
                    placement = new Position((baseLocation.getX() + 10) + NumberUtils.random(10),
                        (baseLocation.getY() + 50) - NumberUtils.random(15), 0);
                    projectileBlocked = ProjectilePathFinder.hasLineOfSight(region, base, placement, false);
                    npcOnTile = (TileControl.getSingleton().locationOccupied(mageZombie, placement.getX(),
                        placement.getY(), 0) || barricadeLocations.get(placement) != null);
                }
                mageZombie.setPosition(placement);
                mageZombie.getAttributes().set("force_multi", true);
                mageZombie.setPestGameSession(this);
                addNPC(mageZombie);
                World.getWorld().addNPC(mageZombie);
            }
        }
        for (int amount = 0; amount < amountBase; amount++) {
            NPC lowZombie = new NPC(73);
            if (lowZombie.getSkills() == null) {
                lowZombie.setSkills(new NPCSkills());
            }
            lowZombie.setAttackLayout(new PestZombie());
            lowZombie.setHp(45);
            lowZombie.getSkills().maxHitpoints = 45;
            lowZombie.getSkills().attack = 100;
            lowZombie.getSkills().magic = 100;
            lowZombie.getSkills().defence = 100;
            final Position base = new Position(baseLocation.getX() + 10, baseLocation.getY() + 50, 0);
            Position placement = new Position((baseLocation.getX() + 10) + NumberUtils.random(10),
                (baseLocation.getY() + 50) - NumberUtils.random(15), 0);
            boolean projectileBlocked = ProjectilePathFinder.hasLineOfSight(region, base, placement, false);
            boolean npcOnTile = (TileControl.getSingleton().locationOccupied(lowZombie, placement.getX(),
                placement.getY(), 0) || barricadeLocations.get(placement) != null);
            while (npcOnTile || !projectileBlocked) {
                placement = new Position((baseLocation.getX() + 10) + NumberUtils.random(10),
                    (baseLocation.getY() + 50) - NumberUtils.random(15), 0);
                projectileBlocked = ProjectilePathFinder.hasLineOfSight(region, base, placement, false);
                npcOnTile = (TileControl.getSingleton().locationOccupied(lowZombie, placement.getX(), placement.getY(),
                    0) || barricadeLocations.get(placement) != null);
            }
            lowZombie.setPosition(placement);
            lowZombie.getAttributes().set("force_multi", true);
            lowZombie.setPestGameSession(this);
            addNPC(lowZombie);
            World.getWorld().addNPC(lowZombie);
        }
    }

    public List<NPC> getNPCS() {
        return npcs;
    }

    public void addNPC(final NPC npc) {
        npcs.add(npc);
    }

    public void transferPlayers() {
        // ghost shopkeeper
        this.shopKeeper = new NPC(1699);
        if (this.shopKeeper.getSkills() == null) {
            this.shopKeeper.setSkills(new NPCSkills());
        }
        this.shopKeeper.setHp(1);
        this.shopKeeper.setPosition(new Position(baseLocation.getX() + 45, baseLocation.getY() + 49, 0));
        this.shopKeeper.getAttributes().set("force_multi", true);
        this.shopKeeper.getAttributes().set("pest_control", this);
        this.shopKeeper.setPestGameSession(this);
        World.getWorld().addNPC(this.shopKeeper);
        // ghost shopkeeper
        // void knight
        this.knight = new NPC(3784);
        if (this.knight.getSkills() == null) {
            this.knight.setSkills(new NPCSkills());
            this.knight.setCombatDefinition(new NPCCombatDefinition());
            this.knight.getCombatDefinition().defendAnim = -1;
            this.knight.getCombatDefinition().deathAnim = -1;
        }
        this.knight.setHp(250);
        this.knight.getSkills().maxHitpoints = 250;
        this.knight.setPosition(new Position(baseLocation.getX() + 10, baseLocation.getY() + 42, 0));
        this.knight.getAttributes().set("force_multi", true);
        this.knight.getAttributes().set("pest_control", this);
        this.knight.setPestGameSession(this);
        npcs.add(knight);
        World.getWorld().addNPC(this.knight);
        // void knight
        List<Position> placements = new LinkedList<>();
        final Position instancedWellPosition = new Position(baseLocation.getX() + 10, baseLocation.getY() + 42, 0);
        Position zombie_base = new Position(instancedWellPosition.getX() + 48, instancedWellPosition.getY() + 2, 0);
        placements.add(new Position(zombie_base.getX() - 2, zombie_base.getY() + 10, 0));
        placements.add(new Position(zombie_base.getX() - 1, zombie_base.getY() - 9, 0));
        placements.add(new Position(zombie_base.getX(), zombie_base.getY() - 14, 0));
        final GameObject well = World.getObjectWithId(instancedWellPosition, 12897);
        for (Player player : players) {
            if (player != null) {
                final Position teleLoc = placements.get(NumberUtils.random(placements.size() - 1));
                if (well != null) {
                    player.getFrames().removeObject(well);
                }
                player.teleport(teleLoc);
                player.getFrames().sendHintArrow(knight);
                player.getAttributes().set("force_multi", true);
                player.getAttributes().set("zombies_shop_credits", 0);
                player.setPestGameSession(this);
                player.getInterfaceSettings().openOverlay(407);
                player.getFrames().modifyText("<col=52D017>Knights health: " + knight.getHp(), 407, 1);
                player.getFrames().modifyText("<col=52D017>Zombies remaining: " + zombieCount(), 407, 2);
                player.getFrames().modifyText("<col=52D017>Game will begin soon..", 407, 3);
                player.getFrames().modifyText("<col=52D017>Next round in: " + pausedTimer, 407, 4);
            }
        }
    }

    public void handleEndEffects(final Mob attacker, final Mob victim, final Hit hit) {
        if (attacker.isPlayer()) {
            Player player = (Player) attacker;
            if (player.getCombatState().getCombatType().equals(CombatType.MAGIC)) {
                if (PestShopSession.wandNeeded(player)) {
                    int casts = player.getAttributes().getInt("wand_casts");
                    if (casts >= 100) {
                        player.getAttributes().set("wand_casts", 0);
                        player.getAttributes().addInt("wand_lvl", 1);
                    } else {
                        player.getAttributes().addInt("wand_casts", 1);
                    }
                }
            }
        }
    }

    public boolean handleDeath(final Mob mob) {
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            removeZombiesItems(player);
            player.getAttributes().remove("force_multi");
            players.remove(player);
            player.resetVariables();
            player.playAnimation(-1, Animation.AnimationPriority.HIGH);
            player.teleport(2657, 2639, 0);
            player.getAttributes().addInt("zombies_points", points());
            player.getInterfaceSettings().closeOverlay();
            player.setPestGameSession(null);
            if (playerCount() <= 0) {
                running = false;
                return true;
            }
        } else if (mob.isNPC()) {
            Mob killer = mob.getCombatState().getDamageMap().highestDamage();

            if (killer != null && killer.isPlayer())
                killer.getAttributes().set("zombies_shop_credits", killer.getAttributes().getInt("zombies_shop_credits") + 1);

            NPC npc = (NPC) mob;
            if (npc.getId() == 1532) {
                barricadeLocations.remove(npc);
            }
            removeNPC(npc);
            World.getWorld().unregister(npc);
            if (npc.getId() == knight.getId()) {
                running = false;
                return true;
            }
            if (zombieCount() <= 0) {
                pausedTimer = 30;
                nextRound = true;
                for (Player player : players) {
                    player.getFrames().modifyText("<col=52D017>Next round in: " + pausedTimer, 407, 4);
                }
            }
            return true;
        }
        return false;
    }

    public int playerCount() {
        int count = 0;
        for (Player player : players) {
            if (player != null)
                count++;
        }
        return count;
    }

    public void removeNPC(final NPC npc) {
        npcs.remove(npc);
    }

    public void handleLogout(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            removeZombiesItems(player);
            player.resetVariables();
            player.setLocation(2657, 2639, 0);
            player.getAttributes().addInt("zombies_points", points());
        }
    }

    public boolean handleFollowing(final Mob mob, final Mob victim) {
        if (mob.isPlayer()) {
            Player player = (Player) mob;
        } else if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            if (npc.getId() == 73) {
                NPCFollowing.executePathFinding(npc, victim, true);
                return true;
            } else if (npc.getId() == 750) {
                Directions.NormalDirection dir = Directions.directionFor(npc.getPosition(), victim.getPosition());
                if (dir != null) {
                    Position next = npc.getPosition().transform(Directions.DIRECTION_DELTA_X[dir.intValue()],
                        Directions.DIRECTION_DELTA_Y[dir.intValue()], 0);
                    if (next != null && !next.equals(victim.getPosition())) {
                        NPC npcInPath = closestNPC(npc, dir);
                        boolean anotherInWay = npcInPath != null && npcInPath.getId() == 750;
                        if (!anotherInWay) {
                            int x = next.getX(), y = next.getY();
                            npc.executePath(new PrimitivePathFinder(), x, y);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean ableToAttack(final Mob mob, final Mob mob2) {
        if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            if (npc.getId() == shopKeeper.getId() || npc.getId() == knight.getId() || npc.getId() == 1532) {
                npc.getCombatState().end(1);
                return false;
            }
        }
        if (mob.isPlayer()) {
            if (mob2.isNPC()) {
                NPC npc = (NPC) mob2;
                return npc.getId() != shopKeeper.getId() && npc.getId() != knight.getId() && npc.getId() != 1532;
            } else {
                return !mob2.isPlayer();
            }
        }
        return true;
    }

    public boolean handleNpcClicks(final Player player, final NPC npc, int option) {
        if (npc.getId() == 1699) {
            Shop zombie_shop = new Shop();
            ShopItem[] items = new ShopItem[]{new ShopItem(10542, 5), new ShopItem(35, 5), new ShopItem(4053, 5)};
            zombie_shop.setStock(items);
            zombie_shop.setName("Pest Control Supplies");
            zombie_shop.setCurrency("ZOMBIES_SHOP_CREDITS");
            ShopSession session = new DefaultShopSession(player, zombie_shop);
            player.getAttributes().set("shop_session", session);
            return true;
        }
        return false;
    }

    public boolean handleObjectClicks(final Player player, final GameObject object, int option) {
        return false;
    }

    public boolean handleItemOnNpc(final Player player, final NPC npc, final Item item) {
        if (npc.getId() == knight.getId()) {// void knight
            if (item.getId() == EXCALIBUR) {
                knight.getAttributes().addInt("void_powerup", 15);
                knight.playGraphic(247);
                knight.getSkills().defence += 25;
                player.getInventory().deleteItem(EXCALIBUR);
                player.getFrames().sendMessage("You empowered Void Knight with the great excalibur!");
            } else if (item.getId() >= 10542 && item.getId() <= 10545) {// healing
                // potions
                int next = item.getId() + 1;
                if (next <= 10546) {
                    player.getInventory().deleteItem(item.getId());
                    player.getInventory().addItem(next);
                    knight.heal(15);
                }
            }
            return true;
        }
        return false;
    }

    public boolean handleItemClicks(final Player player, final Item item, int option) {
        if (option == 1) {
            if (item.getId() == 4053) {// Barricade
                final Position position = player.getPosition();

                if (barricadeLocations.get(position) != null || TileControl.getSingleton().locationOccupied(position.getX(), position.getY(), position.getZ())) {
                    player.getFrames().sendMessage("You can't place a barricade here!");
                } else if (barricadeLocations.size() >= 8) {
                    player.getFrames().sendMessage("On second thought... It would be a waste to place another barricade right now.");
                } else {
                    player.getInventory().deleteItem(4053);
                    player.getInventory().refresh();

                    NPC barricade = new NPC(1532);
                    barricade.setCombatDefinition(new NPCCombatDefinition());
                    barricade.getCombatDefinition().defendAnim = -1;
                    barricade.getCombatDefinition().deathAnim = -1;
                    barricade.setSkills(new NPCSkills());
                    barricade.setHp(30);
                    barricade.getSkills().maxHitpoints = 30;
                    barricade.getSkills().defence = 1;
                    barricade.setPosition(position);
                    barricade.getAttributes().set("force_multi", true);
                    barricade.setPestGameSession(this);
                    addNPC(barricade);
                    barricadeLocations.put(position, barricade);
                    World.getWorld().addNPC(barricade);
                }
            }
        }
        return false;
    }

    public NPC getKnight() {
        return knight;
    }
}
