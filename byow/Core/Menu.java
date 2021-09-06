package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Menu {
    private TETile[][] screen;
    private TERenderer ter;


    public Menu(){
        genBlankScreen();
        this.ter = new TERenderer();
        ter.initialize(World.WIDTH, World.HEIGHT);
    }

    public void mainMenu() {
        ter.renderFrame(screen);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.YELLOW);

        Font title = new Font("Arial", Font.BOLD, 55);
        StdDraw.setFont(title);
        StdDraw.text(40, 35, "Star Worlds");
        StdDraw.setPenColor(Color.WHITE);
        Font desc = new Font("Arial", Font.PLAIN, 20);
        StdDraw.setFont(desc);
        StdDraw.text(40, 33, "Collect more stars than the enemy to win.");
        Font options = new Font("Arial", Font.PLAIN, 32);
        StdDraw.setFont(options);
        StdDraw.text(40, 22, "New Game (N)");
        StdDraw.text(40, 18, "Load Game (L)");
        StdDraw.text(40, 14, "Rules (R)");
        StdDraw.text(40, 10, "Quit (Q)");

        StdDraw.show();
    }

    public long enterSeed() {
        ter.renderFrame(screen);
        StdDraw.clear(Color.BLACK);
        Font seed = new Font("Arial", Font.BOLD, 45);
        Font input = new Font("Arial", Font.BOLD, 35);
        String s = "";
        StdDraw.setFont(seed);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(40, 37, "Enter seed:");
        StdDraw.setFont(input);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(40, 22, s);
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (Character.isDigit(c)) {
                    s += c;
                }
                else if (c == 'S' && s.length()>0){
                    return Long.parseLong(s);
                }
                StdDraw.clear(Color.BLACK);
                StdDraw.setFont(seed);
                StdDraw.setPenColor(Color.YELLOW);
                StdDraw.text(40, 37, "Enter seed:");
                StdDraw.setFont(input);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(40, 22, s);
                StdDraw.show();
            }

        }
    }

    public void rules() {
        ter.renderFrame(screen);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.YELLOW);

        Font title = new Font("Arial", Font.BOLD, 45);
        StdDraw.setFont(title);
        StdDraw.text(40, 40, "Rules");
        StdDraw.setPenColor(Color.WHITE);
        Font rules = new Font("Arial", Font.PLAIN, 22);
        StdDraw.setFont(rules);
        StdDraw.text(40, 30, "1. There is an odd number of stars. You must collect more than the enemy to win.");
        StdDraw.text(40, 27, "2. The enemy always finds the shortest path to a star. It has access to portals, you do not.");
        StdDraw.text(40, 24, "3. To compensate for portals, you can throw balls to catch the star. The balls go infinitely in any direction you throw them.");
        StdDraw.text(40, 21, "4. Balls will cease under 3 conditions: if they go out of bounds, if they go through more than 3 walls, or if they hit an enemy.");
        StdDraw.text(40, 18, "5. Enemy will scan and create a hazardous path in its wake as it looks for the nearest path. If you step on this path, you will perish.");
        StdDraw.text(40, 15, "6. Control your character with WASD keys and throw balls in each direction with IJKL keys.");
        StdDraw.text(40, 12, "7. There are many cool animations and texts to enjoy! Make sure not to press any key during such events.");
        Font back = new Font("Arial", Font.BOLD, 25);
        StdDraw.setFont(back);
        StdDraw.text(40, 5, "Press (B) to go back");
        StdDraw.show();

        while (true){
            if (StdDraw.hasNextKeyTyped()){
                if (Character.toUpperCase(StdDraw.nextKeyTyped()) == 'B'){
                    return;
                }
            }
        }
    }

    public void winScreen(){
        genBlankScreen();
        ter.renderFrame(screen);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.YELLOW);
        Font title = new Font("Arial", Font.BOLD, 55);
        StdDraw.setFont(title);
        StdDraw.text(40, 35, "Congratulations! You Win!");
        title = new Font("Arial", Font.PLAIN, 25);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(40, 25, "You managed to collect more stars than the enemy! (Window will close shortly)");
        Font back = new Font("Arial", Font.BOLD, 25);
        StdDraw.setFont(back);
        StdDraw.text(40, 9, "Press (R) to play seed again");
        StdDraw.text(40, 6, "Press (Q) to quit");
        StdDraw.show();
    }

    public void loseScreen1() {
        genBlankScreen();
        ter.renderFrame(screen);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.RED);
        Font title = new Font("Arial", Font.BOLD, 55);
        StdDraw.setFont(title);
        StdDraw.text(40, 35, "You lose!");
        title = new Font("Arial", Font.PLAIN, 25);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(40, 25, "Enemy collected more stars than you. (Window will close shortly)");
        Font back = new Font("Arial", Font.BOLD, 25);
        StdDraw.setFont(back);
        StdDraw.text(40, 9, "Press (R) to play seed again");
        StdDraw.text(40, 6, "Press (Q) to quit");
        StdDraw.show();
    }

    public void loseScreen2(){
        Font title = new Font("Arial", Font.BOLD, 50);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.RED);
        StdDraw.text(40, 32, "You have perished!");
        title = new Font("Arial", Font.PLAIN, 30);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(40, 22, "Impeding on the hazardous path will kill you. (Window will close shortly)");
        Font back = new Font("Arial", Font.BOLD, 25);
        StdDraw.setFont(back);
        StdDraw.text(40, 9, "Press (R) to play seed again");
        StdDraw.text(40, 6, "Press (Q) to quit");
        StdDraw.show();

    }

    public void loadError(){
        genBlankScreen();
        ter.renderFrame(screen);
        StdDraw.clear(Color.BLACK);

        Font title = new Font("Monoco", Font.BOLD, 35);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.text(45, 25, "Error! No previous game saved.");
        title = new Font("Monoco", Font.PLAIN, 20);
        StdDraw.setFont(title);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(45, 15, "(Window will close shortly)");
        StdDraw.show();
        StdDraw.pause(6000);
        System.exit(0);
    }


    private void genBlankScreen(){
        this.screen = new TETile[World.WIDTH][World.HEIGHT];
        for (int col = 0; col < World.WIDTH; col += 1) {
            for (int row = 0; row < World.HEIGHT; row += 1) {
                screen[col][row] = Tileset.NOTHING;
            }
        }
    }

}
