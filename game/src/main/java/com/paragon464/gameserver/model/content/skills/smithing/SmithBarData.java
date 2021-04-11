package com.paragon464.gameserver.model.content.skills.smithing;

import com.paragon464.gameserver.model.item.Item;

public class SmithBarData {

    public static final int HAMMER = 2347;

    public static final int[] BARS = {2349, // Bronze
        9467, // Blurite
        2351, // Iron
        2355, // Silver
        2353, // Steel
        2357, // Gold
        2359, // Mithril
        2361, // Adamant
        2363, // Rune
    };

    public static final String[] BAR_NAMES = {

        "Bronze", // Bronze
        "Blurite", // Blurite
        "Iron", // Iron
        "Silver", // Silver
        "Steel", // Steel
        "Gold", // Gold
        "Mithril", // Mithril
        "Adamant", // Adamant
        "Rune", // Rune
    };

    // reward, reqlvl, amountofreward, barAmount, xp, name, inter placement
    public static final Object[][] BRONZE = {{new Item(1205), 1, 1, 1, 12.0, "Dagger", 121, 153},
        {new Item(1277), 4, 1, 1, 12.0, "Sword", 112, 152},
        {new Item(1321), 5, 1, 2, 25.0, "Scimitar", 114, 143},
        {new Item(1291), 6, 1, 2, 25.0, "Longsword", 113, 116},
        {new Item(1307), 14, 1, 3, 37.0, "2 hand sword", 115, 117},
        //
        {new Item(1351), 1, 1, 1, 12.0, "Axe", 118, 154}, {new Item(1422), 2, 1, 1, 12.0, "Mace", 120, 157},
        {new Item(1337), 9, 1, 3, 37.0, "Warhammer", 111, 145},
        {new Item(1375), 10, 1, 3, 37.0, "Battleaxe", 119, 122},
        {new Item(3095), 13, 1, 2, 25.0, null, 165, 164}, // claws
        //
        {new Item(1103), 11, 1, 3, 37.0, "Chainbody", 125, 136},
        {new Item(1075), 16, 1, 3, 37.0, "Platelegs", 126, 137},
        {new Item(1087), 16, 1, 3, 37.0, "Plateskirt", 128, 138},
        {new Item(1117), 18, 1, 5, 62.0, "Platebody", 127, 139},
        {new Item(4540), 26, 1, 1, 25.0, null, 171, 169}, // oil
        // latern
        //
        {new Item(1139), 3, 1, 1, 12.0, "Medium helm", 129, 155},
        {new Item(1155), 7, 1, 2, 25.0, "Full Helm", 130, 140},
        {new Item(1173), 8, 1, 2, 25.0, "Square shield", 131, 141},
        {new Item(1189), 12, 1, 3, 37.0, "Kiteshield", 132, 142},
        {new Item(4820), 19, 15, 1, 25.0, null, 173, 172}, // nails
        //
        {new Item(819, 25), 4, 10, 1, 12.0, "Dart tips", 134, 156},
        {new Item(39, 25), 5, 15, 1, 12.0, "Arrow tips", 135, 158},
        {new Item(864, 25), 7, 5, 1, 12.0, "Throwing knife", 133, 159}, {null, 0, 0, 0, 0, null, 123, 160}, // other
        {null, 0, 0, 0, 0, null, 162, 163}, // studs
        //
        {new Item(9375), 3, 10, 1, 12.0, "Bolts", 178, 176}, {null, 76, 1, 1, 62.5, null, 175, 177}, // limbs
        {null, 0, 0, 0, 0, null, 181, 182},// grapple tips
    };

