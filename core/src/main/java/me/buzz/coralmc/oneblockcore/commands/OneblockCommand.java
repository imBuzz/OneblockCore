package me.buzz.coralmc.oneblockcore.commands;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.buzz.coralmc.oneblockcore.commands.enums.SenderType;
import me.buzz.coralmc.oneblockcore.structures.maps.StringMap;
import me.buzz.coralmc.oneblockcore.utils.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class OneblockCommand extends Command {

    protected String permission = "";
    @Getter
    protected Predicate<CommandSender> checkPermission = sender -> permission.isEmpty() || sender.hasPermission(permission);
    protected SenderType senderType = SenderType.PLAYER;
    protected StringMap<OneblockCommand> children = new StringMap<>();
    protected String usage = "";
    protected String name;
    protected List<String> aliases = Lists.newArrayList();

    public OneblockCommand(String name, String description, String usage, List<String> aliases) {
        super(name, description, usage, aliases);


        this.usage = usage;
        this.aliases = aliases;

        this.name = name;
    }

    public OneblockCommand(String name) {
        super(name);
        this.name = name;
    }

    private boolean checkChildren(CommandSender sender, String[] args) {
        if (children.isEmpty()) {
            return false;
        }
        if (args.length < 1) {
            return false;
        }

        if (children.containsKey(args[0].toLowerCase())) {
            OneblockCommand childrenCommand = children.get(args[0]);
            if (!childrenCommand.getCheckPermission().test(sender)) {
                sender.sendMessage(Strings.translate("&cErrore, non hai il permesso per eseguire quest'azione!"));
                return false;
            }
            childrenCommand.execute(sender, "", args);
            return true;
        }

        return false;
    }

    public List<String> tabCompleter(CommandSender commandSender) {
        if (!children.isEmpty()) return new ArrayList<>(children.keySet());
        return Lists.newArrayList();
    }

    public boolean fakeExecute(CommandSender sender, String[] args) {
        if (checkChildren(sender, args)) return false;

        if (!senderType.getCanExecute().test(sender)) {
            sender.sendMessage("&cErrore, questo comando non è eseguibile da quest'entità");
            return false;
        }
        if (!checkPermission.test(sender)) {
            sender.sendMessage("&cErrore, non hai il permesso per eseguire quest'azione!");
            return false;
        }

        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        return fakeExecute(sender, args);
    }


}
