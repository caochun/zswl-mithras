package cn.zswltech.mithras.fund.application.lib.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingDetailConverter;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPledgeInfoLib;
import cn.zswltech.mithras.fund.application.lib.financing.handler.FundFinancingAbstractLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Component
public class FundFinancingPledgeInfoLibHandler extends FundFinancingAbstractLibHandler<FundFinancingPledgeInfoLib, FundFinancingPledgeInfo, FundFinancingPledgeListRSP> {
    @Resource
    private FundFinancingDetailConverter fundFinancingDetailConverter;

    @Override
    protected FundFinancingPledgeInfoLib entity2Lib(FundFinancingPledgeInfo f) {
        return BeanUtil.copyProperties(f, FundFinancingPledgeInfoLib.class);
    }

    @Override
    protected FundFinancingPledgeInfo lib2Entity(FundFinancingPledgeInfoLib t) {
        return BeanUtil.copyProperties(t, FundFinancingPledgeInfo.class);
    }

    @Override
    protected FundFinancingPledgeListRSP lib2Rsp(FundFinancingPledgeInfoLib f) {
        if (ObjectUtil.isEmpty(f)) {
            return new FundFinancingPledgeListRSP();
        }
        FundFinancingPledgeListRSP rsp = BeanUtil.copyProperties(actualLib2Entity(f), FundFinancingPledgeListRSP.class);
        return rsp;
    }

    @Override
    protected List<FundFinancingPledgeListRSP> lib2RspList(List<FundFinancingPledgeInfoLib> fList) {
        if (CollectionUtil.isEmpty(fList)) {
            return Collections.emptyList();
        }
        List<FundFinancingPledgeInfo> entityList = fList.stream().map(this::actualLib2Entity).collect(Collectors.toList());
        return fundFinancingDetailConverter.convertPledgeInfoToRSPList(entityList);
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.PLEDGE;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected String businessModuleName() {
        return "FUND_FINANCING";
    }
}