    public static final Object[][] IRON = {{new Item(1203), 15, 1, 1, 25.0, "Dagger", 121, 153},
        {new Item(1279), 19, 1, 1, 25.0, "Sword", 112, 152},
        {new Item(1323), 20, 1, 2, 50.0, "Scimitar", 114, 143},
        {new Item(1293), 21, 1, 2, 50.0, "Longsword", 113, 116},
        {new Item(1309), 29, 1, 3, 75.0, "2 hand sword", 115, 117},
        //
        {new Item(1349), 16, 1, 1, 25.0, "Axe", 118, 154}, {new Item(1420), 17, 1, 1, 25.0, "Mace", 120, 157},
        {new Item(1335), 24, 1, 3, 75.0, "Warhammer", 111, 145},
        {new Item(1363), 25, 1, 3, 75.0, "Battleaxe", 119, 122},
        {new Item(3096), 28, 1, 2, 50.0, null, 165, 164}, // claws
        //
        {new Item(1101), 26, 1, 3, 75.0, "Chainbody", 125, 136},
        {new Item(1067), 31, 1, 3, 75.0, "Platelegs", 126, 137},
        {new Item(1081), 31, 1, 3, 75.0, "Plateskirt", 128, 138},
        {new Item(1115), 33, 1, 5, 125.0, "Platebody", 127, 139},
        {new Item(4540), 26, 1, 1, 25.0, null, 171, 169}, // oil
        // latern
        //
        {new Item(1137), 18, 1, 1, 25.0, "Medium helm", 129, 155},
        {new Item(1153), 22, 1, 2, 50.0, "Full Helm", 130, 140},
        {new Item(1175), 23, 1, 2, 50.0, "Square shield", 131, 141},
        {new Item(1193), 42, 1, 3, 112.0, "Kiteshield", 132, 142},
        {new Item(4820), 19, 15, 1, 25, null, 173, 172}, // nails
        //
        {new Item(820, 25), 19, 10, 1, 25.0, "Dart tips", 134, 156},
        {new Item(40, 25), 20, 15, 1, 25.0, "Arrow tips", 135, 158},
        {new Item(863, 25), 22, 5, 1, 25.0, "Throwing knife", 133, 159}, {null, 0, 0, 0, 0, null, 123, 160}, // other
        {null, 0, 0, 0, 0, null, 162, 163}, // studs
        //
        {new Item(9377, 25), 18, 10, 1, 25.0, "Bolts", 178, 176}, {null, 76, 1, 1, 62.5, null, 175, 177}, // limbs
        {null, 0, 0, 0, 0, null, 181, 182},// grapple tips
    };

    public static final Object[][] STEEL = {{new Item(1207), 30, 1, 1, 37.0, "Dagger", 121, 153},
        {new Item(1281), 34, 1, 1, 37.0, "Sword", 112, 152},
        {new Item(1325), 35, 1, 2, 75.0, "Scimitar", 114, 143},
        {new Item(1295), 36, 1, 2, 75.0, "Longsword", 113, 116},
        {new Item(1311), 44, 1, 3, 112.0, "2 hand sword", 115, 117},
        //
        {new Item(1353), 31, 1, 1, 37.0, "Axe", 118, 154}, {new Item(1424), 32, 1, 1, 37.0, "Mace", 120, 157},
        {new Item(1339), 39, 1, 3, 112.0, "Warhammer", 111, 145},
        {new Item(1365), 40, 1, 3, 112.0, "Battleaxe", 119, 122},
        {new Item(3097), 43, 1, 2, 75.0, null, 165, 164}, // claws
        //
        {new Item(1105), 41, 1, 3, 112.0, "Chainbody", 125, 136},
        {new Item(1069), 46, 1, 3, 112.0, "Platelegs", 126, 137},
        {new Item(1083), 46, 1, 3, 112.0, "Plateskirt", 128, 138},
        {new Item(1119), 48, 1, 5, 187.0, "Platebody", 127, 139}, {null, 0, 0, 0, 0, null, 171, 169}, // oil
        // latern
        //
        {new Item(1141), 33, 1, 1, 37.0, "Medium helm", 129, 155},
        {new Item(1157), 37, 1, 2, 75.0, "Full Helm", 130, 140},
        {new Item(1177), 38, 1, 2, 75.0, "Square shield", 131, 141},
        {new Item(1193), 42, 1, 3, 112.0, "Kiteshield", 132, 142}, {null, 0, 0, 0, 0, null, 173, 172}, // nails
        //
        {new Item(821, 25), 34, 10, 1, 37.0, "Dart tips", 134, 156},
        {new Item(41, 25), 35, 15, 1, 37.0, "Arrow tips", 135, 158},
        {new Item(865, 25), 37, 5, 1, 37.0, "Throwing knife", 133, 159}, {null, 0, 0, 0, 0, null, 123, 160}, // other
        {null, 0, 0, 0, 0, null, 162, 163}, // studs
        //
        {new Item(9378, 25), 33, 10, 1, 37.0, "Bolts", 178, 176}, {null, 76, 1, 1, 62.5, null, 175, 177}, // limbs
        {null, 0, 0, 0, 0, null, 181, 182},// grapple tips
    };

