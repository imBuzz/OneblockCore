package me.buzz.coralmc.oneblockcore.commands.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@RequiredArgsConstructor
@Getter
public enum SenderType {

    PLAYER(sender -> sender instanceof Player),
    CONSOLE(sender -> sender instanceof ConsoleCommandSender),
    BOTH(sender -> true);

    private final Predicate<CommandSender> canExecute;


}

