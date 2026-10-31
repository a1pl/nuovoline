package cc.nuovoline.gui.screen.alt.repository;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.gui.screen.alt.login.AltLoginListener;
import cc.nuovoline.gui.screen.alt.login.AltLoginThread;
import cc.nuovoline.gui.screen.alt.login.AltType;
import cc.nuovoline.gui.screen.alt.repository.credential.AltCredential;
import cc.nuovoline.utils.notifications.NotificationType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
// alt record
public class Alt {

    public final AltCredential credential;
    public long addedTimestamp = System.currentTimeMillis();
    public boolean invalid;

    public AltType type = AltType.CRACKED;
    public String uuid;
    public String token;
    public String refreshToken;
    @Setter
    @Getter
    private long unbanDate;

    public Alt(AltCredential credential) {
        this.credential = credential;
    }

    public CompletableFuture<Session> logIn(boolean announce) {
        return CompletableFuture.supplyAsync(() -> new AltLoginThread(credential, new AltLoginListener() {
            @Override
            public void onLoginSuccess(AltType type, Session session) {
                invalid = false;
                AltRepository.get().setCurrentAlt(Alt.this);
                Minecraft.getInstance().session = session;
                if (announce) {
                    Nuovoline.getInstance().getNotificationManager()
                            .pop("Logged in as " + session.getUsername(), NotificationType.SUCCESS);
                }
            }

            @Override
            public void onLoginFailed() {
                invalid = true;
                if (announce) {
                    Nuovoline.getInstance().getNotificationManager()
                            .pop("Invalid credentials for " + getDisplayName() + "!", NotificationType.ERROR);
                }
            }
        }).run(), ForkJoinPool.commonPool());
    }

    public String getDisplayName() {
        return credential.getLogin();
    }

    public String getTypeLabel() {
        if (type == AltType.MICROSOFT) return "Microsoft";
        if (type == AltType.TOKEN || type == AltType.SESSION) return "Token";
        return credential.getPassword() == null ? "Cracked" : "Mojang";
    }

    public String getRelativeTime() {
        long diff = System.currentTimeMillis() - addedTimestamp;
        long seconds = diff / 1000;
        if (seconds < 60) return seconds + "s ago";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        return (hours / 24) + "d ago";
    }
}
