package cn.zswltech.mithras.service.overdue.domain.litigation;

import cn.zswltech.mithras.service.overdue.domain.share.Entity;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 09:24
 */
@Data
public class Defendant implements Entity<LongId> {

    private LongId id;

    private LongId lrId;

    private String name;

    private String role;

    private String certificateType;

    private String certificateNumber;


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


}
