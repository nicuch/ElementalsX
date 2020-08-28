package ro.nicuch.elementalsx.deathmessage;

import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.BlockProjectileSource;
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
        if (optionalUser.isEmpty())
            return;
        optionalUser.get().setLastDamageCause(event.getCause());
    }

    @EventHandler
    public void event(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity()))
            return;
        Optional<User> optionalUser = ElementalsX.getUser(event.getEntity());
        if (optionalUser.isEmpty())
            return;
        User user = optionalUser.get();
        int change_2 = ElementalsUtil.nextInt(2);
        switch (user.getLastDamageCause()) {
            case BLOCK_EXPLOSION:
                if (change_2 == 0)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa facut poc."));
                else if (change_2 == 1)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa explodat."));
                return;
            case CONTACT:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa imbratisat un cactus."));
                return;
            case DROWNING:
                if (change_2 == 0)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a inecat."));
                else if (change_2 == 1)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&onu a stiut sa inoate"));
                return;
            case ENTITY_ATTACK:
                Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
                /*if (MythicMobs.inst().getAPIHelper().isMythicMob(damager)) {
                    if (damager.getType() == EntityType.RABBIT) {
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost sfasiat de &f&o"
                                + MythicMobs.inst().getAPIHelper().getMythicMobInstance(damager).getDisplayName() + "&9&o."));
                    } else
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un &f&o"
                                + MythicMobs.inst().getAPIHelper().getMythicMobInstance(damager).getDisplayName() + "&9&o."));
                    return;
                } else*/
                switch (damager.getType()) {
                    case PLAYER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de &f&o"
                                + (damager).getName() + "&9&o."));
                        return;
                    case BEE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de o &eALBINA UCIGASA&9&o!"));
                        return;
                    case BLAZE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un blaze."));
                        return;
                    case CAVE_SPIDER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un paianjen de pestera."));
                        return;
                    case ENDER_DRAGON:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de dragon."));
                        return;
                    case ENDERMAN:
                        if (ElementalsUtil.hasTag(damager, "end_monster")) {
                            event.setDeathMessage(ElementalsUtil.color(
                                    "&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un &5Enderman de End&9&o."));
                            return;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un enderman."));
                        return;
                    case ENDERMITE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un endermite."));
                        return;
                    case GIANT:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un gigant?!"));
                        return;
                    case GUARDIAN:
                    case ELDER_GUARDIAN:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un gardian."));
                        return;
                    case IRON_GOLEM:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un golem de fier."));
                        return;
                    case MAGMA_CUBE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un cub de magma."));
                        return;
                    case ZOMBIFIED_PIGLIN:
                        if (ElementalsUtil.hasTag(damager, "lava_monster")) {
                            event.setDeathMessage(ElementalsUtil.color(
                                    "&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un &4Zombie de Lava&9&o."));
                            return;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un piglin zombie."));
                        return;
                    case PIGLIN:
                        PlayerInventory playerInventory = user.getBase().getInventory();
                        Material helmet = playerInventory.getHelmet() == null ? Material.AIR : playerInventory.getHelmet().getType();
                        Material chest_plate = playerInventory.getChestplate() == null ? Material.AIR : playerInventory.getChestplate().getType();
                        Material leggings = playerInventory.getLeggings() == null ? Material.AIR : playerInventory.getLeggings().getType();
                        Material boots = playerInventory.getBoots() == null ? Material.AIR : playerInventory.getBoots().getType();
                        if (helmet == Material.GOLDEN_HELMET || chest_plate == Material.GOLDEN_CHESTPLATE || leggings == Material.GOLDEN_LEGGINGS || boots == Material.GOLDEN_BOOTS) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa enervat un piglin si a fost omorat."));
                            return;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&onu avea armura de aur echipata si sa intalnit cu un piglin."));
                        return;
                    case PIGLIN_BRUTE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un piglin brut."));
                        return;
                    case HOGLIN:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost fugarit de un hoglin."));
                        return;
                    case ZOGLIN:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost fugarit de un zoglin."));
                        return;
                    case SILVERFISH:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un peste argintiu."));
                        return;
                    case SKELETON:
                        if (ElementalsUtil.hasTag(damager, "herobrine")) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de &cHerobrine&9&o."));
                            return;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un schelete."));
                        return;
                    case WITHER_SKELETON:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un schelete wither."));
                        return;
                    case STRAY:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un stray."));
                        return;
                    case WOLF:
                        if (((Wolf) damager).isTamed())
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un caine."));
                        else
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un lup."));
                        return;
                    case ZOMBIE:
                        if (ElementalsUtil.hasTag(damager, "ice_monster")) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un Zombie de Gheata."));
                            return;
                        } else if (ElementalsUtil.hasTag(damager, "herobrine")) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de &cHerobrine&9&o."));
                            return;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie."));
                        return;
                    case HUSK:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un husk."));
                        return;
                    case ZOMBIE_VILLAGER:
                        switch (((ZombieVillager) damager).getVillagerProfession()) {
                            case ARMORER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie fierar de armuri."));
                                return;
                            case BUTCHER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie macelar."));
                                return;
                            case FARMER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie fermier."));
                                return;
                            case LIBRARIAN:
                                event.setDeathMessage(ElementalsUtil.color(
                                        "&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie bibliotecar."));
                                return;
                            case CARTOGRAPHER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie cartograf."));
                                return;
                            case CLERIC:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie preot."));
                                return;
                            case FISHERMAN:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie pescar."));
                                return;
                            case FLETCHER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie arcas."));
                                return;
                            case LEATHERWORKER:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie pielar."));
                                return;
                            case MASON:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie zidar."));
                                return;
                            case SHEPHERD:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie pastor."));
                                return;
                            case TOOLSMITH:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie fierar de unelte."));
                                return;
                            case WEAPONSMITH:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie fierar de arme."));
                                return;
                            case NITWIT:
                            case NONE:
                            default:
                                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie villager."));
                                return;
                        }
                    case POLAR_BEAR:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un urs polar."));
                        return;
                    case SPIDER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un paianjen."));
                        return;
                    case SLIME:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un slime."));
                        return;
                    case VINDICATOR:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un vindicator."));
                        return;
                    case VEX:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un demon."));
                        return;
                    case EVOKER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un magician."));
                        return;
                    case EVOKER_FANGS:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost mancat."));
                        return;
                    case PANDA:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un urs panda."));
                        return;
                    case PILLAGER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un pillager."));
                        return;
                    case RAVAGER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un ravanger."));
                        return;
                    case DOLPHIN:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un delfin."));
                        return;
                    case DROWNED:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un zombie inecat."));
                        return;
                    case PHANTOM:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat o fantoma a noptii."));
                        return;
                    case FALLING_BLOCK:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost strivit."));
                        return;
                    case WITHER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un wither."));
                        return;
                    default:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa murit."));
                        return;
                }
            case ENTITY_EXPLOSION:
                switch (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager().getType()) {
                    case CREEPER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa imbratisat un creeper."));
                        return;
                    case PRIMED_TNT:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa s-a jucat cu TNT."));
                        return;
                    case MINECART_TNT:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa s-a jucat cu TNT pe sinele de tren."));
                        return;
                    case FIREBALL:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa a intalnit un ghast."));
                        return;
                    case DRAGON_FIREBALL:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa a intalnit un dragon."));
                        return;
                    case ENDER_CRYSTAL:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa murit deoarece &kwhy are u reading this?&9&o."));
                        return;
                    case WITHER:
                    case WITHER_SKULL:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a jucat cu un wither si a explodat."));
                        return;
                    default:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa e explodat."));
                        return;
                }
            case FALL:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa incercat sa zboare."));
                return;
            case FALLING_BLOCK:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost lovit in cap cu o nicovala."));
                return;
            case FIRE:
            case FIRE_TICK:
                if (change_2 == 0)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa devenit friptura."));
                else if (change_2 == 1)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a jucat cu focul."));
                return;
            case LAVA:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa inotat in lava."));
                return;
            case LIGHTNING:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost fulgerat."));
                return;
            case MAGIC:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost transformat in broasca."));
                return;
            case POISON:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost otravit."));
                return;
            case PROJECTILE:
                if (((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager())
                        .getShooter() instanceof BlockProjectileSource) {
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName()
                            + " &9&oa incercat sa verifice daca mai sunt sageti in dispenser."));
                    return;
                }
                Entity dmgr = (Entity) ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause())
                        .getDamager()).getShooter();
                switch (dmgr.getType()) {
                    case BLAZE:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un blaze."));
                        return;
                    case PLAYER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de "
                                + ((Player) dmgr).getDisplayName() + "&a."));
                        return;
                    case SNOWMAN:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un om de zapada."));
                        return;
                    case SKELETON:
                        if (dmgr.hasMetadata("herobrine")) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de &cHerobrine&9&o."));
                            return;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un schelete."));
                        return;
                    case GHAST:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un ghast."));
                        return;
                    case WITHER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, a dat cap in cap cu un wither."));
                        return;
                    case ENDER_DRAGON:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, nu te pune cu dragonul! ^^"));
                        return;
                    case LLAMA:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&oa fost scuipat de o lama."));
                        return;
                    case PILLAGER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat de un pillager."));
                        return;
                    case SHULKER:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost impuscat de un shulker."));
                        return;
                    case PIGLIN:
                        PlayerInventory playerInventory = user.getBase().getInventory();
                        Material helmet = playerInventory.getHelmet() == null ? Material.AIR : playerInventory.getHelmet().getType();
                        Material chest_plate = playerInventory.getChestplate() == null ? Material.AIR : playerInventory.getChestplate().getType();
                        Material leggings = playerInventory.getLeggings() == null ? Material.AIR : playerInventory.getLeggings().getType();
                        Material boots = playerInventory.getBoots() == null ? Material.AIR : playerInventory.getBoots().getType();
                        if (helmet == Material.GOLDEN_HELMET || chest_plate == Material.GOLDEN_CHESTPLATE || leggings == Material.GOLDEN_LEGGINGS || boots == Material.GOLDEN_BOOTS) {
                            event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa enervat un piglin si a fost omorat."));
                            return;
                        }
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&onu avea armura de aur echipata si sa intalnit cu un piglin."));
                        return;
                    default:
                        event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost lovit cu un proiectil."));
                        return;
                }
            case STARVATION:
                if (change_2 == 0)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, unde iti este mancarea?"));
                else if (change_2 == 1)
                    event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa murit de foame."));
                return;
            case SUFFOCATION:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa devenit una cu pamantul."));
                return;
            case SUICIDE:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&osi-a luat adio de la viata."));
                return;
            case THORNS:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost intepat (putin)."));
                return;
            case VOID:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, cum ai ajuns in void? O.o"));
                return;
            case WITHER:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + "&9&o, witherul are 3 capete iar tu doar unul."));
                return;
            case DRAGON_BREATH:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa simtit un miros neplacut."));
                return;
            case FLY_INTO_WALL:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a dat cu capul de un perete."));
                return;
            case HOT_FLOOR:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa mers pe unde nu trebuia."));
                return;
            case CRAMMING:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa fost omorat cu o imbratisare."));
                return;
            case MELTING:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a topit de la caldura."));
                return;
            case ENTITY_SWEEP_ATTACK:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&ose afla in raza unui atac."));
                return;
            case DRYOUT:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&os-a deshidratat.")); //TODO not finished
                return;
            case CUSTOM:
            default:
                event.setDeathMessage(ElementalsUtil.color("&8[&4Info&8] &f&o" + user.getBase().getName() + " &9&oa murit."));
        }
    }
}
