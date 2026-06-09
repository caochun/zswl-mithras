package cn.zswltech.mithras.rating.lib.ratingamount.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountDetailLibRSP;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;


public abstract class RatingAmountAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long ratingId, boolean needClearLast, Integer versionType) {
        if (!needHandle(ratingId)) {
            return;
        }
        super.flushData(version, ratingId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param ratingId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long ratingId, String version) {
        if (!needHandle(ratingId)) {
            return;
        }
        super.reset(ratingId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "rating_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "rating_id";
    }

    public abstract RatingAmountDetailLibRSP getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    protected String businessModuleName() {
        return "RATING_AMOUNT";
    }

}
