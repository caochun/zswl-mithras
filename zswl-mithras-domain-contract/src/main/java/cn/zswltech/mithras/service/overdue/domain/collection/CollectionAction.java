package cn.zswltech.mithras.service.overdue.domain.collection;

import cn.zswltech.mithras.service.enums.overdue.LetterType;
import cn.zswltech.mithras.service.enums.overdue.OverdueCollectionType;
import cn.zswltech.mithras.service.overdue.domain.share.Entity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 16:44
 */
@Data
public class CollectionAction implements Entity<CollectionActionId> {
    private CollectionActionId actionId;
    private ActionCode actionCode;
    private CollectionId collectionId;
    private LocalDateTime date;
    private OverdueCollectionType type;
    private String describe;
    private Long createBy;
    private String processPerson;

    private LetterType letterType;
    private List<Long> contractIds;
    private List<String> contractCodes;
    private String processStatus;
    private String processId;

    public void setDate(LocalDate date) {
        if (date == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        this.date = now.withYear(date.getYear()).withMonth(date.getMonthValue()).withDayOfMonth(date.getDayOfMonth());
    }

    @Override
    public boolean sameIdentityAs(CollectionActionId other) {
        return false;
    }


    @Override
    public CollectionActionId getBizId() {
        return getActionId();
    }

    @Override
    public String bizIdString() {
        return actionId.getId().toString();
    }
}
