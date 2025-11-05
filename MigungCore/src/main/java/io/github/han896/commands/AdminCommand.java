package io.github.han896.commands;

import io.github.han896.MigungCore;
import io.github.han896.player.PlayerData;
import io.github.han896.player.StatUpdateManager;
import io.github.han896.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("migung.admin")) {
            sender.sendMessage(ChatColor.RED + "권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "--- 미궁 관리자 명령어 ---");
            sender.sendMessage(ChatColor.GOLD + "/mga resetstats <player>");
            sender.sendMessage(ChatColor.GOLD + "/mga givestatpoints <player> <amount>");
            sender.sendMessage(ChatColor.GOLD + "/mga giveitem <item_id> [amount]");
            sender.sendMessage(ChatColor.GOLD + "/mga iteminfo");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "resetstats":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "사용법: /mga resetstats <플레이어이름>");
                    return true;
                }
                Player targetReset = Bukkit.getPlayer(args[1]);
                if (targetReset == null) {
                    sender.sendMessage(ChatColor.RED + "플레이어를 찾을 수 없습니다.");
                    return true;
                }
                PlayerData dataReset = MigungCore.getInstance().getPlayerDataManager().getPlayerData(targetReset);
                if (dataReset != null) {
                    dataReset.getStats().initRandomStats(); // PlayerStats의 메소드 호출
                    StatUpdateManager.updateStats(targetReset, dataReset);
                    sender.sendMessage(ChatColor.GREEN + targetReset.getName() + "님의 스탯을 리세마라했습니다.");
                    targetReset.sendMessage(ChatColor.GOLD + "관리자에 의해 당신의 스탯이 리세마라되었습니다.");
                }
                return true;

            case "givestatpoints":
                if (args.length != 3) {
                    sender.sendMessage(ChatColor.RED + "사용법: /mga givestatpoints <플레이어이름> <갯수>");
                    return true;
                }
                Player targetGive = Bukkit.getPlayer(args[1]);
                if (targetGive == null) {
                    sender.sendMessage(ChatColor.RED + "플레이어를 찾을 수 없습니다.");
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    PlayerData dataGive = MigungCore.getInstance().getPlayerDataManager().getPlayerData(targetGive);
                    if (dataGive != null) {
                        dataGive.getStats().addStatPoints(amount); // PlayerStats의 메소드 호출
                        sender.sendMessage(ChatColor.GREEN + targetGive.getName() + "님에게 스탯 포인트 " + amount + "개를 지급했습니다.");
                        targetGive.sendMessage(ChatColor.AQUA + "관리자로부터 스탯 포인트 " + amount + "개를 받았습니다.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "갯수는 반드시 숫자여야 합니다.");
                }
                return true;

            case "giveitem":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "사용법: /mga giveitem <아이템ID> [갯수]");
                    return true;
                }
                String itemId = args[1];
                int amount = (args.length > 2) ? Integer.parseInt(args[2]) : 1;

                ItemFactory factory = MigungCore.getInstance().getItemFactory();
                ItemStack item = factory.createItem(itemId);

                if (item != null) {
                    item.setAmount(amount);
                    player.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GREEN + itemId + " 아이템을 " + amount + "개 획득했습니다.");
                } else {
                    player.sendMessage(ChatColor.RED + "알 수 없는 아이템 ID입니다: " + itemId);
                }
                return true;

            case "iteminfo":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
                    return true;
                }
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                // ItemFactory의 getTag는 static 메소드가 아니므로, 인스턴스를 통해 호출해야 합니다.
                // 혹은 getTag를 static으로 만들 수도 있습니다. 여기서는 ItemFactory의 역할을 존중하여 인스턴스를 사용합니다.
                String tag = MigungCore.getInstance().getItemFactory().getTag(itemInHand, "your_key_here"); // TODO: 확인할 키를 지정해야 함

                if (tag != null) {
                    player.sendMessage(ChatColor.GREEN + "아이템 고유 코드: " + ChatColor.WHITE + tag);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "이 아이템에는 고유 코드가 없습니다.");
                }
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("resetstats", "givestatpoints", "giveitem", "iteminfo");
            return StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("resetstats") || args[0].equalsIgnoreCase("givestatpoints")) {
                List<String> playerNames = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(p -> playerNames.add(p.getName()));
                return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<>());
            }
            if (args[0].equalsIgnoreCase("giveitem")) {
                List<String> itemIds = new ArrayList<>(MigungCore.getInstance().getItemFactory().getItemIds());
                return StringUtil.copyPartialMatches(args[1], itemIds, new ArrayList<>());
            }
        }
        return Collections.emptyList();
    }
}