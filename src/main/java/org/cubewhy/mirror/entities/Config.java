package org.cubewhy.mirror.entities;

import lombok.Data;

import java.util.*;

@Data
public class Config {
    Set<String> mirrors = new LinkedHashSet<>();

    {
        mirrors.addAll(List.of(new String[]{
                "textures.lunarclientcdn.com"
        }));
    }
}
