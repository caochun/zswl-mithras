package cn.zswltech.mithras.application.orchestration.contract.operationprepare.impl;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.*;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ContractSettleOwnerChangeRender;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.contract.ContractSignInfoService;
import cn.zswltech.mithras.contract.application.process.prepare.AbstractContractOperationPrepare;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description 准备发起合同结清-正常结清
 */
@Component
public class SettleNormalPrepare extends AbstractContractOperationPrepare {
    @Resource
    protected MaterialsListService materialsListService;
    @Resource
    protected ContractSignInfoService contractSignInfoService;
    @Resource
    private ContractSettleOwnerChangeRender contractSettleOwnerChangeRender;
    @Override
    protected void doPrepare(ContractBaseInfo contractBaseInfo) {
        if (Objects.equals(ContractProcessStatusEnum.SETTLE_UNCOMIIT.name(), contractBaseInfo.getContractProcessStatus())) {
            return;
        }
        Assert.isTrue(ContractProcessStatusEnum.canDoStatus().contains(contractBaseInfo.getContractProcessStatus()), () -> MithrasException.newException("当前合同流状态不允许发起该操作"));
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.SETTLE_UNCOMIIT.name(), ContractSettlePlanTypeEnum.SETTLE_NORMAL.name(), contractBaseInfo.getId());

        try {
            this.generateOwnerChange(contractBaseInfo);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            //log.error("生成所有权转移证书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成所有权转移证书发生未知异常");
        }
    }

    private void generateOwnerChange(ContractBaseInfo contractBaseInfo) throws Exception {
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            //初始化所有权转移证书文件
            String fileName = contractSettleOwnerChangeRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            //将文件写入所有权转移证书列表
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.name(), "", BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            contractSignInfoService.saveContractSignInfo(contractSettleOwnerChangeRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractSettleOwnerChangeRender.signatories(contractBaseInfo));
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            //log.error("生成所有权转移证书发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成所有权转移证书发生未知异常");
        } finally {
            if (Objects.nonNull(is)) {
                is.close();
            }
            if (Objects.nonNull(os)) {
                os.close();
            }
        }
    }

    @Override
    public ContractOperationEnum operationScene() {
        return ContractOperationEnum.SETTLE_NORMAL;
    }
}
