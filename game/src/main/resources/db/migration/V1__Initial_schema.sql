CREATE TYPE clan_privilege AS ENUM (
    'ANYONE',
    'ANY_FRIENDS',
    'RECRUIT',
    'CORPORAL',
    'SERGEANT',
    'LIEUTENANT',
    'CAPTAIN',
    'GENERAL',
    'ONLY_ME'
);

CREATE TYPE combat_type AS ENUM (
    'MELEE',
    'RANGED',
    'MAGE'
);

CREATE TYPE equipment_slot AS ENUM (
    'AMMUNITION',
    'AURA',
    'BACK',
    'FEET',
    'FINGERS',
    'HANDS',
    'HEAD',
    'LEGS',
    'MAIN_HAND',
    'NECK',
    'NONE',
    'OFF_HAND',
    'TORSO'
);

CREATE TYPE equipment_type AS ENUM (
    'FULL_BODY',
    'MED_HELM',
    'FULL_MASK',
    'MASK',
    'TWO_HANDED_WEAPON',
    'STANDARD'
);

CREATE TYPE relationship_type AS ENUM (
    'IGNORED',
    'FRIEND',
    'NO_RELATION',
    'RECRUIT',
    'CORPORAL',
    'SERGEANT',
    'LIEUTENANT',
    'CAPTAIN',
    'GENERAL'
);

CREATE TABLE paragon_definition_shop (
    id integer NOT NULL PRIMARY KEY,
    name character varying(100) NOT NULL,
    currency character varying(25) DEFAULT 'COINS'::character varying NOT NULL,
    general_store boolean DEFAULT false NOT NULL,
    buys_back boolean DEFAULT true NOT NULL
);

CREATE TABLE paragon_definition_contents_shop (
    shop_id integer NOT NULL,
    "position" integer NOT NULL,
    item_id integer NOT NULL,
    price_purchase integer DEFAULT 1 NOT NULL,
    price_sell integer DEFAULT 1 NOT NULL,
    PRIMARY KEY (shop_id, "position")
);

CREATE TABLE paragon_definition_npc (
    npc_id integer NOT NULL PRIMARY KEY,
    npc_level_combat integer DEFAULT 1 NOT NULL,
    npc_name character varying(45) NOT NULL,
    npc_examine_text text NOT NULL,
    npc_tile_size smallint DEFAULT 1 NOT NULL,
    npc_time_respawn integer DEFAULT 1 NOT NULL
);

CREATE TABLE paragon_definition_drop_npc (
    id_npc integer NOT NULL,
    id_item integer NOT NULL,
    chance double precision DEFAULT 0.0 NOT NULL,
    amount_minimum integer DEFAULT 1 NOT NULL,
    amount_maximum integer DEFAULT 1 NOT NULL,
    rare boolean DEFAULT false NOT NULL,
    PRIMARY KEY (id_npc, id_item)
);

CREATE TABLE paragon_definition_drop_common (
    name character varying(30) NOT NULL,
    id_item integer NOT NULL,
    chance double precision NOT NULL,
    amount_minimum integer DEFAULT 1 NOT NULL,
    amount_maximum integer DEFAULT 1 NOT NULL,
    rare boolean DEFAULT false NOT NULL,
    PRIMARY KEY (name, id_item)
);

CREATE TABLE paragon_definition_drop_common_npc (
    id_npc integer NOT NULL,
    table_type character varying(25) DEFAULT 'RARE'::character varying NOT NULL,
    chance double precision DEFAULT 0.0 NOT NULL,
    PRIMARY KEY (id_npc, table_type)
);

CREATE TABLE paragon_definition_spawn_npc (
    npc_spawn_id serial NOT NULL PRIMARY KEY,
    npc_id integer NOT NULL,
    coordinate_x integer DEFAULT 3220 NOT NULL,
    coordinate_y integer DEFAULT 3220 NOT NULL,
    coordinate_z integer DEFAULT 0 NOT NULL,
    radius integer DEFAULT 0 NOT NULL,
    direction character varying(255) DEFAULT 'North'::character varying NOT NULL,
    CONSTRAINT paragon_definition_spawn_npc_direction_check CHECK (((direction)::text = ANY (ARRAY[('North'::character varying)::text, ('North-East'::character varying)::text, ('North-West'::character varying)::text, ('East'::character varying)::text, ('West'::character varying)::text, ('South'::character varying)::text, ('South-East'::character varying)::text, ('South-West'::character varying)::text])))
);

