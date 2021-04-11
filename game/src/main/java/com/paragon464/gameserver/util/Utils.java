package com.paragon464.gameserver.util;

import java.security.SecureRandom;

public class Utils {

    public static final byte[] DIRECTION_DELTA_X = new byte[]{-1, 0, 1, -1, 1, -1, 0, 1};
    public static final byte[] DIRECTION_DELTA_Y = new byte[]{1, 1, 1, 0, 0, -1, -1, -1};

    private static final long INIT_MILLIS = System.currentTimeMillis();
    private static final long INIT_NANOS = System.nanoTime();

    private static long millisSinceClassInit() {
        return (System.nanoTime() - INIT_NANOS) / 1000000;
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();//INIT_MILLIS + millisSinceClassInit();
    }

    public static final int getFaceDirection(int xOffset, int yOffset) {
        return ((int) (Math.atan2(-xOffset, -yOffset) * 2607.5945876176133)) & 0x3fff;
    }

    public static int direction(int dx, int dy) {
        if (dx < 0) {
            if (dy < 0) {
                return 5;
            } else if (dy > 0) {
                return 0;
            } else {
                return 3;
            }
        } else if (dx > 0) {
            if (dy < 0) {
                return 7;
            } else if (dy > 0) {
                return 2;
            } else {
                return 4;
            }
        } else {
            if (dy < 0) {
                return 2;
            } else if (dy > 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public static final int getMoveDirection(int xOffset, int yOffset) {
        if (xOffset < 0) {
            if (yOffset < 0)
                return 5;
            else if (yOffset > 0)
                return 0;
            else
                return 3;
        } else if (xOffset > 0) {
            if (yOffset < 0)
                return 7;
            else if (yOffset > 0)
                return 2;
            else
                return 4;
        } else {
            if (yOffset < 0)
                return 6;
            else if (yOffset > 0)
                return 1;
            else
                return -1;
        }
    }

    public static int random(int range) {
        SecureRandom rnd = new SecureRandom();
        int ran = rnd.nextInt(range + 1);
        rnd = null;
        return ran;
    }

    public static int random(int min, int max) {
        int roll = min + (int) (Math.random() * ((max) + 1));
        if (roll > max)
            roll = max;
        return roll;
    }

    public static String[] getLastCodeCalled() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String[] s = new String[stackTraceElements.length];
        int j = 0;
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement ste = stackTraceElements[i];
            String classname = ste.getClassName();
            String methodName = ste.getMethodName();
            int lineNumber = ste.getLineNumber();
            s[j++] = classname + "." + methodName + ":" + lineNumber;
            if (s[j - 1] == null) {
                s[j - 1] = "";
            }
        }
        // System.out.println(Arrays.toString(s));
        return s;
    }
}
