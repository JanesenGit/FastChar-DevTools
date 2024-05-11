package com.fastchar.devtools.interceptor;

import com.fastchar.core.FastChar;
import com.fastchar.core.FastDispatcher;
import com.fastchar.devtools.FastDevToolsConfig;
import com.fastchar.devtools.core.WebResourcesLazyWatcherProvider;
import com.fastchar.devtools.watcher.WebResourcesWatcher;
import com.fastchar.interfaces.IFastRootInterceptor;
import com.fastchar.servlet.http.FastHttpServletRequest;
import com.fastchar.servlet.http.FastHttpServletResponse;
import com.fastchar.utils.FastFileUtils;

import java.io.File;

public class FastDevToolsInterceptor implements IFastRootInterceptor {

    @Override
    public void onInterceptor(FastHttpServletRequest request, FastHttpServletResponse response, FastDispatcher dispatcher) throws Exception {
        if (Boolean.TRUE.equals(WebResourcesLazyWatcherProvider.WEB_RESOURCES_CHANGED.poll())) {
            synchronized (WebResourcesLazyWatcherProvider.class) {
                FastDevToolsConfig config = FastChar.getConfig(FastDevToolsConfig.class);
                for (WebResourcesWatcher webResourcesWatcher : config.getWebResourcesWatchers()) {
                    File srcDir = new File(webResourcesWatcher.getSourcePath());
                    if (srcDir.exists()) {
                        if (Boolean.TRUE.equals(WebResourcesLazyWatcherProvider.WEB_RESOURCES_DELETED.poll())) {
                            File[] files = srcDir.listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    FastFileUtils.deleteQuietly(webResourcesWatcher.toCompilerFile(file.getAbsolutePath()));
                                }
                            }
                        }
                        FastFileUtils.copyDirectory(srcDir, new File(webResourcesWatcher.getCompilerPath()));
                    }
                }
            }
            FastChar.getConstant().setEndInitTime(System.currentTimeMillis());
            FastChar.getLogger().debug(this.getClass(), "web resources has reloaded.");
        }
        dispatcher.invoke();
    }
}