CREATE TABLE paragon_definition_skill_npc (
    npc_id integer NOT NULL,
    npc_skill_label character varying(45) NOT NULL,
    npc_skill_level smallint NOT NULL,
    PRIMARY KEY (npc_id, npc_skill_label)
);

CREATE TABLE paragon_definition_combat_npc (
    npc_id serial NOT NULL PRIMARY KEY,
    npc_time_death integer DEFAULT 1 NOT NULL,
    animation_attack integer DEFAULT 0 NOT NULL,
    animation_defend integer DEFAULT 0 NOT NULL,
    animation_death integer DEFAULT 0 NOT NULL,
    npc_speed_attack integer DEFAULT 1 NOT NULL,
    npc_max_hit smallint DEFAULT 1 NOT NULL,
    npc_max_hit_poison smallint DEFAULT 0 NOT NULL,
    npc_trait_aggressive bit varying(1) NOT NULL,
    npc_trait_retreats bit varying(1) NOT NULL,
    npc_immunity_poison bit varying(1) NOT NULL,
    npc_kill_reward_xp_slayer double precision DEFAULT 0 NOT NULL,
    player_required_level_slayer smallint DEFAULT 0 NOT NULL
);

CREATE TABLE paragon_definition_combat_bonus_npc (
    npc_id serial PRIMARY KEY,
    npc_bonus_defensive_stab integer,
    npc_bonus_defensive_slash integer,
    npc_bonus_defensive_crush integer,
    npc_bonus_defensive_ranged integer,
    npc_bonus_defensive_magic integer,
    npc_bonus_offensive_stab integer,
    npc_bonus_offensive_slash integer,
    npc_bonus_offensive_crush integer,
    npc_bonus_offensive_magic integer,
    npc_bonus_offensive_ranged integer,
    npc_bonus_offensive_attack integer,
    npc_bonus_offensive_strength integer
);

CREATE TABLE paragon_definition_combat_requirements_item (
    id serial NOT NULL,
    skill character varying(30) DEFAULT 'ATTACK'::character varying NOT NULL,
    level smallint DEFAULT 1 NOT NULL,
    PRIMARY KEY (id, skill)
);

CREATE TABLE paragon_definition_spawn_object (
    id_object integer NOT NULL,
    delete boolean DEFAULT false NOT NULL,
    type smallint DEFAULT 0 NOT NULL,
    face smallint DEFAULT 0 NOT NULL,
    pos_x integer DEFAULT 0 NOT NULL,
    pos_y integer DEFAULT 0 NOT NULL,
    pos_z integer DEFAULT 0 NOT NULL,
    id_region integer NOT NULL,
    PRIMARY KEY (id_object, pos_x, pos_y, pos_z)
);

CREATE TABLE paragon_definition_object_doors (
    id serial NOT NULL PRIMARY KEY,
    door_id integer NOT NULL,
    pos_x integer NOT NULL,
    pos_y integer NOT NULL,
    pos_z integer NOT NULL,
    open boolean NOT NULL,
    double_door boolean DEFAULT false NOT NULL,
    face integer NOT NULL,
    type integer NOT NULL
);

CREATE TABLE paragon_api_key (
    uuid character(36) NOT NULL PRIMARY KEY,
    name character varying(100) NOT NULL
);

