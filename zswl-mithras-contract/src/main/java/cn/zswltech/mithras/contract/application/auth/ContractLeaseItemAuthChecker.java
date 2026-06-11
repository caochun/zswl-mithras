package cn.zswltech.mithras.contract.application.auth;

import cn.hutool.core.lang.Assert;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessGuard;
import cn.zswltech.mithras.foundation.auth.DataAuthSponsorUserGuard;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
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
    private DataAuthSponsorUserGuard dataAuthSponsorUserGuard;
    @Resource
    private DataAuthProcessGuard dataAuthProcessGuard;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
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
                    if (!currentUserJobResolver.userIsSpecificJob(currentUserId, JobEnum.legalmanager.name())) {
                        throw new MithrasException("当前流程节点只允许法务经理操作");
                    }
                } else if (Objects.equals(processResp.getCurTaskActivityIds(), "Activity_0wrrxch")) {
                    // 运营管理节点只允许运营操作
                    if (!currentUserJobResolver.userIsSpecificJob(currentUserId, JobEnum.yunYingGuanLi.name())) {
                        throw new MithrasException("当前流程节点只允许运营管理操作");
                    }
                } else {
                    throw new MithrasException("当前流程节点不允许操作");
                }
            }
        } else {
            // 其余的保持不动
            dataAuthSponsorUserGuard.check(businessModule, keyId);
            dataAuthProcessGuard.check(businessModule, keyId);
        }
        return true;
    }
}
