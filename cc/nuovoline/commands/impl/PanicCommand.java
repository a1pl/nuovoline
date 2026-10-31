package cc.nuovoline.commands.impl;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.commands.NovoCommand;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.combat.AntiBot;
import cc.nuovoline.modules.configurations.holder.ModuleHolder;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PanicCommand extends NovoCommand {

    /* constructors */
    public PanicCommand(@NonNull Nuovoline nuovoline) {
        super(nuovoline, "p", "Disable modules", "panic");
    }

    /* methods */
    @Override
    public void process(String[] args) {
        if (args.length > 1) {
            notifyError("Use .panic or .panic (module type)");
            return;
        }

        if (args.length == 0) {
            for (ModuleHolder<?> moduleHolder : this.nuovoline.getModuleManager().getModuleManager().values()) {
                final AbstractModule module = moduleHolder.getModule();
                if (module.isEnabled() && !(module instanceof AntiBot)) {
                    module.toggle();
                }
            }
        } else {
            for (ModuleHolder<?> moduleHolder : this.nuovoline.getModuleManager().getModuleManager().values()) {
                final AbstractModule module = moduleHolder.getModule();
                if (module.isEnabled() && !(module instanceof AntiBot) && module.getType().toString().equalsIgnoreCase(args[0])) {
                    module.toggle();
                }
            }
        }
    }
}
