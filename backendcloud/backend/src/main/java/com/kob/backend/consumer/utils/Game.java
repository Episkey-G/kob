package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; //playing -> finished
    private String loser = ""; // all:平局、A、B

    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    /**
     * 初始化游戏
     * @param rows
     * @param cols
     * @param inner_walls_count
     * @param idA
     * @param botA
     * @param idB
     * @param botB
     */
    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        // 取出botId和botCode
        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }

    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
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

    /**
     *  将当前的局面信息，编码成字符串
     * @param player
     * @return
     */
    private String getInput(Player player) {
        Player me, you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }
        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    /**
     * 判断是否要执行AI代码
     * @param player
     */
    private void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) return; // 亲自上阵，不需要执行代码
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);

    }
    /**
     * 一局游戏的主循环
     */
    private boolean nextStep() { //等待两名玩家下一步操作
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
    /**
     * 两个玩家都下完了一步，判断是否合法
     * @return
     */
    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if (g[cell.x][cell.y] == 1) return false;

        for (int i = 0; i < n - 1; i++) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }

        for (int i = 0; i < n - 1; i++) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }

        return true;
    }
    /**
     * 判断两条蛇操作是否合法
     */
    private void judge() {
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished";

            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }
    private void sendAllMessage(String message) { //向每一个人传递信息
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
    /**
     * 传递移动信息
     */
    private void sendMove() { // 向两个Client传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }

    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }
    /**
     * 更新用户的rating
     */
    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    /**
     * 保存游戏结果
     */
    private void saveToDayaBase() {
        // 更新rating
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if (loser.equals("A")) {
            ratingA -= 2;
            ratingB += 5;
        } else if (loser.equals("B")) {
            ratingA += 5;
            ratingB -= 2;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() { // 向两个Client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDayaBase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if (nextStep()) { //是否获取两条蛇等下一步操作
                judge();
                if (status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
