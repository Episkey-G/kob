package com.kob.backend.consumer.utils;

import java.util.Random;

public class Game {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    public Game(Integer rows, Integer cols, Integer inner_walls_count) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
    }

    public int[][] getG() {
        return g;
    }

    //Flood Fill算法判断是否连通
    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for (int i = 0; i < 4; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (this.check_connectivity(x, y, tx, ty)){
                    g[sx][sy] = 0 ;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() { //画地图
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                g[i][j] = 0;
            }
        }

        //左右两边加上墙
        for (int r = 0; r < this.rows; r++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        //上下两行加上墙
        for (int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        //随机生成轴对称的墙,不重复
        for (int i = 0; i < this.inner_walls_count / 2; i++) {
            int r = random.nextInt(this.rows);
            int c = random.nextInt(this.cols);
            if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) {
                i--;
                continue;
            }
            // 不覆盖到左下角和右上角
            if (r == this.rows - 2 && c == 1 || c == this.cols - 2 && r == 1) {
                i--;
                continue;
            }
            g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        //随机1000次
        for (int i = 0; i < 1000; i++) {
            if (this.draw()) {
                break;
            }
        }
    }
}
