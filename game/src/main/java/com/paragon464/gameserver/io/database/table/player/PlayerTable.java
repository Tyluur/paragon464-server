package com.paragon464.gameserver.io.database.table.player;

import com.paragon464.gameserver.io.database.table.Table;
import com.paragon464.gameserver.model.entity.mob.player.Player;

import java.io.IOException;
import java.sql.SQLException;

public final class PlayerTable extends Table<Player> {

    @Override
    public void load(Player player) throws SQLException, IOException {
      /*  try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM paragon_player WHERE user_id = ?")) {
            statement.setInt(1, player.getDetails().getUserId());

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                player.getVariables().setStarted(resultSet.getBoolean("started"));

                player.setPosition(new Position(resultSet.getInt("pos_x"), resultSet.getInt("pos_Y"), resultSet.getInt("pos_Z")));

                final int[] color = Stream.of(resultSet.getString("colors").split(",")).mapToInt(Integer::parseInt).toArray();
                final int[] style = Stream.of(resultSet.getString("looks").split(",")).mapToInt(Integer::parseInt).toArray();
                player.getAppearance().setGender(resultSet.getByte("gender"));
                player.getAppearance().setColoursArray(color);
                player.getAppearance().setLookArray(style);

                player.getVariables().getCoffinSession().brothersDead = Arrays.stream(resultSet.getString("barrows_dead").split(","))
                    .collect(MoreCollectors.toBooleanArray(Boolean::parseBoolean));

                player.getAttributes().set("mage_arena", resultSet.getBoolean("mage_arena_finished"));
                player.getAttributes().set("lunar_complete", resultSet.getBoolean("lunars_finished"));

                player.getAttributes().set("caves_wave", resultSet.getInt("caves_wave"));
                player.getAttributes().set("wand_lvl", resultSet.getInt("wand_lvl"));
                player.getAttributes().set("rfd_stage", resultSet.getInt("rfd_stage"));

                player.getAttributes().set("slayer_points", resultSet.getInt("slayer_points"));
                player.getAttributes().set("rc_points", resultSet.getInt("runecraft_points"));

                player.getAttributes().set("armadyl_kc", resultSet.getInt("armadyl_kc"));
                player.getAttributes().set("saradomin_kc", resultSet.getInt("saradomin_kc"));
                player.getAttributes().set("bandos_kc", resultSet.getInt("bandos_kc"));
                player.getAttributes().set("zamorak_kc", resultSet.getInt("zamorak_kc"));

                player.getSlayer().setTask(resultSet.getString("slayer_task_name"));
                player.getSlayer().setAmount(resultSet.getByte("slayer_task_count"));
                player.getSlayer().setArea(resultSet.getString("slayer_task_area"));

                player.getSettings().setAutoRetaliate(resultSet.getBoolean("auto_retaliate"));
                player.getSettings().setMagicType(resultSet.getByte("magic_type"));
                player.getCombatState().setRecoilCount(resultSet.getInt("recoil_count"));
                player.getAttackVars().setSkill(resultSet.getString("av_skill"));
                player.getAttackVars().setStyle(resultSet.getString("av_style"));
                player.getAttackVars().setSlot(resultSet.getInt("av_slot"));

                player.getFriendsAndIgnores().setPrivateStatus(resultSet.getInt("pm_status"));
                player.getSettings().setPrivateChatSplit(resultSet.getBoolean("splitchat"), false);

                player.getSettings().setSpecialAmount(resultSet.getByte("special_energy"), false);
                player.getSettings().setPrayerPoints(resultSet.getDouble("prayer_points"));
                player.getSettings().setEnergy(resultSet.getDouble("energy"), false);

                player.getSettings().setNewHits(resultSet.getBoolean("hits_multiplied"));
                player.getSettings().setNewHp(resultSet.getBoolean("hitpoint_bars_type"));
                player.getSettings().setNewMarkers(resultSet.getBoolean("hitmarkers_type"));
                player.getSettings().setNewMenus(resultSet.getBoolean("context_menus_type"));
                player.getSettings().setNewFonts(resultSet.getBoolean("fonts_type"));
                player.getSettings().setNewKeys(resultSet.getBoolean("hotkeys_type"));

                player.getAttributes().set("vote_points", resultSet.getInt("vote_points"));
                player.getAttributes().set("zombies_points", resultSet.getInt("zombies_points"));
                player.getAttributes().set("bank_pin_hash", resultSet.getInt("bank_pin"));
                player.getAttributes().set("dt_stage", resultSet.getInt("dt_stage"));
                player.getAttributes().set("md_stage", resultSet.getInt("md_stage"));
                player.getAttributes().set("brain_robbery_stage", resultSet.getInt("brain_robbery_stage"));
                player.getAttributes().set("quest_points", resultSet.getInt("quest_points"));
                player.getAttributes().set("bonus_xp_ticks", resultSet.getInt("bonus_xp_ticks"));
                player.getAttributes().set("xp_multiplier", resultSet.getInt("xp_multiplier"));
                player.getAttributes().set("last_login", resultSet.getLong("last_login"));
                player.getAttributes().set("received_validation_package", resultSet.getBoolean("received_validation_package"));
                player.getAttributes().set("skull_timer", resultSet.getInt("skull_timer"));
                player.getAttributes().set("nightmare_points", resultSet.getInt("nightmare_points"));
                player.getAttributes().set("nightmare_wave", resultSet.getInt("nightmare_wave"));
                player.getAttributes().set("prayers_unlocked", resultSet.getBoolean("prayers_unlocked"));
                player.getAttributes().set("tutorial_completed", resultSet.getBoolean("tutorial_completed"));
                player.getSettings().toggleCurses(resultSet.getBoolean("curses_enabled"));
                player.getAttributes().set("quick_prayers_value", resultSet.getInt("quick_prayers"));
                player.getVariables().setLastAddress(resultSet.getString("last_address"));
                player.getVariables().setTotalExp(resultSet.getInt("exp_counter"));
                player.getVariables().setExpLocked(resultSet.getBoolean("exp_locked"));
            }
        }*/
    }

