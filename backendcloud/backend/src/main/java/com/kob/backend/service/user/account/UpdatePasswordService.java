package com.kob.backend.service.user.account;

import java.util.Map;

public interface UpdatePasswordService {
    Map<String, String> updatePassword(String oldPassword, String newPassword, String confirmPassword);
}
