package cn.zswltech.mithras.service.service.contract.operationprepare;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description
 */
public abstract class AbstractContractOperationPrepare implements ContractOperationPrepare, InitializingBean {
    @Resource
    protected ContractBaseInfoService contractBaseInfoService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void prepare(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同数据不存在"));
        this.doPrepare(contractBaseInfo);
    }

    protected abstract void doPrepare(ContractBaseInfo contractBaseInfo);

    @Override
    public void afterPropertiesSet() {
        ContractOperationPrepareFactory.register(this);
    }
}
