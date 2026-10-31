package cc.nuovoline.gui.button;

import cc.nuovoline.gui.Element;
import cc.nuovoline.gui.label.Label;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author xDelsy
 */
public interface Button extends Element {

    void click(int mouseKey);

    @Nullable Label getName();

    void setName(@Nullable Label label);

}
