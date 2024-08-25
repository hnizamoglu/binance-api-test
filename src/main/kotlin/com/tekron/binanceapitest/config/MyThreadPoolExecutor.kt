package com.tekron.binanceapitest.config

import org.slf4j.MDC
import java.net.InetAddress
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MyThreadPoolExecutor: ThreadPoolExecutor(
    10,
    10,
    Long.MAX_VALUE,
    TimeUnit.SECONDS,
    LinkedBlockingQueue<Runnable>(),
) {
    override fun beforeExecute(t: Thread?, r: Runnable?) {
        MDC.put("host", InetAddress.getLocalHost().hostName)
    }

    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
        MDC.clear()
    }
}