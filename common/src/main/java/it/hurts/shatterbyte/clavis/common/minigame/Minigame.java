package it.hurts.shatterbyte.clavis.common.minigame;

import it.hurts.shatterbyte.clavis.common.Clavis;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.AbstractMinigameWidget;
import it.hurts.shatterbyte.clavis.common.client.screen.widget.MirrorWidget;
import it.hurts.shatterbyte.clavis.common.data.Lock;
import it.hurts.shatterbyte.clavis.common.minigame.rule.EndRules;
import it.hurts.shatterbyte.clavis.common.minigame.rule.Rule;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

public class Minigame<T extends AbstractMinigameWidget<?>> {
    @Getter
    ResourceLocation minigameType;

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
        this.lootQuality = Clavis.CONFIG.getStartingQuality();
    }

    public void hurt() {
        this.health--;
        this.lootQuality -= Clavis.CONFIG.getQualityPenaltyPerHit();
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

    public void finish() {
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
        boolean shouldSwap = false;

        for (Rule<T> rule : rules) {
            if (rule.equals(EndRules.SWAP)) {
                shouldSwap = true;
                continue;
            }

            if (rule.getOnClick() != null) {
                rule.getOnClick().accept(widget, result);
            }
        }

        if (shouldSwap) {
            EndRules.SWAP.getOnClick().accept((MirrorWidget) widget, result);
        }
    }

    @SuppressWarnings("unchecked")
    public void load(Lock lock, Level level) {
        this.difficulty = lock.getDifficulty();
        this.seed = lock.getSeed();
        this.minigameType = lock.getType(level);
        if (!lock.getRules().isEmpty()) {
            this.rules.addAll(lock.getRules()
                    .stream()
                    .map(id -> (Rule<T>) Rule.getRegisteredRule(id))
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
