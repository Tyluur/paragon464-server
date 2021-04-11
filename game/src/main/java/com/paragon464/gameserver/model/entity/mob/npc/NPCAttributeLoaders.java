package com.paragon464.gameserver.model.entity.mob.npc;

import com.paragon464.gameserver.model.content.combat.npcs.Agrith;
import com.paragon464.gameserver.model.content.combat.npcs.AhrimBrother;
import com.paragon464.gameserver.model.content.combat.npcs.ArmadylKree;
import com.paragon464.gameserver.model.content.combat.npcs.Aviansie;
import com.paragon464.gameserver.model.content.combat.npcs.BattleMages;
import com.paragon464.gameserver.model.content.combat.npcs.Bree;
import com.paragon464.gameserver.model.content.combat.npcs.BrutalGreenDragon;
import com.paragon464.gameserver.model.content.combat.npcs.ChaosElemental;
import com.paragon464.gameserver.model.content.combat.npcs.CommanderZilyana;
import com.paragon464.gameserver.model.content.combat.npcs.CorporealBeast;
import com.paragon464.gameserver.model.content.combat.npcs.DagPrime;
import com.paragon464.gameserver.model.content.combat.npcs.DagSupreme;
import com.paragon464.gameserver.model.content.combat.npcs.Dagannoth;
import com.paragon464.gameserver.model.content.combat.npcs.Dessous;
import com.paragon464.gameserver.model.content.combat.npcs.DharokBrother;
import com.paragon464.gameserver.model.content.combat.npcs.Dragons;
import com.paragon464.gameserver.model.content.combat.npcs.ElfWarrior;
import com.paragon464.gameserver.model.content.combat.npcs.Fareed;
import com.paragon464.gameserver.model.content.combat.npcs.FlockleaderGeerin;
import com.paragon464.gameserver.model.content.combat.npcs.GeneralGraador;
import com.paragon464.gameserver.model.content.combat.npcs.Growler;
import com.paragon464.gameserver.model.content.combat.npcs.Inadequacy;
import com.paragon464.gameserver.model.content.combat.npcs.InfernoMage;
import com.paragon464.gameserver.model.content.combat.npcs.JungleDemon;
import com.paragon464.gameserver.model.content.combat.npcs.KBD;
import com.paragon464.gameserver.model.content.combat.npcs.KQ;
import com.paragon464.gameserver.model.content.combat.npcs.Kamil;
import com.paragon464.gameserver.model.content.combat.npcs.KarilsBrother;
import com.paragon464.gameserver.model.content.combat.npcs.KetZek;
import com.paragon464.gameserver.model.content.combat.npcs.Kolodions;
import com.paragon464.gameserver.model.content.combat.npcs.MithrilDragon;
import com.paragon464.gameserver.model.content.combat.npcs.MonkeyGuards;
import com.paragon464.gameserver.model.content.combat.npcs.SergeantGrimspike;
import com.paragon464.gameserver.model.content.combat.npcs.SergeantSteelwill;
import com.paragon464.gameserver.model.content.combat.npcs.Spinolayp;
import com.paragon464.gameserver.model.content.combat.npcs.SpiritualMage;
import com.paragon464.gameserver.model.content.combat.npcs.SpiritualRanger;
import com.paragon464.gameserver.model.content.combat.npcs.TokXil;
import com.paragon464.gameserver.model.content.combat.npcs.TormentedDemon;
import com.paragon464.gameserver.model.content.combat.npcs.TzTokJad;
import com.paragon464.gameserver.model.content.combat.npcs.Venenatis;
import com.paragon464.gameserver.model.content.combat.npcs.Wallasaki;
import com.paragon464.gameserver.model.content.combat.npcs.WingmanSkree;
import com.paragon464.gameserver.model.content.combat.npcs.ZamorakGritch;
import com.paragon464.gameserver.model.content.combat.npcs.ZamorakKreeyath;
import com.paragon464.gameserver.model.content.combat.npcs.ZamorakKril;
import com.paragon464.gameserver.model.content.combat.npcs.ZombieMage;

public class NPCAttributeLoaders {

