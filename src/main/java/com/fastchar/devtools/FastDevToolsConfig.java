package com.fastchar.devtools;

import com.fastchar.devtools.watcher.WebResourcesWatcher;
import com.fastchar.interfaces.IFastConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FastDevToolsConfig implements IFastConfig {


    private final List<WebResourcesWatcher> webResourcesWatchers = new ArrayList<>();
    private int webResourceWatcherDuration = 3;
    private boolean enable = false;
    private boolean lazyWatch = false;

    public FastDevToolsConfig addWebResourcesWatcher(WebResourcesWatcher webResourcesWatcher) {
        webResourcesWatchers.add(webResourcesWatcher);
        return this;
    }

    /**
     * 添加监听web资源
     * @param sourcePath web源路径
     * @param compilerPath web编译后的路径
     * @return this
     */
    public FastDevToolsConfig addWebResourcesWatcher(String sourcePath,String compilerPath) {
        webResourcesWatchers.add(new WebResourcesWatcher().setSourcePath(sourcePath).setCompilerPath(compilerPath));
        return this;
    }

    /**
     * 添加监听web资源
     * @param sourceDir web源路径
     * @param compilerDir web编译后的路径
     * @return this
     */
    public FastDevToolsConfig addWebResourcesWatcher(File sourceDir, File compilerDir) {
        return this.addWebResourcesWatcher(sourceDir.getAbsolutePath(), compilerDir.getAbsolutePath());
    }

    public int getWebResourceWatcherDuration() {
        return webResourceWatcherDuration;
    }

    /**
     * 设置监听器的时间间隔，单位：秒
     * @param webResourceWatcherDuration 秒
     * @return this
     */
    public FastDevToolsConfig setWebResourceWatcherDuration(int webResourceWatcherDuration) {
        this.webResourceWatcherDuration = webResourceWatcherDuration;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public FastDevToolsConfig setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public List<WebResourcesWatcher> getWebResourcesWatchers() {
        return webResourcesWatchers;
    }

    public boolean isLazyWatch() {
        return lazyWatch;
    }

    public FastDevToolsConfig setLazyWatch(boolean lazyWatch) {
        this.lazyWatch = lazyWatch;
        return this;
    }
}
