package cc.nuovoline.gui.screen.alt.repository;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.gui.screen.alt.repository.credential.AltCredential;
import lombok.Getter;
import lombok.Setter;

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
import java.util.concurrent.ThreadLocalRandom;

// alt storage
// wannabe adjust?
@Getter
public class AltRepository {

    private static final AltRepository INSTANCE = new AltRepository();

    private final List<Alt> alts = new ArrayList<>();
    @Setter
    private Alt currentAlt;

    private AltRepository() {
        load();
    }

    public static AltRepository get() {
        return INSTANCE;
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
        alts.remove(alt);
        if (currentAlt == alt) currentAlt = null;
        save();
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
