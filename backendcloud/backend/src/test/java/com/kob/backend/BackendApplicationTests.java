package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("llx226cc"));
        System.out.println(passwordEncoder.encode("root"));
        System.out.println(passwordEncoder.encode("root"));
    }

    // 测试上传文件
    @Test
    void uploading() {
        String resp = GitHubFileUtil.uploading("/Users/gurun/929203.jpeg", ".png", "测试提交");
        System.out.println("访问|下载地址 = " + resp);
    }

    // 查询所有文件的访问地址
    @Test
    void getFileAll()   {
        List<String> imagesUrlList = GitHubFileUtil.getFileAll("img");
        imagesUrlList.forEach(System.out::println);
    }

    // 替换文件
    @Test
    void updateFile()   {
        String sha = GitHubFileUtil.getSha("img/12.png");
        String url = GitHubFileUtil.updateFile("D:\\12.png", "替换文件", sha, "img/12.png");
        System.out.println("url = " + url);
    }

    // 删除文件
    @Test
    void delFile()   {
        String sha = GitHubFileUtil.getSha("img/12.png");
        Integer code = GitHubFileUtil.delFile("测试删除", sha, "img/12.png");
        System.out.println("结果状态 = " + code);
    }

    // 获取存储库自述文件README.md
    @Test
    void getReadme()   {
        String resp = GitHubFileUtil.getReadme();
        System.out.println("结果 = " + resp);
    }


}
