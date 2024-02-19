package org.cubewhy.mirror;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.cubewhy.mirror.entities.Config;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class AnyMirrorApplication {
    public static final File CONFIG_DIR = new File(System.getProperty("user.home"), ".cubewhy/mirror");
    public static final File MIRROR_BASE = new File(CONFIG_DIR, "files");

    private static final File configFile = new File(CONFIG_DIR, "config.json");
    public static final Config config = readConfig();

    @NotNull
    @Contract(" -> new")
    @SneakyThrows
    private static Config readConfig() {
        if (configFile.exists() && configFile.isFile()) {
            return new Gson().fromJson(FileUtils.readFileToString(configFile, StandardCharsets.UTF_8), Config.class);
        }
        Config cfg = new Config();
        FileUtils.writeStringToFile(configFile, new Gson().toJson(cfg), StandardCharsets.UTF_8);
        return cfg;
    }

    public static void main(String[] args) {
        SpringApplication.run(AnyMirrorApplication.class, args);
    }

}
