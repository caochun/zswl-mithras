package cn.zswltech.mithras.service.auth.checker.contract;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.system.service.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/10/12
 * @description
 */
@Component
public class ContractLeaseItemAuthChecker implements IDataAuthChecker {
    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        ContractBaseInfo contractBaseInfo;
        if (mainData instanceof ContractBaseInfo) {
            contractBaseInfo = (ContractBaseInfo) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        // 判断合同是否结清
        Assert.isTrue(!Objects.equals(ContractStatus.SETTLE.name(), contractBaseInfo.getContractStatus()), () -> MithrasException.newException("合同已结清，无法操作"));
        // 判断类型
        if (Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZL.name()) && Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
            // 回租特殊处理
            ProcessPageReq req = new ProcessPageReq();
            req.setBusinessKey(String.valueOf(keyId));
            req.setPageIndex(1);
            req.setPageSize(1);
            req.setModelKeyList(businessModule.getModelKeyList());
            req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
            ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                    .stream().findFirst().orElse(null);
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            if (Objects.isNull(processResp)) {
                // 流程为空只允许主办修改
                if (!Objects.equals(currentUserId, contractBaseInfo.getProjSponsorUserId())) {
                    throw new MithrasException("当前状态只允许项目主办操作");
                }
            } else {
                // 流程不为空，项目主办、法务经理、运营可以在各自流程节点操作
                if (Objects.equals(processResp.getCurTaskActivityIds(), FlowConstants.START_USER_TASK)) {
                    // 发起人节点只允许项目经理操作
                    if (!Objects.equals(currentUserId, contractBaseInfo.getProjSponsorUserId())) {
                        throw new MithrasException("当前流程节点只允许项目主办操作");
                    }
                } else if (Objects.equals(processResp.getCurTaskActivityIds(), "userTask_lawManager")) {
                    // 法务经理节点只允许法务经理操作
                    if (!sysUserService.userIsSpecificJob(currentUserId, JobEnum.legalmanager.name())) {
                        throw new MithrasException("当前流程节点只允许法务经理操作");
                    }
                } else if (Objects.equals(processResp.getCurTaskActivityIds(), "Activity_0wrrxch")) {
                    // 运营管理节点只允许运营操作
                    if (!sysUserService.userIsSpecificJob(currentUserId, JobEnum.yunYingGuanLi.name())) {
                        throw new MithrasException("当前流程节点只允许运营管理操作");
                    }
                } else {
                    throw new MithrasException("当前流程节点不允许操作");
                }
            }
        } else {
            // 其余的保持不动
            dataAuthSponsorUserRule.check(businessModule, keyId);
            dataAuthProcessRule.check(businessModule, keyId);
        }
        return true;
    }
}
