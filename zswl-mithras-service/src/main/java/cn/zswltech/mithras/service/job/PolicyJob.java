package cn.zswltech.mithras.service.job;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.policy.PolicyMaintenanceREQ;
import cn.zswltech.mithras.dto.policy.PolicyMaintenanceRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.policy.domain.enums.PolicyApprovalStatusEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.domain.enums.PolicyStatusEnum;
import cn.zswltech.mithras.policy.infrastructure.persistence.dto.NearPolicyEndTimeDTO;
import cn.zswltech.mithras.policy.infrastructure.persistence.model.PolicyInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.policy.infrastructure.persistence.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.flow.MyTaskService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.policy.PolicyInfoService;
import cn.zswltech.mithras.service.service.policy.PolicyInfoVersionService;
import cn.zswltech.mithras.service.service.policy.PolicyLedgerService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static cn.hutool.json.JSONUtil.toBean;


@Slf4j
@Component
public class PolicyJob {

    @Resource
    private PolicyInfoService policyInfoService;
    @Resource
    private PolicyInfoMapper policyInfoMapper;
    @Resource
    private PolicyLedgerService policyLedgerService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private PolicyInfoVersionService policyInfoVersionService;
    @Resource
    private ExecutionService executionService;
    @Resource
    private MyTaskService myTaskService;

    private final static String AUTO_COMMIT_MESSAGE = "超时未处理系统自动同意";

