package cn.zswltech.mithras.contract.overdue.domain.share.diff;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 16:09
 */
@Data
public class EntityDiff {

    public static final EntityDiff EMPTY = new EntityDiff();

    private final Map<String, Diff> diffMap = new HashMap<>();

    private boolean selfModified;

    public EntityDiff() {
    }

    public boolean isEmpty() {
        return diffMap.isEmpty();
    }

    public Diff getDiff(String lineItems) {
        return diffMap.get(lineItems);
    }

    public void addDiff(String lineItems, Diff diff) {
        diffMap.put(lineItems, diff);
    }
}