    public static void init(NPC npc) {
        int id = npc.getId();
        if (id >= 907 && id <= 911) {
            npc.setAttackLayout(new Kolodions());
            return;
        }
        switch (id) {
            case 1472:
                npc.setAttackLayout(new JungleDemon());
                break;
            case 106504:
                npc.setAttackLayout(new Venenatis());
                break;
            case 3200:
                npc.setAttackLayout(new ChaosElemental());
                break;
            case 8133:
                npc.setAttackLayout(new CorporealBeast(npc));
                break;
            case 8349:
                npc.setAttackLayout(new TormentedDemon(npc));
                break;
            case 2457:
                npc.setAttackLayout(new Wallasaki());
                break;
            case 6221:
            case 6231:
            case 6278:
            case 6257:
                npc.setAttackLayout(new SpiritualMage());
                break;
            case 6220:
            case 6230:
            case 6276:
            case 6256:
                npc.setAttackLayout(new SpiritualRanger());
                break;
            case 6225:
                npc.setAttackLayout(new FlockleaderGeerin());
                break;
            case 6203:
                npc.setAttackLayout(new ZamorakKril());
                break;
            case 6206:
                npc.setAttackLayout(new ZamorakGritch());
                break;
            case 6208:
                npc.setAttackLayout(new ZamorakKreeyath());
                break;
            case 6250:
                npc.setAttackLayout(new Growler());
                break;
            case 6252:
                npc.setAttackLayout(new Bree());
                break;
            case 6247:
                npc.setAttackLayout(new CommanderZilyana());
                break;
            case 6222:
                npc.setAttackLayout(new ArmadylKree());
                break;
            case 6260:
                npc.setAttackLayout(new GeneralGraador());
                break;
            case 6232:// Aviansie
                npc.setAttackLayout(new Aviansie());
                break;
            case 912:
            case 913:
            case 914:
                npc.setAttackLayout(new BattleMages());
                break;
            case 1643:// Infernal mage
                npc.setAttackLayout(new InfernoMage());
                break;
            case 1590:// Bronze drag
            case 1591:// Iron drag
            case 1592:// Steel drag
            case 941:// Green drag
            case 53:// Red drag
            case 54:// Black drag
            case 55:// Blue drag
                npc.setAttackLayout(new Dragons());
                break;
            case 5362:// Brutal green dragon
                npc.setAttackLayout(new BrutalGreenDragon());
                break;
            case 5363:// Mith dragon
                npc.setAttackLayout(new MithrilDragon());
                break;
            case 3068:// Skeletal wyvern
                // npc.setAttackLayout(new SkeletalWyvern());
                break;
            case 2882:// Dag prime
                npc.setAttackLayout(new DagPrime());
                break;
            case 2881:// Dag supreme
                npc.setAttackLayout(new DagSupreme());
                break;
            case 2892:// Spino
                npc.setAttackLayout(new Spinolayp());
                break;
            case 1183:// Elf warrior
                npc.setAttackLayout(new ElfWarrior());
                break;
            case 6223:// Wingman Skree
                npc.setAttackLayout(new WingmanSkree());
                break;
            case 6263:// Sergeant Steelwill
                npc.setAttackLayout(new SergeantSteelwill());
                break;
            case 6265:// Sergeant Grimspike
                npc.setAttackLayout(new SergeantGrimspike());
                break;
            case 1459:// Monkey guard
                npc.setAttackLayout(new MonkeyGuards());
                break;
            case 750:// Zombie mage
                npc.setAttackLayout(new ZombieMage());
                break;
            case 2745:// Jad
                npc.setAttackLayout(new TzTokJad());
                break;
            case 2743:// lvl 360 fight cave monster
                npc.setAttackLayout(new KetZek());
                break;
            case 2739:// lvl 90 range fight cave monster
                npc.setAttackLayout(new TokXil());
                break;
            case 3497:// dag mother
            case 1338:// regular dag
                npc.setAttackLayout(new Dagannoth());
                break;
            case 3493:// Agrith
                npc.setAttackLayout(new Agrith());
                break;
            case 5902:// Inadequacy
                npc.setAttackLayout(new Inadequacy());
                break;
            case 1158:// KQ
            case 1160:// KQ 2nd
                npc.setAttackLayout(new KQ());
                break;
            case 50:// KBD
                npc.setAttackLayout(new KBD());
                break;
            case 2026:// Dharok
                npc.setAttackLayout(new DharokBrother());
                break;
            case 2025:// Ahrim
                npc.setAttackLayout(new AhrimBrother());
                break;
            case 2028:// Karil
                npc.setAttackLayout(new KarilsBrother());
                break;
            case 1914:// Dessous
                npc.setAttackLayout(new Dessous());
                break;
            case 1913:// Kamil
            case 3495:// Not sure
                npc.setAttackLayout(new Kamil());
                break;
            case 1977:// Fareed
                npc.setAttackLayout(new Fareed());
                break;
        }
    }
}