    @XxlJob("policyAddJobHandler")
    @Transactional(rollbackFor = Throwable.class)
    public void policyAddJobHandler() {
        log.info("policyAddJob, start.");
        LocalDate end = LocalDate.now().plusDays(15);
        List<NearPolicyEndTimeDTO> endTimeList = policyInfoMapper.nearPolicyEndTimeList(end);
        Set<Long> noSettleProj = policyInfoService.noSettleProj();
        List<Long> npIds = endTimeList.stream().map(NearPolicyEndTimeDTO::getProjId).collect(Collectors.toList());
        Map<Long, LocalDate> projEndDate = policyLedgerService.getProjEndDate(npIds);
        List<Long> ids = endTimeList.stream().filter(o -> projEndDate.get(o.getProjId()) != null && o.getMaxDate().isBefore(projEndDate.get(o.getProjId())) && noSettleProj.contains(o.getProjId())).map(NearPolicyEndTimeDTO::getProjId).collect(Collectors.toList());
        List<PolicyInfo> policyInfos = policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery().eq(PolicyInfo::getAutomatic, 1).eq(PolicyInfo::getApprovalStatus, PolicyApprovalStatusEnum.NEW_UN_SUBMIT));
        Set<Long> idset = policyInfos.stream().map(PolicyInfo::getProjId).collect(Collectors.toSet());
        List<Long> needAdd = ids.stream().filter(o -> !idset.contains(o)).collect(Collectors.toList());
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.listByIds(needAdd);
        Map<Long, ProjReviewBaseInfo> infoMap = projReviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, o -> o));
        List<MessageAddREQ> messages = new ArrayList<>();
        for (Long projId : needAdd) {
            PolicyInfo info = new PolicyInfo();
            info.setProjId(projId);
            info.setApprovalStatus(PolicyApprovalStatusEnum.NEW_UN_SUBMIT.name());
            info.setAutomatic(1);
            policyInfoMapper.insert(info);
            ProjReviewBaseInfo baseInfo = infoMap.get(projId);
            MessageAddREQ message = new MessageAddREQ();
            message.setFrom("系统通知");
            Set<Long> to = new HashSet<>();
            if (baseInfo.getProjSponsorUserId() != null) {
                to.add(baseInfo.getProjSponsorUserId());
            }
            if (baseInfo.getProjCosponsorUserIds() != null) {
                to.addAll(toBean(baseInfo.getProjCosponsorUserIds(), new TypeReference<List<Long>>() {
                }, true));
            }
            message.setTo(new ArrayList<>(to));
            message.setRelation(baseInfo.getProjName());
            message.setContent(baseInfo.getProjName());
            message.setNeedOa(false);
            message.setNoticeSource(NoticeSourceENUM.POLICY.name());
            message.setMessageType(MessageTypeEnum.POLICY.name());
            message.setPcurl(StringUtils.format(MessageUrlEnum.POLICY.pcUrl,info.getId()));
            message.setBusinessId(String.valueOf(info.getId()));
            messages.add(message);
        }
        messages.forEach(o -> messageService.sendMessage(messageConver.reqToMessage(o)));
        log.info("policyAddJob, end.");
    }

    @XxlJob("policyNoticeHandler")
    @Transactional(rollbackFor = Throwable.class)
    public void policyNoticeHandler() {
        log.info("policyNoticeHandler, start.");
        List<PolicyMaintenanceRSP> rsps = policyLedgerService.maintenanceList2(new PolicyMaintenanceREQ(),null, null);
        //发送通知
        List<Long> noticeIds = new ArrayList<>();
        //
        Map<Long, List<PolicyMaintenanceRSP>> noticeUsers = new HashMap<>();
        rsps.forEach(base -> {
            if(!YesOrNoNumberEnum.YES.getCode().equals(base.getNoticeFlag())){
                if(ObjectUtil.isNotEmpty(base.getId())){
                    noticeIds.add(base.getId());
                }
                List<PolicyMaintenanceRSP> orDefault = noticeUsers.getOrDefault(base.getProjSponsorUserId(), new ArrayList<>());
                orDefault.add(base);
                noticeUsers.put(base.getProjSponsorUserId(), orDefault);
                if(ObjectUtil.isNotEmpty(base.getProjCosponsorUserIds())){
                    base.getProjCosponsorUserIds().forEach(id -> {
                        List<PolicyMaintenanceRSP> orDefault1 = noticeUsers.getOrDefault(base.getProjSponsorUserId(), new ArrayList<>());
                        orDefault1.add(base);
                        noticeUsers.put(id, orDefault1);
                    });
                }
            }
        });
        policyInfoService.updateNotice(noticeIds, YesOrNoNumberEnum.YES.getCode());
        notice(noticeUsers);
        log.info("policyNoticeHandler, end.");
    }

    private void notice(Map<Long, List<PolicyMaintenanceRSP>> noticeUsers){
        try {
            CompletableFuture.runAsync(() -> {
                noticeUsers.forEach((key, value) -> {
                    value.forEach(base -> {
                        if(ObjectUtil.isNotEmpty(base.getPolicyCode())) {
                            MessageAddREQ messageAddREQ = new MessageAddREQ();
                            messageAddREQ.setFrom("系统通知");
                            messageAddREQ.setTo(Collections.singletonList(key));
                            messageAddREQ.setPcurl("/workbench");
                            messageAddREQ.setContent(base.getPolicyCode());
                            messageAddREQ.setFlowid(base.getPolicyCode());
                            messageAddREQ.setNeedOa(false);
                            messageAddREQ.setRelation(base.getPolicyCode() + "保单即将到期,请及时处理");
                            messageAddREQ.setMessageType(MessageTypeEnum.REMINDER_NOTICE.name());
                            messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
                        }
                    });
                });
            });
        }catch (Exception e){
            log.error("保单通知失败", e);
        }
    }

    //发起保单到期提示流程
    @XxlJob("policyStartReminderProcessHandler")
    @Transactional(rollbackFor = Throwable.class)
    public void policyStartReminderProcessHandler() {
        LocalDate now = LocalDate.now();
        //1.保单到期提示流程
        try {
            //查询15天内到期且未发起过保单提醒流程的
            List<PolicyInfo> policyInfos = policyInfoService.list(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                    .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                    .eq(PolicyInfo::getExpirationReminderFlag, YesOrNoNumberEnum.NO.getCode())
                    .eq(PolicyInfo::getRenewInsuranceResult, YesOrNoNumberEnum.NO.getCode())
                    .ge(PolicyInfo::getInsuranceEndDate, now)
                    .lt(PolicyInfo::getInsuranceEndDate, now.plusDays(15)));
            //modify 260112 关联流程信息，排除对应合同流程状态是：审核通过&&（正常结清||提前结清状态）的保单id
            excludedFinishContract(policyInfos);
            if (ObjectUtil.isNotEmpty(policyInfos)) {
                //发起到期提醒流程
                policyInfos.forEach(policyInfo -> {
                    policyInfoVersionService.policyStartReminderProcess(policyInfo);
                });
            }

        } catch (Exception e) {
            log.error("保单到期提示失败", e);
        }

        //2. 续保逾期提示流程
        try {
            //查询到期且未续保完成的保单
            List<PolicyInfo> policyInfos = new ArrayList<>();
            List<PolicyInfo> policy5 = policyInfoService.list(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                    .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                    .eq(PolicyInfo::getRenewalOverdueFlag, YesOrNoNumberEnum.NO.getCode())
                    .eq(PolicyInfo::getRenewInsuranceResult, YesOrNoNumberEnum.NO.getCode())
                    .eq(PolicyInfo::getInsuranceEndDate, now.minusDays(5)));
            List<PolicyInfo> policy30 = policyInfoService.list(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                    .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                    .ge(PolicyInfo::getCreateTime, LocalDate.of(2024,12,27))
                    .eq(PolicyInfo::getRenewInsuranceResult, YesOrNoNumberEnum.NO.getCode())
                    .eq(PolicyInfo::getInsuranceEndDate, now.minusDays(30)));
            if(ObjectUtil.isNotEmpty(policy5)){
                policyInfos.addAll(policy5);
            }
            if(ObjectUtil.isNotEmpty(policy30)){
                policyInfos.addAll(policy30);
            }
            //modify 260112 关联合同信息，排除合同已经结束的保单
            excludedFinishContract(policyInfos);
            if (ObjectUtil.isEmpty(policyInfos)) {
                return;
            }
            //发起到期提醒流程
            policyInfos.forEach(policyInfo -> {
                policyInfoVersionService.policyStartOverdueReminderProcess(policyInfo);
            });
        } catch (Exception e) {
            log.error("保单到期提示失败", e);
        }
    }

    private void excludedFinishContract(List<PolicyInfo> policyInfos) {
        if (ObjectUtil.isEmpty(policyInfos)) {
            return;
        }
        //查询流程:（审核通过&&一键通过）&&（正常结清||提前结清状态）
        List<String> contractIds = policyInfos.stream()
                .map(policyInfo -> policyInfo.getContractId().toString())
                .distinct()
                .collect(Collectors.toList());

        List<Long> endContractIds = myTaskService.getProcessEndContractTask(contractIds);

        policyInfos.removeIf(policyInfo -> endContractIds.contains(policyInfo.getContractId()));
    }

    //节点到期自动提交
    @XxlJob("policyNodeAutoCommit")
    @Transactional(rollbackFor = Throwable.class)
    public void policyNodeAutoCommit() {
        //1.保单到期日当天项目经理/运营经办仍未提交的，系统将自动提交
        try {
            LocalDate now = LocalDate.now();
            //查询15天内到期且未发起过保单提醒流程的
            List<PolicyInfo> policyInfos = policyInfoService.list(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                    .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                    .eq(PolicyInfo::getExpirationReminderFlag, YesOrNoNumberEnum.YES.getCode())
                    .eq(PolicyInfo::getInsuranceEndDate, now));
            if (ObjectUtil.isNotEmpty(policyInfos)) {
                //查询流程中数据
                Set<String> needCommitSet = new HashSet<>();
                needCommitSet.add("userTask_projectSponsor");
                //needCommitSet.add("userTask_operation_hand");
                //查询流程
                List<TaskResp> processRunningTask = myTaskService.getProcessRunningTask(policyInfos.stream().map(PolicyInfo::getId).map(String::valueOf).collect(Collectors.toList()), ProcessModelTypeEnum.PolicyReminderFlow.name());
                if (ObjectUtil.isNotEmpty(processRunningTask)) {
                    //过滤符合条件的
                    List<TaskResp> needCommitTask = processRunningTask.stream().filter(e -> needCommitSet.contains(e.getTaskActivityId())).collect(Collectors.toList());
                    if (ObjectUtil.isNotEmpty(needCommitTask)) {
                        needCommitTask.forEach(e -> executionService.currentNodeAutoCommit(e, String.valueOf(GlobalConstants.READONLY_ID), AUTO_COMMIT_MESSAGE));
                    }
                }
            }
        } catch (Exception e) {
            log.error("保单到期自动提交失败", e);
        }
        // 2.续保逾期到达后超过5天项目主办、运营经办仍未提交的，系统将强制项目经理提交
        try {
            LocalDate now = LocalDate.now();
            //查询15天内到期且未发起过保单提醒流程的
            List<PolicyInfo> policyInfos = policyInfoService.list(Wrappers.<PolicyInfo>lambdaQuery()
                    .eq(PolicyInfo::getRenewInsuranceFlag, PolicyRenewInsuranceEnum.RENEWAL_UPON_EXPIRATION.name())
                    .eq(PolicyInfo::getPolicyStatus, PolicyStatusEnum.EFFECT.name())
                    .eq(PolicyInfo::getRenewalOverdueFlag, YesOrNoNumberEnum.YES.getCode())
                    .le(PolicyInfo::getInsuranceEndDate, now));
            if (ObjectUtil.isNotEmpty(policyInfos)) {
                //查询流程中数据
                Set<String> needCommitSet = new HashSet<>();
                needCommitSet.add("userTask_projectSponsor");
                //needCommitSet.add("userTask_operation_hand");
                //查询流程
                List<TaskResp> processRunningTask = myTaskService.getProcessRunningTask(policyInfos.stream().map(PolicyInfo::getId).map(String::valueOf).collect(Collectors.toList()), ProcessModelTypeEnum.PolicyOverdueReminderFlow.name());
                if (ObjectUtil.isNotEmpty(processRunningTask)) {
                    //过滤符合条件的
                    List<TaskResp> needCommitTask = processRunningTask.stream().filter(e -> needCommitSet.contains(e.getTaskActivityId()) && e.getTaskCreateTime() != null &&
                            LocalDateTime.ofInstant(e.getTaskCreateTime().toInstant(), ZoneId.systemDefault()).isBefore(LocalDateTime.now().minusDays(5))).collect(Collectors.toList());
                    if (ObjectUtil.isNotEmpty(needCommitTask)) {
                        needCommitTask.forEach(e -> executionService.currentNodeAutoCommit(e, String.valueOf(GlobalConstants.READONLY_ID), AUTO_COMMIT_MESSAGE));
                    }
                }
            }
        } catch (Exception e) {
            log.error("保单到期自动提交失败", e);
        }
    }


}
