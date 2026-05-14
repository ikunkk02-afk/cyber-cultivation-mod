package com.cybercultivation.cultivation;

import com.cybercultivation.component.PlayerQiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class AptitudeGenerator {
    private AptitudeGenerator() {
    }

    public static void generateIfNeeded(UUID uuid, PlayerQiData data) {
        if (data.isAptitudeTested()) {
            return;
        }

        Random random = new Random(uuid.getMostSignificantBits() ^ Long.rotateLeft(uuid.getLeastSignificantBits(), 17));
        CultivationPath path = pick(random, CultivationPath.values());
        CultivationDiscipline mainDiscipline = pick(random, CultivationDiscipline.values());
        List<CultivationDiscipline> subDisciplines = new ArrayList<>(List.of(CultivationDiscipline.values()));
        subDisciplines.remove(mainDiscipline);
        Collections.shuffle(subDisciplines, random);

        CultivationElement element = pick(random, CultivationElement.values());
        data.applyAptitudeResult(
                path,
                mainDiscipline,
                subDisciplines.subList(0, Math.min(2, subDisciplines.size())),
                element
        );
    }

    private static <T> T pick(Random random, T[] values) {
        return values[random.nextInt(values.length)];
    }
}
