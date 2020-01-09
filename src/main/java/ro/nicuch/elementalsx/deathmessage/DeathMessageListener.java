package ro.nicuch.elementalsx.deathmessage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.BlockProjectileSource;

import net.citizensnpcs.api.CitizensAPI;
import ro.nicuch.elementalsx.ElementalsX;
import ro.nicuch.elementalsx.User;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

import java.util.Optional;

public class DeathMessageListener implements Listener {


    @EventHandler
    public void event(EntityDamageEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        if (event.getEntity().getType() != EntityType.PLAYER)
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getEntity().getUniqueId());
        if (!optionalUser.isPresent())
            return;
        optionalUser.get().setLastDamageCause(event.getCause());
    }

    @EventHandler
    public void event(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getEntity());
        if (!optionalUser.isPresent())
            return;
        User user = optionalUser.get();
        int change_2 = ElementalsUtil.nextInt(2);
        switch (user.getLastDamageCause()) {
            case BLOCK_EXPLOSION:
                if (change_2 == 0)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa facut poc."));
                else if (change_2 == 1)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa explodat."));
                break;
            case CONTACT:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa imbratisat un cactus."));
                break;
            case DROWNING:
                if (change_2 == 0)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a inecat."));
                else if (change_2 == 1)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&onu a stiut sa inoate"));
                break;
            case ENTITY_ATTACK:
                Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
                switch (damager.getType()) {
                    case PLAYER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de &f&o"
                                + (damager).getName() + "&9&o."));
                        break;
                    case BEE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de o &eALBINA UCIGASA&9&o!"));
                        break;
                    case BLAZE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un blaze."));
                        break;
                    case CAVE_SPIDER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un paianjen de pestera."));
                        break;
                    case ENDER_DRAGON:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de dragon."));
                        break;
                    case ENDERMAN:
                        if (ElementalsUtil.hasTag(damager, "end_monster")) {
                            event.setDeathMessage(ElementalsUtil.color(
                                    "&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un &5Enderman de End&9&o."));
                            break;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un enderman."));
                        break;
                    case ENDERMITE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un endermite."));
                        break;
                    case GIANT:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un gigant?!"));
                        break;
                    case GUARDIAN:
                    case ELDER_GUARDIAN:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un gardian."));
                        break;
                    case IRON_GOLEM:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un golem de fier."));
                        break;
                    case MAGMA_CUBE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un cub de magma."));
                        break;
                    case PIG_ZOMBIE:
                        if (ElementalsUtil.hasTag(damager, "lava_monster")) {
                            event.setDeathMessage(ElementalsUtil.color(
                                    "&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un &4Zombie de Lava&9&o."));
                            break;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un pigman."));
                        break;
                    case SILVERFISH:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un peste argintiu."));
                        break;
                    case SKELETON:
                        if (ElementalsUtil.hasTag(damager, "herobrine")) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de &cHerobrine&9&o."));
                            break;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un schelete."));
                        break;
                    case WITHER_SKELETON:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un schelete wither."));
                        break;
                    case STRAY:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un stray."));
                        break;
                    case WOLF:
                        if (((Wolf) damager).isTamed())
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un caine."));
                        else
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un lup."));
                        break;
                    case ZOMBIE:
                        if (ElementalsUtil.hasTag(damager, "ice_monster")) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un Zombie de Gheata."));
                            break;
                        } else if (ElementalsUtil.hasTag(damager, "herobrine")) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de &cHerobrine&9&o."));
                            break;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie."));
                        break;
                    case HUSK:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un husk."));
                        break;
                    case ZOMBIE_VILLAGER:
                        switch (((ZombieVillager) damager).getVillagerProfession()) {
                            case ARMORER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie fierar de armuri."));
                                break;
                            case BUTCHER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie macelar."));
                                break;
                            case FARMER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie fermier."));
                                break;
                            case LIBRARIAN:
                                event.setDeathMessage(ElementalsUtil.color(
                                        "&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie bibliotecar."));
                                break;
                            case CARTOGRAPHER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie cartograf."));
                                break;
                            case CLERIC:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie preot."));
                                break;
                            case FISHERMAN:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie pescar."));
                                break;
                            case FLETCHER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie arcas."));
                                break;
                            case LEATHERWORKER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie pielar."));
                                break;
                            case MASON:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie zidar."));
                                break;
                            case SHEPHERD:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie pastor."));
                                break;
                            case TOOLSMITH:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie fierar de unelte."));
                                break;
                            case WEAPONSMITH:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie fierar de arme."));
                                break;
                            case NITWIT:
                            case NONE:
                            default:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie villager."));
                                break;
                        }
                        break;
                    case POLAR_BEAR:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un urs polar."));
                        break;
                    case SPIDER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un paianjen."));
                        break;
                    case SLIME:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un slime."));
                        break;
                    case VINDICATOR:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un vindicator."));
                        break;
                    case VEX:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un demon."));
                        break;
                    case EVOKER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un magician."));
                        break;
                    case EVOKER_FANGS:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost mancat."));
                        break;
                    case PANDA:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un urs panda."));
                        break;
                    case PILLAGER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un pillager."));
                        break;
                    case RAVAGER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un ravanger."));
                        break;
                    case DOLPHIN:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un delfin."));
                        break;
                    case DROWNED:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie inecat."));
                        break;
                    case PHANTOM:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat o fantoma a noptii."));
                        break;
                    case FALLING_BLOCK:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost strivit."));
                        break;
                    case WITHER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un wither."));
                        break;
                    default:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa murit."));
                        break;
                }
                break;
            case ENTITY_EXPLOSION:
                switch (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager().getType()) {
                    case CREEPER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa imbratisat un creeper."));
                        break;
                    case PRIMED_TNT:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa s-a jucat cu TNT."));
                        break;
                    case MINECART_TNT:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa s-a jucat cu TNT pe sinele de tren."));
                        break;
                    case FIREBALL:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa a intalnit un ghast."));
                        break;
                    case DRAGON_FIREBALL:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa a intalnit un dragon."));
                        break;
                    case ENDER_CRYSTAL:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa murit deoarece &kwhy are u reading this?&9&o."));
                        break;
                    default:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa e explodat."));
                        break;
                }
                break;
            case FALL:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa incercat sa zboare."));
                break;
            case FALLING_BLOCK:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost lovit in cap cu o nicovala."));
                break;
            case FIRE:
            case FIRE_TICK:
                if (change_2 == 0)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa devenit friptura."));
                else if (change_2 == 1)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a jucat cu focul."));
                break;
            case LAVA:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa inotat in lava."));
                break;
            case LIGHTNING:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost fulgerat."));
                break;
            case MAGIC:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost transformat in broasca."));
                break;
            case POISON:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost otravit."));
                break;
            case PROJECTILE:
                if (((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager())
                        .getShooter() instanceof BlockProjectileSource) {
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName()
                            + " &9&oa incercat sa verifice daca mai sunt sageti in dispenser."));
                    break;
                }
                Entity dmgr = (Entity) ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause())
                        .getDamager()).getShooter();
                switch (dmgr.getType()) {
                    case BLAZE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un blaze."));
                        break;
                    case PLAYER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de "
                                + ((Player) dmgr).getDisplayName() + "&a."));
                        break;
                    case SNOWMAN:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un om de zapada."));
                        break;
                    case SKELETON:
                        if (dmgr.hasMetadata("herobrine")) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de &cHerobrine&9&o."));
                            break;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un schelete."));
                        break;
                    case GHAST:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un ghast."));
                        break;
                    case WITHER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, a dat cap in cap cu un wither."));
                        break;
                    case ENDER_DRAGON:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, nu te pune cu dragonul! ^^"));
                        break;
                    case LLAMA:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&oa fost scuipat de o lama."));
                        break;
                    case PILLAGER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un pillager."));
                        break;
                    case SHULKER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost impuscat de un shulker."));
                        break;
                    default:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost lovit cu un proiectil."));
                        break;
                }
                break;
            case STARVATION:
                if (change_2 == 0)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, unde iti este mancarea?"));
                else if (change_2 == 1)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa murit de foame."));
                break;
            case SUFFOCATION:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa devenit una cu pamantul."));
                break;
            case SUICIDE:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&osi-a luat adio de la viata."));
                break;
            case THORNS:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost intepat (putin)."));
                break;
            case VOID:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, cum ai ajuns in void? O.o"));
                break;
            case WITHER:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, witherul are 3 capete iar tu doar unul."));
                break;
            case DRAGON_BREATH:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa simtit un miros neplacut."));
                break;
            case FLY_INTO_WALL:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a dat cu capul de un perete."));
                break;
            case HOT_FLOOR:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa mers pe unde nu trebuia."));
                break;
            case CRAMMING:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat cu o imbratisare."));
                break;
            case MELTING:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a topit de la caldura."));
                break;
            case ENTITY_SWEEP_ATTACK:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&ose afla in raza unui atac."));
                break;
            case DRYOUT:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a deshidratat.")); //TODO not finished
                break;
            case CUSTOM:
            default:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa murit."));
                break;
        }
    }
}