CREATE TABLE runenova_definition_item (
    id serial NOT NULL PRIMARY KEY,
    id_note integer DEFAULT '-1'::integer NOT NULL,
    id_lend integer DEFAULT '-1'::integer NOT NULL,
    id_template_note integer DEFAULT '-1'::integer NOT NULL,
    id_template_lend integer DEFAULT '-1'::integer NOT NULL,
    stackable boolean DEFAULT false NOT NULL,
    tradable boolean DEFAULT false NOT NULL,
    droppable boolean DEFAULT false NOT NULL,
    members_only boolean DEFAULT false NOT NULL,
    name character varying(120) DEFAULT 'NULL'::character varying NOT NULL,
    value integer DEFAULT '-1.0'::numeric NOT NULL,
    weight double precision DEFAULT '-1.0'::numeric NOT NULL,
    examine character varying(200) DEFAULT 'NULL'::character varying NOT NULL
);

CREATE TABLE runenova_definition_item_equipment (
    id serial NOT NULL PRIMARY KEY,
    equipment_slot equipment_slot DEFAULT 'NONE'::equipment_slot NOT NULL,
    offensive_stab smallint DEFAULT 0 NOT NULL,
    offensive_slash smallint DEFAULT 0 NOT NULL,
    offensive_crush smallint DEFAULT 0 NOT NULL,
    offensive_magic smallint DEFAULT 0 NOT NULL,
    offensive_ranged smallint DEFAULT 0 NOT NULL,
    defensive_stab smallint DEFAULT 0 NOT NULL,
    defensive_slash smallint DEFAULT 0 NOT NULL,
    defensive_crush smallint DEFAULT 0 NOT NULL,
    defensive_magic smallint DEFAULT 0 NOT NULL,
    defensive_ranged smallint DEFAULT 0 NOT NULL,
    defensive_summoning smallint DEFAULT 0 NOT NULL,
    absorb_melee double precision DEFAULT 0.00 NOT NULL,
    absorb_magic double precision DEFAULT 0.00 NOT NULL,
    absorb_ranged double precision DEFAULT 0.00 NOT NULL,
    strength_melee smallint DEFAULT 0 NOT NULL,
    strength_ranged smallint DEFAULT 0 NOT NULL,
    prayer smallint DEFAULT 0 NOT NULL,
    magic_damage double precision DEFAULT 0.00 NOT NULL,
    equipment_type equipment_type DEFAULT 'STANDARD'::equipment_type NOT NULL
);

CREATE TABLE runenova_definition_item_equipment_weapon (
    id serial NOT NULL PRIMARY KEY,
    id_interface integer DEFAULT '-1'::integer NOT NULL,
    id_interface_child integer DEFAULT '-1'::integer NOT NULL,
    combat_type combat_type DEFAULT 'MELEE'::combat_type NOT NULL,
    animation_stand integer DEFAULT '-1'::integer NOT NULL,
    animation_walk integer DEFAULT '-1'::integer NOT NULL,
    animation_run integer DEFAULT '-1'::integer NOT NULL,
    animation_block integer DEFAULT '-1'::integer NOT NULL,
    animation_accurate integer DEFAULT '-1'::integer NOT NULL,
    animation_aggressive integer DEFAULT '-1'::integer NOT NULL,
    animation_defensive integer DEFAULT '-1'::integer NOT NULL,
    animation_controlled integer DEFAULT '-1'::integer NOT NULL,
    special_energy double precision DEFAULT 0.0 NOT NULL,
    speed_accurate smallint DEFAULT 5 NOT NULL,
    speed_aggressive smallint DEFAULT 5 NOT NULL,
    speed_defensive smallint DEFAULT 5 NOT NULL,
    speed_controlled smallint DEFAULT 5 NOT NULL
);

CREATE TABLE runenova_definition_item_equipment_weapon_ranged (
    id serial NOT NULL PRIMARY KEY,
    id_animation integer DEFAULT '-1'::integer NOT NULL,
    id_projectile integer DEFAULT '-1'::integer NOT NULL,
    uses_ammo boolean DEFAULT false NOT NULL
);

CREATE TABLE runenova_definition_item_equipment_weapon_ranged_usableammo (
    id integer NOT NULL,
    id_ammo integer DEFAULT 882 NOT NULL,
    PRIMARY KEY (id, id_ammo)
);

