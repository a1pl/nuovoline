package cc.nuovoline.modules.move;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.MotionUpdateEvent;
import cc.nuovoline.events.events.MoveEvent;
import cc.nuovoline.events.events.PlayerUpdateEvent;
import cc.nuovoline.events.events.TickUpdateEvent;
import cc.nuovoline.events.types.Priority;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.combat.KillAura;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.BooleanProperty;
import cc.nuovoline.modules.configurations.property.object.DoubleProperty;
import cc.nuovoline.modules.configurations.property.object.PropertyFactory;
import cc.nuovoline.modules.configurations.property.object.StringProperty;
import cc.nuovoline.utils.Timer;
import cc.nuovoline.utils.notifications.NotificationType;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class Flight extends AbstractModule {

    private static final int MAX_ENDER_PEARL_SCAN_DIST = 20;
    private static final long PEARL_DISABLE_DURATION = 2000L;

    private static final C08PacketPlayerBlockPlacement BLOCK_PLACEMENT = new C08PacketPlayerBlockPlacement(
            new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f);

    @Property("flight-mode")
    private final StringProperty flightModeProperty = PropertyFactory.createString(FlightMode.MOTION.name())
            .acceptableValues(FlightMode.MOTION.name(), FlightMode.WATCHDOG.name());
    @Property("view-bobbing")
    private final BooleanProperty viewBobbingProperty = PropertyFactory.booleanTrue();
    @Property("pearl-exploit")
    private final BooleanProperty pearlFlyProperty = PropertyFactory.booleanTrue();
    @Property("toggle-aura")
    private final BooleanProperty toggleAuraProperty = PropertyFactory.booleanTrue();
    @Property("timer")
    private final BooleanProperty timerProperty = PropertyFactory.booleanTrue();
    @Property("speed")
    private final DoubleProperty speedProperty = PropertyFactory.createDouble(2.5D).minimum(0.1D).maximum(5.0D);

    private final Timer timer = new Timer();
    private final Timer disabledTimer = new Timer();

    private long estimatedTimeUntilLanded;
    private double distWhenThrown;
    private boolean killauraWasEnabled;
    private KillAura aura;
    private boolean pearlAirBourne;
    private boolean isThrowing;
    private boolean hasLanded;
    private int pearlSlot;

    @EventTarget(Priority.LOWEST)
    public void onStepEvent(PlayerUpdateEvent event) {
        mc.player.stepHeight = 0.0F;
    }

    @EventTarget(Priority.HIGHEST)
    public void onMoveEntityEvent(MoveEvent e) {
        switch (mode()) {
            case MOTION:
                if (pearlFlyProperty.get() && !hasLanded) {
                    cancel(e);
                    return;
                }
                if (mc.player.isMoving())
                    e.setMoveSpeed(speedProperty.get());
                break;
            case WATCHDOG:
                cancel(e);
                break;
        }
    }

    @EventTarget(Priority.HIGHEST)
    public void onUpdatePositionEvent(MotionUpdateEvent e) {
        if (e.getState() == MotionUpdateEvent.State.PRE) {
            final EntityPlayerSP player = mc.player;

            if (mode() == FlightMode.WATCHDOG) {
                player.motionY = 0.0D;
                if (player.isMoving() && player.ticksExisted % 10 == 0) {
                    double baseMoveSpeed = player.getBaseMoveSpeed();
                    double yaw = Math.toRadians(player.rotationYaw);
                    player.setPosition(
                            player.posX + -Math.sin(yaw) * baseMoveSpeed,
                            player.posY,
                            player.posZ + Math.cos(yaw) * baseMoveSpeed
                    );
                }
                return;
            }

            if (mode() == FlightMode.MOTION && pearlFlyProperty.get()) {
                if (disabledTimer.delay(PEARL_DISABLE_DURATION)) {
                    toggle();
                    return;
                }

                if (!pearlAirBourne && !hasLanded) {
                    final int pearlStackSlot = findPearlsInHotBar();

                    if (pearlStackSlot == -1) {
                        nuovoline.getNotificationManager().pop(
                                "Pearl Fly",
                                "You must have pearls on your hotbar",
                                NotificationType.ERROR);
                        toggle();
                        return;
                    }

                    if (!isThrowing) {
                        final double dist = estimateDistFromGround(MAX_ENDER_PEARL_SCAN_DIST);

                        if (dist < MAX_ENDER_PEARL_SCAN_DIST) {
                            final boolean needSwitch = mc.player.inventory.currentItem != pearlStackSlot;

                            if (needSwitch)
                                sendPacketNoEvent(new C09PacketHeldItemChange(pearlStackSlot));

                            distWhenThrown = dist;
                            pearlSlot = pearlStackSlot;
                            isThrowing = true;
                            e.setPitch(90.0F);
                        }
                    } else {
                        if (pearlStackSlot != pearlSlot) {
                            toggle();
                            return;
                        }

                        estimatedTimeUntilLanded = (long) (((distWhenThrown / 20) / 1.5) * 1000);
                        sendPacketNoEvent(BLOCK_PLACEMENT);
                        final int physicalHeldItem = mc.player.inventory.currentItem;
                        if (pearlSlot != physicalHeldItem)
                            sendPacketNoEvent(new C09PacketHeldItemChange(physicalHeldItem));
                        isThrowing = false;
                        pearlAirBourne = true;
                        disabledTimer.reset();
                    }
                }

                if (pearlAirBourne && disabledTimer.delay(estimatedTimeUntilLanded + getPingToCurrentServer() * 2)) {
                    nuovoline.getNotificationManager().pop(
                            "Pearl Fly",
                            String.format("You can now fly for %ss", PEARL_DISABLE_DURATION / 1000),
                            (int) PEARL_DISABLE_DURATION,
                            NotificationType.SUCCESS);
                    hasLanded = true;
                    disabledTimer.reset();
                    pearlAirBourne = false;
                }
            }

            if (viewBobbingProperty.get())
                player.cameraYaw = 0.105F;

            // MOTION mode Y-motion control
            if (mode() == FlightMode.MOTION) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    player.motionY = 1.0F;
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    player.motionY = -1.0F;
                } else {
                    mc.player.motionY = 0.0D;
                }
            }
        }
    }

    public Flight(@NotNull ModuleManager moduleManager) {
        super(moduleManager, EnumModuleType.MOVEMENT, "Flight", "Flight");
        Manager.put(new Setting("FLY_MODE", "Mode", SettingType.COMBOBOX, this, flightModeProperty));
        Manager.put(new Setting("FLY_VB", "View Bobbing", SettingType.CHECKBOX, this, viewBobbingProperty));
        Manager.put(new Setting("FLY_PEARL", "Pearl Exploit", SettingType.CHECKBOX, this, pearlFlyProperty,
                () -> mode() == FlightMode.MOTION));
        Manager.put(new Setting("FLY_TOGGLE_AURA", "Toggle Aura", SettingType.CHECKBOX, this, toggleAuraProperty));
        Manager.put(new Setting("FLY_TIMER", "Timer", SettingType.CHECKBOX, this, timerProperty));
        Manager.put(new Setting("FLY_SPEED", "Speed", SettingType.SLIDER, this, speedProperty, 0.05,
                () -> mode() != FlightMode.WATCHDOG));
    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        setSuffix(flightModeProperty.get());
    }

    @Override
    public void onEnable() {
        setSuffix(flightModeProperty.get());
        isThrowing = false;
        hasLanded = false;
        pearlAirBourne = false;
        pearlSlot = -1;

        disabledTimer.reset();
        timer.reset();

        if (aura == null)
            aura = getModule(KillAura.class);

        if (toggleAuraProperty.get() && (killauraWasEnabled = aura.isEnabled()))
            aura.toggle();
    }

    private int findPearlsInHotBar() {
        for (int i = 36; i < 45; i++) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemEnderPearl)
                return i - 36;
        }
        return -1;
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6F;
        mc.timer.timerSpeed = 1.0F;

        if (toggleAuraProperty.get() && (killauraWasEnabled && !aura.isEnabled()))
            aura.toggle();
    }

    private FlightMode mode() {
        return FlightMode.valueOf(flightModeProperty.get());
    }

    private static void cancel(MoveEvent e) {
        e.setX(0.0D);
        e.setY(0.0D);
        e.setZ(0.0D);
    }

    private double estimateDistFromGround(int maxDist) {
        for (int i = 0; i < maxDist; i++) {
            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - i, mc.player.posZ);
            if (!(mc.world.getBlockState(pos).getBlock() instanceof BlockAir))
                return mc.player.posY - (pos.getY() + 1);
        }
        return maxDist;
    }

    private long getPingToCurrentServer() {
        if (mc.getNetHandler() == null) return 0L;
        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.player.getName());
        return info != null ? info.getResponseTime() : 0L;
    }

    private enum FlightMode {
        MOTION,
        WATCHDOG
    }
}