package cn.zswltech.mithras.application.orchestration.document.file.impl;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRetreatInfo;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.workflow.flow.service.ProcessService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/6/20
 * @description
 */
@Component
public class ContarctDepositProviderCheck implements IDataAuthChecker {
    @Resource
    private ProcessService processService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        ContractRetreatInfo contractRetreatInfo;
        if (mainData instanceof ContractRetreatInfo) {
            contractRetreatInfo = (ContractRetreatInfo) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractRetreatInfo.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同数据不存在");
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        ProcessResp processResp = processService.findRelatedProcess(String.valueOf(contractRetreatInfo.getId()), BusinessModuleEnum.CONTARCT_DEPOSIT.getModelKeyList());
        if (Objects.isNull(processResp)) {
            // 没有提交流程，只允许项目经理进行操作
            if (!Objects.equals(contractBaseInfo.getProjSponsorUserId(), currentUserId)) {
                throw new AuthCheckException("当前状态仅支持所属合同的项目经理进行操作");
            }
        } else {
            // 流程中根据节点分情况
            if (Objects.equals(processResp.getCurTaskActivityIds(), FlowConstants.START_USER_TASK)) {
                // 发起人，只允许项目经理操作
                if (!Objects.equals(contractBaseInfo.getProjSponsorUserId(), currentUserId)) {
                    throw new AuthCheckException("审批中，当前状态仅支持所属合同的项目经理进行操作");
                }
            } else {
//                throw new AuthCheckException("审批中，当前状态不允许操作");
            }
        }
        return true;
    }
}
