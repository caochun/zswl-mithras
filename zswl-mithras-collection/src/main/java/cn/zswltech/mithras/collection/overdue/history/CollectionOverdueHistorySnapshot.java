package cn.zswltech.mithras.collection.overdue.history;

import lombok.Data;

@Data
public class CollectionOverdueHistorySnapshot {

    private Long clientId;

    private Long overdueRent;

    private Long lateCharge;

    private Integer curMaxOverdueDays;
}
