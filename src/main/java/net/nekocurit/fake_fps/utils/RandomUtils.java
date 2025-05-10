package net.nekocurit.fake_fps.utils;

import java.util.Random;

public final class RandomUtils {

    private static final Random random = new Random();

    public static int random(int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);

        return random.nextInt(max - min + 1) + min;
    }
}
