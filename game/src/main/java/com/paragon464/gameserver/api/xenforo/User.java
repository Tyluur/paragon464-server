package com.paragon464.gameserver.api.xenforo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Builder
@Wither
@JsonDeserialize(builder = User.UserBuilder.class)
@JsonRootName(value = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class User {

    private final int age;
    @Builder.Default
    private final Dob dob = new Dob(0, 0, 0);
    private final String about;
    private final AvatarUrls avatars;
    private final boolean canBan;
    private final boolean canConverse;
    private final boolean canEdit;
    private final boolean canFollow;
    private final boolean canIgnore;
    private final boolean canPostProfile;
    private final boolean canViewProfile;
    private final boolean canViewProfilePosts;
    private final boolean canWarn;
    private final CustomFields customFields;
    private final boolean superAdmin;
    private final boolean admin;
    private final boolean moderator;
    private final boolean staff;
    private final boolean banned;
    private final boolean visible;
    private final boolean activityVisible;
    private final boolean dobDateVisible;
    private final boolean dobYearVisible;
    private final boolean receiveAdminEmails;
    private final boolean receiveEmailOnConversation;
    private final boolean receivePushOnConversation;
    private final boolean usingTwoFactor;
    private final long lastActivity;
    private final long registration;
    private final String location;
    private final String signature;
    private final int postCount;
    private final int points;
    private final int reactionScore;
    private final int warningPoints;
    private final int id;
    private final int group;
    private final int[] secondaryGroups;
    private final String title;
    private final String customTitle;
    private final String name;
    private final String email;
    private final String website;
    private final String state;

    public boolean isValidated() {
        return state != null
            && state.equalsIgnoreCase("valid")
            && email != null
            && !email.isEmpty();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserBuilder {
        @JsonProperty("age")
        private int age;
        @JsonProperty("about")
        private String about;
        @JsonProperty("avatar_urls")
        private AvatarUrls avatars;
        @JsonProperty("can_ban")
        private boolean canBan;
        @JsonProperty("can_converse")
        private boolean canConverse;
        @JsonProperty("can_edit")
        private boolean canEdit;
        @JsonProperty("can_follow")
        private boolean canFollow;
        @JsonProperty("can_ignore")
        private boolean canIgnore;
        @JsonProperty("can_post_profile")
        private boolean canPostProfile;
        @JsonProperty("can_view_profile")
        private boolean canViewProfile;
        @JsonProperty("can_view_profile_posts")
        private boolean canViewProfilePosts;
        @JsonProperty("can_warn")
        private boolean canWarn;
        @JsonProperty("custom_fields")
        private CustomFields customFields;
        @JsonProperty("is_super_admin")
        private boolean superAdmin;
        @JsonProperty("is_admin")
        private boolean admin;
        @JsonProperty("is_moderator")
        private boolean moderator;
        @JsonProperty("is_staff")
        private boolean staff;
        @JsonProperty("is_banned")
        private boolean banned;
        @JsonProperty("is_visible")
        private boolean visible;
        @JsonProperty("activity_visible")
        private boolean activityVisible;
        @JsonProperty("show_dob_year")
        private boolean dobYearVisible;
        @JsonProperty("show_dob_date")
        private boolean dobDateVisible;
        @JsonProperty("use_tfa")
        private boolean usingTwoFactor;
        @JsonProperty("receive_admin_email")
        private boolean receiveAdminEmails;
        @JsonProperty("email_on_conversation")
        private boolean receiveEmailOnConversation;
        @JsonProperty("push_on_conversation")
        private boolean receivePushOnConversation;
        @JsonProperty("last_activity")
        private long lastActivity;
        @JsonProperty("location")
        private String location;
        @JsonProperty("message_count")
        private int postCount;
        @JsonProperty("reaction_score")
        private int reactionScore;
        @JsonProperty("register_date")
        private long registration;
        @JsonProperty("signature")
        private String signature;
        @JsonProperty("trophy_points")
        private int points;
        @JsonProperty("warning_points")
        private int warningPoints;
        @JsonProperty("user_id")
        private int id;
        @JsonProperty("user_title")
        private String title;
        @JsonProperty("custom_title")
        private String customTitle;
        @JsonProperty("username")
        private String name;
        @JsonProperty("email")
        private String email;
        @JsonProperty("website")
        private String website;
        @JsonProperty("user_state")
        private String state;
        @JsonProperty("user_group_id")
        private int group;
        @JsonProperty("secondary_group_ids")
        private int[] secondaryGroups;
    }
}
