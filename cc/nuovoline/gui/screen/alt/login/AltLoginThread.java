package cc.nuovoline.gui.screen.alt.login;

import cc.nuovoline.gui.screen.alt.repository.credential.AltCredential;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.util.Session;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.Proxy;

public final class AltLoginThread {

    /* fields */
    @NonNull
    public final AltCredential credentials;
    @Nullable
    public AltLoginListener handler;

    // constructors
    public AltLoginThread(@NonNull AltCredential credential, @NonNull AltLoginListener handler) {
        this.credentials = credential;
        this.handler = handler;
    }

    /* methods */
    @Nullable
    public Session run() {
        final String password = this.credentials.getPassword();

        if (password == null) {
            final Session crackedSession = Session.createOffline(this.credentials.getLogin());

            if (this.handler != null) this.handler.onLoginSuccess(AltType.CRACKED, crackedSession);
            return crackedSession;
        }

        final Session session = createSession(this.credentials.getLogin(), password);

        if (session == null) {
            if (this.handler != null) this.handler.onLoginFailed();
            return null;
        }

        if (this.handler != null) this.handler.onLoginSuccess(AltType.PREMIUM, session);
        return session;
    }

    @Nullable
    public static Session createSession(String username, String password) {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service
                .createUserAuthentication(Agent.MINECRAFT);

        auth.setUsername(username);
        auth.setPassword(password);

        try {
            auth.logIn();
            final GameProfile selectedProfile = auth.getSelectedProfile();
            return new Session(selectedProfile.getName(), selectedProfile.getId().toString(),
                    auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
