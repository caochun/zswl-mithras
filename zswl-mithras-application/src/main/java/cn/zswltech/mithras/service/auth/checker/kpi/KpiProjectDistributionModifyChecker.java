package cn.zswltech.mithras.service.auth.checker.kpi;


import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.workflow.application.flow.constant.FlowConstants;
import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.workflow.application.flow.service.ProcessService;
import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionBaseInfoService;
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
public class KpiProjectDistributionModifyChecker implements IDataAuthChecker {
    @Resource
    private ProcessService processService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private KpiProjectDistributionBaseInfoService kpiProjectDistributionBaseInfoService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        KpiProjectDistribution kpiProjectDistribution;
        if (mainData instanceof KpiProjectDistribution) {
            kpiProjectDistribution = (KpiProjectDistribution) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(kpiProjectDistribution.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同数据不存在");
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        ProcessResp processResp = processService.findRelatedProcess(String.valueOf(kpiProjectDistribution.getId()), BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION.getModelKeyList());
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
            } else if (Objects.equals(processResp.getCurTaskActivityIds(), "userTask_teamLeader")) {
                KpiProjectDistributionBaseInfo baseInfo = kpiProjectDistributionBaseInfoService.getOneByProjectDistributionId(kpiProjectDistribution.getId());
                // 团队长，只允许团队长操作
                if (!Objects.equals(baseInfo.getTeamLeaderId(), currentUserId)) {
                    throw new AuthCheckException("审批中，当前状态仅支持团队长进行操作");
                }
            } else {
                throw new AuthCheckException("审批中，当前状态不允许操作");
            }
        }
        return true;
    }
}
