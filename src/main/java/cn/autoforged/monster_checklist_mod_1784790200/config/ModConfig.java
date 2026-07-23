package cn.autoforged.monster_checklist_mod_1784790200.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ModConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> MILESTONE_THRESHOLDS;

    private static final List<Integer> DEFAULT_MILESTONES = List.of(5, 10, 20, 35, 50);

    static {
        var builder = new ModConfigSpec.Builder();

        builder.comment("Milestone rewards settings").push("milestones");

        MILESTONE_THRESHOLDS = builder
                .comment("Milestone unlock thresholds (sorted ascending). Add new milestones as you progress.")
                .defineList("milestoneThresholds", DEFAULT_MILESTONES, () -> 1, o -> o instanceof Integer i && i > 0);

        builder.pop();

        SPEC = builder.build();
    }

    public static int[] getMilestoneArray() {
        return MILESTONE_THRESHOLDS.get().stream().mapToInt(Integer::intValue).toArray();
    }

    private ModConfig() {}
}
