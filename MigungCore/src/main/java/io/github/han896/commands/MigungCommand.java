package io.github.han896.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MigungCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("io/github/han896/skills") && args[1].equalsIgnoreCase("all")) {
            if (!player.hasPermission("migung.admin.getskills")) {
                player.sendMessage(ChatColor.RED + "[미궁] 당신은 이 명령어를 사용할 권한이 없습니다.");
                return true;
            }

            player.sendMessage(ChatColor.YELLOW + "[미궁] 모든 스킬을 잠금 해제합니다...");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm i give " + player.getName() + " Longsword_Spin 1");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm i give " + player.getName() + " Tome_Heal 1");
            player.sendMessage(ChatColor.GREEN + "[미궁] 모든 스킬이 지급되었습니다.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "잘못된 명령어입니다. 사용법: /migung skills all");
        return true;
    }
}

