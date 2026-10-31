package cc.nuovoline.gui.screen.alt.repository;

import cc.nuovoline.gui.screen.alt.login.AltType;
import cc.nuovoline.gui.screen.alt.repository.comp.MicrosoftOAuthTranslation;
import cc.nuovoline.gui.screen.alt.repository.comp.SessionChanger;
import cc.nuovoline.gui.screen.alt.repository.comp.SessionIdLogin;
import cc.nuovoline.gui.screen.alt.repository.credential.AltCredential;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;


public final class GuiAddAlt extends GuiScreen {

    private static final int BG = 0xFF0A0A0A;
    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int MUTED = 0xFF888888;

    public enum AccountType {
        Mcrosoft("Microsoft"),
        Brwsr("Browser"),
        TOKEN("Token / Session"),
        Offline("Cracked");

        final String label;
        AccountType(String label) { this.label = label; }
    }

    private final GuiScreen parent;
    private AccountType type = AccountType.Mcrosoft;

    private GuiTextField tokenField;
    private GuiTextField nameField;

    private String status = "";
    private boolean statusError;
    private boolean busy;

    private static final int IdDone = 0;
    private static final int IdCancel = 1;
    private static final int IdType = 2;
    private static final int IdClipboard = 3;
    private static final int IdOpenLink = 4;
    private static final int IdCopyLink = 5;

    private volatile String pendingRefreshToken = null;

    public GuiAddAlt(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        rebuildButtons();
        super.initGui();
    }

