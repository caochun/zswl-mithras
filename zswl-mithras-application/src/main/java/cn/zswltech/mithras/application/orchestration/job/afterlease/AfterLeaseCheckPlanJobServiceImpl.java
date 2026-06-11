package cn.zswltech.mithras.application.orchestration.job.afterlease;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;

import cn.zswltech.mithras.afterlease.application.job.AfterLeaseCheckPlanJobService;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseClientPlanRSP;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.afterlease.enums.*;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.message.model.MessageModel;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckPlanVersionService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/21
 * @description
 */
@Slf4j
@Component
public class AfterLeaseCheckPlanJobServiceImpl implements AfterLeaseCheckPlanJobService {
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private AfterLeaseCheckPlanVersionService afterLeaseCheckPlanVersionService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;

    @Override
    public void startCheckPlan() {
        LambdaQueryWrapper<NewAfterLeaseCheckPlanBase> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckPlanBase::getPlanStatus, AfterLeaseCheckPlanStatusEnum.PUBLISH.name());
        query.le(NewAfterLeaseCheckPlanBase::getStartDate, LocalDate.now());
        List<NewAfterLeaseCheckPlanBase> todoList = afterLeaseCheckPlanBaseService.list(query);
        if (CollectionUtil.isEmpty(todoList)) {
            return;
        }
        for (NewAfterLeaseCheckPlanBase planBase : todoList) {
            try {
                List<NewAfterLeaseCheckPlanClient> checkPlanClientList = afterLeaseCheckPlanClientService.listBy(planBase.getId());
                for (NewAfterLeaseCheckPlanClient checkPlanClient : checkPlanClientList) {
                    // 尝试获取上一次的模板
                    NewAfterLeaseCheckReportMeta byCheckPlanClientId = SpringUtil.getBean(AfterLeaseCheckReportMetaService.class).getByCheckPlanClientId(checkPlanClient.getId());
                    if (byCheckPlanClientId == null || CharSequenceUtil.isBlank(byCheckPlanClientId.getReportType())) {
                        continue;
                    }
                    // 处理元数据
                    List<CorpCommerceInfo> byClientId = corpCommerceInfoService.findByClientId(checkPlanClient.getClientId());
                    if (ObjectUtil.isEmpty(byClientId)) {
                        continue;
                    }
                    CorpCommerceInfo corpCommerceInfo = byClientId.get(0);
                    AfterLeaseCheckReportTypeEnum reportType = AfterLeaseCheckReportTypeEnum.ofName(byCheckPlanClientId.getReportType());
                    if (Objects.isNull(reportType)) {
                        if (RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(corpCommerceInfo.getRiskControlIndustryClassify()) || RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(corpCommerceInfo.getRiskControlIndustryClassify())) {
                            reportType = AfterLeaseCheckReportTypeEnum.PUBLIC;
                        } else {
                            reportType = AfterLeaseCheckReportTypeEnum.NON_PUBLIC;
                        }
                    }
                    afterLeaseCheckPlanBaseService.startChecking(checkPlanClient.getPlanId(), reportType);
                }
            } catch (Exception e) {
                log.error("开始检查计划发生异常[id: {}]", planBase.getId(), e);
            }
        }
    }

    // XMX-34 租后检查计划代办-截止日前60天可见
    @Override
    public void updateCheckPlanStatus() {
        List<CommonProcessPrepare> prepares = commonProcessPrepareService.list(Wrappers.<CommonProcessPrepare>lambdaQuery()
                .eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.WAITING_PEND.name())
                .eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name())
        );
        if(CollectionUtil.isNotEmpty(prepares)){
            List<CommonProcessPrepare> updates = new ArrayList<>();
            prepares.forEach(prepare -> {
                // 计算截止日
                List<AfterLeaseClientPlanRSP> plan = afterLeaseCheckPlanBaseService.getNextCheckPlan(Long.parseLong(prepare.getBusinessId()), null);
                if (CollectionUtil.isNotEmpty((plan))) {
                    plan = plan.stream().filter(item -> Objects.nonNull(item.getDeadLine()))
                            .filter(item -> item.getDeadLine().isBefore(LocalDate.now().plusDays(60)))
                            .collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(plan)) {
                        updates.add(CommonProcessPrepare.builder().id(prepare.getId()).status(CommonProcessPrepareStatus.PEND_COMMIT.name()).build());
                    }
                }
            });
            // 距截止日<=60天的待办更新为可见
            if (CollectionUtil.isNotEmpty(updates)) {
                commonProcessPrepareService.saveOrUpdateBatch(updates);
            }
        }
    }

    //维护待发起数据
    @Override
    public void checkPlanToBeInitiated() {
        try {
            List<NewAfterLeaseCheckPlanClient> needContinuePlanClientList = afterLeaseCheckPlanClientService.list(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                    .eq(NewAfterLeaseCheckPlanClient::getNextCheckFlag, 0))
                    .stream()
                    .filter(item -> Objects.nonNull(item.getNextDeadline()) && item.getNextDeadline().isBefore(LocalDate.now().plusDays(20)))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(needContinuePlanClientList)) {
                List<Long> clientIds = needContinuePlanClientList.stream().map(NewAfterLeaseCheckPlanClient::getClientId).distinct().collect(Collectors.toList());
                Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
                ArrayList<Long> clientIdList = new ArrayList<>();
                // 根据客户ID将应收款分组，计算客户的风险敞口
                SpringUtil.getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                .in(CollectionBaseInfo::getClientId, clientIds))
                        .stream().collect(Collectors.groupingBy(CollectionBaseInfo::getClientId))
                        .forEach((clientId, collectionBaseInfos) -> {
                            long openMouth = 0;
                            if (CollUtil.isNotEmpty(collectionBaseInfos)) {
                                // 找到实际核销的收款本金总和
                                long planPrincipalSum = collectionBaseInfos.stream().filter(obj -> Objects.nonNull(obj.getPrincipal()))
                                        .mapToLong(CollectionBaseInfo::getPrincipal).sum();
                                long actualPrincipalSum = collectionBaseInfos.stream().filter(obj -> Objects.nonNull(obj.getCollectionPrincipal()))
                                        .mapToLong(CollectionBaseInfo::getCollectionPrincipal).sum();
                                openMouth = planPrincipalSum - actualPrincipalSum;
                            }
                            if (openMouth > 0) {
                                clientIdList.add(clientId);
                            }
                        });
                if(CollectionUtil.isNotEmpty(clientIdList)){
                    List<NewAfterLeaseCheckPlanClient> collect =
                            needContinuePlanClientList.stream().filter(item -> clientIdList.contains(item.getClientId())).collect(Collectors.toList());

                    Map<Long, Long> stockRiskExposureMap = SpringUtil.getBean(ClientService.class).clientStockRiskExposureMap(clientIdList);

                    collect.forEach(afterLeaseCheckPlanClient -> {
                        String clientName = clientId2Name.get(afterLeaseCheckPlanClient.getClientId());
                        long clientId = afterLeaseCheckPlanClient.getClientId();

                        NewAfterLeaseCheckPlanBase afterLeaseCheckPlanBase = afterLeaseCheckPlanBaseService.getOne(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery().eq(NewAfterLeaseCheckPlanBase::getId, afterLeaseCheckPlanClient.getPlanId()));
                        // 构建计划
                        NewAfterLeaseCheckPlanBase newPlan = BeanUtil.copyProperties(afterLeaseCheckPlanBase, NewAfterLeaseCheckPlanBase.class, "id");
                        newPlan.setPlanName(getNextPlanName(afterLeaseCheckPlanClient.getClientId()));
                        newPlan.setPlanStatus(AfterLeaseCheckPlanStatusEnum.NEW.name());
                        newPlan.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.PLAN_ACK_PASS.name());
                        newPlan.setCreateTime(LocalDateTime.now());
                        newPlan.setUpdateTime(LocalDateTime.now());
                        newPlan.setCheckWay(afterLeaseCheckPlanClient.getNextCheckWay());
                        newPlan.setLastCheckWay(afterLeaseCheckPlanBase.getCheckWay());
                        newPlan.setDeadLine(afterLeaseCheckPlanClient.getNextDeadline());
                        newPlan.setDeadlineLabel(JSONUtil.toJsonStr(ListUtil.of(AfterLeaseDeadlineLabelEnum.ASSERT_MANAGER_CONFIRM.name())));
                        SpringUtil.getBean(AfterLeaseCheckPlanBaseService.class).saveOrUpdate(newPlan);

                        // 构建本次检查计划与客户关系
                        NewAfterLeaseCheckPlanClient newPlanClient = BeanUtil.copyProperties(afterLeaseCheckPlanClient, NewAfterLeaseCheckPlanClient.class,
                                "id","checkTime","checkFillTime","tmpNextDeadline","nextCheckWay","tmpNextCheckWay","nextDeadline","check_change_status","next_check_flag");
                        newPlanClient.setPlanId(newPlan.getId());
                        newPlanClient.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
                        newPlanClient.setCreateTime(LocalDateTime.now());
                        newPlanClient.setUpdateTime(LocalDateTime.now());
                        newPlanClient.setCommitTime(null);
                        newPlanClient.setOverdueDays(null);
                        newPlanClient.setIsNotify(false);
                        newPlanClient.setNextCheckFlag(null);
                        newPlanClient.setCheckWay(afterLeaseCheckPlanClient.getNextCheckWay());
                        if (CollUtil.isNotEmpty(stockRiskExposureMap)) {
                            newPlanClient.setStockRiskExposure(stockRiskExposureMap.get(afterLeaseCheckPlanClient.getClientId()));
                            newPlanClient.setRemainingPrincipal(stockRiskExposureMap.get(afterLeaseCheckPlanClient.getClientId()));
                        }
                        afterLeaseCheckPlanClientService.saveOrUpdate(newPlanClient);

                        // 构建自动确认代办
                        CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder()
                                .status(CommonProcessPrepareStatus.AUTO_COMMITTED.name())
                                .businessId(String.valueOf(clientId))
                                .businessData(String.valueOf(newPlan.getId()))
                                .clientName(clientName)
                                .applyTime(LocalDateTime.now())
                                .processType(ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name())
                                .formName(clientName + "-租后检查发布计划")
                                .currentNode("自动确认")
                                .isAssetConfirm("已确认")
                                .build();
                        commonProcessPrepareService.save(commonProcessPrepare);

                        // 生成报告
                        NewAfterLeaseCheckReportMeta byCheckPlanClientId = SpringUtil.getBean(AfterLeaseCheckReportMetaService.class).getByCheckPlanClientId(afterLeaseCheckPlanClient.getId());
                        SpringContextHolder.getBean(AfterLeaseCheckPlanBaseService.class).generateCheckReportMetaList(newPlan.getId(), AfterLeaseCheckReportTypeEnum.ofName(byCheckPlanClientId.getReportType()));

                        // 更新状态
                        afterLeaseCheckPlanClient.setNextCheckFlag(1);
                        afterLeaseCheckPlanClientService.updateById(afterLeaseCheckPlanClient);
                    });
                }
                List<NewAfterLeaseCheckPlanClient> stop =
                        needContinuePlanClientList.stream().filter(item -> !clientIdList.contains(item.getClientId())).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(stop)){
                    stop.forEach(item -> item.setNextCheckFlag(2));
                    afterLeaseCheckPlanClientService.saveOrUpdateBatch(stop);
                }
            }
            //关闭到期的任务 结束时间小于20天的都关闭，且自动发布
            List<NewAfterLeaseCheckPlanBase> needEndPlanBaseList = afterLeaseCheckPlanBaseService.list(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                    .eq(NewAfterLeaseCheckPlanBase::getPlanType, AfterLeaseCheckPlanTypeEnum.COMMONLY.name())
                    .in(NewAfterLeaseCheckPlanBase::getPlanStatus, ListUtil.toList(AfterLeaseCheckPlanStatusEnum.NEW.name(), AfterLeaseCheckPlanStatusEnum.CHECKING.name(), AfterLeaseCheckPlanStatusEnum.PUBLISH.name()))
                    .le(NewAfterLeaseCheckPlanBase::getDeadLine, LocalDate.now().plusDays(20)));

            //尝试关闭
            if (CollectionUtil.isNotEmpty(needEndPlanBaseList)) {
                Map<Long, List<NewAfterLeaseCheckPlanClient>> clientMapByPlan = afterLeaseCheckPlanClientService.list(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                                .in(NewAfterLeaseCheckPlanClient::getPlanId, needEndPlanBaseList.stream().map(NewAfterLeaseCheckPlanBase::getId).collect(Collectors.toList())))
                        .stream().collect(Collectors.groupingBy(NewAfterLeaseCheckPlanClient::getPlanId));
                clientMapByPlan.forEach((k, v) -> {
                    if (ObjectUtil.isNotEmpty(v)) {
                        NewAfterLeaseCheckPlanClient newAfterLeaseCheckPlanClient = v.get(0);
                        List<CommonProcessPrepare> commonProcessPrepares = commonProcessPrepareService.list(Wrappers.<CommonProcessPrepare>lambdaQuery()
                                .eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name())
                                .eq(CommonProcessPrepare::getBusinessId, newAfterLeaseCheckPlanClient.getClientId())
                                .eq(CommonProcessPrepare::getBusinessData, String.valueOf(v.get(0).getPlanId()))
                                .in(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name(), CommonProcessPrepareStatus.AUTO_COMMITTED.name())
                                .eq(CommonProcessPrepare::getIsAssetConfirm, "已确认"));
                        if (CollectionUtil.isNotEmpty(commonProcessPrepares)) {
                            //修改任务状态
                            List<CommonProcessPrepare> updates = new ArrayList<>();
                            commonProcessPrepares.forEach(e -> updates.add(CommonProcessPrepare.builder().id(e.getId()).status(CommonProcessPrepareStatus.COMMITTED.name()).build()));
                            commonProcessPrepareService.saveOrUpdateBatch(updates);
                            //修改计划状态
                            NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase = new NewAfterLeaseCheckPlanBase();
                            newAfterLeaseCheckPlanBase.setId(k);
                            newAfterLeaseCheckPlanBase.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.PLAN_ACK_PASS.name());
                            newAfterLeaseCheckPlanBase.setPlanStatus(AfterLeaseCheckPlanStatusEnum.CHECKING.name());
                            newAfterLeaseCheckPlanBase.setUpdateTime(LocalDateTimeUtil.now());
                            afterLeaseCheckPlanBaseService.updateById(newAfterLeaseCheckPlanBase);
                            afterLeaseCheckPlanVersionService.recordVersion(newAfterLeaseCheckPlanBase.getId(), VersionTypeEnum.EFFECT, null,
                                    null, VersionTypeConstants.NORMAL);

                            //给项目经理发起任务
                            CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder()
                                    .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                                    .businessId(String.valueOf(newAfterLeaseCheckPlanClient.getId()))
                                    .clientName(newAfterLeaseCheckPlanClient.getClientName())
                                    .processType(ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name())
                                    .formName(newAfterLeaseCheckPlanClient.getClientName() + "-租后检查（一般检查计划）")
                                    .applyTime(LocalDateTime.now())
                                    .currentNode("项目经理")
                                    .currentAssignee(JSONUtil.toJsonStr(ListUtil.toList(newAfterLeaseCheckPlanClient.getBelongSponsorId())))
                                    .build();
                            commonProcessPrepareService.save(commonProcessPrepare);
                            // 消息通知
                            try{
                                noticeMessage(commonProcessPrepare, newAfterLeaseCheckPlanClient);
                            }catch (Exception e){
                                log.error("消息通知失败",e);
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.error("checkPlanToBeInitiated error", e);
        }
    }

    private String getNextPlanName(Long clientId){
        int phase = 0;
        //查询检查信息
        List<NewAfterLeaseCheckPlanClient> newAfterLeaseCheckPlanClients = afterLeaseCheckPlanClientService.list(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .ge(NewAfterLeaseCheckPlanClient::getCreateTime, LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))
                .eq(NewAfterLeaseCheckPlanClient::getClientId, clientId));
        if (CollectionUtil.isNotEmpty(newAfterLeaseCheckPlanClients)) {
            Set<Long> planIds = newAfterLeaseCheckPlanClients.stream().map(NewAfterLeaseCheckPlanClient::getPlanId).collect(Collectors.toSet());
            if (ObjectUtil.isNotEmpty(planIds)) {
                phase = afterLeaseCheckPlanBaseService.count(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                        .in(NewAfterLeaseCheckPlanBase::getId, planIds)
                        .eq(NewAfterLeaseCheckPlanBase::getPlanType, AfterLeaseCheckPlanTypeEnum.COMMONLY.name()));
            }
        }
        return String.format("%s【%s】年第【%s】次检查", id2NameService.clientId2NameSingle(clientId), LocalDate.now().getYear(), ++phase);
    }

    //租后检查报告（一般检查）与租后检查报告流程超时提醒
    @Override
    public void afterLeaseCheckRemind(String jobParam) {
        try {
            log.info("afterLeaseCheckRemind start");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // jobParam = "2067";
            afterLeaseCheckPlanVersionService.afterLeaseCheckRemind(jobParam);
            stopWatch.stop();
            log.info("afterLeaseCheckRemind end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("afterLeaseCheckRemind 执行异常，e={}", e);
        }
    }

    // XMX-34 租后检查截止日前45个自然日推送消息提醒
    public void noticeMessage(CommonProcessPrepare prepare,NewAfterLeaseCheckPlanClient client) {
        if (ObjectUtil.isEmpty(prepare) || ObjectUtil.isEmpty(client)) {
            return;
        }
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setMessageType(MessageTypeEnum.PROCESS_PREPARE.name());
        messageAddREQ.setContent(prepare.getBusinessId());
        messageAddREQ.setFlowid(String.valueOf(client.getId()));
        messageAddREQ.setPcurl(String.format(MessageUrlEnum.CLIENT_PLAN_CHECK.pcUrl, client.getId()));
        Set<Long> to = new HashSet<>(Collections.singletonList(client.getBelongSponsorId()));
        // 风控经理不是必选
        if(Objects.nonNull(client.getRiskManagerId())){
            to.add(client.getRiskManagerId());
        }
        // 获取部门负责人
        List<UserDO> userList = SpringUtil.getBean(SysUserService.class).listSpecificOrgJobUser(client.getBelongDeptId(), JobEnum.businesshead.name());
        if(CollectionUtil.isNotEmpty(userList)){
            to.addAll(userList.stream().map(UserDO::getId).collect(Collectors.toList()));
        }
        messageAddREQ.setTo(new ArrayList<>(to));
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setNeedQa(false);
        NewAfterLeaseCheckPlanBase plan = afterLeaseCheckPlanBaseService.getOne(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery().
                eq(NewAfterLeaseCheckPlanBase::getId, client.getPlanId()));
        messageAddREQ.setRelation(String.format("%s将于本年第%s次进行租后检查，检查形式为%s，检查截止日期为%s",
                client.getClientName(), StrUtil.sub(plan.getPlanName(), -5, -4),
                AfterLeaseCheckWayEnum.find(plan.getCheckWay()).display(),plan.getDeadLine()));
        messageAddREQ.setMessageType(MessageTypeEnum.AFTER_LEASE_CHECK.name());
        messageAddREQ.setNoticeSource(NoticeSourceENUM.AFTER_LEASE_CHECK.name());
        MessageModel messageModel = messageConvert.reqToMessage(messageAddREQ);
        messageService.sendMessage(messageModel);
    }
}
