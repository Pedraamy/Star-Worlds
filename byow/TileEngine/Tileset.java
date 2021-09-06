package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('♛', Color.white, Color.black, "YOU");
    public static final TETile WALL = new TETile('#', Color.CYAN, Color.darkGray,
            "WALL");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "FLOOR");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");

    public static final TETile STAR = new TETile('✰', Color.yellow, Color.black, "STAR");

    public static final TETile BLUE_PORTAL = new TETile('❂', Color.CYAN, Color.BLUE, "BLUE PORTAL");
    public static final TETile RED_PORTAL = new TETile('❂', Color.PINK, Color.RED, "RED PORTAL");
    public static final TETile GREEN_PORTAL = new TETile('❂', Color.GREEN, new Color(0, 100, 100), "GREEN PORTAL");

    public static final TETile ENEMY = new TETile('⅀', Color.ORANGE, Color.black, "ENEMY");

    public static final TETile ENEMY_PATH = new TETile('☢', Color.red, Color.black,
            "HAZARDOUS PATH");

    public static final TETile DEAD_CHAR = new TETile('♛', Color.white, Color.RED, "YOU DEAD");

    public static final TETile BALL = new TETile('◎', new Color(172, 25, 235), Color.black, "BALL");




}


