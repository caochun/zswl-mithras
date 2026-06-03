package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.policy.PolicyInfoVersionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.*;

/**
 * @create: 2023-03-20
 **/
@Component
public class PolicyProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private PolicyInfoVersionService policyInfoVersionService;

    @Resource
    private PolicyInfoMapper policyInfoMapper;

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), PolicyCreateFlow.name(),PolicyModifyFlow.name(), PolicyReminderFlow.name(), PolicyOverdueReminderFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        Boolean passed =  ProcessBusinessStatusEnum.success(endContext.getEndType());
        if (equalsAny(endContext.getModelKey(), PolicyReminderFlow.name(), PolicyOverdueReminderFlow.name())) {
            List<PolicyInfo> policyInfos = policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getParentId, Long.valueOf(endContext.getBusinessKey())));
            if (ObjectUtil.isNotEmpty(policyInfos)) {
                policyInfos.forEach(e -> policyInfoVersionService.processEnd(e.getId(), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey()));
            }
            PolicyInfo policyInfo = policyInfoMapper.selectById(Long.valueOf(endContext.getBusinessKey()));
            if (policyInfoMapper.selectCount(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getParentId, policyInfo.getId())) > 0) {
                policyInfo.setRenewInsuranceResult(YesOrNoNumberEnum.YES.getCode());
            } else {
                policyInfo.setRenewInsuranceResult(YesOrNoNumberEnum.NO.getCode());
            }
            policyInfoMapper.updateById(policyInfo);
        } else {
            policyInfoVersionService.processEnd(Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId(), endContext.getModelKey());
        }
        //抄送
        List<Long> ccUserIdList = new ArrayList<>();
        PolicyInfo policyInfo = policyInfoMapper.selectById(Long.valueOf(endContext.getBusinessKey()));
        if (ObjectUtil.isNotEmpty(policyInfo)) {
            if (ObjectUtil.isNotEmpty(policyInfo.getContractId())) {
                ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(policyInfo.getContractId());
                if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                    ccUserIdList.add(contractBaseInfo.getBizDivisionLeaderId());
                }
            }
            ccUserIdList.addAll(sysUserService.queryJobUserIds(JobEnum.yyglbleader.name()));
            if (ObjectUtil.equals(endContext.getModelKey(), PolicyOverdueReminderFlow.name())) {
                ccUserIdList.addAll(sysUserService.queryJobUserIds(JobEnum.headoflegalcompliance.name()));
            }
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(endContext.getProcessInstanceId());
            req.setCcUserIdList(ccUserIdList);
            req.setMessage(passed ? "审批通过" : "审批拒绝");
            getBean(ExecutionApi.class).cc(req);
        }
    }
}