    @Override
    public void save(Player player) throws SQLException, IOException {
 /*       final String PLAYER_VARS = "user_id, pos_x, pos_y, pos_z, energy, prayer_points, magic_type, special_energy, gender, looks, colors, av_skill, av_style, av_slot, started, pm_status, auto_retaliate, " +
            "recoil_count, hitpoint_bars_type, hitmarkers_type, context_menus_type, hits_multiplied, hotkeys_type, fonts_type, caves_wave, barrows_dead, mage_arena_finished, lunars_finished, rfd_stage, " +
            "wand_lvl, poison_count, slayer_points, runecraft_points, splitchat, armadyl_kc, bandos_kc, saradomin_kc, zamorak_kc, slayer_task_name, slayer_task_count, slayer_task_area, vote_points, bank_pin, " +
            "dt_stage, md_stage, brain_robbery_stage, quest_points, bonus_xp_ticks, xp_multiplier, last_login, received_validation_package, zombies_points, skull_timer, nightmare_points, nightmare_wave, prayers_unlocked, "
            + "tutorial_completed, curses_enabled, quick_prayers, last_address, exp_counter, exp_locked";

        StringBuilder query = new StringBuilder("INSERT INTO paragon_player (");
        query.append(PLAYER_VARS);
        query.append(") VALUES (");
        String[] varSplits = PLAYER_VARS.split(", ");
        for (int i = 0; i < varSplits.length; i++) {
            if (i == (varSplits.length - 1)) {
                query.append("?");
            } else if (i == 59) {
                query.append("?::INET, "); // sorry lmao
            } else {
                query.append("?, ");
            }
        }
        query.append(") ON CONFLICT (user_id) DO UPDATE SET ");
        for (String playerVar : PLAYER_VARS.trim().split(",")) {
            query.append(playerVar);
            query.append(" = EXCLUDED.");
            query.append(playerVar);
            query.append(", ");
        }
        query.delete(query.length() - 2, query.length());

        try (Connection connection = ConnectionPool.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement(query.toString())) {
            statement.setInt(1, player.getDetails().getUserId());
            statement.setInt(2, player.getPosition().getX());
            statement.setInt(3, player.getPosition().getY());
            statement.setInt(4, player.getPosition().getZ());
            statement.setDouble(5, player.getSettings().getEnergy());
            statement.setDouble(6, player.getSettings().getPrayerPoints());
            statement.setByte(7, (byte) player.getSettings().getMagicType());
            statement.setByte(8, (byte) player.getSettings().getSpecialAmount());
            statement.setByte(9, (byte) player.getAppearance().getGender());
            statement.setString(10, TextUtils.implode(",", player.getAppearance().getLookArray()));
            statement.setString(11, TextUtils.implode(",", player.getAppearance().getColoursArray()));
            statement.setString(12, player.getAttackVars().getSkill().name());
            statement.setString(13, player.getAttackVars().getStyle().name());
            statement.setInt(14, player.getAttackVars().getSlot());
            statement.setBoolean(15, player.getVariables().hasReceivedStarter());
            statement.setInt(16, player.getFriendsAndIgnores().getPrivateStatus());
            statement.setBoolean(17, player.getSettings().isAutoRetaliating());
            statement.setInt(18, player.getCombatState().getRecoilCount());
            statement.setBoolean(19, player.getSettings().isNewHp());
            statement.setBoolean(20, player.getSettings().isNewMarkers());
            statement.setBoolean(21, player.getSettings().isNewMenus());
            statement.setBoolean(22, player.getSettings().isNewHits());
            statement.setBoolean(23, player.getSettings().isNewKeys());
            statement.setBoolean(24, player.getSettings().isNewFonts());
            statement.setByte(25, player.getAttributes().getByte("caves_wave"));
            statement.setString(26, TextUtils.implode(",", player.getVariables().getCoffinSession().brothersDead));
            statement.setBoolean(27, player.getAttributes().is("mage_arena"));
            statement.setBoolean(28, player.getAttributes().is("lunar_complete"));
            statement.setByte(29, player.getAttributes().getByte("rfd_stage"));
            statement.setByte(30, player.getAttributes().getByte("wand_lvl"));
            statement.setByte(31, (byte) player.getCombatState().getPoisonCount());
            statement.setInt(32, player.getAttributes().getInt("slayer_points"));
            statement.setInt(33, player.getAttributes().getInt("rc_points"));
            statement.setBoolean(34, player.getSettings().isPrivateChatSplit());
            statement.setInt(35, player.getAttributes().getInt("armadyl_kc"));
            statement.setInt(36, player.getAttributes().getInt("bandos_kc"));
            statement.setInt(37, player.getAttributes().getInt("saradomin_kc"));
            statement.setInt(38, player.getAttributes().getInt("zamorak_kc"));
            statement.setString(39, player.getSlayer().getTask());
            statement.setByte(40, player.getSlayer().getAmount());
            statement.setString(41, player.getSlayer().getArea());
            statement.setInt(42, player.getAttributes().getInt("vote_points"));
            statement.setInt(43, player.getAttributes().getInt("bank_pin_hash"));
            statement.setInt(44, player.getAttributes().getInt("dt_stage"));
            statement.setInt(45, player.getAttributes().getInt("md_stage"));
            statement.setInt(46, player.getAttributes().getInt("brain_robbery_stage"));
            statement.setInt(47, player.getAttributes().getInt("quest_points"));
            statement.setInt(48, player.getAttributes().getInt("bonus_xp_ticks"));
            statement.setInt(49, player.getAttributes().getInt("xp_multiplier"));
            statement.setLong(50, player.getAttributes().getLong("last_login"));
            statement.setBoolean(51, player.getAttributes().is("received_validation_package"));
            statement.setInt(52, player.getAttributes().getInt("zombies_points"));
            statement.setInt(53, player.getAttributes().getInt("skull_timer"));
            statement.setInt(54, player.getAttributes().getInt("nightmare_points"));
            statement.setInt(55, player.getAttributes().getInt("nightmare_wave"));
            statement.setBoolean(56, player.getAttributes().is("prayers_unlocked"));
            statement.setBoolean(57, player.getAttributes().is("tutorial_completed"));
            statement.setBoolean(58, player.getSettings().isCursesEnabled());
            statement.setInt(59, player.getAttributes().getInt("quick_prayers_value"));
            statement.setString(60, player.getVariables().getCurrentAddress());
            statement.setInt(61, player.getVariables().getTotalExp());
            statement.setBoolean(62, player.getVariables().isExpLocked());
            statement.executeUpdate();
        }*/
    }
}
