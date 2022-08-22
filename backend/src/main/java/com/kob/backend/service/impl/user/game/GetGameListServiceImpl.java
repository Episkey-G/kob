package com.kob.backend.service.impl.user.game;

import com.kob.backend.mapper.GameMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Game;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.game.GetGameListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetGameListServiceImpl implements GetGameListService {
    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Game> getGameList() {
        return gameMapper.selectList(null);
    }

    @Override
    public Map<String, String> getUser(Map<String, String> data) {

        int userId = Integer.parseInt(data.get("user_id"));
        User user = userMapper.selectById(userId);
        Map<String, String> map = new HashMap<>();
        map.put("username", user.getUsername());


        return map;
    }
}
