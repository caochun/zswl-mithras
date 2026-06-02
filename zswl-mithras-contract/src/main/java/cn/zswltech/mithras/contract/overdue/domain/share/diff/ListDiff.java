package cn.zswltech.mithras.contract.overdue.domain.share.diff;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 16:14
 */
@Data
public class ListDiff extends Diff implements Iterable<Diff> {
    private List<Diff> itemDiffs = new ArrayList<>();

    @Override
    public Iterator<Diff> iterator() {
        return itemDiffs.iterator();
    }

    public void addDiff(Diff diff) {
        itemDiffs.add(diff);
    }

    public boolean isEmpty() {
        return itemDiffs.isEmpty();
    }
}
