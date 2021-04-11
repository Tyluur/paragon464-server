package com.paragon464.gameserver.api.xenforo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.Optional;

@Value
@Wither
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = UserResult.UserResultBuilder.class)
public final class UserResult {

    private final ReturnCode returnCode;
    private final String message;
    @Getter(AccessLevel.NONE)
    private final User user;

    public UserResult(final ReturnCode returnCode, final String message, final User user) {
        if (user != null && returnCode == null) {
            this.returnCode = ReturnCode.SUCCESS;
        } else if (user == null && returnCode == null) {
            this.returnCode = ReturnCode.UNKNOWN_FAILURE;
        } else {
            this.returnCode = returnCode;
        }
        this.message = message;
        this.user = user;
    }

    public UserResult(final ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.message = null;
        this.user = null;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    public boolean wasSuccessful() {
        return returnCode == ReturnCode.SUCCESS;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserResultBuilder {
        @JsonProperty("code") @JsonAlias("success")
        private ReturnCode returnCode;
        @JsonProperty("message")
        private String message;
        @JsonProperty("user") @JsonAlias("exact")
        private User user;
    }
}
