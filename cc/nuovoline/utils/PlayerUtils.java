package cc.nuovoline.utils;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.binds.KeyboardKeybind;
import cc.nuovoline.modules.configurations.holder.ModuleHolder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

public final class PlayerUtils {

	private static int pressCount;
	private static final Minecraft mc = Minecraft.getInstance();
	private static double lastX, lastY, lastZ;


	public static void onBindInGui() {
		if(Keyboard.getEventKeyState()) {
			int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

			for(ModuleHolder<?> holder : Nuovoline.getInstance().getModuleManager().getModuleManager().values()) {
				AbstractModule module = holder.getModule();

				//noinspection InstanceofConcreteClass
				if(module.getKeybind().get() instanceof KeyboardKeybind && module.getKeybind().get().getKey() == k) {
					if(Keyboard.isKeyDown(k)) {
						pressCount++;
					}

					if(pressCount == 1) {
						module.toggle();
					}
				}
			}
		} else {
			pressCount = 0;
		}
	}

	public static boolean inTeam(@NotNull ICommandSender entity0, @NotNull ICommandSender entity1) {
		String s = "\u00a7" + teamColor(entity0);

		return     entity0.getDisplayName().getFormattedText().contains(s)
				&& entity1.getDisplayName().getFormattedText().contains(s);
	}

	public static boolean inTeamWithMinecraftPlayer(@NotNull ICommandSender entity) {
		return inTeam(Minecraft.getInstance().player, entity);
	}

	public static @NotNull String teamColor(@NotNull ICommandSender player) {
		Matcher matcher = Pattern.compile("\u00a7(.).*\u00a7r").matcher(player.getDisplayName().getFormattedText());
		return matcher.find() ? matcher.group(1) : "f";
	}

	@Contract(value = "-> fail", pure = true)
	private PlayerUtils() {
		throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
	public static float getMoveForward() {
		if (mc.gameSettings.keyBindForward.isKeyDown()) {
			return 1.0F; // Moving forward
		} else if (mc.gameSettings.keyBindBack.isKeyDown()) {
			return -1.0F; // Moving backward
		}
		return 0.0F; // No forward/backward movement
	}

	/**
	 * Get the strafe movement input from the player.
	 * @return 1.0F if moving left, -1.0F if moving right, 0.0F if no strafe input.
	 */
	public static float getMoveStrafe() {
		if (mc.gameSettings.keyBindLeft.isKeyDown()) {
			return 1.0F; // Strafing left
		} else if (mc.gameSettings.keyBindRight.isKeyDown()) {
			return -1.0F; // Strafing right
		}
		return 0.0F; // No strafe movement
	}


	/**
	 * Teleports the player relatively by the specified deltas.
	 * @param dx Movement on the X-axis
	 * @param dy Movement on the Y-axis
	 * @param dz Movement on the Z-axis
	 */
	public static void tpRel(double dx, double dy, double dz) {
		EntityPlayerSP player = mc.player;
		if (player != null) {
			player.setPosition(player.posX + dx, player.posY + dy, player.posZ + dz);
		}
	}

	/**
	 * Updates the position of the player on the client/server side.
	 * Usually used after manually setting position to inform the server.
	 */
	public static void updatePosition(EntityPlayerSP player) {
		if (player != null &&
				(player.posX != lastX || player.posY != lastY || player.posZ != lastZ)) {

			mc.player.connection.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
					player.posX, player.posY, player.posZ, player.onGround));

			lastX = player.posX;
			lastY = player.posY;
			lastZ = player.posZ;
		}
	}
}



