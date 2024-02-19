package org.cubewhy.mirror.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Objects;

import static org.cubewhy.mirror.AnyMirrorApplication.MIRROR_BASE;

@RestController
public class MirrorController {
    @Resource
    OkHttpClient httpClient;

    @GetMapping("/{name}/**")
    public void mirror(@PathVariable String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
        File local = new File(MIRROR_BASE, name + "/" + request.getRequestURI());
        String baseURL = ""; // TODO
        if (local.exists()) {
            IOUtils.copy(FileUtils.openInputStream(local), response.getOutputStream());
            return;
        }
        // cache the file
        Request r = new Request.Builder()
                .get()
                .url(baseURL + request.getRequestURI()).build();
        try (Response res = httpClient.newCall(r).execute()) {
            FileUtils.writeByteArrayToFile(local, Objects.requireNonNull(res.body()).bytes());
        }
        IOUtils.copy(FileUtils.openInputStream(local), response.getOutputStream());
    }
}
