package cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.fund.financing.collectaccount.FundFinancingCollectAccountListRSP;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCollectAccount;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCollectAccountLib;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.FundFinancingAbstractLibHandler;
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
public class FundFinancingCollectAccountLibHandler extends FundFinancingAbstractLibHandler<FundFinancingCollectAccountLib, FundFinancingCollectAccount, FundFinancingCollectAccountListRSP> {
    @Override
    protected FundFinancingCollectAccountLib entity2Lib(FundFinancingCollectAccount f) {
        return BeanUtil.copyProperties(f, FundFinancingCollectAccountLib.class);
    }

    @Override
    protected FundFinancingCollectAccount lib2Entity(FundFinancingCollectAccountLib t) {
        return BeanUtil.copyProperties(t, FundFinancingCollectAccount.class);
    }

    @Override
    protected FundFinancingCollectAccountListRSP lib2Rsp(FundFinancingCollectAccountLib f) {
        if (ObjectUtil.isEmpty(f)) {
            return new FundFinancingCollectAccountListRSP();
        }
        return BeanUtil.copyProperties(this.actualLib2Entity(f), FundFinancingCollectAccountListRSP.class);
    }

    @Override
    protected List<FundFinancingCollectAccountListRSP> lib2RspList(List<FundFinancingCollectAccountLib> fList) {
        if (CollectionUtil.isEmpty(fList)) {
            return Collections.emptyList();
        }
        List<FundFinancingCollectAccount> entityList = fList.stream().map(this::actualLib2Entity).collect(Collectors.toList());
        return BeanUtil.copyToList(entityList, FundFinancingCollectAccountListRSP.class);
    }

    @Override
    public FundFinancingLibModelEnum getSubModule() {
        return FundFinancingLibModelEnum.COLLECTION_ACCOUNT;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
