package io.github.han896.skill.type;

import io.github.han896.skill.SkillData;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface Skill {
    /**
     * 스킬을 시전합니다.
     * @param caster 스킬을 시전한 플레이어
     * @param skillData 시전된 스킬의 정보
     */
    void execute(Player caster, SkillData skillData);
}