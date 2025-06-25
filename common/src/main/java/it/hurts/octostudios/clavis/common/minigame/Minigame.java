package it.hurts.octostudios.clavis.common.minigame;

import it.hurts.octostudios.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.octostudios.clavis.common.minigame.rule.OverworldRules;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import lombok.Getter;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Minigame<T extends AbstractMinigameWidget<?>> {
    T widget;
    List<Rule<T>> rules = new ArrayList<>();
    @Getter float difficulty;
    @Getter int health = 5;

    public Minigame(T widget) {
        this.widget = widget;
        this.difficulty = 1f;
    }

    public void hurt() {
        this.health--;

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

    public void processOnTickRules(long tickCount) {
        rules.forEach(rule -> {
            if (rule.getEveryTick() != null) {
                rule.getEveryTick().accept(widget, tickCount);
            }
        });
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
