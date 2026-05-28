package cn.zswltech.mithras.service.service.contract.operationprepare.impl;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.service.enums.contract.ContractOperationEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractTextInfoService;
import cn.zswltech.mithras.service.service.contract.operationprepare.AbstractContractChangePrepare;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起合同变更-其他
 */
@Component
public class ChangeOtherPrepare extends AbstractContractChangePrepare {
    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
        super.doPrepare(contractBaseInfo);
        // 其他变更需要重置合同文本类型标识位
        SpringUtil.getBean(ContractTextInfoService.class).cancelConfirm(contractBaseInfo.getId());
    }

    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.CHANGE_OTHER;
    }

    @Override
    protected ContractChangeTypeEnum contractChangeType() {
        return ContractChangeTypeEnum.OTHER;
    }
}
