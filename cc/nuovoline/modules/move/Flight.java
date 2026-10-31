package cc.nuovoline.modules.move;

import cc.nuovoline.events.EventTarget;
import cc.nuovoline.events.events.*;
import cc.nuovoline.gui.screen.setting.Manager;
import cc.nuovoline.gui.screen.setting.Setting;
import cc.nuovoline.gui.screen.setting.SettingType;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.EnumModuleType;
import cc.nuovoline.modules.ModuleManager;
import cc.nuovoline.modules.configurations.annotation.Property;
import cc.nuovoline.modules.configurations.property.object.*;
import cc.nuovoline.modules.exploits.Disabler;
import cc.nuovoline.utils.ServerUtils;
import cc.nuovoline.utils.Servers;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import org.checkerframework.checker.nullness.qual.NonNull;
import viaversion.viarewind.utils.PacketUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Flight extends AbstractModule {

    private static final int LAG_EXPLOIT_TICKS = 120;
    private static final double BRUH_MIN = 0.00000014D;
    private static final double BRUH_MAX = 0.00000043D;
    private static final double[] LAG_EXPLOIT_OFFSETS = {
            0.41999998688697815D,
            0.7531999805212024D,
            1.0013359791121417D
    };

    private int tick;
    private int ticks;

    private int bruhTick;
    private double bruh;
    private int lagExploitTicks;

    private boolean wait;
    private int key;
    private short id;
    private List<Packet> listPosition = new CopyOnWriteArrayList();
    private List<Packet> listPing = new CopyOnWriteArrayList();
    @Property("Fly-mode")
    private final StringProperty fly_mode = PropertyFactory.createString("Karhu").acceptableValues("Karhu", "Hypixel");

    @Property("dmg-mode")
    private final StringProperty dmg_mode = PropertyFactory.createString("Normal").acceptableValues("Fast", "Normal");
    @Property("speed")
    private final DoubleProperty speed = PropertyFactory.createDouble(2.5D).maximum(9.0D).minimum(1.0D);
    @Property("viewbobbing")
    private final FloatProperty view_bobbing = PropertyFactory.createFloat(60.0F).minimum(0.0F).maximum(100.0F);
    @Property("pearl")
    private final BooleanProperty pearl = PropertyFactory.booleanFalse();

    public Flight(@NonNull ModuleManager nuovoline) {
        super(nuovoline, EnumModuleType.MOVEMENT, "Flight", "Flight");
        //   Manager.put(new Setting("FLY_DMG_MODE", "Damage", SettingType.COMBOBOX, this, dmg_mode));
        Manager.put(new Setting("Fly_Mode","Mode",SettingType.COMBOBOX,this,fly_mode));
        Manager.put(new Setting("FLY_SPEED", "Speed", SettingType.SLIDER, this, speed, 0.1,()->fly_mode.get().equals("Hypixel")));
        Manager.put(new Setting("FLY_VB", "Viewbobbing", SettingType.SLIDER, this, view_bobbing, 5.0F,()->fly_mode.get().equals("Hypixel")));
        Manager.put(new Setting("FLY_PEARL", "Pearl boost", SettingType.CHECKBOX, this, pearl,()->fly_mode.get().equals("Hypixel")));
    }

    @Override
    public void onDisable() {

        getModule(Disabler.class).getTimer().reset();

        if (fly_mode.get().equals("Karhu")){
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }else if (fly_mode.get().equals("Hypixel")){
            if (tick > 0) {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
                mc.player.motionY = -0.41999998688698;
                mc.timer.timerSpeed = 1.0F;
            }
            wait = false;
            tick = 0;
            bruhTick = 0;
            bruh = 0.0D;
            lagExploitTicks = 0;
        }
    }


    @Override
    public void onEnable() {
        checkModule(Speed.class,Scaffold.class);
        setSuffix(fly_mode.get());

        if (mc.player == null) return;

        if (fly_mode.get().equals("Hypixel")) {
            tick = 0;
            bruhTick = 0;
            wait = false;
            lagExploitTicks = LAG_EXPLOIT_TICKS;
            sendLagExploit();

            int projectile = projectileSlot();
            if (mc.player.onGround && pearl.get() && projectile != -1) {
                wait = true;
                throwProjectile(projectile);
                mc.player.motionY = mc.player.getBaseMotionY();
            }
        }else if (fly_mode.get().equals("Karhu")){
            ticks = 0;
            PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.player.posX, mc.player.posY - 2, mc.player.posZ,
                    mc.player.rotationYaw, mc.player.rotationPitch, false));
        }

    }

    private void sendLagExploit() {
        for (double offset : LAG_EXPLOIT_OFFSETS) {
            sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.player.posX, mc.player.posY + offset, mc.player.posZ, false));
        }
    }

    public int projectileSlot() {
        int fireCharge = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == null) continue;

            if (stack.getItem() == Items.ender_pearl) {
                if (ServerUtils.serverIs(Servers.SW) && ServerUtils.inGameSeconds() < 30 &&
                        stack.getDisplayName().contains("\u00A7")) {
                    continue;
                }

                return i;
            }

            if (stack.getItem() == Items.fire_charge && fireCharge == -1) {
                fireCharge = i;
            }
        }

        return fireCharge;
    }

    private void throwProjectile(int slot) {
        ItemStack held = mc.player.inventory.getStackInSlot(slot);

        sendPacketNoEvent(new C09PacketHeldItemChange(slot));
        sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.player.rotationYaw, 89.4F, mc.player.onGround));
        sendPacketNoEvent(new C08PacketPlayerBlockPlacement(held));
        sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
        sendPacketNoEvent(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (fly_mode.get().equals("Hypixel")) {
            if (event.getState().equals(PacketEvent.State.INCOMING)) {
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    if (wait) {
                        wait = false;
                    } else if (tick > 3 && lagExploitTicks <= 0) {
                        checkModule(getClass());
                    }
                }
            }
        }

    }

    @EventTarget
    public void onTick(TickUpdateEvent event) {
        if (fly_mode.get().equals("Hypixel")) {
            if (lagExploitTicks > 0) {
                lagExploitTicks--;
            } else if (wait) {
                wait = false;
            }

            if (!wait && mc.player.isMoving()) {
                tick++;
            }
        }

        setSuffix(fly_mode.get());
    }

    private int getPacketsSize() {
        int jumps = mc.player.isPotionActive(Potion.jump) ? mc.player.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0;
        int hypixel = ServerUtils.serverIs(Servers.UHC) || ServerUtils.serverIs(Servers.SG) || ServerUtils.serverIs(Servers.MW) ? 1 : 0;
        double fallHeight = 3 + jumps + hypixel, amp = 0.125;

        return (int) (fallHeight / amp) * 2 + 2;
    }
    @EventTarget
    public void onStrafe(StrafeEvent event){
        if (fly_mode.get().equals("Karhu")){
            final float speed = 1;
            event.setSpeed(speed);

        }
    }

    @EventTarget
    public void onPre(MotionUpdateEvent event) {
        if (fly_mode.get().equals("Hypixel")) {
            if (event.getState() == MotionUpdateEvent.State.PRE) {
                mc.player.cameraYaw = view_bobbing.get() / 1000.0F;

                boolean up = mc.player.movementInput().jump();

                if (up) {
                    mc.player.motionY = 1.8;
                } else if (mc.player.movementInput().sneak()) {
                    mc.player.motionY = -1.8;
                } else if (!mc.player.onGround) {
                    mc.player.motionY = 0.0;
                }

                bruhTick++;
                bruh = ThreadLocalRandom.current().nextDouble(BRUH_MIN, BRUH_MAX);
                event.setY(event.getY() + (float) bruh);
                event.setOnGround((bruhTick % 5 == 0 || up) && ServerUtils.isHypixel());
            }
        }else if (fly_mode.get().equals("Karhu")){
            final float speed = 1;

            mc.player.motionY = -1E-10D
                    + (mc.gameSettings.keyBindJump.isKeyDown() ? speed : 0.0D)
                    - (mc.gameSettings.keyBindSneak.isKeyDown() ? speed : 0.0D);

            if (mc.player.getDistance(mc.player.lastReportedPosX, mc.player.lastReportedPosY, mc.player.lastReportedPosZ) <= 10 - speed - 0.15) {
                event.setCancelled(true);
            } else {
                ticks++;

                if (ticks >= 8) {
                    mc.player.motionX = 0;
                    mc.player.motionZ = 0;

                    toggle();
                    //getParent().toggle();
                }
            }
        }

    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if (fly_mode.get().equals("Hypixel")) {
            event.setMoveSpeed(wait ? 0 : speed.get());
        }

    }

    public DoubleProperty getSpeed() {
        return speed;
    }

    public int getTick() {
        return tick;
    }
}
