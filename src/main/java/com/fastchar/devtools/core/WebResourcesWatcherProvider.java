package com.fastchar.devtools.core;

import com.fastchar.core.FastChar;
import com.fastchar.devtools.watcher.WebResourcesWatcher;
import com.fastchar.utils.FastFileUtils;
import com.fastchar.utils.FastThreadUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebResourcesWatcherProvider {

    private final Map<String, ResourceEntry<WebResourcesWatcher>> resourceEntryMap = new HashMap<>();
    private List<WebResourcesWatcher> watchers = new ArrayList<>();
    private boolean stopped;
    private boolean started;
    private long duration = 3;

    public synchronized void start(List<WebResourcesWatcher> watchers) {
        if (this.started) {
            return;
        }
        this.watchers = watchers;
        this.stopped = false;
        this.started = true;
        this.initMap(false);

        new Thread(() -> {
            while (!stopped) {

                List<String> waitRemovePaths = new ArrayList<>();

                boolean changed = false;

                //检测存在的文件更新或删除
                for (Map.Entry<String, ResourceEntry<WebResourcesWatcher>> resourceEntryEntry : resourceEntryMap.entrySet()) {
                    ResourceEntry<WebResourcesWatcher> resourceEntry = resourceEntryEntry.getValue();
                    WebResourcesWatcher resource = resourceEntry.getResource();
                    File compilerFile = resource.toCompilerFile(resourceEntryEntry.getKey());

                    if (resourceEntry.isDelete()) {
                        waitRemovePaths.add(resourceEntryEntry.getKey());
                        FastFileUtils.deleteQuietly(compilerFile);
                        changed = true;
                    } else if (resourceEntry.isModified()) {
                        try {
                            FastFileUtils.copyFile(new File(resourceEntryEntry.getKey()), compilerFile);
                            changed = true;
                        } catch (IOException e) {
                            FastChar.getLogger().error(WebResourcesWatcherProvider.class, e);
                        }finally {
                            resourceEntry.refreshFile(resourceEntry.getResourceFile());
                        }
                    }
                }

                for (String waitRemovePath : waitRemovePaths) {
                    resourceEntryMap.remove(waitRemovePath);
                }

                //检测新增的文件
                if (this.initMap(true)) {
                    changed = true;
                }
                if (changed) {
                    FastChar.getLogger().debug(this.getClass(), "web resources has changed.");
                }

                try {
                    FastThreadUtils.sleep(Duration.ofSeconds(duration));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }).start();

    }


    private boolean initMap(boolean checkCreated) {
        boolean changed = false;
        for (WebResourcesWatcher watcher : this.watchers) {
            List<ResourceEntry<WebResourcesWatcher>> entries = this.listResources(watcher);
            for (ResourceEntry<WebResourcesWatcher> entry : entries) {
                if (checkCreated) {
                    if (!resourceEntryMap.containsKey(entry.getAbsolutePath())) {
                        try {
                            if (entry.isDirectory()) {
                                FastFileUtils.copyDirectory(new File(entry.getAbsolutePath()), watcher.toCompilerFile(entry.getAbsolutePath()));
                            }else{
                                FastFileUtils.copyFile(new File(entry.getAbsolutePath()), watcher.toCompilerFile(entry.getAbsolutePath()));
                            }
                            changed = true;
                        } catch (IOException e) {
                            FastChar.getLogger().error(WebResourcesWatcherProvider.class, e);
                        }
                        resourceEntryMap.put(entry.getAbsolutePath(), entry);
                    }
                }else{
                    resourceEntryMap.put(entry.getAbsolutePath(), entry);
                }
            }
        }
        return changed;
    }


    public synchronized void stop() {
        this.stopped = true;
        this.started = false;
        this.resourceEntryMap.clear();
    }

    public long getDuration() {
        return duration;
    }

    public WebResourcesWatcherProvider setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    private List<ResourceEntry<WebResourcesWatcher>> listResources(WebResourcesWatcher watcher) {
        List<ResourceEntry<WebResourcesWatcher>> list = new ArrayList<>();
        List<File> files = this.listFiles(new File(watcher.getSourcePath()));
        for (File file : files) {
            ResourceEntry<WebResourcesWatcher> resourceEntry = new ResourceEntry<>();
            resourceEntry.refreshFile(file);
            resourceEntry.setResource(watcher);
            list.add(resourceEntry);
        }
        return list;
    }

    private List<File> listFiles(File file) {
        List<File> files = new ArrayList<>();
        if (!file.exists()) {
            return files;
        }
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            if (fileArray != null) {
                for (File subFile : fileArray) {
                    files.addAll(this.listFiles(subFile));
                }
            }
        }
        files.add(file);
        return files;
    }


}
