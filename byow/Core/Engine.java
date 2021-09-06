package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Engine {
    private World world;
    private String sofar;
    private Menu menu;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File lastSave = Utils.join(CWD, "lastSave.txt");

    public Engine() {
        this.sofar = "";
        this.menu = new Menu();
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        startMenu();


    }

    public static TETile[][] interactWithInputString(String input) {
        return null;
    }

    public void startMenu(){
        menu.mainMenu();
        while (true){
            if (StdDraw.hasNextKeyTyped()){
                char c = getNextKey();
                if (c == 'N'){
                    long seed = menu.enterSeed();
                    createWorld(seed, 0);
                    playGame();
                    return;
                }
                else if (c == 'L'){
                    load();
                    return;
                }
                else if (c == 'R'){
                    menu.rules();
                    startMenu();
                    return;
                }
                else if (c == 'Q'){
                    saveAndQuit();
                    return;
                }
            }
        }
    }

    public char getNextKey() {
        char c = Character.toUpperCase(StdDraw.nextKeyTyped());
        if (c == ':') {
            while (true){
                if (StdDraw.hasNextKeyTyped()){
                    if (Character.toUpperCase(StdDraw.nextKeyTyped()) == 'Q'){
                        return '$';
                    }
                    else {
                        return c;
                    }
                }
            }
        }
        return c;
    }

    public void saveAndQuit() {
        Utils.writeContents(lastSave, sofar);
        System.exit(0);
    }


    public void load() {
        if (!lastSave.exists()){
            menu.loadError();
        }
        else{
            String rep = Utils.readContentsAsString(lastSave);
            if (rep.length() == 0){
                menu.loadError();
            }
            replay(rep);
        }
    }

    public void replay(String rep){
        sofar += rep;
        int i = 0;
        while (Character.isDigit(rep.charAt(i))){
            i++;
        }
        long seed = Long.parseLong(rep.substring(0, i));
        createWorld(seed, 1);
        playReplay(rep.substring(i));


    }

    public void playReplay(String rep){
        world.replay = true;
        world.renderWorld();
        for(int i = 0; i < rep.length(); i++){
            char c = rep.charAt(i);
            world.enemyMove();
            if (c == 'L'){
                world.throwBall(0);
            }
            else if (c == 'I'){
                world.throwBall(1);
            }
            else if (c == 'J'){
                world.throwBall(2);
            }
            else if (c == 'K'){
                world.throwBall(3);
            }
            else{
                world.move(c);
            }
            world.renderWorld();
            StdDraw.pause(100);
        }
        world.replay = false;
        playGame();
    }


    public void createWorld(long seed, int verdad){
        sofar += String.valueOf(seed);
        world = new World(seed, verdad);
    }

    public void retry(){
        createWorld(world.seed, 0);
        playGame();
    }

    public void playGame() {
        world.renderWorld();
        while (world.run) {
            world.getMouseTile();
            if (StdDraw.hasNextKeyTyped()) {
                char c = getNextKey();
                if (c == '$') {
                    saveAndQuit();
                    return;
                } else {
                    sofar += c;
                }
                world.enemyMove();
                if (c == 'L'){
                    world.throwBall(0);
                }
                else if (c == 'I'){
                    world.throwBall(1);
                }
                else if (c == 'J'){
                    world.throwBall(2);
                }
                else if (c == 'K'){
                    world.throwBall(3);
                }
                else{
                    world.move(c);
                }
            }
            world.renderWorld();
        }
        if (world.outcome == 0) {
            menu.winScreen();
            trueQuitLoop();
        } else if (world.outcome == 1) {
            menu.loseScreen1();
            trueQuitLoop();
        } else {
            world.renderWorld();
            menu.loseScreen2();
            trueQuitLoop();
        }
    }

    public void trueQuitLoop(){
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (c == 'R') {
                    retry();
                    return;
                } else if (c == 'Q') {
                    System.exit(0);
                }
            }
        }
    }
}

