package com.paragon464.gameserver.api.xenforo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReturnCode {
    API_COMMUNICATION_FAILURE,
    DESERIALIZATION_FAILURE,
    @JsonEnumDefaultValue
    UNKNOWN_FAILURE,
    @JsonProperty("success")
    SUCCESS,
    @JsonProperty("usernames_must_be_unique")
    NAME_NOT_UNIQUE,
    @JsonProperty("email_addresses_must_be_unique")
    EMAIL_NOT_UNIQUE,
    @JsonProperty("incorrect_password")
    INVALID_PASSWORD,
    @JsonProperty("requested_user_x_not_found") @JsonAlias({"requested_user_not_found", "requested_page_not_found"})
    NOT_FOUND,
    @JsonProperty("your_account_has_temporarily_been_locked_due_to_failed_login_attempts")
    MAX_LOGIN_ATTEMPTS,
    @JsonProperty("you_must_enable_two_step_access_control_panel")
    REQUIRE_2FA,
    @JsonProperty("your_account_does_not_have_admin_privileges")
    MUST_BE_ADMIN,
    @JsonProperty("your_existing_password_is_not_correct")
    CURRENT_PASSWORD_INCORRECT,
    @JsonProperty("do_not_have_permission")
    NO_PERMISSION
}
