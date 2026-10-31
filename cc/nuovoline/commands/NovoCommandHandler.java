package cc.nuovoline.commands;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.commands.impl.*;
import net.minecraft.command.CommandHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author xDelsy
 */

//@Protect
public class NovoCommandHandler extends CommandHandler {

	public NovoCommandHandler(@NotNull Nuovoline nuovoline) {
		super(".");
		registerCommands(nuovoline);
	}

	public void registerCommands(Nuovoline nuovoline) {
		registerCommand(new TargetCommand(nuovoline));
		registerCommand(new BindCommand(nuovoline));
		registerCommand(new ConfigCommand(nuovoline));
		registerCommand(new FriendCommand(nuovoline));
		registerCommand(new NameCommand(nuovoline));
		registerCommand(new ToggleCommand(nuovoline));
		registerCommand(new VClipCommand(nuovoline));
		registerCommand(new WaypointCommand(nuovoline));
		registerCommand(new PanicCommand(nuovoline));
		registerCommand(new HideCommand(nuovoline));
		registerCommand(new StatusCommand(nuovoline));
		registerCommand(new KillsultsCommand(nuovoline));
		registerCommand(new TeleportCommand(nuovoline));
		registerCommand(new RenameCommand(nuovoline));

		registerCommand(new TestCommand(nuovoline));
	}
}
