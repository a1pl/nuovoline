package cc.nuovoline.commands.impl;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.commands.NovoCommand;
import cc.nuovoline.modules.AbstractModule;
import cc.nuovoline.modules.configurations.holder.ModuleHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public final class ToggleCommand extends NovoCommand {

    /* constructors */
    public ToggleCommand(@NonNull Nuovoline nuovoline) {
        super(nuovoline, "t", "Toggles module", "toggle");
    }

    /* methods */
    @Override
    public void process(String[] args) {
        if (args.length < 1) {
            notifyError("Use .toggle (module)");
            return;
        }

        for (String arg : args) {
            if (arg != null) {
                final AbstractModule module = Nuovoline.getInstance().getModuleManager().getByNameIgnoreCase(arg);

                if (module == null) {
                    notifyError("Module " + arg + " was not found!");
                    continue;
                }

                notify((module.toggle() ? "Enabled" : "Disabled") + " " + module.getName());
            }
        }
    }

    @Override
    public @NonNull List<String> completeTabOptions(@NonNull String[] args) {
        final String arg = args[args.length - 1].toLowerCase();
        return this.nuovoline.getModuleManager().getModuleManager().values().stream().map(ModuleHolder::getModule)
                .map(AbstractModule::getName).map(String::toLowerCase).filter(s -> s.startsWith(arg))
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }
}
