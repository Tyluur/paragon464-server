

package com.paragon464.gameserver.api.xenforo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paragon464.gameserver.net.http.HttpClient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

@Value @Builder @Wither @Slf4j
public final class XenForo {

    @Builder.Default
    private final String scheme = "https";
    private final String boardUrl;
    private final String apiKey;
    @Builder.Default
    private final OkHttpClient client = HttpClient.STANDARD.getClient();
    @Getter(AccessLevel.NONE)
    private final ObjectMapper mapper = new ObjectMapper() {{
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }};

    public UserResult getUser(final long userId) {
        final HttpUrl url = new HttpUrl.Builder().scheme(scheme).host(boardUrl).addPathSegment("api")
            .addPathSegment("users").addQueryParameter("api_bypass_permissions", "1")
            .addPathSegment(String.valueOf(userId)).build();
        try (final Response response = client.newCall(new Request.Builder().addHeader("XF-API-Key", apiKey)
            .url(url).get().build()).execute()) {
            try {
                return Objects.requireNonNullElseGet(parseUser(response), () ->
                    new UserResult(ReturnCode.NOT_FOUND, "", null));
            } catch (Exception e) {
                log.error("Failed to map API response to user object! User ID: {}.", userId, e);
                return new UserResult(ReturnCode.DESERIALIZATION_FAILURE, "", null);
            }
        } catch (IOException e) {
            log.error("Failed to communicate with XenForo API", e);
            return new UserResult(ReturnCode.API_COMMUNICATION_FAILURE, "", null);
        }
    }

    public UserResult getUser(final String username) {
        final HttpUrl url = new HttpUrl.Builder()
            .scheme(scheme).host(boardUrl).addPathSegment("api")
            .addPathSegment("users").addPathSegment("find-name")
            .addQueryParameter("username", username)
            .addQueryParameter("api_bypass_permissions", "1")
            .build();
        try (final Response response = client.newCall(new Request.Builder().addHeader("XF-API-Key", apiKey)
            .url(url).get().build()).execute()) {
            try {
                return Objects.requireNonNullElseGet(parseUser(response), () ->
                    new UserResult(ReturnCode.NOT_FOUND, "", null));
            } catch (Exception e) {
                log.error("Failed to map API response to user object! Username: {}.", username, e);
                return new UserResult(ReturnCode.DESERIALIZATION_FAILURE, "", null);
            }
        } catch (IOException e) {
            log.error("Failed to communicate with XenForo API", e);
            return new UserResult(ReturnCode.API_COMMUNICATION_FAILURE, "", null);
        }
    }

    public UserResult authenticateUser(final String login, final String password, final String ip) {
        final HttpUrl url = new HttpUrl.Builder().scheme(scheme).host(boardUrl).addPathSegment("api")
            .addPathSegment("auth").addQueryParameter("api_bypass_permissions", "1").build();
        final RequestBody body = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("login", login)
            .addFormDataPart("password", password)
            .addFormDataPart("limit_ip", ip).build();
        try (final Response response = client.newCall(new Request.Builder().addHeader("XF-API-Key", apiKey)
            .url(url).post(body).build()).execute()) {
            try {
                return Objects.requireNonNullElseGet(parseUser(response), () ->
                    new UserResult(ReturnCode.NOT_FOUND, "", null));
            } catch (Exception e) {
                log.error("Failed to map API response to user object! Username: {}.", login, e);
                return new UserResult(ReturnCode.DESERIALIZATION_FAILURE, "", null);
            }
        } catch (IOException e) {
            log.error("Failed to communicate with XenForo API", e);
            return new UserResult(ReturnCode.API_COMMUNICATION_FAILURE, "", null);
        }
    }

    public UserResult createUser(final String username, final String password) {
        final HttpUrl url = new HttpUrl.Builder().scheme(scheme).host(boardUrl).addPathSegment("api")
            .addPathSegment("users").addQueryParameter("api_bypass_permissions", "1").build();
        final RequestBody body = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("password", password).build();
        try (final Response response = client.newCall(new Request.Builder().addHeader("XF-API-Key", apiKey)
            .url(url).post(body).build()).execute()) {
            try {
                if (response.body() != null) {
                    JsonNode res = mapper.readTree(response.body().string());
                    final var errors = res.get("errors");
                    if (errors != null && !errors.isNull() && errors.has(0)) {
                        return mapper.treeToValue(errors.get(0), UserResult.class);
                    } else {
                        ((ObjectNode) res).remove("success");
                        ((ObjectNode) res).put("code", "success");
                        return mapper.treeToValue(res, UserResult.class);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to map API response to user object! Username: {}.", username, e);
                return new UserResult(ReturnCode.DESERIALIZATION_FAILURE, "", null);
            }
        } catch (IOException e) {
            log.error("Failed to communicate with XenForo API", e);
            return new UserResult(ReturnCode.API_COMMUNICATION_FAILURE, "", null);
        }
        return new UserResult(ReturnCode.UNKNOWN_FAILURE, "", null);
    }

    @Nullable
    private UserResult parseUser(@NonNull final Response response) throws IOException {
        if (response.body() != null) {
            JsonNode tree = mapper.readTree(response.body().string());
            if (!tree.isNull()) {
                val errors = tree.get("errors");
                if (errors != null && !errors.isNull()) {
                    tree = errors.get(0);
                } else if (tree.has("success")) {
                    ((ObjectNode) tree).remove("success");
                    ((ObjectNode) tree).put("code", "success");
                }
                return mapper.treeToValue(tree, UserResult.class);
            }
        }
        return null;
    }
}
