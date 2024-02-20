package org.cubewhy.mirror.controller;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URL;
import java.util.Objects;

import static org.cubewhy.mirror.AnyMirrorApplication.MIRROR_BASE;
import static org.cubewhy.mirror.AnyMirrorApplication.config;

@RestController
@Log4j2
public class MirrorController {
    @Resource
    OkHttpClient httpClient;

    @Scheduled(cron = "0 0 0 1/1 * ? ")
    public void sync() throws Exception {
        log.info("Start sync files");
        for (String s : config.getSync()) {
            File local = new File(MIRROR_BASE, s.replaceFirst("https://", "http://").replaceFirst("http://", ""));
            Request r = new Request.Builder()
                    .get()
                    .url(s).build();
            try (Response res = httpClient.newCall(r).execute()) {
                if (res.isSuccessful()) {
                    log.info("Synced " + local + " successfully");
                    FileUtils.writeByteArrayToFile(local, Objects.requireNonNull(res.body()).bytes());
                } else {
                    log.error("Synced " + local + " failed");
                }
            }
        }
        log.info("Sync finished");
    }

    @GetMapping("/mirror-list")
    public void mirrorList(HttpServletResponse response) throws Exception {
        response.getWriter().write(new Gson().toJson(config.getMirrors()));
    }

    @GetMapping("/**")
    public void mirror(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uri = request.getRequestURI().substring(1);
        if (uri.endsWith("favicon.ico")) return;
        String host;
        if (!(uri.startsWith("http://") || uri.startsWith("https://"))) {
            host = new URL(request.getRequestURL().toString()).getHost();
            uri = request.getRequestURL().toString();
        } else {
            host = new URL(uri).getHost();
        }
        if (!config.getMirrors().contains(host)) {
            response.setStatus(403);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("为防止镜像源被滥用, 已禁止访问, 若你是站长, 请在配置中将 " + host + " 加入白名单");
            return;
        }
        File local = new File(MIRROR_BASE, uri.replaceFirst("https://", "http://").replaceFirst("http://", ""));
        if (local.exists()) {
            log.info("Getting " + uri + ": File exists");
            IOUtils.copy(FileUtils.openInputStream(local), response.getOutputStream());
            return;
        }
        // cache the file
        log.info("Getting " + uri + ": File not found, caching");
        Request r = new Request.Builder()
                .get()
                .url(uri).build();
        try (Response res = httpClient.newCall(r).execute()) {
            if (res.isSuccessful()) {
                FileUtils.writeByteArrayToFile(local, Objects.requireNonNull(res.body()).bytes());
            } else {
                response.setStatus(res.code());
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("服务器未给出正确响应, Code为" + res.code());
                return;
            }
        }
        IOUtils.copy(FileUtils.openInputStream(local), response.getOutputStream());
    }
}
