package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Bot implements java.util.function.Supplier<Integer> {
    static class Cell {
        public int x, y, g, h, f;
        public Cell parent;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Cell(int x, int y, int g, int h) {
            this(x, y);
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        public Cell(int x, int y, int g, int h, int f, Cell parent) {
            this(x, y, g, h);
            this.f = f;
            this.parent = parent;
        }
    }

    // Existing methods...
    private Integer nextMoveAvoidingObstacles(int[][] g, List<Cell> aCells, int[] dx, int[] dy) {
        for (int i = 0; i < 4; i++) {
            int x = aCells.get(aCells.size() - 1).x + dx[i];
            int y = aCells.get(aCells.size() - 1).y + dy[i];
            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
                return i;
            }
        }


        return 0;
    }
    private boolean isCellOccupiedByOtherSnake(int x, int y, List<Cell> bCells) {
        for (Cell c : bCells) {
            if (c.x == x && c.y == y) {
                return true;
            }
        }
        return false;
    }


    private int heuristic(Cell current, Cell target) {
        return Math.abs(current.x - target.x) + Math.abs(current.y - target.y);
    }

    private Integer nextMoveUsingAStar(int[][] g, List<Cell> aCells, List<Cell> bCells, int[] dx, int[] dy) {
        Cell start = aCells.get(aCells.size() - 1);
        Cell target = new Cell(6, 7); // Assume the target position is (6, 7), modify as needed

        PriorityQueue<Cell> openList = new PriorityQueue<>(Comparator.comparingInt(c -> c.f));
        boolean[][] visited = new boolean[13][14];

        openList.add(new Cell(start.x, start.y, 0, heuristic(start, target)));
        visited[start.x][start.y] = true;

        while (!openList.isEmpty()) {
            Cell current = openList.poll();

            if (current.x == target.x && current.y == target.y) {
                // Found the target, return the first step
                Cell firstMove = current;
                while (firstMove.parent != null && firstMove.parent.parent != null) {
                    firstMove = firstMove.parent;
                }

                for (int i = 0; i < 4; i++) {
                    if (firstMove.x - start.x == dx[i] && firstMove.y - start.y == dy[i]) {
                        return i;
                    }
                }
            }

            for (int i = 0; i < 4; i++) {
                int x = current.x + dx[i];
                int y = current.y + dy[i];

                if (x >= 0 && x < 13 && y >= 0 && y < 14 && !visited[x][y] && g[x][y] == 0) {
                    int gCost = current.g + 1;
                    int h = heuristic(new Cell(x, y), target);
                    int f = gCost + h;

                    Cell next = new Cell(x, y, gCost, h, f, current);
                    openList.add(next);
                    visited[x][y] = true;
                }
                if (isCellOccupiedByOtherSnake(x, y, bCells)) {
                    continue;
                }

            }
        }

        // If no path is found, use the avoiding obstacles strategy
        return nextMoveAvoidingObstacles(g, aCells, dx, dy);
    }

    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        int[][] g = new int[13][14];
        for (int i = 0, k = 0; i < 13; i++) {
            for (int j = 0; j < 14; j++, k++) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }

        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);

        List<Cell> aCells = getCells(aSx, aSy, strs[3]);
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);

        for (Cell c : aCells) g[c.x][c.y] = 1;
        for (Cell c : bCells) g[c.x][c.y] = 1;

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

        // Use A* algorithm to choose the next move
        return nextMoveUsingAStar(g, aCells, bCells, dx, dy);
    }
    // Your other methods like check_tail_increasing, getCells, etc.
    private boolean check_tail_increasing(int step) {
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells(int sx, int sy, String steps) {
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int i = 0; i < steps.length(); i++) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing(++step)) {
                res.remove(0);
            }
        }
        return res;
    }


    @Override
    public Integer get() {
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
