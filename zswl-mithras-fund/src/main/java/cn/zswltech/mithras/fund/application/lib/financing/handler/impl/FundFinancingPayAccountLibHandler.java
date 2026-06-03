package cn.zswltech.mithras.fund.application.lib.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.financing.payaccount.FundFinancingPayAccountListRSP;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPayAccountLib;
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
public class FundFinancingPayAccountLibHandler extends FundFinancingAbstractLibHandler<FundFinancingPayAccountLib, FundFinancingPayAccount, FundFinancingPayAccountListRSP> {
    @Override
    protected FundFinancingPayAccountLib entity2Lib(FundFinancingPayAccount f) {
        return BeanUtil.copyProperties(f, FundFinancingPayAccountLib.class);
    }

    @Override
    protected FundFinancingPayAccount lib2Entity(FundFinancingPayAccountLib t) {
        return BeanUtil.copyProperties(t, FundFinancingPayAccount.class);
    }

    @Override
    protected FundFinancingPayAccountListRSP lib2Rsp(FundFinancingPayAccountLib f) {
        if(ObjectUtil.isEmpty(f)){
            return new FundFinancingPayAccountListRSP();
        }
        return BeanUtil.copyProperties(this.actualLib2Entity(f), FundFinancingPayAccountListRSP.class);
    }

    @Override
    protected List<FundFinancingPayAccountListRSP> lib2RspList(List<FundFinancingPayAccountLib> fList) {
        if (CollectionUtil.isEmpty(fList)) {
            return Collections.emptyList();
        }
        List<FundFinancingPayAccount> entityList = fList.stream().map(this::actualLib2Entity).collect(Collectors.toList());
        return BeanUtil.copyToList(entityList, FundFinancingPayAccountListRSP.class);
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.PAY_ACCOUNT;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
