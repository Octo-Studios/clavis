package it.hurts.octostudios.clavis.common.minigame;

import it.hurts.octostudios.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Minigame<T extends AbstractMinigameWidget<?>> {
    T widget;
    @Getter List<Rule<T>> rules = new ArrayList<>();
    @Getter float difficulty;
    @Getter int health = 5;

    @Getter long tickCount;
    @Getter float lootQuality;

    public Minigame(T widget) {
        this.widget = widget;
        this.difficulty = 1f;
        this.lootQuality = 1.5f;
    }

    public void hurt() {
        this.health--;
        this.widget.getScreen().animateHeart();

        if (this.health <= 0) {
            this.lose();
            return;
        }

        widget.playHurtAnimation();
    }

    public void lose() {
        widget.playLoseAnimation();
    }

    public void win() {
        widget.playWinAnimation();
    }

    public void processOnTickRules() {
        rules.forEach(rule -> {
            if (rule.getEveryTick() != null) {
                rule.getEveryTick().accept(widget, tickCount);
            }
        });

        tickCount++;
    }

    public void processOnCreateRules() {
        rules.forEach(rule -> {
            if (rule.getOnCreate() != null) {
                rule.getOnCreate().accept(widget);
            }
        });
    }

    public void processOnClickRules(boolean result) {
        rules.forEach(rule -> {
            if (rule.getOnClick() != null) {
                rule.getOnClick().accept(widget, result);
            }
        });
    }

    @SafeVarargs
    public final void addRules(Rule<T>... rules) {
        this.rules.addAll(Arrays.asList(rules));
    }
}
