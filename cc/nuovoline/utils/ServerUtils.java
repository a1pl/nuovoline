package cc.nuovoline.utils;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.modules.visual.ClickGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerUtils {

    private static Nuovoline nuovoline = Nuovoline.getInstance();
    private static Minecraft mc = Minecraft.getInstance();
    private static ClickGUI clickGUI = nuovoline.getModuleManager().getModule(ClickGUI.class);
    private static boolean rewound;
    private static boolean fakeRewound;

    public static boolean serverIs(Servers server) {
        if (!mc.isSingleplayer() && clickGUI.getCurrentServer() != null) {
            return clickGUI.getCurrentServer().equals(server);
        }

        return false;
    }

    public static boolean channelIs(Channels channel) {
        return clickGUI.getChannel().equals(channel);
    }

    public static void checkRewound(ServerData serverData) {
        if (serverData.serverIP.toLowerCase().contains("rewound.cc") && !hostModified("rewound") && !fakeRewound) {
            rewound = true;
        } else {
            rewound = false;
            clickGUI.setCurrentServer(Servers.NONE);
        }
    }

    public static boolean isRewound() {
        //removed shit packetlog check
        return true;
    }

    public static boolean hostModified(String server) {
        Path path = Paths.get(System.getenv("SystemDrive") + "\\Windows\\System32\\drivers\\etc\\hosts");

        if (Files.notExists(path)) {
            return false;
        } else {
            try {
                return Files.lines(path).anyMatch(s -> s.toLowerCase().contains(server));
            } catch (IOException e) {
                mc.getNetHandler().getNetworkManager().closeChannel(new ChatComponentText(EnumChatFormatting.RED + "Connection error! Contact staff"));
                return true;
            }
        }
    }

    public static void setFakeRewound(boolean fakeRewound) {
        ServerUtils.fakeRewound = fakeRewound;
    }

    public static boolean isFakeRewound() {
        return fakeRewound;
    }

    public static int inGameSeconds() {
        return clickGUI.getTicks() / 20;
    }

    public static int inGameTicks() {
        return clickGUI.getTicks();
    }
}
