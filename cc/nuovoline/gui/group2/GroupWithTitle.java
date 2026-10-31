package cc.nuovoline.gui.group2;

import cc.nuovoline.gui.label.Label;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author xDelsy
 */
public interface GroupWithTitle extends Group {

    @Nullable Label getTitle();

    void setTitle(@Nullable Label title);

}
