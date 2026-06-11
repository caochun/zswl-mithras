package cn.zswltech.mithras.contract.versioning.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.contract.enums.contract.ContractLibModelEnum;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;

/**
 * 客户版本处理器
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:49 PM
 */
public abstract class ContractLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long projReviewId, boolean needClearLast, Integer versionType) {
        if (!needHandle(projReviewId)) {
            return;
        }
        super.flushData(version, projReviewId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param projReviewId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long projReviewId, String version) {
        if (!needHandle(projReviewId)) {
            return;
        }
        super.reset(projReviewId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "contract_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "contract_id";
    }

    public void validateData(Client client) {
    }

    public abstract ContractLibModelEnum getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    protected String businessModuleName() {
        return "CONTRACT";
    }

    protected Integer calculateFeeRate(Long target, Long total) {
        if (total == 0) {
            total = 1L;
        }
        return BigDecimal.valueOf(target).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000000)).intValue();
    }
}
