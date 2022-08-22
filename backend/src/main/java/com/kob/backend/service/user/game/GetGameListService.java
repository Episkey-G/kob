package com.kob.backend.service.user.game;

import com.kob.backend.pojo.Game;

import java.util.List;
import java.util.Map;

public interface GetGameListService {
    List<Game> getGameList();

    Map<String, String> getUser(Map<String, String> data);
}
