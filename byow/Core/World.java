package byow.Core;

import byow.TileEngine.*;
import edu.princeton.cs.introcs.StdDraw;


import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class World {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 48;
    public static final int[][] dirs = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    public long seed;
    private TETile[][] map;
    private TERenderer ter;
    private List<int[]> validSquares;
    private List<int[]> walls;
    private HashMap<int[], List<int[]>> roomWalls;
    private int[] currBorder;
    private Random rand;
    public int[] currAvatar;
    public int[] currEnemy;
    private TETile avatarTile;
    private TETile enemyTile;
    private ArrayDeque<int[]> enemyPath;
    private int[] enemyTarget;
    private List<int[]> coinsPlc;
    private TreeMap<int[], int[]> portalMap;
    private int stars;
    private int enemyStars;
    private String mouseTile;
    public boolean run;
    public int outcome;
    public boolean replay;

    public World(long seed, int verdad) {
        this.seed = seed;
        this.rand = new Random(seed);
        this.validSquares = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.roomWalls = new HashMap<>();
        this.stars = 0;
        this.enemyStars = 0;
        this.outcome = 0;
        this.run = true;
        if (verdad == 0){
            this.replay = false;
        }
        else {
            this.replay = true;
        }
        genBlankCanvas();
        int numRooms = RandomUtils.uniform(rand, 9, 13);
        for (int i = 0; i < numRooms; i++) {
            genRoom();
        }
        List<int[]> rooms = sortIntArrList(new ArrayList<>(roomWalls.keySet()));
        for (int[] c : rooms) {
            int n = RandomUtils.uniform(rand, 3, 6);
            for (int i = 0; i < n; i++) {
                genHallway(c);
            }
        }
        fixMap();
        redoValid();
        genPieces();
        this.enemyPath = enemyBFS();
        this.ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        drawEnemyPath();
    }

    public void renderWorld(){
        StdDraw.setFont();
        StdDraw.setPenColor(Color.WHITE);
        ter.renderFrame(map);
        drawHUD();
        StdDraw.show();
    }

    private void drawHUD(){
        if (replay){
            Font rep = new Font("Arial", Font.BOLD, 45);
            String replay = "REPLAY IN PROGRESS.";
            String wait = "WAIT.";
            StdDraw.setFont();
            StdDraw.setPenColor(Color.ORANGE);
            StdDraw.text(70, 47, replay);
            StdDraw.text(70, 45, wait);
            StdDraw.text(10, 47, replay);
            StdDraw.text(10, 45, wait);

        }
        String hud = String.format("Seed: %d    Stars: %d    Enemy Stars: %d    Tile Type: %s", seed, stars, enemyStars, mouseTile);
        StdDraw.setFont();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(40, 47, hud);
    }

    private void drawHUDCoin(){
        ter.renderFrame(map);
        drawHUD();
        StdDraw.setPenColor(Color.YELLOW);
        Font flash = new Font("Arial", Font.BOLD, 45);
        StdDraw.setFont(flash);
        StdDraw.text(40, 33, "Stars +1");
        StdDraw.setFont();
        StdDraw.show();
        StdDraw.pause(800);
        StdDraw.clear(StdDraw.BLACK);

    }

    private void drawBallResponse(String response){
        ter.renderFrame(map);
        drawHUD();
        StdDraw.setPenColor(new Color(172, 25, 235));
        Font flash = new Font("Arial", Font.BOLD, 45);
        StdDraw.setFont(flash);
        StdDraw.text(40, 40, response);
        StdDraw.setFont();
        StdDraw.show();
        StdDraw.pause(800);
        StdDraw.clear(StdDraw.BLACK);

    }

    private void drawHUDEnemyCoin(){
        ter.renderFrame(map);
        drawHUD();
        StdDraw.setPenColor(Color.RED);
        Font flash = new Font("Arial", Font.BOLD, 45);
        StdDraw.setFont(flash);
        StdDraw.text(40, 33, "Enemy Stars +1");
        StdDraw.setFont();
        StdDraw.show();
        StdDraw.pause(800);
        StdDraw.clear(StdDraw.BLACK);

    }

    public TETile[][] getMap() {
        return this.map;
    }

    public void move(char c) {
        int[] next = charToDir(c);
        if (next == null){
            return;
        }
        if (!containsSpot(next, validSquares) || portalMap.containsKey(next) || Arrays.equals(next, currEnemy)) {
            return;
        }
        setTile(currAvatar, avatarTile);
        avatarTile = getTileFromPos(next);
        setTile(next, Tileset.AVATAR);
        currAvatar = next;
        if (avatarTile.equals(Tileset.STAR)){
            avatarTile = Tileset.FLOOR;
            coinsPlc = removedItem(coinsPlc, next);
            stars += 1;
            drawHUDCoin();
            if (stars == 4){
                outcome = 0;
                run = false;
            }
        }
        else if (avatarTile.equals(Tileset.ENEMY_PATH)){ //make sure to change mountain back to enemypath
            setTile(currAvatar, Tileset.DEAD_CHAR);
            outcome = 2;
            run = false;
        }

    }

    public void fire(){

    }


    private void removeEnemyPath(){
        for (int[] c : validSquares){
            if (getTileFromPos(c).equals(Tileset.ENEMY_PATH)){
                setTile(c, Tileset.FLOOR);
            }
        }
    }

    public void getMouseTile(){
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (x >= 0 && x <= 84 && y >= 0 && y <= 47){
            mouseTile = getTileFromPos(new int[]{x, y}).description();
        }
    }



    private int[] charToDir(char c) {
        if (c == 'D') {
            return getNextDir(currAvatar, 0);
        } else if (c == 'W') {
            return getNextDir(currAvatar, 1);
        } else if (c == 'A') {
            return getNextDir(currAvatar, 2);
        } else if (c == 'S') {
            return getNextDir(currAvatar, 3);
        }
        return null;
    }

    public ArrayDeque<int[]> getEnemyBFS(){
        return this.enemyPath;
    }

    public void enemyMove(){
        if (!map[enemyTarget[0]][enemyTarget[1]].equals(Tileset.STAR)) {
            removeEnemyPath();
            enemyPath = enemyBFS();
            drawEnemyPath();
        }
        setTile(currEnemy, enemyTile);
        int[] next = enemyPath.poll();
        enemyTile = getTileFromPos(next);
        if (enemyTile.equals(Tileset.ENEMY_PATH)){
            enemyTile = Tileset.FLOOR;
        }
        setTile(next, Tileset.ENEMY);
        currEnemy = next;
        if (enemyPath.isEmpty()){
            enemyTile = Tileset.FLOOR;
            coinsPlc = removedItem(coinsPlc, next);
            drawHUDEnemyCoin();
            enemyStars += 1;
            if (enemyStars == 4){
                outcome = 1;
                run = false;
                return;
            }
            enemyPath = enemyBFS();
            drawEnemyPath();
            enemyMove();
        }

    }

    public void drawEnemyPath(){
        StdDraw.setFont();
        int i = 0;
        String s = "Enemy scanning. Please wait.";
        String curr = s;
        for (int[] c : enemyPath){
            if (getTileFromPos(c).equals(Tileset.FLOOR)){
                StdDraw.setFont();
                StdDraw.setPenColor(Color.WHITE);
                setTile(c, Tileset.ENEMY_PATH);
                ter.renderFrame(map);
                drawHUD();
                StdDraw.setPenColor(Color.RED);
                Font scan = new Font("Arial", Font.BOLD, 30);
                StdDraw.setFont(scan);
                StdDraw.text(40, 45, curr);
                StdDraw.show();
                StdDraw.pause(100);
                curr += '.';
                i += 1;
                if (i == 3){
                    curr = s;
                    i = 0;
                }
            }
            StdDraw.setFont();
        }
    }

    private ArrayDeque<int[]> enemyBFS(){
        ArrayDeque<ArrayDeque<int[]>> queue = new ArrayDeque<>();
        ArrayDeque<int[]> start = new ArrayDeque<>();
        start.add(currEnemy);
        queue.add(start);
        List<int[]> visited = new ArrayList<>();
        while (true) {
            ArrayDeque<int[]> sofar = queue.poll();
            int[] curr = sofar.peekLast();
            if (containsSpot(curr, coinsPlc)){
                enemyTarget = curr;
                return sofar;
            }
            for (int i = 0; i < 4; i++) {
                int[] next = getNextDir(curr, i);
                if (containsSpot(next, validSquares) && !containsSpot(next, visited) && !getTileFromPos(next).equals(Tileset.AVATAR)){
                    if (portalMap.containsKey(next)){
                        next = portalMap.get(next);
                    }
                    visited.add(next);
                    ArrayDeque<int[]> adder = cloneArrDeque(sofar);
                    adder.add(next);
                    queue.add(adder);
                }
            }
        }
    }

    public void throwBall(int n){
        int life = 0;
        List<int[]> path = new ArrayList<>();
        int[] next = getNextDir(currAvatar, n);
        String response;
        while (true){
            if (next[0] < 0 || next[0] > 79 || next[1] < 0 || next[1] > 44){
                response = "Missed!";
                break;
            }
            else if (getTileFromPos(next).equals(Tileset.ENEMY)){
                response = "Enemy blocked!";
                break;
            }
            else if (getTileFromPos(next).equals(Tileset.WALL)){
                life += 1;
                if (life == 4){
                    response = "Ball perished!";
                    break;
                }
            }
            else if (getTileFromPos(next).equals(Tileset.STAR)){
                response = "Star caught!";
                path.add(next);
                break;
            }
            path.add(next);
            next = getNextDir(next, n);
        }
        drawBallThrow(path);
        drawBallResponse(response);
        if (response.equals("Star caught!")){
            stars += 1;
            coinsPlc = removedItem(coinsPlc, path.get(path.size()-1));
            if (stars == 4){
                outcome = 0;
                run = false;
            }

        }

    }

    private void drawBallThrow(List<int[]> path){
        int[] prev = currAvatar;
        TETile prevT = Tileset.AVATAR;
        for (int[] c : path){
            setTile(prev, prevT);
            prev = c;
            prevT = getTileFromPos(c);
            setTile(c, Tileset.BALL);
            renderWorld();
            StdDraw.pause(100);
        }
        if (prevT.equals(Tileset.STAR)){
            setTile(prev, Tileset.FLOOR);
        }
        else {
            setTile(prev, prevT);
        }
        renderWorld();
        StdDraw.pause(100);
    }

    private void redoValid() {
        validSquares = new ArrayList<>();
        for (int col = 0; col < WIDTH; col++) {
            for (int row = 0; row < 45; row++) {
                int[] c = new int[]{col, row};
                if (getTileFromPos(c).equals(Tileset.FLOOR)) {
                    validSquares.add(c);
                }
            }
        }
    }

    private TETile getTileFromPos(int [] pos){
        return map[pos[0]][pos[1]];
    }

    private void setTile(int[] pos, TETile curr){
        map[pos[0]][pos[1]] = curr;
    }

    private void genBlankCanvas() {
        this.map = new TETile[WIDTH][HEIGHT];
        for (int col = 0; col < WIDTH; col += 1) {
            for (int row = 0; row < HEIGHT; row += 1) {
                map[col][row] = Tileset.NOTHING;
            }
        }
    }

    private void genRoom() {
        int x = RandomUtils.uniform(rand, 0, 74);
        int y = RandomUtils.uniform(rand, 0, 39);
        int rw = RandomUtils.uniform(rand, 6, Math.min(21, 80 - x));
        int rh = RandomUtils.uniform(rand, 6, Math.min(21, 45 - y));
        List<int[]> currWalls = new ArrayList<>();
        List<int[]> currSpaces = new ArrayList<>();
        for (int col = x; col <= x + rw; col++) {
            for (int row = y; row <= y + rh; row++) {
                if (!map[col][row].equals(Tileset.NOTHING)) {
                    genRoom();
                    return;
                }
                int[] curr = new int[]{col, row};
                if (row == y || col == x || row == y + rh || col == x + rw) {
                    currWalls.add(curr);
                } else {
                    currSpaces.add(curr);
                }
            }
        }
        roomWalls.put(new int[]{x - 1, x + rw + 1, y - 1, y + rh + 1}, currWalls);
        for (int[] c : currWalls) {
            map[c[0]][c[1]] = Tileset.WALL;
            walls.add(c);
        }
        for (int[] c : currSpaces) {
            map[c[0]][c[1]] = Tileset.FLOOR;
            validSquares.add(c);
        }

    }

    private void genHallway(int[] key) {
        currBorder = key;
        List<int[]> spots = getValidStarts(roomWalls.get(key));
        if (spots.size() == 0) {
            return;
        }
        int n = RandomUtils.uniform(rand, 0, spots.size());
        int[] curr = spots.get(n);
        int col = curr[0];
        int row = curr[1];
        int[] corr = new int[2];
        for (int i = 0; i < 4; i++) {
            int cc = col + dirs[i][0];
            int cr = row + dirs[i][1];
            if (map[cc][cr].equals(Tileset.FLOOR)) {
                corr = dirs[i];
                map[col][row] = Tileset.FLOOR;
                break;
            }
        }
        Stack<List<int[]>> start = new Stack<>();
        List<int[]> here = new ArrayList<>();
        here.add(new int[]{curr[0] + corr[0] * -1, curr[1] + corr[1] * -1});
        start.push(here);
        List<int[]> path;
        try {
            path = DFS(start, new ArrayList<>());
        } catch (StackOverflowError e) {
            path = new ArrayList<>();
        }

        if (path.size() > 0) {
            drawHallway(path);
            validSquares.add(new int[]{col, row});
        } else {
            map[col][row] = Tileset.WALL;
        }
    }

    private List<int[]> getValidStarts(List<int[]> lst) {
        List<int[]> spots = new ArrayList<>();
        for (int[] c : lst) {
            if (isValidStart(c)) {
                spots.add(c);
            }
        }
        return spots;
    }

    private boolean isValidStart(int[] curr) {
        int col = curr[0];
        int row = curr[1];
        if (col <= 1 || col >= 78 || row <= 1 || row >= 43) {
            return false;
        }
        if (map[col][row].equals(Tileset.FLOOR)) {
            return false;
        }
        int pos = 0;
        for (int i = 0; i < 4; i++) {
            int cc = col + dirs[i][0];
            int cr = row + dirs[i][1];
            if (map[cc][cr].equals(Tileset.FLOOR)) {
                pos = i % 2;
            }
        }
        if (pos == 0) {
            if (!map[col][row + 1].equals(Tileset.WALL) || !map[col][row - 1].equals(Tileset.WALL)) {
                return false;
            }
            if (map[col][row + 2].equals(Tileset.FLOOR) || map[col][row - 2].equals(Tileset.FLOOR)) {
                return false;
            }
        } else {
            if (!map[col + 1][row].equals(Tileset.WALL) || !map[col - 1][row].equals(Tileset.WALL)) {
                return false;
            }
            if (map[col + 2][row].equals(Tileset.FLOOR) || map[col - 2][row].equals(Tileset.FLOOR)) {
                return false;
            }
        }
        return true;
    }

    private List<int[]> DFS(Stack<List<int[]>> sofar, List<int[]> visited) {
        List<int[]> currFar = sofar.pop();
        int[] curr = currFar.get(currFar.size() - 1);
        int col = curr[0];
        int row = curr[1];
        if (containsSpot(curr, visited)) {
            if (sofar.isEmpty()) {
                return new ArrayList<>();
            }
            return DFS(sofar, visited);
        }
        if (map[col][row].equals(Tileset.WALL)) {
            return currFar;
        }
        visited.add(curr);
        for (int i = 0; i < 4; i++) {
            int[] next = getNextDir(curr, i);
            col = next[0];
            row = next[1];
            if (isValidNext(curr, col, row)) {
                List<int[]> adder = cloneArrList(currFar);
                adder.add(next);
                sofar.push(adder);
            }
        }
        if (sofar.isEmpty()) {
            return new ArrayList<>();
        }
        return DFS(sofar, visited);
    }


    private int[] getNextDir(int[] curr, int n) {
        int[] ans = new int[2];
        ans[0] = curr[0] + dirs[n][0];
        ans[1] = curr[1] + dirs[n][1];
        return ans;
    }

    private boolean isValidNext(int[] curr, int col, int row) {
        int[] wall1;
        int[] wall2;
        if (map[col][row].equals(Tileset.WALL)) {
            return true;
        }
        if (col <= 0 || col >= 79 || row <= 0 || row >= 44) {
            return false;
        }
        if (col >= currBorder[0] && col <= currBorder[1] && row >= currBorder[2] && row <= currBorder[3]) {
            return false;
        }
        if (col == curr[0]) {
            wall1 = new int[]{col + 1, row};
            wall2 = new int[]{col - 1, row};
        } else {
            wall1 = new int[]{col, row + 1};
            wall2 = new int[]{col, row - 1};
        }
        if (!map[wall1[0]][wall1[1]].equals(Tileset.NOTHING) || !map[wall2[0]][wall2[1]].equals(Tileset.NOTHING)) {
            return false;
        }
        return true;
    }

    private void drawHallway(List<int[]> path) {
        for (int[] c : path) {
            map[c[0]][c[1]] = Tileset.FLOOR;
            validSquares.add(new int[]{c[0], c[1]});
        }
        for (int[] c : path) {
            drawIfNothing(c[0], c[1]);
        }
    }

    private void drawIfNothing(int col, int row) {
        for (int[] c : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}}) {
            int cc = c[0] + col;
            int cr = c[1] + row;
            if (map[cc][cr].equals(Tileset.NOTHING)) {
                map[cc][cr] = Tileset.WALL;
            }
        }
    }

    private void fixMap(){
        for (int col = 0; col<WIDTH; col++){
            for (int row = 0; row<45; row++){
                int[] c = new int[] {col, row};
                if (getTileFromPos(c).equals(Tileset.FLOOR)){
                    fixIso(c);
                    fixCorners(c);
                }
            }
        }
    }

    private void fixCorners(int[] c){
        int[] tl = new int[] {c[0]-1, c[1]+1};
        int[] tr = new int[] {c[0]+1, c[1]+1};
        int[] t = new int[] {c[0], c[1]+1};
        int[] l = new int[] {c[0]-1, c[1]};
        int[] r = new int[] {c[0]+1, c[1]};
        if (getTileFromPos(t).equals(Tileset.FLOOR)){
            return;
        }
        int[] nw;
        int[] changed = new int[] {99, 99};
        if (containsSpot(tl, validSquares) && getTileFromPos(l).equals(Tileset.WALL)){
            if (c[0] < 25){
                setTile(t, Tileset.FLOOR);
                nw = new int[] {t[0]+1, t[1]+1};
                setTile(nw, Tileset.WALL);
                changed = t;
            }
            else {
                setTile(l, Tileset.FLOOR);
                nw = new int[] {l[0]-1, l[1]-1};
                setTile(nw, Tileset.WALL);
                changed = l;
            }
        }
        else if (containsSpot(tr, validSquares) && getTileFromPos(r).equals(Tileset.WALL)){
            if (c[0] < 25){
                setTile(t, Tileset.FLOOR);
                nw = new int[] {t[0]-1, t[1]+1};
                setTile(nw, Tileset.WALL);
                changed = t;
            }
            else {
                setTile(r, Tileset.FLOOR);
                nw = new int[] {r[0]+1, r[1]-1};
                setTile(nw, Tileset.WALL);
                changed = r;
            }

        }
        if (changed[0] != 99) {
            validSquares.add(changed);
        }
    }


    private void fixIso(int[] curr){
        for (int[] c : dirs){
            int col = curr[0] + c[0];
            int row = curr[1] + c[1];
            if (!map[col][row].equals(Tileset.WALL)){
                return;
            }
        }
        map[curr[0]][curr[1]] = Tileset.WALL;
        validSquares = removedItem(validSquares, curr);
    }

    private void genPieces(){
        this.coinsPlc = new ArrayList<>();
        this.portalMap = new TreeMap<>(Arrays::compare);
        List<int[]> placed = new ArrayList<>();
        int[] plc;
        int[][] currPortals = new int[2][];
        int n;
        int leng = validSquares.size();
        for (int i = 0; i<7; i++){
            n = RandomUtils.uniform(rand, 0, leng);
            plc = validSquares.get(n);
            while (containsSpot(plc, placed)){
                n = RandomUtils.uniform(rand, 0, leng);
                plc = validSquares.get(n);
            }
            placed.add(plc);
            coinsPlc.add(plc);
            map[plc[0]][plc[1]] = Tileset.STAR;
        }
        for (int i = 0; i<2; i++){
            n = RandomUtils.uniform(rand, 0, leng);
            plc = validSquares.get(n);
            while (containsSpot(plc, placed)){
                n = RandomUtils.uniform(rand, 0, leng);
                plc = validSquares.get(n);
            }
            placed.add(plc);
            currPortals[i] = plc;
            map[plc[0]][plc[1]] = Tileset.BLUE_PORTAL;
        }
        portalMap.put(currPortals[0], currPortals[1]);
        portalMap.put(currPortals[1], currPortals[0]);
        for (int i = 0; i<2; i++){
            n = RandomUtils.uniform(rand, 0, leng);
            plc = validSquares.get(n);
            while (containsSpot(plc, placed)){
                n = RandomUtils.uniform(rand, 0, leng);
                plc = validSquares.get(n);
            }
            placed.add(plc);
            currPortals[i] = plc;
            map[plc[0]][plc[1]] = Tileset.RED_PORTAL;
        }
        portalMap.put(currPortals[0], currPortals[1]);
        portalMap.put(currPortals[1], currPortals[0]);
        for (int i = 0; i<2; i++){
            n = RandomUtils.uniform(rand, 0, leng);
            plc = validSquares.get(n);
            while (containsSpot(plc, placed)){
                n = RandomUtils.uniform(rand, 0, leng);
                plc = validSquares.get(n);
            }
            placed.add(plc);
            currPortals[i] = plc;
            map[plc[0]][plc[1]] = Tileset.GREEN_PORTAL;
        }
        portalMap.put(currPortals[0], currPortals[1]);
        portalMap.put(currPortals[1], currPortals[0]);
        for (int i = 0; i<1; i++){
            n = RandomUtils.uniform(rand, 0, leng);
            plc = validSquares.get(n);
            while (containsSpot(plc, placed)){
                n = RandomUtils.uniform(rand, 0, leng);
                plc = validSquares.get(n);
            }
            placed.add(plc);
            map[plc[0]][plc[1]] = Tileset.AVATAR;
            currAvatar = plc;
            avatarTile = Tileset.FLOOR;
        }
        for (int i = 0; i<1; i++){
            n = RandomUtils.uniform(rand, 0, leng);
            plc = validSquares.get(n);
            while (containsSpot(plc, placed)){
                n = RandomUtils.uniform(rand, 0, leng);
                plc = validSquares.get(n);
            }
            placed.add(plc);
            map[plc[0]][plc[1]] = Tileset.ENEMY;
            currEnemy = plc;
            enemyTile = Tileset.FLOOR;
        }
    }



    private boolean containsSpot(int [] curr, List<int[]> lst){
        for (int[] c : lst){
            if (curr[0] == c[0] && curr[1] == c[1]){
                return true;
            }
        }
        return false;
    }

    private List<int[]> removedItem(List<int[]> lst, int[] item){
        List<int[]> ans = new ArrayList<>();
        for (int[] c : lst){
            if (c[0] == item[0] && c[1] == item[1]){
                continue;
            }
            ans.add(c);
        }
        return ans;
    }

    private List<int[]> cloneArrList(List<int[]> lst){
        List<int[]> ans = new ArrayList<>();
        for (int[] c : lst){
            ans.add(c);
        }
        return ans;
    }

    private ArrayDeque<int[]> cloneArrDeque(ArrayDeque<int[]> lst){
        ArrayDeque<int[]> ans = new ArrayDeque<>();
        for (int[] c : lst){
            ans.add(c);
        }
        return ans;
    }

    private List<int[]> sortIntArrList(List<int[]> lst){
        lst.sort(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                for (int i = 0; i<4; i++){
                    if (o1[i] == o2[i]){
                        continue;
                    }
                    else {
                        return o1[i] - o2[i];
                    }
                }
                return 0;
            }
        });
        return lst;
    }
}





