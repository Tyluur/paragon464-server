
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
@JsonDeserialize(builder = CustomFields.CustomFieldsBuilder.class)
final class CustomFields {

    private String occupation2;
    private String gender;
    private String occupation;
    private String playstationId;
    private String gamertag;
    private String steamId;
    private String aim;
    private String icq;
    private String yahoo;
    private String skype;
    private String gtalk;
    private String facebook;
    private String twitter;

    @JsonPOJOBuilder(withPrefix = "")
    static final class CustomFieldsBuilder {
        @JsonProperty("occupation_2")
        private String occupation2;
        @JsonProperty("gender")
        private String gender;
        @JsonProperty("occupation")
        private String occupation;
        @JsonProperty("PSNID")
        private String playstationId;
        @JsonProperty("XBLGT")
        private String gamertag;
        @JsonProperty("SteamID")
        private String steamId;
        @JsonProperty("aim")
        private String aim;
        @JsonProperty("icq")
        private String icq;
        @JsonProperty("yahoo")
        private String yahoo;
        @JsonProperty("skype")
        private String skype;
        @JsonProperty("gtalk")
        private String gtalk;
        @JsonProperty("facebook")
        private String facebook;
        @JsonProperty("twitter")
        private String twitter;
    }
}
