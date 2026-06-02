package cn.zswltech.mithras.contract.overdue.domain.docprinting;

import cn.zswltech.mithras.contract.enums.overdue.PrintingType;
import cn.zswltech.mithras.contract.overdue.domain.litigation.LongId;
import cn.zswltech.mithras.contract.overdue.domain.share.Aggregate;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:32
 */
@Data
public class Printing implements Aggregate<LongId> {

    private LongId id;

    private PrintingCode code;

    private PrintingType type;

    private String reason;

    private String processStatus;

    private String processId;

    private Long lockVersion;

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

    @Override
    public void createNewId(LongId longId) {
        setId(longId);
    }
}
