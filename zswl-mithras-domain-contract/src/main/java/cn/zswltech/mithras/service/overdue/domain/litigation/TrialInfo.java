package cn.zswltech.mithras.service.overdue.domain.litigation;

import cn.zswltech.mithras.service.overdue.domain.share.Entity;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 09:24
 */
@Data
public class TrialInfo implements Entity<LongId> {

    private LongId id;

    private LongId lrId;

    private String fcaseNo;

    private LocalDate ffilingDate;

    private String facceptingCourt;

    private LocalDate fhearingDate;

    private LocalDate fjudgmentDate;

    private String scaseNo;

    private LocalDate sfilingDate;

    private String sacceptingCourt;

    private LocalDate shearingDate;

    private LocalDate sjudgmentDate;

    private String tcaseNo;

    private LocalDate tfilingDate;

    private String tacceptingCourt;

    private LocalDate thearingDate;

    private LocalDate tjudgmentDate;

    private String executionNo;

    private LocalDate executionDate;

    private LocalDate preservationCompletionDate;

    private LocalDate sealingExpirationDate;

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
