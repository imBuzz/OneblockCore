package me.buzz.coralmc.oneblockcore.commands.impl.list;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.commands.OneblockCommand;
import me.buzz.coralmc.oneblockcore.islands.object.Island;
import me.buzz.coralmc.oneblockcore.players.User;
import me.buzz.coralmc.oneblockcore.utils.Strings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IsCreate extends OneblockCommand {
    private final OneblockCore core = OneblockCore.get();

    public IsCreate() {
        super("create");
        permission = "oneblock.island.create";
    }

    @Override
    public boolean fakeExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        User user = core.getGame().getUsers().get(player.getName());
        if (user.hasIsland()) {
            player.sendMessage(Strings.translate("Non puoi creare un'altra isola poichè ne hai già una!"));
            return false;
        }

        Island island = new Island(player);
        user.setIslandUUID(island.getUuid());

        player.sendMessage(Strings.translate("&aIsola creata con successo! Per recarti digita: /is go"));
        return super.fakeExecute(sender, args);
    }
}
