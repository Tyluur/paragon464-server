package com.paragon464.gameserver;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.AccountManager;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.task.WorldTasksManager;
import com.paragon464.gameserver.task.impl.NPCResetTask;
import com.paragon464.gameserver.task.impl.NPCTickTask;
import com.paragon464.gameserver.task.impl.PlayerOutgoingPacketTask;
import com.paragon464.gameserver.task.impl.PlayerResetTask;
import com.paragon464.gameserver.task.impl.PlayerTickTask;
import com.paragon464.gameserver.task.impl.PlayerUpdaters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.lang.String.format;

/**
 * The 'core' class of the server which processes all the logic tasks in one
 * single logic <code>ExecutorService</code>. This service is scheduled which
 * means <code>Event</code>s are also submitted to it.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class GameEngine implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameEngine.class);
    public static ScheduledExecutorService slowExecutor;
    static ScheduledExecutorService engineScheduler;
    private int systemUpdateTimer;
    private boolean running = false;

    private List<Runnable> packetTasks = new ArrayList<>();
    private List<Runnable> tickTasks = new ArrayList<>();
    private List<Runnable> updateTasks = new ArrayList<>();
    private List<Runnable> resetTasks = new ArrayList<>();

    private long creationTime = System.currentTimeMillis();
    private long totalCycleTime = 0;
    private long recentTotalCycleTime = 0;
    private int tickCount = 0;

    //logout/login, packets, ticks, updating, resetting
    @Override
    public void run() {
        try {
            long startTime = System.currentTimeMillis();
            AccountManager.tickLogins();
            packetTasks.clear();
            tickTasks.clear();
            updateTasks.clear();
            resetTasks.clear();
            for (Player player : World.getWorld().getPlayers()) {
                if (player == null) continue;
                if (player.isDestroyed() || !player.getAttributes().isSet("active")) {
                    continue;
                }
                tickTasks.add(new PlayerTickTask(player));
                updateTasks.add(new PlayerUpdaters(player));
                packetTasks.add(new PlayerOutgoingPacketTask(player));
                resetTasks.add(new PlayerResetTask(player));
            }
            for (NPC npc : World.getWorld().getNPCS()) {
                if (npc == null || npc.isDestroyed())
                    continue;
                tickTasks.add(new NPCTickTask(npc));
                resetTasks.add(new NPCResetTask(npc));
            }
            WorldTasksManager.processTasks();
            World.getWorld().getTickableManager().pulse();
            tickTasks.stream().forEach(Runnable::run);
            updateTasks.parallelStream().forEach(Runnable::run);
            packetTasks.stream().forEach(Runnable::run);
            resetTasks.stream().forEach(Runnable::run);
            AccountManager.tickLogouts();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            if (tickCount > 0 && (tickCount % 100) == 0 && Config.DEBUG_MODE) {
                LOGGER.debug(format("Tick #%d was completed in %d ms.", tickCount, elapsedTime) + " "
                    + format("Average cycle speed for the past minute: %d ms.", recentTotalCycleTime / 100) + " "
                    + format("Average cycle speed since launch: %d ms.", totalCycleTime / tickCount));
                recentTotalCycleTime = 0;
            }
            totalCycleTime += elapsedTime;
            recentTotalCycleTime += elapsedTime;
            tickCount++;
        } catch (Exception e) {
            World.getWorld().handleError(e);
        }
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void start() {
        if (running) {
            throw new IllegalStateException("The engine is already running.");
        }
        running = true;
        engineScheduler = Executors.newSingleThreadScheduledExecutor();
        slowExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Stops the <code>GameEngine</code>'s thread.
     */
    public void stop() {
        if (!running) {
            throw new IllegalStateException("The engine is already stopped.");
        }
        running = false;
        engineScheduler.shutdown();
        slowExecutor.shutdown();
    }

    public boolean isRunning() {
        return running;
    }

    public long getTotalCycleTime() {
        return totalCycleTime;
    }

    public long getRecentTotalCycleTime() {
        return recentTotalCycleTime;
    }

    public int getTickCount() {
        return tickCount;
    }

    public void setUpdateTimer(int var) {
        this.systemUpdateTimer = var;
    }

    public void deductSystemTimer(int var) {
        this.systemUpdateTimer -= var;
        if (this.systemUpdateTimer < 0) {
            this.systemUpdateTimer = 0;
        }
    }

    public int getSystemUpdateTimer() {
        return systemUpdateTimer;
    }

    public boolean systemUpdating() {
        return systemUpdateTimer > 0;
    }
}
