package com.paragon464.gameserver.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class NumberUtils {

    public static int[] randomizeArray(int[] array) {
        Random rgen = new Random();  // Random number generator
        for (int i = 0; i < array.length; i++) {
            int randomPosition = rgen.nextInt(array.length);
            int temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }

        return array;
    }

    public static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static final double getRandomDouble(double maxValue) {
        return ThreadLocalRandom.current().nextDouble(maxValue + 1);
    }

    public static int random(int range) {
        return ThreadLocalRandom.current().nextInt(range + 1);
    }

    public static int getDays(int seconds) {
        return (int) TimeUnit.SECONDS.toDays(seconds);
    }

    public static int getHours(int seconds) {
        return (int) TimeUnit.SECONDS.toHours(seconds);
    }

    public static int getMinutes(int seconds) {
        return (int) TimeUnit.SECONDS.toMinutes(seconds);
    }

    public static int getSeconds(int seconds) {
        return (int) TimeUnit.SECONDS.toSeconds(seconds);
    }

    public static int getDaysFromMillis(long time) {
        int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        return (hours / 24);
    }

    public static int getHoursFromMillis(long time) {
        int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
        int minutes = seconds / 60;
        return (minutes / 60);
    }

    public static String getMinutesDisplay(int timer) {
        String display = "";
        int minutes = (timer / 60);
        int seconds = (timer - minutes * 60);
        if (minutes > 0) {
            display += minutes + " minutes ";
        } else {
            display += seconds + "s";
        }
        return display;
    }

    public int getHours(long ms) {
        return (int) ((ms / (1000 * 60 * 60)) % 24);
    }

    public int getMinutes(long ms) {
        return (int) ((ms / (1000 * 60)) % 60);
    }

    public int getSeconds(long ms) {
        return (int) ((ms / 1000) % 60);
    }
}
