package io.github.han896.quest;

import java.util.List;

public class Quest {
    private final String id;
    private final String name;
    private final List<QuestStep> steps;
    // TODO: rewards, giver 등 추가 필드

    public Quest(String id, String name, List<QuestStep> steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<QuestStep> getSteps() {
        return steps;
    }
}