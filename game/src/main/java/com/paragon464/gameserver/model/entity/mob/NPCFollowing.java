package com.paragon464.gameserver.model.entity.mob;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.pathfinders.Directions;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.Position;

/**
 * @author Lazaro <lazaro@ziotic.com>
 */
public class NPCFollowing {

    public static boolean executePathFinding(Mob mob, Mob partner, boolean combat) {
        //try {
        mob.setInteractingMob(partner);
        Position loc = mob.getPosition();
        int dx = loc.getX() - partner.getPosition().getX();
        int dy = loc.getY() - partner.getPosition().getY();// eCenter.getY()
        // -
        // pCenter.getY();
        int distance = mob.getCoverage().center().getDistanceFrom(partner.getCoverage().center());
        boolean success = false;
        int x = mob.getPosition().getX();
        int y = mob.getPosition().getY();
        int counter = 1;
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            if (partner.isPlayer()) {
                counter = (player.getWalkingQueue().isMoving() || partner.getWalkingQueue().isMoving()) ? 2 : 1;
            } else {
                counter = player.getWalkingQueue().isMoving() ? 2 : 1;
            }
        } else {
            counter = 1;
        }
        for (int i = 0; i < counter; i++) {
            success = false;
            loc = new Position(x, y, mob.getPosition().getZ());
            NextNode next = getNextNode(loc, dx, dy, distance, combat, mob, partner);
            if (next == null) {
                break;
            }
            if (next.tile == null) {
                break;
            }
            if (next.canMove) {
                if (partner.getCoverage().within(next.tile) && !TileControl.locationOccupied(mob, partner)) {
                    success = true;
                    continue;
                }
                x = next.tile.getX();
                y = next.tile.getY();
                dx = x - partner.getPosition().getX();
                dy = y - partner.getPosition().getY();
                success = true;
                mob.updateCoverage(next.tile);
                mob.getWalkingQueue().addStep(next.tile.getX(), next.tile.getY());
                mob.getWalkingQueue().finish();
            } else {
                // TODO handle being stucked!
                break;
            }
        }
        return success;
    }

    protected static NextNode getNextNode(Position loc, int dx, int dy, int distance, boolean combat, Mob mob,
                                          Mob partner) {
        Directions.NormalDirection direction = null;
        boolean npcCheck = (mob.isNPC());
        if (combat) {
            if (mob.getCoverage().correctCombatPosition(mob, partner, partner.getCoverage(), 1,
                CombatType.MELEE)) {
                return null;
            }
        } else {
            if (mob.getCoverage().correctFinalFollowPosition(partner.getCoverage())) {
                return null;
            }
        }
        if (mob.getSize() > 1) {
            Position eCenter = mob.getCoverage().center();
            Position pCenter = partner.getCoverage().center();
            if (mob.getCoverage().intersect(partner.getCoverage())) {
                if (eCenter == pCenter) {
                    if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.SOUTH_WEST;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.WEST;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.SOUTH;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.NORTH_WEST;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.NORTH_EAST;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.SOUTH_EAST;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.EAST;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.NORTH;
                    }
                } else if (eCenter.getX() > pCenter.getX()) {
                    if (eCenter.getY() > pCenter.getY()) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH_EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH;
                        }
                    } else if (pCenter.getY() < pCenter.getY()) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH_EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH;
                        }
                    } else {
                        if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH_EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH_EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH;
                        }
                    }
                } else if (eCenter.getX() < pCenter.getX()) {
                    if (eCenter.getY() > pCenter.getY()) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH_WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH;
                        }
                    } else if (pCenter.getY() < pCenter.getY()) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH_WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH;
                        }
                    } else {
                        if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH_WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH_WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH;
                        }
                    }
                } else {
                    if (eCenter.getY() > pCenter.getY()) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH_EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH_WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.WEST;
                        }
                    } else if (eCenter.getY() < pCenter.getY()) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH_EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH_WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.WEST;
                        }
                    }
                }
            } else {
                Coverage eC = mob.getCoverage();
                Coverage pC = partner.getCoverage();
                int absDX = Math.abs(dx);
                int absDY = Math.abs(dy);
                if (eC.right(pC)) {
                    if (eC.above(pC)) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.getSize(), npcCheck)) {
                            if (absDY <= 1 && absDY >= 0) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            } else if (absDX <= 1 && absDX >= 0) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                }
                            } else {
                                direction = Directions.NormalDirection.SOUTH_WEST;
                            }
                        } else {
                            if (dx > dy) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                }
                            } else if (dx < dy) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            } else {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            }
                        }
                    } else if (eC.under(pC)) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.getSize(), npcCheck)) {
                            if (absDY <= 1 && absDY >= 0) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            } else if (absDX <= 1 && absDX >= 0) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                }
                            } else {
                                direction = Directions.NormalDirection.NORTH_WEST;
                            }
                        } else {
                            if (dx > -dy) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                }
                            } else if (dx < -dy) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            } else {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            }
                        }
                    } else {
                        if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.WEST;
                        }
                    }
                } else if (eC.left(pC)) {
                    if (eC.above(pC)) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.getSize(), npcCheck)) {
                            if (absDY <= 1 && absDY >= 0) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            } else if (absDX <= 1 && absDX >= 0) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                }
                            } else {
                                direction = Directions.NormalDirection.SOUTH_EAST;
                            }
                        } else {
                            if (-dx > dy) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                }
                            } else if (-dx < dy) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            } else {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            }
                        }
                    } else if (eC.under(pC)) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.getSize(), npcCheck)) {
                            if (absDY <= 1 && absDY >= 0) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            } else if (absDX <= 1 && absDX >= 0) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                }
                            } else {
                                direction = Directions.NormalDirection.NORTH_EAST;
                            }
                        } else {
                            if (-dx > -dy) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                }
                            } else if (-dx < -dy) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            } else {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            }
                        }
                    } else {
                        if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.EAST;
                        }
                    }
                } else {
                    if (eC.above(pC)) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH;
                        }
                    } else if (eC.under(pC)) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH;
                        }
                    }
                }
            }
            if (direction == null) {
                return null;
            }
            return new NextNode(loc, direction, TileControl.canMove(mob, direction, mob.getSize(), npcCheck));
        } else {
            if (dx > 0) {
                if (dy > 0) {
                    if (dx == 1 && dy == 1 && combat) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH;
                        } else {
                            direction = Directions.NormalDirection.WEST; // random w/e
                        }
                    } else {
                        if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH_WEST;
                        } else {
                            if (dy > dx) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                } else {
                                    direction = Directions.NormalDirection.SOUTH;
                                }
                            } else if (dy < dx) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                } else {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            } else {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH_WEST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                } else {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            }
                        }
                    }
                } else if (dy < 0) {
                    if (dx == 1 && dy == -1 && combat) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.WEST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH;
                        } else {
                            direction = Directions.NormalDirection.WEST; // random w/e
                        }
                    } else {
                        if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH_WEST;
                        } else {
                            if (Math.abs(dy) > Math.abs(dx)) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.WEST;
                                } else {
                                    direction = Directions.NormalDirection.NORTH;
                                }
                            } else if (Math.abs(dy) < Math.abs(dx)) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                } else {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            } else {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_WEST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH_WEST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                } else {
                                    direction = Directions.NormalDirection.WEST;
                                }
                            }
                        }
                    }
                } else {
                    direction = Directions.NormalDirection.WEST;
                }
            } else if (dx < 0) {
                if (dy > 0) {
                    if (dx == -1 && dy == 1 && combat) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH;
                        } else {
                            direction = Directions.NormalDirection.EAST; // random w/e
                        }
                    } else {
                        if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.SOUTH_EAST;
                        } else {
                            if (Math.abs(dy) > Math.abs(dx)) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                }
                            } else if (Math.abs(dy) < Math.abs(dx)) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            } else {
                                if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH_EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH_EAST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.SOUTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            }
                        }
                    }
                } else if (dy < 0) {
                    if (dx == -1 && dy == -1 && combat) {
                        if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.EAST;
                        } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH;
                        } else {
                            direction = Directions.NormalDirection.EAST; // random w/e
                        }
                    } else {
                        if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.getSize(), npcCheck)) {
                            direction = Directions.NormalDirection.NORTH_EAST;
                        } else {
                            if (Math.abs(dy) > Math.abs(dx)) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                }
                            } else if (Math.abs(dy) < Math.abs(dx)) {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            } else {
                                if (TileControl.canMove(mob, Directions.NormalDirection.NORTH_EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH_EAST;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(),
                                    npcCheck)) {
                                    direction = Directions.NormalDirection.NORTH;
                                } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                                    direction = Directions.NormalDirection.EAST;
                                }
                            }
                        }
                    }
                } else {
                    direction = Directions.NormalDirection.EAST;
                }
            } else {
                if (dy > 0) {
                    direction = Directions.NormalDirection.SOUTH;
                } else if (dy < 0) {
                    direction = Directions.NormalDirection.NORTH;
                } else {
                    if (TileControl.canMove(mob, Directions.NormalDirection.WEST, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.WEST;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.EAST, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.EAST;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.NORTH, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.NORTH;
                    } else if (TileControl.canMove(mob, Directions.NormalDirection.SOUTH, mob.getSize(), npcCheck)) {
                        direction = Directions.NormalDirection.SOUTH;
                    } else {
                        direction = Directions.NormalDirection.SOUTH; // random w/e
                    }
                }
            }
            if (direction == null) {
                return null;
            }
            return new NextNode(loc, direction, TileControl.canMove(mob, direction, mob.getSize(), npcCheck));
        }
    }

    private static class NextNode {
        Position tile = null;
        boolean canMove = false;

        public NextNode(Position loc, Directions.NormalDirection dir, boolean canMove) {
            this.canMove = canMove;
            if (canMove) {
                tile = loc.transform(Directions.DIRECTION_DELTA_X[dir.intValue()],
                    Directions.DIRECTION_DELTA_Y[dir.intValue()], 0);
            }
        }
    }
}
