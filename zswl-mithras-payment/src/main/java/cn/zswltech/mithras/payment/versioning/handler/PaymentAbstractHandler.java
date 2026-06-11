package cn.zswltech.mithras.payment.versioning.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * 客户版本处理器
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:49 PM
 */
public abstract class PaymentAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long paymentId, boolean needClearLast, Integer versionType) {
        if (!needHandle(paymentId)) {
            return;
        }
        super.flushData(version, paymentId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param paymentId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long paymentId, String version) {
        if (!needHandle(paymentId)) {
            return;
        }
        super.reset(paymentId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "payment_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "payment_id";
    }

    public abstract PaymentInfoModule getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    protected String businessModuleName() {
        return "PAYMENT";
    }
}
