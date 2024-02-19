package org.cubewhy.mirror;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class AnyMirrorApplication {
    public static final File CONFIG_DIR = new File(System.getProperty("user.home"), ".cubewhy/mirror");
    public static final File MIRROR_BASE = new File(CONFIG_DIR, "files");

    public static void main(String[] args) {
        SpringApplication.run(AnyMirrorApplication.class, args);
    }

}