    public static final Object[][] MITHRIL = {{new Item(1209), 50, 1, 1, 50.0, "Dagger", 121, 153},
        {new Item(1285), 54, 1, 1, 50.0, "Sword", 112, 152},
        {new Item(1329), 55, 1, 2, 100.0, "Scimitar", 114, 143},
        {new Item(1299), 56, 1, 2, 100.0, "Longsword", 113, 116},
        {new Item(1315), 64, 1, 3, 150.0, "2 hand sword", 115, 117},
        //
        {new Item(1355), 51, 1, 1, 50.0, "Axe", 118, 154}, {new Item(1428), 52, 1, 1, 50.0, "Mace", 120, 157},
        {new Item(1343), 59, 1, 3, 150.0, "Warhammer", 111, 145},
        {new Item(1369), 60, 1, 3, 150.0, "Battleaxe", 119, 122},
        {new Item(3099), 63, 1, 2, 100.0, null, 165, 164}, // claws
        //
        {new Item(1109), 61, 1, 3, 150.0, "Chainbody", 125, 136},
        {new Item(1071), 66, 1, 3, 150.0, "Platelegs", 126, 137},
        {new Item(1085), 66, 1, 3, 150.0, "Plateskirt", 128, 138},
        {new Item(1121), 68, 1, 5, 250.0, "Platebody", 127, 139}, {null, 0, 0, 0, 0, null, 171, 169}, // oil
        // latern
        //
        {new Item(1143), 53, 1, 1, 50.0, "Medium helm", 129, 155},
        {new Item(1159), 57, 1, 2, 100.0, "Full Helm", 130, 140},
        {new Item(1181), 58, 1, 2, 100.0, "Square shield", 131, 141},
        {new Item(1197), 62, 1, 3, 150.0, "Kiteshield", 132, 142}, {null, 0, 0, 0, 0, null, 173, 172}, // nails
        //
        {new Item(822, 25), 54, 10, 1, 50.0, "Dart tips", 134, 156},
        {new Item(42, 25), 55, 15, 1, 50.0, "Arrow tips", 135, 158},
        {new Item(866, 25), 57, 5, 1, 50.0, "Throwing knife", 133, 159}, {null, 0, 0, 0, 0, null, 123, 160}, // other
        {null, 0, 0, 0, 0, null, 162, 163}, // studs
        //
        {new Item(9379), 53, 10, 1, 50.0, "Bolts", 178, 176}, {null, 76, 1, 1, 62.5, null, 175, 177}, // limbs
        {null, 0, 0, 0, 0, null, 181, 182},// grapple tips
    };

