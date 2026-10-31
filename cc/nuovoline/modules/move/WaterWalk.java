package cc.nuovoline.modules.move;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.MotionUpdateEvent;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class WaterWalk extends AbstractModule {

    /* properties */

    /* constructors */
    public WaterWalk(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "WaterWalk", "Water Walk", EnumModuleType.MOVEMENT, "");
    }

    /* methods */
    @EventTarget
    public void onPreUpdate(MotionUpdateEvent event) {
        if (event.getState().equals(MotionUpdateEvent.State.PRE)) {
            if (!mc.player.movementInput().sneak() && mc.player.isInLiquid() && neededLevel(event.getX(), event.getY(), event.getZ())) {
                mc.player.motionY = 0.12;
            }
        }
    }

    private boolean waterUpper(MotionUpdateEvent event) {
        return mc.world.getBlockState(new BlockPos(event.getX(),
                mc.player.getEntityBoundingBox().maxY - 1,
                event.getZ())).getBlock() instanceof BlockLiquid;
    }

    private boolean neededLevel(double x, double y, double z) {
        return (int) mc.world.getBlockState(new BlockPos(x, y, z)).getProperties().get(BlockLiquid.LEVEL) < (isEnabled(Speed.class) ? 2 : 4);
    }
}
