package com.paragon464.gameserver.model.content.skills.agility;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.skills.agility.AlkharidCourse.AlkharidObstacle;
import com.paragon464.gameserver.model.content.skills.agility.DraynorCourse.DraynorObstacle;
import com.paragon464.gameserver.model.content.skills.agility.SeersCourse.SeersObstacle;
import com.paragon464.gameserver.model.content.skills.agility.VarrockCourse.VarrockObstacle;
import com.paragon464.gameserver.model.gameobjects.GameObject;

public class AgilityHandler {

    public static boolean isUsingCourse(Player player, GameObject object) {
        final AlkharidObstacle alkharid_obstacle = AlkharidObstacle.forId(object.getId());
        if (alkharid_obstacle != null) {
            return true;
        }
        final DraynorObstacle draynor_obstacle = DraynorObstacle.forId(object.getId());
        if (draynor_obstacle != null) {
            return true;
        }
        final VarrockObstacle varrock_obstacle = VarrockObstacle.forId(object.getId());
        if (varrock_obstacle != null) {
            return true;
        }
        final SeersObstacle seers_obstacle = SeersObstacle.forId(object.getId());
        return seers_obstacle != null;
    }
}
