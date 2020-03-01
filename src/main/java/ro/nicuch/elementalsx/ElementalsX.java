package ro.nicuch.elementalsx;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ro.nicuch.elementalsx.deathmessage.DeathMessageListener;
import ro.nicuch.elementalsx.elementals.ElementalsListener;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.elementals.TopKillsTrickEvent;
import ro.nicuch.elementalsx.elementals.commands.*;
import ro.nicuch.elementalsx.protection.Field;
import ro.nicuch.elementalsx.protection.FieldListener;
import ro.nicuch.elementalsx.protection.FieldUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ElementalsX extends JavaPlugin {

    private static Connection database;
    private final static ConcurrentMap<UUID, User> players = new ConcurrentHashMap<>();
    private static Economy vault;
    private static Permission perm;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        getServer().setSpawnRadius(1);
        new File(this.getDataFolder() + File.separator + "regiuni").mkdirs();
        vault = this.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        perm = this.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
        new WorldCreator("spawn").environment(Environment.NORMAL).generateStructures(false).createWorld();
        new WorldCreator("dungeon").environment(Environment.NORMAL).generateStructures(false).createWorld();
        createDataBase();
        Bukkit.getWorlds().forEach((World world) -> {
            world.setDifficulty(Difficulty.HARD);
            for (Chunk chunk : world.getLoadedChunks())
                if (chunk.isLoaded())
                    FieldUtil.loadFieldsInChunk(chunk);
        });
        this.commandsCreator();
        this.eventCreator();
        this.randomMsg();
        this.timer();
        this.topKillsTrick();
        Bukkit.getOnlinePlayers().forEach(ElementalsX::createUser);
        sendConsoleMessage("&bPluginul a pornit! (" + (System.currentTimeMillis() - start) + "ms)");
        Bukkit.broadcastMessage(ElementalsUtil.color("&bElementals a pornit! (" + (System.currentTimeMillis() - start) + "ms)"));
    }

    private void randomFireWork() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Block block;
                boolean l = true;
                for (int i = 0; i < 15; i++) {
                    block = player.getLocation().getBlock().getRelative(BlockFace.UP, 1).getRelative(BlockFace.UP, i);
                    if (block.getType() != Material.AIR)
                        l = false;
                }
                if (l)
                    ElementalsUtil.randomFirework(player.getLocation().getBlock().getLocation().add(.5, .5, .5));
            }
        }, 20 * 5L, 20 * 5L);
    }

    private void topKillsTrick() {
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getOnlinePlayers().forEach(p -> Bukkit.getPluginManager().callEvent(new TopKillsTrickEvent(p))), 20 * 30, 20 * 2 * 60);
    }

    @Override
    public void onDisable() {
        FieldUtil.getLoadedFields().forEach(Field::save);
        getOnlineUsers().forEach(u -> u.save(true));
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendConsoleMessage("&bPluginul s-a oprit!");
    }

    public static void createUser(Player player) {
        players.putIfAbsent(player.getUniqueId(), new User(player));
    }

    public static boolean existUser(UUID uuid) {
        return players.containsKey(uuid);
    }

    public static boolean existUser(Player player) {
        return existUser(player.getUniqueId());
    }

    public static Plugin get() {
        return Bukkit.getPluginManager().getPlugin("ElementalsX");
    }

    public static Connection getBase() {
        return database;
    }

    public static Collection<User> getOnlineUsers() {
        return players.values();
    }

    public static Optional<User> getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public static Optional<User> getUser(UUID uuid) {
        return Optional.ofNullable(players.get(uuid));
    }

    public static Economy getVault() {
        return vault;
    }

    public static Permission getPermission() {
        return perm;
    }

    public static void removeUser(Player player) {
        removeUser(player.getUniqueId());
    }

    public static void removeUser(UUID uuid) {
        if (players.containsKey(uuid)) {
            players.get(uuid).save(false);
            players.remove(uuid);
        }
    }

    public static void sendConsoleMessage(String arg) {
        Bukkit.getServer().getConsoleSender().sendMessage(ElementalsUtil.color(arg));
    }

    private void commandsCreator() {
        TabExecutor te = new ProtectionCommand();
        this.getCommand("ps").setExecutor(te);
        this.getCommand("ps").setTabCompleter(te);
        this.getCommand("adminchat").setExecutor(new AdminChatCommand());
        this.getCommand("adminchat").setAliases(AdminChatCommand.getAliases());

        this.getCommand("chat").setExecutor(new ChatCommand());
        this.getCommand("randomtp").setExecutor(new RandomTpCommand());
        this.getCommand("randomtp").setAliases(RandomTpCommand.getAliases());
        te = new SoundCommand();
        this.getCommand("sound").setExecutor(te);
        this.getCommand("sound").setTabCompleter(te);
        this.getCommand("giveall").setExecutor(new GiveAllCommand());
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("admin").setExecutor(new AdminCommand());
        te = new SortCommand();
        this.getCommand("sort").setExecutor(te);
        this.getCommand("sort").setTabCompleter(te);
    }

    private void createDataBase() {
        this.saveResource("config.yml", false);
        FileConfiguration cfg = this.getConfig();
        String username = cfg.getString("db_user");
        String password = cfg.getString("db_pass");
        String ip = cfg.getString("db_ip");
        String db_name = cfg.getString("db_name");
        String url = "jdbc:mysql://" + ip + "/" + db_name + "?user=" + username + "&password=" + password + "&useSSL=false&autoReconnect=true";
        try {
            database = DriverManager.getConnection(url);
            PreparedStatement ps = database.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS protection(id VARCHAR(50) PRIMARY KEY, x INT, y INT, z INT, world VARCHAR(50), owner VARCHAR(50), maxx INT, maxz INT, minx INT, minz INT, chunkx INT, chunkz INT);");
            ps.executeUpdate();
            if (ps != null)
                ps.close();
            ps = database.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS randomtp(uuid VARCHAR(50) PRIMARY KEY, next BIGINT);");
            ps.executeUpdate();
            if (ps != null)
                ps.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void eventCreator() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ElementalsListener(), this);
        manager.registerEvents(new FieldListener(), this);
        manager.registerEvents(new DeathMessageListener(), this);
    }

    private void randomMsg() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->
                Bukkit.broadcastMessage(ElementalsUtil.color("&c&l>>&r " + ElementalsUtil.getAutoMessages()
                        .get(ElementalsUtil.nextInt(ElementalsUtil.getAutoMessages().size())))), 1L, 2 * 60 * 20L);
    }

    private void timer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, ElementalsUtil::tickMotd, 20L, 20L);
    }
}