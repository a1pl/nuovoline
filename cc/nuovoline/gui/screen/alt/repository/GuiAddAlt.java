package cc.nuovoline.gui.screen.alt.repository;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.gui.button.RoundedButton;
import cc.nuovoline.gui.group.GuiGroupAltLogin;
import cc.nuovoline.gui.screen.alt.repository.credential.AltCredential;
import cc.nuovoline.utils.fonts.api.FontRenderer;
import static cc.nuovoline.utils.fonts.impl.Fonts.SFBOLD.SFBOLD_20.SFBOLD_20;
import static cc.nuovoline.utils.fonts.impl.Fonts.SFTHIN.SFTHIN_18.SFTHIN_18;
import cc.nuovoline.utils.java.Checks;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import lombok.Getter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import static net.minecraft.util.EnumChatFormatting.RED;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

public final class GuiAddAlt extends GuiScreen {

    private final AltRepositoryGUI gui;
    private final BiConsumer<GuiAddAlt, ? super AltCredential> consumer;

    private final String addAltButtonName;
    private final String title;

    @Getter
    private GuiGroupAltLogin groupAltInfo;
    private PasswordTextField passwordField;
    private GuiTextField usernameField;

    public GuiAddAlt(@NotNull AltRepositoryGUI gui,
                     @NotNull String addAltButtonName,
                     @NotNull String title,
                     @NotNull BiConsumer<GuiAddAlt, ? super AltCredential> consumer) {
        this.gui = gui;
        this.addAltButtonName = addAltButtonName;
        this.title = title;
        this.consumer = consumer;
    }

    @Override
    public void initGui() {
        int height = this.height / 4 + 24;

        this.groupAltInfo = new GuiGroupAltLogin(this, title);

        List<GuiButton> buttonList = this.buttonList;
        buttonList.add(new RoundedButton(addAltButtonName, 0, width / 2 - 100, height + 72 + 12, 15, SFBOLD_20));
        buttonList.add(new RoundedButton("Back", 1, width / 2 - 100, height + 72 + 12 + 24, 15, SFBOLD_20));
        buttonList.add(new RoundedButton("Import alt", 2, width / 2 - 100, height + 72 + 12 + -24, 15, SFBOLD_20));

        this.usernameField = new TokenField(0, mc.fontRendererObj, width / 2 - 100, 60, 200, 20, "Alt Email:");
        this.passwordField = new PasswordTextField(1, mc.fontRendererObj, width / 2 - 100, 100, 200, 20, "Password:");
        usernameField.setFocused(true);

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fontRenderer = SFTHIN_18;

        drawRect(0, 0, width, height, new Color(32, 34, 37).getRGB()); // background

        GuiTextField usernameField = this.usernameField;
        PasswordTextField passwordField = this.passwordField;

        usernameField.drawTextBox();
        passwordField.drawTextBox();

        groupAltInfo.drawGroup(mc, mouseX, mouseY);

        if (StringUtils.isBlank(usernameField.getText()) && !usernameField.isFocused()) {
            fontRenderer.drawString("Username / E-Mail", width / 2F - 96, 64, 0xFF888888);
        }

        if (StringUtils.isBlank(passwordField.getText()) && !passwordField.isFocused()) {
            fontRenderer.drawString("Password", width / 2F - 96, 104, 0xFF888888);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])$");

    static boolean isEmail(@NotNull String email) {
        Checks.notBlank(email, "email");
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void add_login(){
        try {
            if (usernameField.getText().trim().isEmpty()) return; // not gonna happen

            AltCredential altCredential = new AltCredential(usernameField.getText(), passwordField.getText());
            String login = altCredential.getLogin();

            if (altCredential.getPassword() == null) {
                if (!login.matches("^[a-zA-Z0-9_]+$")) {
                    groupAltInfo.updateStatus(RED + "Illegal characters in username");
                    return;
                }

                if (login.length() > 16) {
                    groupAltInfo.updateStatus(RED + "Username is too long");
                    return;
                }
            } else if (!isEmail(login)) {
                groupAltInfo.updateStatus(RED + "Illegal e-mail");
            }

            consumer.accept(this, altCredential);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: // add alt button
                add_login();
                break;

            case 1: // back
                mc.displayGuiScreen(gui);
                break;

            case 2: // import
                AltCredential parts = getDataFromClipboard();
                if (parts == null) return;

                try {
                    usernameField.setText(parts.getLogin());
                    passwordField.setText(parts.getPassword());
                } catch (ArrayIndexOutOfBoundsException ignored) {
                } catch (Throwable t) {
                    Nuovoline.getLogger().warn("An unexpected error occurred while importing alt!", t);
                }

                break;
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1) {
            mc.displayGuiScreen(gui);
            return;
        }
        if(key == Keyboard.KEY_RETURN){
            add_login();
            return;
        }

        switch (character) {
            case '\t':
                boolean passwordFieldFocused = passwordField.isFocused();
                boolean usernameFieldFocused = usernameField.isFocused();

                if (usernameFieldFocused && !passwordFieldFocused) {
                    usernameField.setFocused(false);
                    passwordField.setFocused(true);
                    return;
                }

                usernameField.setFocused(true);
                passwordField.setFocused(false);
                break;

            case '\r':
                actionPerformed(buttonList.get(0));
                break;
        }

        usernameField.textboxKeyTyped(character, key);
        passwordField.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        } catch (Throwable t) {
            Nuovoline.getLogger().warn(t);
        }

        usernameField.mouseClicked(x2, y2, button);
        passwordField.mouseClicked(x2, y2, button);
    }

    private @Nullable AltCredential getDataFromClipboard() {
        String s = null;

        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                s = ((String) transferable.getTransferData(DataFlavor.stringFlavor)).trim();
            }
        } catch (Throwable ignored) {
        }

        if (s == null) {
            return null;
        }

        int index = s.indexOf(':');
        return index == -1 ? null : new AltCredential(s.substring(0, index), s.substring(index + 1));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }


}
