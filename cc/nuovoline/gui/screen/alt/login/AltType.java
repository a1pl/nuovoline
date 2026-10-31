package cc.nuovoline.gui.screen.alt.login;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xDelsy
 */
public enum AltType {

    CRACKED,
    PREMIUM,
    MICROSOFT,
    TOKEN,
    SESSION,
    ;

    private final String capitalized;

    AltType() {
        this.capitalized = StringUtils.capitalize(name().toLowerCase());
    }

    public String getCapitalized() {
        return this.capitalized;
    }

}
