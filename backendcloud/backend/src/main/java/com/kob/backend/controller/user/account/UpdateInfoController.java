package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.UpdateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdateInfoController {
    @Autowired
    private UpdateInfoService updateInfoService;

    @PostMapping("/user/account/updateInfo/")
    public Map<String, String> updateInfo(@RequestParam Map<String, String> data) {
        return updateInfoService.updateUserInfo(data);
    }
}