    private void rebuildButtons() {
        buttonList.clear();

        int cx = width / 2;
        int fieldW = 200;
        int fieldX = cx - fieldW / 2;
        int typeY = height / 2 + 40;

        tokenField = new GuiTextField(12, fontRendererObj, fieldX, 100, fieldW, 18);
        tokenField.setMaxStringLength(2048);
        nameField = new GuiTextField(13, fontRendererObj, fieldX, 110, fieldW, 18);
        nameField.setMaxStringLength(16);

        buttonList.add(new GuiButton(IdType,   cx - 100, typeY,      200, 20, "Account type: " + type.label));
        buttonList.add(new GuiButton(IdDone,   cx - 126, typeY + 28, 120, 20, "Done"));
        buttonList.add(new GuiButton(IdCancel, cx + 6,   typeY + 28, 120, 20, "Cancel"));

        switch (type) {
            case Mcrosoft:
                buttonList.add(new GuiButton(IdClipboard, fieldX, 130, fieldW, 20, "Paste Refresh Token"));
                break;
            case Brwsr:
                buttonList.add(new GuiButton(IdOpenLink, fieldX, 100, fieldW, 20, "Open in Browser"));
                buttonList.add(new GuiButton(IdCopyLink, fieldX, 128, fieldW, 20, "Copy Link"));
                break;
            case TOKEN:
                buttonList.add(new GuiButton(IdClipboard, fieldX, 128, fieldW, 20, "Paste from Clipboard"));
                break;
            default:
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, BG);
        for (int y = 0; y < height; y += 2) drawRect(0, y, width, y + 1, 0x08FFFFFF);

        drawCenteredString(fontRendererObj, "Add account", width / 2, 30, 0xE0E0E0);

        int cx = width / 2;
        int fieldW = 200;
        int fieldX = cx - fieldW / 2;

        switch (type) {
            case Mcrosoft:
                drawCenteredString(fontRendererObj, "Paste your Microsoft refresh token", cx, 80, MUTED);
                fontRendererObj.drawString("Refresh Token", fieldX, 90, MUTED);
                tokenField.drawTextBox();
                break;
            case Brwsr:
                drawCenteredString(fontRendererObj, "Complete login in your browser,", cx, 75, MUTED);
                drawCenteredString(fontRendererObj, "then come back here.", cx, 87, MUTED);
                if (pendingRefreshToken != null)
                    drawCenteredString(fontRendererObj, "Token received!", cx, 75, GREEN);
                break;
            case TOKEN:
                fontRendererObj.drawString("Token / Session ID", fieldX, 90, MUTED);
                tokenField.drawTextBox();
                break;
            case Offline:
                fontRendererObj.drawString("Username", fieldX, 98, MUTED);
                nameField.drawTextBox();
                break;
        }

        if (!status.isEmpty()) {
            drawCenteredString(fontRendererObj, status, cx, height / 2 + 75, statusError ? RED : GREEN);
        }
        if (busy) drawCenteredString(fontRendererObj, "Working...", cx, height / 2 + 90, MUTED);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case IdType:
                AccountType[] vals = AccountType.values();
                type = vals[(type.ordinal() + 1) % vals.length];
                pendingRefreshToken = null;
                status = "";
                rebuildButtons();
                break;

            case IdCancel:
                mc.displayGuiScreen(parent);
                break;

            case IdDone:
                doDone();
                break;

            case IdClipboard:
                pasteFromClipboard();
                break;

            case IdOpenLink:
                busy = true;
                status = "Waiting for browser...";
                statusError = false;
                MicrosoftOAuthTranslation.getRefreshToken(token -> {
                    pendingRefreshToken = token;
                    mc.addScheduledTask(() -> {
                        busy = false;
                        if (token != null) {
                            status = "Got token! Press Done.";
                            statusError = false;
                        } else {
                            status = "Browser auth failed";
                            statusError = true;
                        }
                    });
                });
                break;

            case IdCopyLink:
                String link = "https://login.live.com/oauth20_authorize.srf?client_id=9fbc7315-7200-4b2b-a655-bb38c865da17"
                        + "&response_type=code&redirect_uri=http://localhost:8247&scope=XboxLive.signin%20offline_access";
                try {
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                            .setContents(new StringSelection(link), null);
                    status = "Link copied!";
                    statusError = false;
                } catch (Exception e) {
                    status = "Clipboard error";
                    statusError = true;
                }
                break;
        }
    }

    private void pasteFromClipboard() {
        try {
            Object data = Toolkit.getDefaultToolkit().getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);
            if (data != null) {
                String raw = data.toString().trim();
                if (type == AccountType.Mcrosoft || type == AccountType.TOKEN) {
                    tokenField.setText(raw);
                }
            }
        } catch (Exception ignored) {}
    }

    private void doDone() {
        if (busy) return;

        switch (type) {
            case Brwsr: {
                String rt = pendingRefreshToken;
                if (rt == null) { status = "No token yet — complete browser login first"; statusError = true; return; }
                loginWithRefreshToken(rt);
                break;
            }
            case Mcrosoft: {
                String raw = tokenField.getText().trim();
                if (raw.isEmpty()) { status = "Paste your refresh token first"; statusError = true; return; }
                loginWithRefreshToken(raw);
                break;
            }
            case TOKEN: {
                String raw = tokenField.getText().trim();
                if (raw.isEmpty()) { status = "Enter a token"; statusError = true; return; }
                busy = true;
                status = "Authenticating...";
                statusError = false;
                new Thread(() -> {
                    try {
                        if (MicrosoftOAuthTranslation.isRefreshToken(raw)) {
                            loginWithRefreshToken(raw);
                        } else {
                            SessionIdLogin.LoginResult res = SessionIdLogin.login(raw);
                            mc.addScheduledTask(() -> {
                                mc.setSession(new Session(res.name, res.uuid, res.mcAccessToken, "mojang"));
                                Alt alt = new Alt(new AltCredential(res.name, null));
                                alt.token = res.mcAccessToken;
                                alt.uuid = res.uuid;
                                alt.type = AltType.TOKEN;
                                AltRepository.get().addAlt(alt);
                                status = "Logged in as " + res.name;
                                statusError = false;
                                busy = false;
                                mc.displayGuiScreen(parent);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mc.addScheduledTask(() -> { status = "Token auth failed"; statusError = true; busy = false; });
                    }
                }, "Token-Auth").start();
                break;
            }
            case Offline: {
                String uname = nameField.getText().trim();
                if (uname.isEmpty()) { status = "Enter a username"; statusError = true; return; }
                SessionChanger.getInstance().setUserOffline(uname);
                mc.setSession(new Session(uname, uname, "0", "legacy"));
                AltRepository.get().addAlt(new Alt(new AltCredential(uname, null)));
                mc.displayGuiScreen(parent);
                break;
            }
        }
    }

    private void loginWithRefreshToken(String rt) {
        busy = true;
        status = "Logging in with Microsoft...";
        statusError = false;
        new Thread(() -> {
            try {
                MicrosoftOAuthTranslation.LoginData data = MicrosoftOAuthTranslation.login(rt);
                mc.addScheduledTask(() -> {
                    if (data.isGood()) {
                        mc.setSession(new Session(data.username, data.uuid, data.mcToken, "microsoft"));
                        Alt alt = new Alt(new AltCredential(data.username, null));
                        alt.uuid = data.uuid;
                        alt.token = data.mcToken;
                        alt.refreshToken = data.newRefreshToken != null ? data.newRefreshToken : rt;
                        alt.type = AltType.MICROSOFT;
                        AltRepository.get().addAlt(alt);
                        status = "Logged in as " + data.username;
                        statusError = false;
                        busy = false;
                        mc.displayGuiScreen(parent);
                    } else {
                        status = "Token expired / invalid";
                        statusError = true;
                        busy = false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mc.addScheduledTask(() -> { status = "Microsoft login failed"; statusError = true; busy = false; });
            }
        }, "MS-Login").start();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (type == AccountType.TOKEN) tokenField.mouseClicked(mouseX, mouseY, mouseButton);
        if (type == AccountType.Offline) nameField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) { mc.displayGuiScreen(parent); return; }
        if (keyCode == Keyboard.KEY_RETURN) { doDone(); return; }
        if (type == AccountType.TOKEN) tokenField.textboxKeyTyped(typedChar, keyCode);
        if (type == AccountType.Offline) nameField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        if (type == AccountType.TOKEN) tokenField.updateCursorCounter();
        if (type == AccountType.Offline) nameField.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
