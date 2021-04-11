
package com.paragon464.gameserver.api.xenforo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Builder
@Wither
@JsonDeserialize(builder = AvatarUrls.AvatarUrlsBuilder.class)
final class AvatarUrls {

    private final String original;
    private final String h;
    private final String large;
    private final String medium;
    private final String small;

    @JsonPOJOBuilder(withPrefix = "")
    static final class AvatarUrlsBuilder {
        @JsonProperty("o")
        private String original;
        @JsonProperty("h")
        private String h;
        @JsonProperty("l")
        private String large;
        @JsonProperty("m")
        private String medium;
        @JsonProperty("s")
        private String small;
    }
}
