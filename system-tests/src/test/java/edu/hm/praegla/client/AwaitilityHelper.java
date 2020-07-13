package edu.hm.praegla.client;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;

public class AwaitilityHelper {

    public static void wait(Callable<Boolean> condition) {
        await()
                .atMost(Duration.ofSeconds(5))
                .with()
                .pollInterval(Duration.ofMillis(500))
                .until(condition);
    }
}
