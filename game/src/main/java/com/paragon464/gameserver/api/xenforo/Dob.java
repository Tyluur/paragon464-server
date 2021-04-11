package com.paragon464.gameserver.api.xenforo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Builder
@Wither
@JsonDeserialize(builder = Dob.DobBuilder.class)
@AllArgsConstructor
final class Dob {
    private final int year;
    private final int month;
    private final int day;

    @JsonPOJOBuilder(withPrefix = "")
    static final class DobBuilder {
        @JsonProperty("year")
        private int year;
        @JsonProperty("month")
        private int month;
        @JsonProperty("day")
        private int day;
    }
}