CREATE TABLE runenova_definition_teleport (
    id serial NOT NULL PRIMARY KEY,
    required_level smallint DEFAULT 0 NOT NULL,
    experience double precision DEFAULT 0.00 NOT NULL,
    x smallint DEFAULT 3220 NOT NULL,
    y smallint DEFAULT 3220 NOT NULL,
    z smallint DEFAULT 0 NOT NULL
);

CREATE TABLE runenova_definition_teleport_rune (
    teleport_id serial NOT NULL,
    item_id integer NOT NULL,
    item_amount integer DEFAULT 1 NOT NULL,
    PRIMARY KEY (teleport_id, item_id)
);

CREATE TABLE paragon_player (
    user_id integer NOT NULL PRIMARY KEY,
    pos_x integer NOT NULL,
    pos_y integer NOT NULL,
    pos_z smallint NOT NULL,
    energy double precision DEFAULT 100 NOT NULL,
    prayer_points double precision NOT NULL,
    magic_type smallint NOT NULL,
    special_energy smallint NOT NULL,
    gender smallint NOT NULL,
    looks text NOT NULL,
    colors text NOT NULL,
    av_skill text NOT NULL,
    av_style text NOT NULL,
    av_slot smallint NOT NULL,
    started boolean NOT NULL,
    pm_status smallint NOT NULL,
    auto_retaliate boolean DEFAULT false NOT NULL,
    recoil_count smallint DEFAULT 40 NOT NULL,
    hitpoint_bars_type boolean DEFAULT false NOT NULL,
    hitmarkers_type boolean DEFAULT false NOT NULL,
    context_menus_type boolean DEFAULT false NOT NULL,
    hits_multiplied boolean DEFAULT false NOT NULL,
    hotkeys_type boolean DEFAULT false NOT NULL,
    fonts_type boolean DEFAULT false NOT NULL,
    caves_wave smallint DEFAULT 0 NOT NULL,
    barrows_dead text NOT NULL,
    mage_arena_finished boolean DEFAULT false NOT NULL,
    lunars_finished boolean DEFAULT false NOT NULL,
    rfd_stage smallint DEFAULT 0 NOT NULL,
    wand_lvl smallint DEFAULT 0 NOT NULL,
    poison_count smallint DEFAULT 0 NOT NULL,
    slayer_points integer DEFAULT 0 NOT NULL,
    runecraft_points integer DEFAULT 0 NOT NULL,
    splitchat boolean DEFAULT false NOT NULL,
    armadyl_kc integer DEFAULT 0 NOT NULL,
    bandos_kc integer DEFAULT 0 NOT NULL,
    saradomin_kc integer DEFAULT 0 NOT NULL,
    zamorak_kc integer DEFAULT 0 NOT NULL,
    slayer_task_name text,
    slayer_task_count smallint DEFAULT 0 NOT NULL,
    slayer_task_area text NOT NULL,
    vote_points integer DEFAULT 0 NOT NULL,
    bank_pin integer DEFAULT '-1'::integer NOT NULL,
    dt_stage smallint DEFAULT 0 NOT NULL,
    md_stage smallint DEFAULT 0 NOT NULL,
    brain_robbery_stage smallint DEFAULT 0 NOT NULL,
    quest_points smallint DEFAULT 0 NOT NULL,
    bonus_xp_ticks integer DEFAULT 0 NOT NULL,
    xp_multiplier smallint DEFAULT 1 NOT NULL,
    last_login bigint DEFAULT 0 NOT NULL,
    received_validation_package boolean DEFAULT false NOT NULL,
    zombies_points integer DEFAULT 0 NOT NULL,
    skull_timer smallint DEFAULT 0 NOT NULL,
    nightmare_points integer DEFAULT 0 NOT NULL,
    nightmare_wave smallint DEFAULT 0 NOT NULL,
    prayers_unlocked boolean DEFAULT false NOT NULL,
    tutorial_completed boolean DEFAULT false NOT NULL,
    curses_enabled boolean DEFAULT false NOT NULL,
    quick_prayers integer DEFAULT 0 NOT NULL
);

