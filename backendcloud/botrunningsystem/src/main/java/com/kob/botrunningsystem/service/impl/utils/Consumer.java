package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
/**
 * 用于处理bot的代码
 * 通过反射调用bot的代码
 * 通过restTemplate调用pk系统的接口
 * 通过继承Thread类，添加超时机制
 */
@Component
public class Consumer extends Thread{

    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";
    /**
     * 通过setter注入restTemplate
     * @param restTemplate
     */
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }
    /**
     * 重写start方法，添加超时机制
     * @param timeout
     * @param bot
     */
    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start();

        try {
            this.join(timeout); // 最多等待timeout秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt(); // 终止当前线程
        }
    }

    /**
     *  在code中的Bot类名后添加uid
     * @param code
     * @param uid
     * @return
     */
    private String addUid(String code, String uid) {
        int k = code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterface");
        return code.substring(0, k) + uid + code.substring(k);
    }

    /**
     * 通过反射调用用户的bot类
     * 通过restTemplate调用matchingsystem的接口
     * 传递参数：user_id, direction
     */
    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);
        BotInterface botInterface = Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        Integer direction = botInterface.nextMove(bot.getInput());

        System.out.println("move-direction: " + bot.getUserId() + " " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());

        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
