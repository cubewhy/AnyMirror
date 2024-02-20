package org.cubewhy.mirror.entities;

import lombok.Data;

import java.util.*;

@Data
public class Config {
    Set<String> mirrors = new LinkedHashSet<>();
    Set<String> sync = new LinkedHashSet<>();

    {
        mirrors.addAll(List.of(new String[]{
                "textures.lunarclientcdn.com",
                "resources.download.minecraft.net",
                "piston-data.mojang.com",
                "piston-meta.mojang.com",
                "libraries.minecraft.net",
                "launchermeta.mojang.com" // sync
        }));

        sync.addAll(List.of(new String[] {
                "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json"
        }));
    }
}
