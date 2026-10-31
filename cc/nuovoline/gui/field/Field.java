package cc.nuovoline.gui.field;

import cc.nuovoline.gui.Element;
import cc.nuovoline.gui.label.Label;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author xDelsy
 */
public interface Field extends Element {

    void setHint(@Nullable Label label);

    @Nullable Label getLabel();

}
