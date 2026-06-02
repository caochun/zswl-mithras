package cn.zswltech.mithras.service.overdue.domain.litigation;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.overdue.LitigationStatus;
import cn.zswltech.mithras.service.overdue.domain.share.Aggregate;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 09:03
 */
@Data
public class Litigation implements Aggregate<LongId> {

    private LongId id;

    private LitigationCode code;

    private LitigationStatus status;

    private String clientName;

    private Long clientId;

    private List<Long> contractIds;

    private List<String> contractCodes;

    private List<Defendant> defendants;

    private TrialInfo trialInfo;

    private List<CaseProgress> caseProgresses;

    private Long lockVersion;

    @Override
    public boolean sameIdentityAs(LongId other) {
        return id.equals(other);
    }

    @Override
    public LongId getBizId() {
        return id;
    }

    @Override
    public String bizIdString() {
        return String.valueOf(id.getId());
    }

    public void init() {
        if(ObjectUtil.isEmpty(this.clientId)){
            throw new IllegalArgumentException("clientId is null");
        }
    }
    @Override
    public void createNewId(LongId longId) {
        this.id = longId;
    }
}
