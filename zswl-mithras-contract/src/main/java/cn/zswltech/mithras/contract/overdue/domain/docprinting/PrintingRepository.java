package cn.zswltech.mithras.contract.overdue.domain.docprinting;

import cn.zswltech.mithras.contract.overdue.domain.DbRepositorySupport;
import cn.zswltech.mithras.contract.overdue.domain.litigation.LongId;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/5 18:38
 */
public abstract class PrintingRepository extends DbRepositorySupport<Printing, LongId> {
    protected PrintingRepository(Class<Printing> targetClass) {
        super(targetClass);
    }

    public abstract Printing findLib(LongId longId, String version);
}
