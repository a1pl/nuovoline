package cc.nuovoline.gui.screen.alt.repository;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.gui.screen.alt.login.AltType;
import cc.nuovoline.gui.screen.alt.repository.credential.AltCredential;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// alt storage
// wannabe adjust?
@Getter
public class AltRepository {

    private static final AltRepository INSTANCE = new AltRepository();

    private final List<Alt> alts = new ArrayList<>();
    //launcher session
    private Alt sessionAlt;
    @Setter
    private Alt currentAlt;

    private AltRepository() {
        load();
    }

    public static AltRepository get() {
        return INSTANCE;
    }


    public Alt registerSession(Session session) {
        if (session == null || session.getUsername() == null || session.getUsername().isEmpty()) return null;

        String uuid = normalizeUuid(session.getPlayerID());
        if (uuid == null) return null;

        Alt alt = new Alt(new AltCredential(session.getUsername(), null));
        alt.type = AltType.SESSION;
        alt.uuid = uuid;
        alt.token = session.getToken();
        alt.addedTimestamp = System.currentTimeMillis();

        sessionAlt = alt;
        currentAlt = alt;
        return alt;
    }

    private static String normalizeUuid(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        try {
            if (trimmed.length() == 32) {
                trimmed = trimmed.substring(0, 8) + "-" + trimmed.substring(8, 12) + "-"
                        + trimmed.substring(12, 16) + "-" + trimmed.substring(16, 20) + "-"
                        + trimmed.substring(20);
            }
            return UUID.fromString(trimmed).toString();
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    // dont save launcher session
    public List<Alt> getAlts() {
        if (sessionAlt == null) return alts;
        List<Alt> all = new ArrayList<>(alts.size() + 1);
        all.add(sessionAlt);
        all.addAll(alts);
        return all;
    }

    public void addAlt(Alt alt) {
        alts.add(alt);
        save();
    }

    public Alt addAlt(AltCredential credential) {
        Alt alt = new Alt(credential);
        addAlt(alt);
        return alt;
    }

    public void removeAlt(Alt alt) {
        if (sessionAlt == alt) {
            sessionAlt = null;
        } else {
            alts.remove(alt);
            save();
        }
        if (currentAlt == alt) currentAlt = null;
    }

    public Alt getRandomAlt() {
        return alts.isEmpty() ? null : alts.get(ThreadLocalRandom.current().nextInt(alts.size()));
    }

    private File getFile() {
        File dir = Nuovoline.getInstance().getDataFolder().toFile();
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "alts.txt");
    }

    public void save() {
        File file = getFile();
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            for (Alt alt : alts) {
                String login = alt.credential.getLogin();
                String password = alt.credential.getPassword();
                out.println(login + "|" + (password != null ? password : "") + "|" + alt.addedTimestamp);
            }
        } catch (IOException e) {
            Nuovoline.getLogger().warn("Failed to save alts", e);
        }
    }

    public void load() {
        File file = getFile();
        if (!file.exists()) return;

        alts.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|", -1);
                if (parts.length < 2) continue;

                String login = parts[0];
                String password = parts[1].isEmpty() ? null : parts[1];

                Alt alt = new Alt(new AltCredential(login, password));
                if (parts.length > 2) {
                    try {
                        alt.addedTimestamp = Long.parseLong(parts[2]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                alts.add(alt);
            }
        } catch (IOException e) {
            Nuovoline.getLogger().warn("Failed to load alts", e);
        }
    }
}
