package cc.nuovoline.gui.screen.alt.repository;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.gui.screen.alt.login.AltLoginThread;
import cc.nuovoline.gui.screen.alt.login.AltType;
import cc.nuovoline.gui.screen.alt.login.SessionUpdatingAltLoginListener;
import cc.nuovoline.gui.screen.alt.repository.credential.AltCredential;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// skid
// place i skidded from was prolly vibed but oh well
// its better than the buggy novoline one
public class AltRepositoryGUI extends GuiScreen {

    private static final int PANEL = 0xFF0D0D0D;
    private static final int SELECTED = 0xFF1A1A1A;
    private static final int HOVERED = 0xFF151515;
    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int MUTED = 0xFF888888;

    private static final int ID_LOGIN = 0;
    private static final int ID_ADD = 1;
    private static final int ID_DELETE = 2;
    private static final int ID_BACK = 3;

    private final List<Alt> visible = new ArrayList<>();
    private int selectedIndex = -1;
    private int scrollOffset;
    private final int rowHeight = 22;

    private GuiTextField searchField;
    private String status = "";
    private boolean statusError;
    private boolean loggingIn;

    private int listX, listY, listW, listH;

    public AltRepositoryGUI(Nuovoline nuovoline) {
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();
        selectedIndex = -1;
        scrollOffset = 0;

        int buttonWidth = 80, buttonHeight = 20, gap = 4;
        int totalWidth = buttonWidth * 4 + gap * 3;
        int x = (width - totalWidth) / 2;
        int y = height - 28;

        buttonList.add(new GuiButton(ID_LOGIN, x, y, buttonWidth, buttonHeight, "Login"));
        x += buttonWidth + gap;
        buttonList.add(new GuiButton(ID_ADD, x, y, buttonWidth, buttonHeight, "Add"));
        x += buttonWidth + gap;
        buttonList.add(new GuiButton(ID_DELETE, x, y, buttonWidth, buttonHeight, "Delete"));
        x += buttonWidth + gap;
        buttonList.add(new GuiButton(ID_BACK, x, y, buttonWidth, buttonHeight, "Back"));

        searchField = new GuiTextField(0, fontRendererObj, width - 128, 8, 116, 16);
        searchField.setMaxStringLength(64);

        refreshVisible();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void refreshVisible() {
        if (searchField == null) return;
        visible.clear();
        String query = searchField.getText().toLowerCase().trim();
        for (Alt alt : AltRepository.get().getAlts()) {
            if (query.isEmpty() || alt.getDisplayName().toLowerCase().contains(query)) {
                visible.add(alt);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Alt Manager", width / 2, 8, 0xFFFFFF);
        searchField.drawTextBox();

        listW = Math.min(280, width - 80);
        listX = (width - listW) / 2;
        listY = 32;
        listH = height - listY - 60;

        drawRect(listX - 1, listY - 1, listX + listW + 1, listY + listH + 1, 0xFF111111);
        drawRect(listX, listY, listX + listW, listY + listH, PANEL);

        scrollOffset = Math.min(maxScroll(), Math.max(0, scrollOffset));

        for (int i = 0; i < listH / rowHeight && i + scrollOffset < visible.size(); i++) {
            int idx = i + scrollOffset;
            int rowY = listY + i * rowHeight;
            Alt alt = visible.get(idx);

            boolean selected = idx == selectedIndex;
            boolean hovered = mouseX >= listX && mouseX <= listX + listW && mouseY >= rowY && mouseY < rowY + rowHeight - 1;

            if (selected) {
                drawRect(listX, rowY, listX + listW, rowY + rowHeight - 1, SELECTED);
            } else if (hovered) {
                drawRect(listX, rowY, listX + listW, rowY + rowHeight - 1, HOVERED);
            }

            fontRendererObj.drawString(alt.getDisplayName(), listX + 6, rowY + 3, 0xFFFFFF);
            String meta = alt.getTypeLabel() + " · " + alt.getRelativeTime();
            fontRendererObj.drawString(meta, listX + 6, rowY + 13, MUTED);

            if (alt.invalid) {
                String tag = "invalid";
                fontRendererObj.drawString(tag, listX + listW - fontRendererObj.getStringWidth(tag) - 6, rowY + 8, RED);
            }
        }

        if (!status.isEmpty()) {
            drawCenteredString(fontRendererObj, status, width / 2, listY + listH + 4, statusError ? RED : MUTED);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int maxScroll() {
        return Math.max(0, visible.size() - Math.max(1, listH / rowHeight));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case ID_LOGIN:
                if (selectedIndex >= 0 && selectedIndex < visible.size() && !loggingIn) {
                    login(visible.get(selectedIndex));
                }
                break;
            case ID_ADD:
                mc.displayGuiScreen(new GuiAddAlt(this));
                break;
            case ID_DELETE:
                if (selectedIndex >= 0 && selectedIndex < visible.size()) {
                    removeAlt(visible.get(selectedIndex));
                    selectedIndex = -1;
                    status = "Deleted alt";
                    statusError = false;
                }
                break;
            case ID_BACK:
                mc.displayGuiScreen(null);
                break;
        }
    }

    private void login(Alt alt) {
        loggingIn = true;
        status = "Logging in...";
        statusError = false;

        new Thread(() -> new AltLoginThread(alt.credential, new SessionUpdatingAltLoginListener() {
            @Override
            public void onLoginSuccess(AltType type, Session session) {
                super.onLoginSuccess(type, session);
                alt.invalid = false;
                mc.addScheduledTask(() -> {
                    status = "Logged in as " + session.getUsername();
                    statusError = false;
                    loggingIn = false;
                });
            }

            @Override
            public void onLoginFailed() {
                alt.invalid = true;
                mc.addScheduledTask(() -> {
                    status = "Invalid credentials";
                    statusError = true;
                    loggingIn = false;
                });
            }
        }).run(), "Alt-Login").start();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0 && mouseX >= listX && mouseX <= listX + listW && mouseY >= listY && mouseY < listY + listH) {
            int idx = (mouseY - listY) / rowHeight + scrollOffset;
            if (idx >= 0 && idx < visible.size()) {
                selectedIndex = idx;
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            scrollOffset = wheel > 0 ? Math.max(0, scrollOffset - 1) : Math.min(maxScroll(), scrollOffset + 1);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        searchField.textboxKeyTyped(typedChar, keyCode);
        if (searchField.isFocused()) refreshVisible();

        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        searchField.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    //region Lombok-alternative
    public List<Alt> getAlts() {
        return AltRepository.get().getAlts();
    }

    public Alt getCurrentAlt() {
        return AltRepository.get().getCurrentAlt();
    }

    public Alt getRandomAlt() {
        return AltRepository.get().getRandomAlt();
    }

    public void addAlt(AltCredential credential) {
        Alt alt = AltRepository.get().addAlt(credential);
        refreshVisible();
    }

    public void removeAlt(Alt alt) {
        AltRepository.get().removeAlt(alt);
        refreshVisible();
    }
    //endregion
}
