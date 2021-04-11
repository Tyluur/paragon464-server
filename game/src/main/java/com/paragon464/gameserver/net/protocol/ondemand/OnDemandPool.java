package com.paragon464.gameserver.net.protocol.ondemand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>
 * The ondemand pool manages an executor service with various ondemand workers
 * and request queues.
 * </p>
 * <p>
 * <p>
 * When an ondemand request is submitted, it is added to the appropriate queue
 * and then the workers are notified about this. The workers can then service
 * requests.
 * </p>
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
@SuppressWarnings("unchecked")
public class OnDemandPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnDemandPool.class);

    /**
     * The thread pool size.
     */
    private static final int POOL_SIZE = 20;

    /**
     * The ondemand pool instance.
     */
    private static OnDemandPool pool = new OnDemandPool();
    /**
     * The thread pool.
     */
    private final ExecutorService service = Executors.newFixedThreadPool(POOL_SIZE);
    /**
     * The request queues.
     */
    private BlockingQueue<OnDemandRequest>[] queues = new LinkedBlockingQueue[2];

    /**
     * Creates the thread pool, request queues and workers.
     */
    private OnDemandPool() {
        try {

            for (int i = 0; i < queues.length; i++) {
                queues[i] = new LinkedBlockingQueue<>();
            }

            for (int i = 0; i < POOL_SIZE; i++) {
                try {
                    service.submit(new OnDemandWorker(queues));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred whilst creating thread pool!", e);
        }
    }

    /**
     * Gets the ondemand pool instance.
     *
     * @return The ondemand pool instance.
     */
    public static OnDemandPool getOnDemandPool() {
        return pool;
    }

    /**
     * Pushes a new request onto the appropriate queue and notifies the workers
     * about this change.
     *
     * @param request The ondemand request.
     */
    public void pushRequest(OnDemandRequest request) {
        int priority = request.getPriority();
        if (priority < 0 || priority >= queues.length) {
            return;
        }
        queues[priority].offer(request);
        synchronized (this) {
            notifyAll();
        }
    }

    public void clearRequests() {
        queues[0].clear();
        queues[1].clear();
        synchronized (this) {
            notifyAll();
        }
    }
}
