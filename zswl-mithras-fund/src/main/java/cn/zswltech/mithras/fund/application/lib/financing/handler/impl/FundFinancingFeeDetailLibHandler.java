package cn.zswltech.mithras.fund.application.lib.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.financing.fee.FundFinancingFeeDetailRSP;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingFeeDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingFeeDetailLib;
import cn.zswltech.mithras.fund.application.lib.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundFinancingFeeDetailLibHandler extends FundFinancingAbstractLibHandler<FundFinancingFeeDetailLib, FundFinancingFeeDetail, FundFinancingFeeDetailRSP> {
    @Override
    protected FundFinancingFeeDetailLib entity2Lib(FundFinancingFeeDetail f) {
        return BeanUtil.copyProperties(f, FundFinancingFeeDetailLib.class);
    }

    @Override
    protected FundFinancingFeeDetail lib2Entity(FundFinancingFeeDetailLib t) {
        return BeanUtil.copyProperties(t, FundFinancingFeeDetail.class);
    }

    @Override
    protected FundFinancingFeeDetailRSP lib2Rsp(FundFinancingFeeDetailLib f) {
        if(ObjectUtil.isEmpty(f)){
            return new FundFinancingFeeDetailRSP();
        }
        return BeanUtil.copyProperties(this.actualLib2Entity(f), FundFinancingFeeDetailRSP.class);
    }

    @Override
    protected List<FundFinancingFeeDetailRSP> lib2RspList(List<FundFinancingFeeDetailLib> fList) {
        if (CollectionUtil.isEmpty(fList)) {
            return Collections.emptyList();
        }
        List<FundFinancingFeeDetail> entityList = fList.stream().map(this::actualLib2Entity).collect(Collectors.toList());
        return BeanUtil.copyToList(entityList, FundFinancingFeeDetailRSP.class);
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.FEE_DETAIL;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
