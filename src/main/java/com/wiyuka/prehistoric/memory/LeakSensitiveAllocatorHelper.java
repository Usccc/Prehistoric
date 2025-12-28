package com.wiyuka.prehistoric.memory;

import com.sun.management.OperatingSystemMXBean;
import com.wiyuka.prehistoric.util.ThreadedExecutor;
import io.netty.buffer.ByteBuf;

import java.lang.management.ManagementFactory;


public class LeakSensitiveAllocatorHelper {

    private static final ThreadedExecutor sharedExecutor = ThreadedExecutor.newExecutor();
    private static final OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final int MB = 1024 * 1024;
    private static final int SECURE_THRESHOLD = 512 * MB;

    public static int adjustDirectCapacity(int original) {
        long memory = os.getFreeMemorySize();
        if (memory >= 256 * MB + SECURE_THRESHOLD) {
            return 256 * MB;
        } else if (memory >= 16 * MB + SECURE_THRESHOLD) {
            return 16 * MB;
        } else if (memory >= MB + SECURE_THRESHOLD) {
            return MB;
        }
        return original;
    }

    public static ByteBuf allocateBuffer(ByteBuf buffer) {
        buffer.capacity(adjustDirectCapacity(buffer.capacity()));
        sharedExecutor.execute(() -> {
            try {
                // Release buffer after a period if not used
                Thread.sleep(600_000);
                buffer.clear();
                buffer.release();
            } catch (Exception ignore) {
            }
        });
        return buffer;
    }
}
