package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String username;
    private final String playerID;
    private final String token;
    private final Session.Type sessionType;

    public Session(String usernameIn, String playerIDIn, String tokenIn, String sessionTypeIn) {
        this.username = usernameIn;
        this.playerID = playerIDIn;
        this.token = tokenIn;
        this.sessionType = Session.Type.setSessionType(sessionTypeIn);
    }

    public String getSessionID() {
        return "token:" + this.token + ":" + this.playerID;
    }

    public String getPlayerID() {
        return this.playerID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public GameProfile getProfile() {
        UUID uuid;
        try {
            uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
        } catch (IllegalArgumentException | NullPointerException var2) {
            // Offline/cracked sessions (and launches without --uuid) have no real
            // player id. Derive the standard offline UUID so the profile is never
            // null; authlib's joinServer serialises the id and NPEs on null.
            uuid = getOfflineUUID(this.username);
        }
        return new GameProfile(uuid, this.username);
    }

    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    public static Session createOffline(String username) {
        return new Session(username, getOfflineUUID(username).toString(), "0", "legacy");
    }

    /**
     * Returns either 'legacy' or 'mojang' whether the account is migrated or not
     */
    public Session.Type getSessionType() {
        return this.sessionType;
    }

    public enum Type {

        LEGACY("legacy"),
        MOJANG("mojang");

        private static final Map<String, Session.Type> SESSION_TYPES = Maps.newHashMap();
        private final String sessionType;

        Type(String sessionTypeIn) {
            this.sessionType = sessionTypeIn;
        }

        public static Session.Type setSessionType(String sessionTypeIn) {
            return SESSION_TYPES.get(sessionTypeIn.toLowerCase());
        }

        static {
            for (Session.Type session$type : values()) {
                SESSION_TYPES.put(session$type.sessionType, session$type);
            }
        }

    }

}
