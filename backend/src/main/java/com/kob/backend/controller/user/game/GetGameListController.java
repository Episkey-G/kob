package com.kob.backend.controller.user.game;

import com.kob.backend.pojo.Game;
import com.kob.backend.service.user.game.GetGameListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GetGameListController {

    @Autowired
    private GetGameListService getGameListService;

    @GetMapping("/user/game/getGameList/")
    public List<Game> getList() {
        return getGameListService.getGameList();
    }

    @GetMapping("/user/account/username/")
    public Map<String, String> getUser(@RequestParam Map<String, String> data) {
        return getGameListService.getUser(data);
    }
}
