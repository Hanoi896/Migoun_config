package io.github.han896.quest;

public class QuestStep {
    private final String type;
    private final String target;
    private final int amount;
    private final String description;

    public QuestStep(String type, String target, int amount, String description) {
        this.type = type;
        this.target = target;
        this.amount = amount;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}