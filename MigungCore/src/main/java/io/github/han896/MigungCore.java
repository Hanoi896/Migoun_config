package io.github.han896;

import io.github.han896.commands.*;
import io.github.han896.listeners.*;
import io.github.han896.player.PlayerDataManager;
import io.github.han896.quest.QuestManager;
import io.github.han896.skill.SkillManager;
import io.github.han896.ui.UIManager;
import io.github.han896.util.ItemFactory;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class MigungCore extends JavaPlugin {

    private static MigungCore instance;
    private PlayerDataManager playerDataManager;
    private UIManager uiManager;
    private QuestManager questManager;
    private ItemFactory itemFactory;
    private SkillManager skillManager;

    @Override
    public void onEnable() {
        instance = this;
        // 모든 관리자 클래스 초기화
        playerDataManager = new PlayerDataManager();
        uiManager = new UIManager(this);
        questManager = new QuestManager(this);
        itemFactory = new ItemFactory(this);
        skillManager = new SkillManager(this);

        getLogger().info("MigungCore 플러그인이 활성화되었습니다.");

        // 모든 명령어 등록
        registerCommand("migungadmin", new AdminCommand());
        registerCommand("migung", new MigungCommand());
        registerCommand("myskills", new MySkillsCommand());
        registerCommand("stats", new StatsCommand());
        registerCommand("magic", new MagicCommand());

        // 모든 리스너 등록
        getServer().getPluginManager().registerEvents(new CombatModeListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new GUIClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerAttackListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new SpellCastListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("MigungCore 플러그인이 비활성화되었습니다.");
    }

    // 명령어와 탭 자동완성을 함께 등록하는 헬퍼 메소드
    private void registerCommand(String cmd, CommandExecutor exec) {
        PluginCommand command = this.getCommand(cmd);
        if (command != null) {
            command.setExecutor(exec);
            if (exec instanceof TabCompleter) {
                command.setTabCompleter((TabCompleter) exec);
            }
        }
    }

    // 다른 클래스에서 관리자 클래스에 쉽게 접근할 수 있도록 하는 Getter 메소드들
    public static MigungCore getInstance() { return instance; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public UIManager getUiManager() { return uiManager; }
    public QuestManager getQuestManager() { return questManager; }
    public ItemFactory getItemFactory() { return itemFactory; }
    public SkillManager getSkillManager() { return skillManager; }
}