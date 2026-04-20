import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
// pacman class
class Pacman {
    int x, y;
    Pacman(int x, int y) {
        this.x = x;
        this.y = y;
    }
    void move(int dx, int dy, GameBoard board) {
        int newX = x + dx;
        int newY = y + dy;

        if (!board.isValidMove(newX, newY)) {
            System.out.println("You can't move there!");
            return;
        }
        x = newX;
        y = newY;
        if (board.hasFood(x, y)) {
            board.collectFoodAt(x, y);
            System.out.println("Food collected! Score = " + board.getScore());
        }
    }
}

// ghost class
class Ghost {
    int x, y;
    Ghost(int x, int y) {
        this.x = x;
        this.y = y;
    }
    void move(GameBoard board, Random rand) {
        int newX = x;
        int newY = y;
        int dir = rand.nextInt(4);
        if (dir == 0) newY--;
        else if (dir == 1) newY++;
        else if (dir == 2) newX--;
        else if (dir == 3) newX++;

        if (board.isValidMove(newX, newY)) {
            x = newX;
            y = newY;
        }
    }
}
// food class
class Food {
    boolean[][] grid;
    Food(int size) {
        grid = new boolean[size][size];
    }
    void initialize(int px, int py, List<Ghost> ghosts, int[][] walls) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {

                boolean blocked = (r == px && c == py);
                for (Ghost g : ghosts) {
                    if (r == g.x && c == g.y) blocked = true;
                }
                for (int[] w : walls) {
                    if (r == w[0] && c == w[1]) blocked = true;
                }
                if (!blocked) grid[r][c] = true;
            }
        }
    }
    boolean hasFood(int x, int y) {
        return grid[x][y];
    }
    void eat(int x, int y) {
        grid[x][y] = false;
    }
    boolean allEaten() {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++)
                if (grid[i][j]) return false;
        return true;
    }
}
// gameboard class
class GameBoard {
    static final int SIZE = 5;
    Pacman pacman;
    List<Ghost> ghosts;
    Food food;

    int[][] walls = {{2, 1}, {0, 3}};
    private int score = 0;

    GameBoard(Pacman pacman, List<Ghost> ghosts, Food food) {
        this.pacman = pacman;
        this.ghosts = ghosts;
        this.food = food;

        food.initialize(pacman.x, pacman.y, ghosts, walls);
    }
    public int getScore() {
        return score;
    }
    boolean isValidMove(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) return false;

        for (int[] w : walls) {
            if (x == w[0] && y == w[1]) {
                System.out.println("Hit a wall!");
                return false;
            }
        }
        return true;
    }
    boolean hasFood(int x, int y) {
        return food.hasFood(x, y);
    }
    void collectFoodAt(int x, int y) {
        if (food.hasFood(x, y)) {
            food.eat(x, y);
            score++;
        }
    }
    boolean checkCollision() {
        for (Ghost g : ghosts) {
            if (g.x == pacman.x && g.y == pacman.y) {
                return true;
            }
        }
        return false;
    }
    boolean allFoodEaten() {
        return food.allEaten();
    }
    void printGrid() {

        System.out.println("\nGRID:");
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {

                if (x == pacman.x && y == pacman.y)
                    System.out.printf("%-3s", "😋");
                else if (isGhost(x, y))
                    System.out.printf("%-3s", "👻");
                else if (isWall(x, y))
                    System.out.printf("%-3s", "🧱");
                else if (food.hasFood(x, y))
                    System.out.printf("%-3s", "•");
                else
                    System.out.printf("%-3s", "-");

            }
            System.out.println();
        }
    }
    boolean isGhost(int x, int y) {
        for (Ghost g : ghosts) {
            if (g.x == x && g.y == y) return true;
        }
        return false;
    }

    boolean isWall(int x, int y) {
        for (int[] w : walls) {
            if (x == w[0] && y == w[1]) return true;
        }
        return false;
    }
}
public class PacmanGame {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        Random rand = new Random();
        Pacman pacman = new Pacman(0, 0);

        List<Ghost> ghosts = new ArrayList<>();
        ghosts.add(new Ghost(4, 4));
        ghosts.add(new Ghost(4, 0));
        Food food = new Food(5);
        GameBoard board = new GameBoard(pacman, ghosts, food);
        System.out.println("Welcome to PAC-MAN GAME");
        while (true) {

            board.printGrid();
            System.out.print("Move (^, v, <, >): ");
            char move = input.next().charAt(0);
            int dx = 0, dy = 0;
            if (move == '^') dy = -1;
            else if (move == 'v') dy = 1;
            else if (move == '>') dx = 1;
            else if (move == '<') dx = -1;
            board.pacman.move(dx, dy, board);

            for (Ghost g : board.ghosts) {
                g.move(board, rand);
            }
            if (board.checkCollision()) {
                System.out.println("Oh no! Ghost caught you. GAME OVER!");
                break;
            }
            if (board.allFoodEaten()) {
                System.out.println("YOU WIN!");
                break;
            }
        }
        input.close();
    }
}