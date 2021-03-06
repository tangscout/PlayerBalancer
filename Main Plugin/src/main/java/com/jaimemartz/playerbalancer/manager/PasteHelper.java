package com.jaimemartz.playerbalancer.manager;

import com.google.common.io.CharStreams;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.utils.GuestPaste;
import com.jaimemartz.playerbalancer.utils.GuestPaste.PasteException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.BiConsumer;

public enum PasteHelper {
    PLUGIN((sender, address) -> {
        if (sender instanceof ProxiedPlayer) {
            sender.sendMessage(new ComponentBuilder("Click me for the PlayerBalancer configuration")
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, address.toString()))
                    .color(ChatColor.GREEN)
                    .create()
            );
        } else {
            sender.sendMessage(new ComponentBuilder("PlayerBalancer configuration link: " + address.toString()).create());
        }
    }) {
        @Override
        public URL paste(PlayerBalancer plugin) throws Exception {
            File file = new File(plugin.getDataFolder(), "plugin.conf");
            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                    String content = CharStreams.toString(reader);
                    GuestPaste paste = new GuestPaste("e3ff18d8fb001a3ece08ae0d7d4a87bd", content);

                    paste.setName("{name} ({version} on {bungee_version})"
                            .replace("{name}", plugin.getDescription().getName())
                            .replace("{version}", plugin.getDescription().getVersion())
                            .replace("{bungee_version}", plugin.getProxy().getVersion())
                    );

                    paste.setExpiration(GuestPaste.Expiration.ONE_MONTH);
                    paste.setExposure(GuestPaste.Exposure.UNLISTED);
                    paste.setFormat("properties");

                    return paste.paste();
                }
            }
        }
    },

    BUNGEE((sender, address) -> {
        if (sender instanceof ProxiedPlayer) {
            sender.sendMessage(new ComponentBuilder("Click me for the BungeeCord configuration")
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, address.toString()))
                    .color(ChatColor.GREEN)
                    .create()
            );
        } else {
            sender.sendMessage(new ComponentBuilder("BungeeCord configuration link: " + address.toString()).create());
        }
    }) {
        @Override
        public URL paste(PlayerBalancer plugin) throws Exception {
            File file = new File("config.yml");
            try (FileInputStream stream = new FileInputStream(file)) {
                try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                    String content = CharStreams.toString(reader);
                    content = content.replaceAll("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}", "?.?.?.?");
                    GuestPaste paste = new GuestPaste("e3ff18d8fb001a3ece08ae0d7d4a87bd", content);

                    paste.setName("{name} ({version})"
                            .replace("{name}", plugin.getProxy().getName())
                            .replace("{version}", plugin.getProxy().getVersion())
                    );

                    paste.setExpiration(GuestPaste.Expiration.ONE_MONTH);
                    paste.setExposure(GuestPaste.Exposure.UNLISTED);
                    paste.setFormat("yaml");

                    return paste.paste();
                }
            }
        }
    };

    private URL url;

    private final BiConsumer<CommandSender, URL> consumer;

    PasteHelper(BiConsumer<CommandSender, URL> consumer) {
        this.consumer = consumer;
    }

    public void send(PlayerBalancer plugin, CommandSender sender) {
        boolean cached = url != null;
        if (url == null) {
            try {
                url = paste(plugin);
            } catch (PasteException e) {
                sender.sendMessage(new ComponentBuilder("An pastebin exception occurred: " + e.getMessage())
                        .color(ChatColor.RED)
                        .create()
                );
                e.printStackTrace();
            } catch (Exception e) {
                sender.sendMessage(new ComponentBuilder("An internal error occurred while attempting to perform this command")
                        .color(ChatColor.RED)
                        .create()
                );
                e.printStackTrace();
            }
        }

        if (url != null) {
            consumer.accept(sender, url);
            if (cached) {
                sender.sendMessage(new ComponentBuilder("This is a cached link, reload the plugin for it to refresh!")
                        .color(ChatColor.RED)
                        .create()
                );
            }
        }
    }

    public URL getURL() {
        return url;
    }

    public abstract URL paste(PlayerBalancer plugin) throws Exception;

    public static void reset() {
        for (PasteHelper helper : values()) {
            helper.url = null;
        }
    }
}
