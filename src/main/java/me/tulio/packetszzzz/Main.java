package me.tulio.packetszzzz;

import io.netty.channel.*;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        injectChannelPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeChannelPlayer(event.getPlayer());
    }

    private void injectChannelPlayer(Player player) {
        ChannelDuplexHandler handler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                if (o instanceof PacketPlayInTabComplete) {
                    PacketPlayInTabComplete packet = (PacketPlayInTabComplete) o;
                    String[] args = packet.a().split(" ");
                    for (String arg : args) {
                        if (arg.contains("for") || arg.contains("while")) {
                            // Insert your code here
                            System.out.println("[Packetzzzzzzz] " + player.getName() + " tried to use a for loop!");
                            return;
                        }
                    }
                }
                super.channelRead(channelHandlerContext, o);
            }
        };

        ChannelPipeline pipeline = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getUniqueId().toString(), handler);
    }

    private void removeChannelPlayer(Player player) {
        Channel channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getUniqueId().toString());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
