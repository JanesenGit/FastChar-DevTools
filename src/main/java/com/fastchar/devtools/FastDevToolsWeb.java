package com.fastchar.devtools;

import com.fastchar.core.FastEngine;
import com.fastchar.devtools.core.WebResourcesLazyWatcherProvider;
import com.fastchar.devtools.core.WebResourcesWatcherProvider;
import com.fastchar.devtools.interceptor.FastDevToolsInterceptor;
import com.fastchar.interfaces.IFastWeb;

@SuppressWarnings("unused")
public class FastDevToolsWeb implements IFastWeb {


    @Override
    public void onFinish(FastEngine engine) throws Exception {
        IFastWeb.super.onFinish(engine);
        FastDevToolsConfig config = engine.getConfig(FastDevToolsConfig.class);
        if (config.isEnable()) {
            if (config.isLazyWatch()) {
                engine.getInterceptors().addRoot(FastDevToolsInterceptor.class, "/*");
                engine.getOverrides().singleInstance(WebResourcesLazyWatcherProvider.class)
                        .start(config.getWebResourcesWatchers());
            }else{
                engine.getOverrides().singleInstance(WebResourcesWatcherProvider.class)
                        .setDuration(config.getWebResourceWatcherDuration())
                        .start(config.getWebResourcesWatchers());
            }
        }
    }

    @Override
    public void onDestroy(FastEngine engine) throws Exception {
        IFastWeb.super.onDestroy(engine);
        FastDevToolsConfig config = engine.getConfig(FastDevToolsConfig.class);
        if (config.isEnable()) {
            engine.getOverrides().singleInstance(WebResourcesWatcherProvider.class).stop();
            engine.getOverrides().singleInstance(WebResourcesLazyWatcherProvider.class).stop();
        }
    }
}
