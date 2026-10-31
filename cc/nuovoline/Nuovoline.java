package cc.nuovoline;

import cc.nuovoline.commands.NovoCommandHandler;
import cc.nuovoline.gui.screen.alt.repository.AltRepositoryGUI;
import cc.nuovoline.gui.screen.click.DiscordGUI;
import cc.nuovoline.gui.screen.dropdown.DropdownGUI;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.PlayerManager;
import cc.nuovoline.utils.fonts.api.FontManager;
import cc.nuovoline.utils.fonts.impl.SimpleFontManager;
import cc.nuovoline.utils.notifications.NotificationManager;
import cc.nuovoline.utils.tasks.TaskManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
//import net.skidunion.irc.IRCClient;
//import net.skidunion.irc.entities.UserEntity;
//import net.skidunion.security.Protection;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Nuovoline {

    private static Nuovoline INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    private final Minecraft mc = Minecraft.getInstance();
    /*Paths.get(this.mc.mcDataDir.getAbsolutePath() + "\\Nuovoline")*/
    @Getter
    private final Path dataFolder;

    public String version = "hvh";

    /* managers */ // todo public fields
    @Getter
    private final NovoCommandHandler novoCommandHandler = new NovoCommandHandler(this);
    @Setter
    @Getter
    private ModuleManager moduleManager;
    @Getter
    public TaskManager taskManager;
    @Getter
    public PlayerManager playerManager;
    @Getter
    public NotificationManager notificationManager;
    @Getter
    public FontManager fontManager = SimpleFontManager.create();

    /* gui */
    @Getter
    public DiscordGUI discordGUI;
    @Getter
    public DropdownGUI dropDownGUI;
    public AltRepositoryGUI altRepositoryGUI;

//    public IRCClient irc;
//    private Protection protection;

    /* misc */
    @Getter
    private ServerData lastConnectedServer;

    /* constructors */
    public Nuovoline() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Cannot instantiate " + getClass().getCanonicalName() + " twice");
        }
    }

    /* methods */
    public void onStart() {
    }

    public void onLoaded() {
    }

    public void onDisable() {
        try {
            getModuleManager().getConfigManager().save("default");
        } catch (IOException e) {
            getLogger().warn("An I/O error occurred while " + e.getMessage() + "!", e);
        } catch (ObjectMappingException e) {
            getLogger().warn("An I/O error occurred while serializing config!", e);
        }
    }

    {
        this.dataFolder = Paths.get(mc.mcDataDir.getAbsolutePath()).resolve("Nuovoline");
    }

//    public IRCClient getIRC() {
//        return irc;
//    }

    public AltRepositoryGUI getAltRepositoryGUI() {
        if (altRepositoryGUI == null) {
            altRepositoryGUI = new AltRepositoryGUI(this);
        }
        return altRepositoryGUI;
    }

    public String getVersion() {
        String version = this.version.toCharArray()[0] != '@' ? this.version : new SimpleDateFormat("MMddyy").format(new Date());
        /* fields */
        boolean BETA = true;
        boolean HOTFIX = false;
        return version + (BETA ? "-BETA" : "") + (HOTFIX ? "-H" : "");
    }

    public String getPathString() {
        return mc.mcDataDir.getAbsolutePath() + "\\Nuovoline\\";
    }

    public void setLastConnectedServer(@NotNull ServerData lastConnectedServer) {
        this.lastConnectedServer = lastConnectedServer;
    }

    public void init() {

    }

    public String Xor(Object obj, String key) {
        StringBuilder sb = new StringBuilder();
        char[] keyChars = key.toCharArray();

        int i = 0;

        for (char c : obj.toString().toCharArray()) {
            sb.append((char) (c ^ keyChars[i % keyChars.length]));
            i++;
        }

        return sb.toString();
    }

    public boolean isAnythingNull() {
        return moduleManager == null || playerManager == null || notificationManager == null;
    }

    public Minecraft getMinecraft() {
        return mc;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Nuovoline getInstance() {
        try {
            if (INSTANCE == null) INSTANCE = new Nuovoline();
            return INSTANCE;
        } catch (Throwable t) {
            LOGGER.warn(t);
            throw t;
        }
    }

    //    public Protection getProtection() {
//        return protection;
//    }

//    public void setProtection(Protection protection) {
//        this.protection = protection;
//    }
    //endregion

//    public UserEntity getIRCUser(String name) {
//        return irc.getUserManager().findByNickname(name);
//    }

    public int viaVersion() {
        return 404;
    }
}
