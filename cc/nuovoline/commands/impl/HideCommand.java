package cc.nuovoline.commands.impl;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.commands.NovoCommand;
import cc.nuovoline.modules.AbstractModule;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

public final class HideCommand extends NovoCommand {

    public HideCommand(@NonNull Nuovoline nuovoline) {
        super(nuovoline, "Hide", "Hides modules from arraylist", Arrays.asList("hide", "h"));
    }

    @Override
    public void process(String[] args) {
        if (args.length == 1) {
            final String arg = args[0];
            final AbstractModule module = this.nuovoline.getModuleManager().getByNameIgnoreCase(arg);

            if (module == null) {
                notifyError("Module " + arg + " was not found!");
                return;
            }

            module.setHidden(!module.isHidden());
            notify((module.isHidden() ? "Hidden" : "Shown") + " " + module.getName());
        } else {
            notifyError("Use .hide <module> to hide a module!");
        }
    }

}
