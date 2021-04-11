package com.paragon464.gameserver.api;

import com.moandjiezana.toml.Toml;
import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.api.xenforo.XenForo;
import com.paragon464.gameserver.net.http.HttpClient;

public class XfApi {

    private static class SingletonHolder {
        private static final Toml config = Config.getConfig().getTable("forum");
        private static final XenForo INSTANCE = XenForo.builder()
            .apiKey(config.getString("api-key"))
            .boardUrl(config.getString("board-url"))
            .scheme(config.getString("scheme"))
            .client(HttpClient.STANDARD.getClient())
            .build();
    }

    private XfApi() {
    }

    public static XenForo getBoard() {
        return SingletonHolder.INSTANCE;
    }
}
