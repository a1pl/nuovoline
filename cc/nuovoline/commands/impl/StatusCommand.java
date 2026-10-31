package cc.nuovoline.commands.impl;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.commands.NovoCommand;
import cc.nuovoline.utils.ServerUtils;
import cc.nuovoline.utils.notifications.NotificationType;
import org.checkerframework.checker.nullness.qual.NonNull;


public class StatusCommand extends NovoCommand {

    public StatusCommand(@NonNull Nuovoline nuovoline) {
        super(nuovoline, "status");
    }


    @Override
    public void process(String[] args) {
        if (args.length != 0) {
            return;
        }

        String status = "Status: " + (ServerUtils.isHypixel() ? "Enabled" : "Disabled");
        NotificationType type = ServerUtils.isHypixel() ? NotificationType.SUCCESS : NotificationType.ERROR;

        notifyClient("AutoBypass", status, 5000, type);
    }
}