package it.hurts.octostudios.clavis.common.minigame;

import it.hurts.octostudios.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.octostudios.clavis.common.data.Lock;
import it.hurts.octostudios.clavis.common.minigame.rule.Rule;
import lombok.Getter;

import java.util.*;

public class Minigame<T extends AbstractMinigameWidget<?>> {
    T widget;
    @Getter
    List<Rule<T>> rules = new ArrayList<>();
    @Getter
    float difficulty;
    @Getter
    int health = 5;

    @Getter
    long tickCount;
    @Getter
    float lootQuality;

    @Getter
    long seed;

    public Minigame(T widget) {
        this.widget = widget;
        this.difficulty = 1f;
        this.lootQuality = 1.5f;
    }

    public void hurt() {
        this.health--;
        this.lootQuality -= 0.2f;
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

    @SuppressWarnings("unchecked")
    public void load(Lock lock) {
        this.difficulty = lock.getDifficulty();
        this.seed = lock.getSeed();
        if (!lock.getRules().isEmpty()) {
            this.rules.addAll(lock.getRules()
                    .stream()
                    .map(id -> (Rule<T>) Rule.getRegisteredRule(id, widget.getClass()))
                    .filter(Objects::nonNull)
                    .toList()
            );
        }
    }

    @SafeVarargs
    public final void addRules(Rule<T>... rules) {
        this.rules.addAll(Arrays.asList(rules));
    }

    public final void addRules(List<Rule<T>> rules) {
        this.rules.addAll(rules);
    }
}
