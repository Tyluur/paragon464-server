package com.paragon464.gameserver.net.packet;

public class Packets {

    /**
     * Incoming packet sizes array. int = 4, short = 2, long = 8, string = auto
     * -1, byte = 1
     */
    public static int[] SIZES = new int[]{
        // 0---1---2---3---4---5---6---7---8---9
        -3, -3, -3, -3, 2, -3, -3, 1, -3, 8, // 0
        -3, -3, -3, -3, -3, -3, -3, -3, -3, 2, // 10
        -3, -3, -3, -3, -3, -3, -3, -3, 8, -3, // 20
        -3, -3, -3, -3, -3, -3, -1, 8, -3, -3, // 30
        -3, -3, -3, -1, 8, -3, -3, 10, 6, 13, // 40
        -1, 2, -3, -3, -3, -3, -3, -3, -3, -3, // 50
        -3, -3, -3, -3, -3, -1, -3, -3, 2, 8, // 60
        -3, 0, 2, 12, -3, -3, -3, -3, 4, 12, // 70
        -3, -3, 12, -3, 2, -3, 10, -3, 10, -3, // 80
        -3, -3, 4, -3, 2, -3, -3, -3, -3, 9, // 90
        -3, 10, -1, 16, 8, -3, 2, -3, -3, 6, // 100
        -3, 2, -3, 8, 2, -1, -3, -1, -3, 8, // 110
        8, 9, 8, 8, -3, -3, -3, -3, 4, 2, // 120
        9, 4, -3, -1, 8, -3, -3, 8, -3, -3, // 130
        8, -3, -3, -1, -3, -3, -3, -3, 8, -3, // 140
        -3, -3, -3, 4, -3, -3, 2, 3, -3, 10, // 150
        -3, -3, 8, 16, -3, -1, 20, -1, -3, -3, // 160
        6, -3, -3, -3, 4, 2, 2, 10, -1, 4, // 170
        2, -3, -3, 2, -3, 1, -3, 12, -1, -3, // 180
        -3, -3, -3, -3, -3, -3, -3, -1, -3, -3, // 190
        -3, -3, 0, -3, -3, 2, -3, -3, -3, -3, // 200
        8, -3, 10, -3, -1, 10, 8, -3, 2, -1, // 210
        10, -3, -3, -3, 8, -3, -3, -3, 6, -3, // 220
        0, -3, -3, 12, -3, -3, -3, -3, -1, -3, // 230
        4, -3, -3, 6, 8, -3, -3, 10, 10, -3, // 240
        -3, -3, -3, -3, -3, 8 // 250
    };
}