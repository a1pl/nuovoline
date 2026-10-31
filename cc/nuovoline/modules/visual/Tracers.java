package cc.nuovoline.modules.visual;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.Render3DEvent;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.PlayerManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.BooleanProperty;
import cc.nuovoline.modules.configurations.property.object.PropertyFactory;
import cc.nuovoline.utils.RenderUtils;
import net.minecraft.entity.player.EntityPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.stream.Collectors;

public class Tracers extends AbstractModule {

    @Property("only-tar")
    BooleanProperty only_tar = PropertyFactory.booleanFalse();

    public Tracers(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Tracers", EnumModuleType.VISUALS);
        Manager.put(new Setting("TC_ONLY_TAR", "Targets Only", SettingType.CHECKBOX, this, only_tar));
    }

    @EventTarget
    public void on3DRender(Render3DEvent event) {
        for (EntityPlayer player : mc.world.getPlayerEntities().stream().filter(e -> !only_tar.get()
                || nuovoline.getPlayerManager().hasType(e.getName(), PlayerManager.EnumPlayerType.TARGET)).collect(Collectors.toList())) {

            if (player.isEntityAlive() && player != mc.player && !player.isInvisible()) {
                final double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().renderPosX;
                final double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().renderPosY;
                final double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().renderPosZ;
                boolean old = mc.gameSettings.viewBobbing;

                RenderUtils.startDrawing();
                mc.gameSettings.viewBobbing = false;
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                mc.gameSettings.viewBobbing = old;

                boolean target = nuovoline.getPlayerManager().hasType(player.getName(), PlayerManager.EnumPlayerType.TARGET);
                double[] color = target ? new double[]{1.0D, 0.0D, 0.0D} : new double[]{1.0D, 1.0D, 1.0D};
                RenderUtils.drawLine(player, color, posX, posY + player.getEyeHeight(), posZ);
                RenderUtils.stopDrawing();
            }
        }
    }
}