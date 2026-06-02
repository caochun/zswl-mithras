package cn.zswltech.mithras.contract.overdue.domain.litigation;

import cn.zswltech.mithras.contract.enums.overdue.LitigationStatus;
import cn.zswltech.mithras.contract.overdue.domain.share.Entity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 09:25
 */
@Data
public class CaseProgress implements Entity<LongId> {

    private LongId id;

    private LongId lrId;

    private String stage;

    private LitigationStatus status;

    private String processPerson;

    private LocalDateTime createTime;

    private Long createBy;

    @Override
    public boolean sameIdentityAs(LongId other) {
        return id.equals(other);
    }


    @Override
    public LongId getBizId() {
        return getId();
    }

    @Override
    public String bizIdString() {
        return String.valueOf(id.getId());
    }
}
