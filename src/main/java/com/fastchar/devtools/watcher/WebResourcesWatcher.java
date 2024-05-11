package com.fastchar.devtools.watcher;

import java.io.File;

public class WebResourcesWatcher {


    /**
     * 源路径
     */
    private String sourcePath;


    /**
     * 编译路径
     */
    private String compilerPath;


    public File toCompilerFile(String sourceAbsolutePath) {
        return new File(compilerPath, sourceAbsolutePath.replace(this.sourcePath, ""));
    }


    public String getSourcePath() {
        return sourcePath;
    }

    public WebResourcesWatcher setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
        return this;
    }

    public String getCompilerPath() {
        return compilerPath;
    }

    public WebResourcesWatcher setCompilerPath(String compilerPath) {
        this.compilerPath = compilerPath;
        return this;
    }
}
