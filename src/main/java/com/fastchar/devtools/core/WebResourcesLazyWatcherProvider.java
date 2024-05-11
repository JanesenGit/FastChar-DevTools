package com.fastchar.devtools.core;

import com.fastchar.core.FastChar;
import com.fastchar.devtools.watcher.WebResourcesWatcher;
import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebResourcesLazyWatcherProvider {
    public static ConcurrentLinkedQueue<Boolean> WEB_RESOURCES_CHANGED = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Boolean> WEB_RESOURCES_DELETED = new ConcurrentLinkedQueue<>();


    private boolean started = false;
    private boolean stopped = false;
    private WatchService watchService;
    private List<WebResourcesWatcher> watchers;
    private final Set<String> registered = new HashSet<>();

    public synchronized void start(List<WebResourcesWatcher> watchers) throws IOException {
        if (this.started) {
            return;
        }
        this.started = true;
        this.watchers = watchers;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.register();

        new Thread(() -> {
            while (!this.stopped) {
                WatchKey key = watchService.poll();
                if (key == null) {
                    continue;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    WEB_RESOURCES_CHANGED.add(true);
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        WEB_RESOURCES_DELETED.add(true);
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        this.register();
                    }
                    key.reset();
                    FastChar.getLogger().debug(this.getClass(), "web resources has changed.[" + event.kind() + "]");
                }
            }
        }).start();
    }


    public synchronized void stop() {
        this.stopped = true;
        this.started = false;
    }


    private void register() {
        for (WebResourcesWatcher watcher : this.watchers) {
            List<File> listDirectory = this.listDirectory(new File(watcher.getSourcePath()));
            for (File file : listDirectory) {
                if (registered.contains(file.getAbsolutePath())) {
                    continue;
                }
                registered.add(file.getAbsolutePath());
                Path path = Paths.get(file.getAbsolutePath()).toAbsolutePath();
                WatchEvent.Kind<?>[] kinds = new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY};
                try {
                    path.register(watchService, kinds, SensitivityWatchEventModifier.HIGH);
                } catch (IOException e) {
                    FastChar.getLogger().error(this.getClass(), e);
                }
            }
        }
    }


    private List<File> listDirectory(File file) {
        List<File> files = new ArrayList<>();
        if (!file.exists()) {
            return files;
        }
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles(File::isDirectory);
            if (fileArray != null) {
                for (File subFile : fileArray) {
                    files.addAll(this.listDirectory(subFile));
                }
            }
        }
        files.add(file);
        return files;
    }


}