CREATE TABLE paragon_player_bank (
    user_id integer NOT NULL,
    slot integer NOT NULL,
    id integer NOT NULL,
    amount integer NOT NULL,
    PRIMARY KEY (user_id, slot)
);

CREATE TABLE paragon_player_bank_count (
    user_id integer NOT NULL PRIMARY KEY,
    tab_1 smallint DEFAULT 0 NOT NULL,
    tab_2 smallint DEFAULT 0 NOT NULL,
    tab_3 smallint DEFAULT 0 NOT NULL,
    tab_4 smallint DEFAULT 0 NOT NULL,
    tab_5 smallint DEFAULT 0 NOT NULL,
    tab_6 smallint DEFAULT 0 NOT NULL,
    tab_7 smallint DEFAULT 0 NOT NULL,
    tab_8 smallint DEFAULT 0 NOT NULL,
    tab_9 smallint DEFAULT 0 NOT NULL
);

CREATE TABLE paragon_player_equipment (
    user_id integer NOT NULL,
    slot character varying(45) DEFAULT 'Head'::character varying NOT NULL,
    id integer NOT NULL,
    amount integer DEFAULT 1 NOT NULL,
    PRIMARY KEY (user_id, slot)
);

CREATE TABLE paragon_player_inventory (
    user_id integer NOT NULL,
    slot smallint NOT NULL,
    id integer NOT NULL,
    amount integer NOT NULL,
    PRIMARY KEY (user_id, slot)
);

CREATE TABLE paragon_player_relationship (
    user_id integer NOT NULL,
    peer_id integer NOT NULL,
    status_relationship relationship_type DEFAULT 'FRIEND'::relationship_type NOT NULL,
    PRIMARY KEY (user_id, peer_id)
);

CREATE TABLE paragon_player_clan (
    user_id integer NOT NULL PRIMARY KEY,
    clan_name character varying(40) NOT NULL,
    entry_requirement clan_privilege DEFAULT 'ANYONE'::clan_privilege NOT NULL,
    talk_requirement clan_privilege DEFAULT 'ANYONE'::clan_privilege NOT NULL,
    kick_requirement clan_privilege DEFAULT 'SERGEANT'::clan_privilege NOT NULL
);

CREATE TABLE paragon_player_skill (
    user_id integer NOT NULL,
    label_skill character varying(45) NOT NULL,
    level smallint DEFAULT 1 NOT NULL,
    exp double precision DEFAULT 0 NOT NULL,
    PRIMARY KEY (user_id, label_skill)
);

CREATE TABLE paragon_player_log_chat (
    user_id integer NOT NULL,
    date timestamp without time zone DEFAULT now() NOT NULL,
    pm boolean DEFAULT false NOT NULL,
    message text NOT NULL,
    PRIMARY KEY (user_id, date)
);

CREATE TABLE paragon_player_log_packet (
    user_id integer NOT NULL,
    data text NOT NULL,
    date timestamp without time zone DEFAULT now() NOT NULL
);

CREATE TABLE paragon_player_log_stake (
    user_id integer NOT NULL,
    items_staked text NOT NULL,
    items_received text NOT NULL,
    other_staker text NOT NULL,
    date timestamp without time zone DEFAULT now() NOT NULL,
    PRIMARY KEY (user_id, date)
);

CREATE TABLE paragon_player_log_trade (
    user_id integer NOT NULL,
    date timestamp without time zone DEFAULT now() NOT NULL,
    items_traded text NOT NULL,
    items_received text NOT NULL,
    other_username text NOT NULL,
    PRIMARY KEY (user_id, date)
);

CREATE TABLE paragon_player_log_command (
    id serial NOT NULL PRIMARY KEY,
    user_id integer NOT NULL,
    command text NOT NULL,
    date timestamp without time zone DEFAULT now() NOT NULL
);

CREATE TABLE paragon_map_keys (
    id serial NOT NULL PRIMARY KEY,
    region_id integer NOT NULL,
    key_1 integer,
    key_2 integer NOT NULL,
    key_3 integer NOT NULL,
    key_4 integer NOT NULL
);

