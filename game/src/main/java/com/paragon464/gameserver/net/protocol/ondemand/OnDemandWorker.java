package com.paragon464.gameserver.net.protocol.ondemand;

import java.io.FileNotFoundException;
import java.util.concurrent.BlockingQueue;

/**
 * <p>
 * A class which waits for ondemand requests to queue up and then processes
 * them.
 * </p>
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class OnDemandWorker implements Runnable {

    /**
     * The array of request queues.
     */
    private BlockingQueue<OnDemandRequest>[] queues;

    /**
     * Creates the ondemand worker.
     *
     * @param queues The array of request queues.
     * @throws FileNotFoundException if the cache could not be found.
     * @throws InvalidCacheException if the cache is invalid.
     */
    public OnDemandWorker(BlockingQueue<OnDemandRequest>[] queues) throws FileNotFoundException {
        this.queues = queues;
    }

    @Override
    public void run() {
        while (true) {
            for (BlockingQueue<OnDemandRequest> activeQueue : queues) {
                OnDemandRequest request;
                while ((request = activeQueue.poll()) != null) {
                    request.service();
                }
            }
            synchronized (OnDemandPool.getOnDemandPool()) {
                try {
                    OnDemandPool.getOnDemandPool().wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
