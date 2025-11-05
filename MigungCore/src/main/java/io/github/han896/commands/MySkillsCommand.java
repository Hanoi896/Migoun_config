package io.github.han896.commands;

import io.github.han896.ui.SkillGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MySkillsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 1. 명령어를 실행한 것이 콘솔이 아닌, 플레이어인지 확인합니다.
        if (!(sender instanceof Player player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        // 2. 새로운 SkillGUI 객체를 만들고, 플레이어에게 GUI를 열어줍니다.
        new SkillGUI().openInventory(player);
        return true;
    }
}