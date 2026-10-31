package cc.nuovoline.gui.screen.alt.repository.comp;

import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.util.UUID;

public class SessionChanger {

    private static SessionChanger instance;
    private final UserAuthentication auth;

    public static SessionChanger getInstance() {
        if (instance == null) {
            instance = new SessionChanger();
        }
        return instance;
    }

    private SessionChanger() {
        AuthenticationService authService = new YggdrasilAuthenticationService(
                Minecraft.getMinecraft().getProxy(), UUID.randomUUID().toString());
        auth = authService.createUserAuthentication(Agent.MINECRAFT);
        authService.createMinecraftSessionService();
    }

    public void setUserOffline(String username) {
        auth.logOut();
        Minecraft.getMinecraft().setSession(new Session(username, username, "0", "legacy"));
    }
}