    public static final Object[][] ADAMANT = {{new Item(1211), 70, 1, 1, 62.5, "Dagger", 121, 153},
        {new Item(1287), 74, 1, 1, 62.5, "Sword", 112, 152},
        {new Item(1331), 75, 1, 2, 125.0, "Scimitar", 114, 143},
        {new Item(1301), 76, 1, 2, 125.0, "Longsword", 113, 116},
        {new Item(1317), 84, 1, 3, 187.5, "2 hand sword", 115, 117},
        //
        {new Item(1357), 71, 1, 1, 62.5, "Axe", 118, 154}, {new Item(1430), 72, 1, 1, 62.5, "Mace", 120, 157},
        {new Item(1345), 79, 1, 3, 187.5, "Warhammer", 111, 145},
        {new Item(1371), 80, 1, 3, 187.5, "Battleaxe", 119, 122},
        {new Item(3100), 83, 1, 2, 125.0, null, 165, 164}, // claws
        //
        {new Item(1111), 81, 1, 3, 187.5, "Chainbody", 125, 136},
        {new Item(1073), 86, 1, 3, 187.5, "Platelegs", 126, 137},
        {new Item(1091), 86, 1, 3, 187.5, "Plateskirt", 128, 138},
        {new Item(1123), 88, 1, 5, 312.0, "Platebody", 127, 139}, {null, 0, 0, 0, 0, null, 171, 169}, // oil
        // latern
        //
        {new Item(1145), 73, 1, 1, 62.5, "Medium helm", 129, 155},
        {new Item(1161), 77, 1, 2, 125.0, "Full Helm", 130, 140},
        {new Item(1183), 78, 1, 2, 125.0, "Square shield", 131, 141},
        {new Item(1199), 82, 1, 3, 187.5, "Kiteshield", 132, 142}, {null, 0, 0, 0, 0, null, 173, 172}, // nails
        //
        {new Item(823, 25), 74, 10, 1, 62.5, "Dart tips", 134, 156},
        {new Item(43, 25), 75, 15, 1, 62.5, "Arrow tips", 135, 158},
        {new Item(867, 25), 77, 5, 1, 62.5, "Throwing knife", 133, 159}, {null, 0, 0, 0, 0, null, 123, 160}, // other
        {null, 0, 0, 0, 0, null, 162, 163}, // studs
        //
        {new Item(9380, 25), 73, 10, 1, 62.5, "Bolts", 178, 176}, {null, 76, 1, 1, 62.5, null, 175, 177}, // limbs
        {null, 0, 0, 0, 0, null, 181, 182},// grapple tips
    };

    public static final Object[][] RUNE = {{new Item(1213), 85, 1, 1, 75.0, "Dagger", 121, 153},
        {new Item(1289), 89, 1, 1, 75.0, "Sword", 112, 152},
        {new Item(1333), 90, 1, 2, 150.0, "Scimitar", 114, 143},
        {new Item(1303), 91, 1, 2, 150.0, "Longsword", 113, 116},
        {new Item(1319), 99, 1, 3, 225.0, "2 hand sword", 115, 117},
        //
        {new Item(1359), 86, 1, 1, 75.0, "Axe", 118, 154}, {new Item(1432), 87, 1, 1, 75.0, "Mace", 120, 157},
        {new Item(1347), 94, 1, 3, 225.0, "Warhammer", 111, 145},
        {new Item(1373), 95, 1, 3, 225.0, "Battleaxe", 119, 122}, {new Item(3101), 98, 1, 2, 150, null, 165, 164}, // claws
        //
        {new Item(1113), 96, 1, 3, 225.0, "Chainbody", 125, 136},
        {new Item(1079), 99, 1, 3, 225.0, "Platelegs", 126, 137},
        {new Item(1093), 99, 1, 3, 225.0, "Plateskirt", 128, 138},
        {new Item(1127), 99, 1, 5, 375.0, "Platebody", 127, 139}, {0, 0, 0, 0, 0, null, 171, 169}, // oil
        // latern
        //
        {new Item(1147), 88, 1, 1, 75.0, "Medium helm", 129, 155},
        {new Item(1163), 92, 1, 2, 150.0, "Full Helm", 130, 140},
        {new Item(1185), 93, 1, 2, 150.0, "Square shield", 131, 141},
        {new Item(1201), 97, 1, 3, 225.0, "Kiteshield", 132, 142}, {0, 0, 0, 0, 0, null, 173, 172}, // nails
        //
        {new Item(824, 25), 89, 10, 1, 75.0, "Dart tips", 134, 156},
        {new Item(44, 25), 90, 15, 1, 75.0, "Arrow tips", 135, 158},
        {new Item(868, 25), 92, 5, 1, 75.0, "Throwing knife", 133, 159}, {0, 0, 0, 0, 0, null, 123, 160}, // other
        {0, 0, 0, 0, 0, null, 162, 163}, // studs
        //
        {new Item(9381, 25), 88, 10, 1, 75.0, "Bolts", 178, 176},
        {new Item(9431), 91, 1, 1, 75.0, null, 175, 177}, // limbs
        {0, 0, 0, 0, 0, null, 181, 182},// grapple tips
    };
}
