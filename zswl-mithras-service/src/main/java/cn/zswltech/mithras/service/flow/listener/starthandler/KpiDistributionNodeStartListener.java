//package cn.zswltech.mithras.service.flow.listener.starthandler;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.map.MapUtil;
//import cn.hutool.core.text.CharSequenceUtil;
//import cn.hutool.json.JSONUtil;
//import cn.zswltech.flow.core.api.FlowVariableApiService;
//import cn.zswltech.flow.core.enums.ProcessNodeVariableEnum;
//import cn.zswltech.flow.core.extension.event.NodeStartEvent;
//import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
//import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
//import cn.zswltech.mithras.service.enums.JobEnum;
//import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
//import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistribution;
//import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionDeptWeight;
//import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionDeptWeightLib;
//import cn.zswltech.mithras.service.others.MithrasException;
//import cn.zswltech.mithras.service.service.SysUserService;
//import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionDeptWeightLibService;
//import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionDeptWeightService;
//import cn.zswltech.mithras.service.service.kpi.KpiProjectDistributionService;
//import cn.zswltech.mithras.service.util.StringUtil;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * @author bigbear
// * @date 2025/4/10 11:23
// * @description 绩效考核-项目分配-节点结束事件监听器
// */
//@Slf4j
//@Component
//public class KpiDistributionNodeStartListener implements ApplicationListener<NodeStartEvent> {
//
//    @Resource
//    private SysUserService sysUserService;
//    @Resource
//    private FlowVariableApiService flowVariableApiService;
//    @Resource
//    private KpiProjectDistributionService kpiProjectDistributionService;
//    @Resource
//    private KpiProjectDistributionDeptWeightService kpiProjectDistributionDeptWeightService;
//    @Resource
//    private KpiProjectDistributionDeptWeightLibService kpiProjectDistributionDeptWeightLibService;
//
//    @Override
//    public void onApplicationEvent(NodeStartEvent nodeEndEvent) {
//        NodeCommonContext context = nodeEndEvent.getNodeCommonContext();
//        log.info("KpiDistributionNodeStartListener onApplicationEvent context: {}", JSONUtil.toJsonStr(context));
//        if (!isValidProcessType(context)) {
//            return;
//        }
//        KpiProjectDistribution distribution = getDistribution(context);
//        handleActivity(context, distribution);
//    }
//
//    private boolean isValidProcessType(NodeCommonContext context) {
//        return CharSequenceUtil.equalsAny(context.getModelKey(),
//                ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name(),
//                ProcessModelTypeEnum.KpiProjectDistributionModifyFlow.name(),
//                ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name());
//    }
//
//    private KpiProjectDistribution getDistribution(NodeCommonContext context) {
//        KpiProjectDistribution distribution = kpiProjectDistributionService.getById(context.getBusinessKey());
//        if (distribution == null) {
//            throw new MithrasException("项目分配信息不存在");
//        }
//        return distribution;
//    }
//
//    private void handleActivity(NodeCommonContext context, KpiProjectDistribution distribution) {
//        String activityId = context.getActivityId();
//        if (CharSequenceUtil.equalsAny(activityId, "userTask_bizDeptLeader", "userTask_bizDeptLeaderNew")) {
//            handleBizDeptLeader(activityId, distribution, context);
//        } else if (activityId != null) {
//            switch (activityId) {
//                case "userTask_bizDeptLeaderPrev":
//                    handlePrevApprover(activityId, distribution, context, JobEnum.businesshead);
//                    break;
//                case "userTask_leaderinchargeNew":
//                case "userTask_leaderincharge":
//                    handleLeaderApprover(activityId, distribution, context);
//                    break;
//                case "userTask_leaderinchargePrev":
//                    handlePrevApprover(activityId, distribution, context, JobEnum.leaderincharge);
//                    break;
//            }
//        }
//    }
//
//    private void handleBizDeptLeader(String activityId, KpiProjectDistribution distribution, NodeCommonContext context) {
//        List<KpiProjectDistributionDeptWeightInfo> weights = getDeptWeights(distribution.getId(), false, null);
//        setApprovers(activityId, context, weights, JobEnum.businesshead);
//    }
//
//    private void handleLeaderApprover(String activityId, KpiProjectDistribution distribution, NodeCommonContext context) {
//        List<KpiProjectDistributionDeptWeightInfo> weights = getDeptWeights(distribution.getId(), false, null);
//        setApprovers(activityId, context, weights, JobEnum.leaderincharge);
//    }
//
//    private void handlePrevApprover(String activityId, KpiProjectDistribution distribution,
//                                    NodeCommonContext context, JobEnum jobType) {
//        KpiProjectDistributionDeptWeightLib version = kpiProjectDistributionDeptWeightLibService.getOne(
//                Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
//                        .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, distribution.getId())
//                        .eq(KpiProjectDistributionDeptWeightLib::getVersionType, 1)
//                        .orderByDesc(KpiProjectDistributionDeptWeightLib::getVersion)
//                        .last(StringUtil.mysqlLimitOne()));
//        if (version == null) {
//            throw new MithrasException("不存在部门分润版本信息");
//        }
//
//        List<KpiProjectDistributionDeptWeightInfo> weights = getDeptWeights(distribution.getId(), true, version.getVersion());
//        setApprovers(activityId, context, weights, jobType);
//    }
//
//    private List<KpiProjectDistributionDeptWeightInfo> getDeptWeights(Long id, Boolean isVersion, String version) {
//        return isVersion ?
//                kpiProjectDistributionDeptWeightLibService.buildDeptWeightInfoByDistributionId(id, version) :
//                kpiProjectDistributionDeptWeightService.queryList(id);
//    }
//
//    private void setApprovers(String activityId, NodeCommonContext context,
//                              List<KpiProjectDistributionDeptWeightInfo> weights, JobEnum jobType) {
//        if (CollUtil.isEmpty(weights)) {
//            throw new MithrasException("不存在部门分润信息");
//        }
//
//        List<Long> userIds = weights.stream()
//                .map(KpiProjectDistributionDeptWeightInfo::getWeightTarget)
//                .filter(Objects::nonNull)
//                .map(target -> sysUserService.getUserIdByOrgJob(target, jobType.name()))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        flowVariableApiService.setVariables(context.getProcessInstanceId(),
//                MapUtil.of(ProcessNodeVariableEnum.ASSIGN_APPROVER_LIST.generateNodeVarName(activityId), userIds));
//    }
//}
//
