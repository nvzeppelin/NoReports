package net.vonzeppelin.noreports;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.io.File;
import java.io.IOException;




public final class Main extends JavaPlugin implements Listener {
    private final Config config = new Config();
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        if (!config.contains("infomessage")) {
            try {
                config.set("infomessage", true);
            } catch (IOException ignored) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player p = e.getPlayer();
        String msg = e.getMessage();

        Bukkit.broadcastMessage(p.getName() + ": "+msg);
    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        String cmd = e.getMessage().split(" ", 2)[0];
        if(contains(cmd)) {
            e.setCancelled(true); // don't run the command
            List<String> args = new java.util.ArrayList<>(Arrays.stream(e.getMessage().split(" ", 2)).toList());
            args.remove(0);
            switch (cmd) {
                case "/tell", "/msg" -> {
                    Player p = Bukkit.getPlayer(args.get(0).split(" ")[0]);
                    if (p == null)  {
                        e.getPlayer().sendMessage("Player could not be found");
                        return;
                    }
                    e.getPlayer().sendMessage("You whispered to " + p.getDisplayName() + ": "+ args.get(0).split(" ",2)[1]);
                    p.sendMessage(e.getPlayer().getDisplayName() + " whispered to you: " + args.get(0).split(" ", 2)[1]);
                }
            }
        }
    }

    private static final String[] forbiddenMessages = {"/tell","/msg",};

    private boolean contains(String target) {
        for (String s : Main.forbiddenMessages) {
            if (Objects.equals(s, target)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (Boolean.parseBoolean(config.get("infomessage").toString()))
            e.getPlayer().sendMessage(ChatColor.RED + "This Server is running NoReports 1.0 by nvzeppelin. This means that you cannot report chat messages!");
    }
}
class Config {
    private final File file;
    private final YamlConfiguration config;

    public Config() {


        File dir = new File("./plugins/NoReports/");
        if (!dir.exists()) {
            dir.mkdirs();

        }


        file = new File(dir, "config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }
    public void set(String path, Object value) throws IOException {
        config.set(path, value);
        config.save(file);
    }
    public Object get(String path) {
        if (!contains(path)) {
            return null;
        }
        return config.get (path);

    }
}
