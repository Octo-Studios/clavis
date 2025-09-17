package it.hurts.shatterbyte.clavis.common.config;

import it.hurts.octostudios.octolib.module.config.annotation.Prop;
import lombok.Data;

@Data
public class Range {
    @Prop
    private double min;

    @Prop
    private double max;

    public Range() {

    }

    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }
}
