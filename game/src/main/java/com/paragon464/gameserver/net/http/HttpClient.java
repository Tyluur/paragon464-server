package com.paragon464.gameserver.net.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Getter
public enum HttpClient {
    STANDARD(new OkHttpClient()),
    EAGER(STANDARD.getClient().newBuilder().readTimeout(500, TimeUnit.MILLISECONDS).build());
    private final OkHttpClient client;
}
