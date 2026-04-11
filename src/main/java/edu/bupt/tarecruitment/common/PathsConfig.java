package edu.bupt.tarecruitment.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathsConfig {
    public static final Path DATA_DIRECTORY = Paths.get("data");
    public static final Path CV_DIRECTORY = DATA_DIRECTORY.resolve("cvs");

    private PathsConfig() {
    }
}
