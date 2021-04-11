package com.paragon464.gameserver;

import com.moandjiezana.toml.Toml;
import com.paragon464.gameserver.model.region.Position;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Config {

    private static final Toml config = new Toml().read(new File("data/settings.toml"));

    public static final String SERVER_NAME = config.getString("general.server_name");

    public static final String AUTH_BACKEND = config.getString("database.forum.auth_backend");

    public static final int CYCLE_RATE = Math.toIntExact(config.getLong("engine.cycle_rate"));

    public static final int PLAYER_LIMIT = Math.toIntExact(config.getLong("engine.player_limit"));

    public static final int NPC_LIMIT = Math.toIntExact(config.getLong("engine.npc_limit"));

    public static final int USERNAME_LENGTH_LIMIT = Math.toIntExact(config.getLong("engine.npc_limit"));

    public static final int PASSWORD_LENGTH_LIMIT = Math.toIntExact(config.getLong("engine.password_length_limit"));

    public static final int CONNECTION_LIMIT = Math.toIntExact(config.getLong("network.connection_limit"));

    public static final int GAME_PORT = Math.toIntExact(config.getLong("network.game_port"));

    public static final int WEBSERVER_PORT = Math.toIntExact(config.getLong("network.webserver_port"));

    public static final String BIND_ADDRESS = config.getString("network.bind_address");

    public static final boolean DEBUG_MODE = config.getBoolean("development.debug_mode");

    public static final Position INITIAL_SPAWN_POSITION = createPos(config.<Long>getList("areas.initial_spawn").stream().map(Math::toIntExact).collect(Collectors.toUnmodifiableList()));

    public static final Position RESPAWN_POSITION = createPos(config.<Long>getList("areas.respawn").stream().map(Math::toIntExact).collect(Collectors.toUnmodifiableList()));

    public static final int CLIENT_MAJOR_VERSION = Math.toIntExact(config.getLong("network.client_version.major_version"));

    public static final int CLIENT_MINOR_VERSION = Math.toIntExact(config.getLong("network.client_version.minor_version"));

    public static final int FREE_MEMORY_MINIMUM = Math.toIntExact(config.getLong("engine.free_memory_minimum"));

    public static Toml getConfig() {
        return new Toml(config);
    }

    // lol
    private static Position createPos(final List<Integer> coords) {
        return new Position(coords.get(0), coords.get(1), coords.get(2));
    }
}
