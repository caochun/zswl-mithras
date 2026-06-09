package cn.zswltech.mithras.service.service.assetclassify;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyBizNodeEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyInitTypeEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyStatusEnum;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyNodeRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.assetclassify.application.AssetClassifyNodeRecordService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyClientLibService;
import cn.zswltech.mithras.assetclassify.application.lib.AssetClassifyLibVersionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
@Slf4j
@Service
public class AssetClassifyProcessService {
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private AssetClassifyLibVersionService assetClassifyLibVersionService;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyNodeRecordService assetClassifyNodeRecordService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private AssetClassifyClientLibService assetClassifyClientLibService;

    @Transactional(rollbackFor = Throwable.class)
    public String startReviewMeeting(Long assetClassifyId) {
        Long userId = this.checkAuth();
        AssetClassify assetClassify = assetClassifyService.getById(assetClassifyId);
        Assert.notNull(assetClassify, () -> MithrasException.newException("资产五级分类记录不存在"));
        // 增加校验: 当前整个季中初分流程中，如果评审审批完成了，应当发起下一风委会而不能重复发起评审
        // 除非是整个流程完成发起下一次季中初分，才可以再次评审
        AssetClassifyNodeRecord node = assetClassifyNodeRecordService.getOne(Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassifyId)
                .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.REVIEW_MEETING.name()).last("limit 1"));
        if (ObjectUtil.isNotEmpty(node)){
            if (node.getNodeStatue().equals(AssetClassifyStatusEnum.FINISH.name())){
                throw new MithrasException("已完成评审流程，请不要重复提交");
            }
            if (node.getNodeStatue().equals(AssetClassifyStatusEnum.PROCESS.name())){
                throw new MithrasException("该用户已处于流程中，请不要重复提交");
            }
        }
        // 生成业务节点记录
        AssetClassifyNodeRecord assetClassifyNodeRecord = new AssetClassifyNodeRecord();
        assetClassifyNodeRecord.setAssetClassifyId(assetClassifyId);
        assetClassifyNodeRecord.setNodeName(AssetClassifyBizNodeEnum.REVIEW_MEETING.name());
        assetClassifyNodeRecord.setStartTime(LocalDateTime.now());
        assetClassifyNodeRecord.setNodeStatue(AssetClassifyStatusEnum.PROCESS.name());
        assetClassifyNodeRecordService.save(assetClassifyNodeRecord);
        // 生成审批流
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.AssetClassifyReviewMeetingFlow.name());
        startProcessReq.setBusinessKey(assetClassifyId.toString());
        // 季中与季末使用不同的流程名称
        if (AssetClassifyInitTypeEnum.QUARTER_MID.name().equals(assetClassify.getInitType())){
            startProcessReq.setProcessInstanceName(assetClassify.getYear() + "年" + assetClassify.getQuarter() + "季度资产五级分类评审会(季中调整)");
        }else {
            startProcessReq.setProcessInstanceName(assetClassify.getYear() + "年" + assetClassify.getQuarter() + "季度资产五级分类评审会");
        }
        startProcessReq.setStartUserId(userId.toString());
        startProcessReq.setStartUserDeptId(this.getStartUserDeptId(userId));
        return flowProcessApiService.start(startProcessReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void endReviewMeeting(ProcessEndContext processEndContext) {
        Long assetClassifyId = Long.valueOf(processEndContext.getBusinessKey());
        boolean processPass = ProcessBusinessStatusEnum.success(processEndContext.getEndType());
        if (processPass) {
            // 更新节点状态
            assetClassifyNodeRecordService.updateNodeStatue(assetClassifyId, AssetClassifyBizNodeEnum.REVIEW_MEETING.name(), AssetClassifyStatusEnum.FINISH.name());
            // 生成版本
            assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId()),
                    processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 异步生成风委会的汇总审批表
                    new Thread(() -> {
                        try {
                            assetClassifyService.generateSummaryFile(assetClassifyId, AssetClassifyBizNodeEnum.RISK_MEETING);
                        } catch (Exception e) {
                            log.error("资产五级分类<评审会>流程结束，生成<认定汇总审批表_风委会>发生异常", e);
                        }
                    }).start();
                }
            });
        } else {
            // 生成一个无效版本
            assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId()),
                    processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            // 数据回退
            assetClassifyLibVersionService.reset(assetClassifyId);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public String startRiskMeeting(Long assetClassifyId) {
        Long userId = this.checkAuth();
        AssetClassify assetClassify = assetClassifyService.getById(assetClassifyId);
        Assert.notNull(assetClassify, () -> MithrasException.newException("资产五级分类记录不存在"));
        // 增加校验: 当前整个季中初分流程中，如果风委会审批完成了，应当发起下一次季中初分而不能重复发起风委会
        AssetClassifyNodeRecord node = assetClassifyNodeRecordService.getOne(Wrappers.<AssetClassifyNodeRecord>lambdaQuery()
                .eq(AssetClassifyNodeRecord::getAssetClassifyId, assetClassifyId)
                .eq(AssetClassifyNodeRecord::getNodeName, AssetClassifyBizNodeEnum.RISK_MEETING.name()).last("limit 1"));
        if (ObjectUtil.isNotEmpty(node)){
            if (node.getNodeStatue().equals(AssetClassifyStatusEnum.FINISH.name())){
                throw new MithrasException("已完成风委会流程，请不要重复提交");
            }
            if (node.getNodeStatue().equals(AssetClassifyStatusEnum.PROCESS.name())){
                throw new MithrasException("该用户已处于流程中，请不要重复提交");
            }
        }
        // 生成业务节点记录
        AssetClassifyNodeRecord assetClassifyNodeRecord = new AssetClassifyNodeRecord();
        assetClassifyNodeRecord.setAssetClassifyId(assetClassifyId);
        assetClassifyNodeRecord.setNodeName(AssetClassifyBizNodeEnum.RISK_MEETING.name());
        assetClassifyNodeRecord.setStartTime(LocalDateTime.now());
        assetClassifyNodeRecord.setNodeStatue(AssetClassifyStatusEnum.PROCESS.name());
        assetClassifyNodeRecordService.save(assetClassifyNodeRecord);
        // 生成审批流
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.AssetClassifyRiskMeetingFlow.name());
        startProcessReq.setBusinessKey(assetClassifyId.toString());
        // 季中与季末使用不同的流程名称
        if (AssetClassifyInitTypeEnum.QUARTER_MID.name().equals(assetClassify.getInitType())){
            startProcessReq.setProcessInstanceName(assetClassify.getYear() + "年" + assetClassify.getQuarter() + "季度资产五级分类风委会(季中调整)");
        }else {
            startProcessReq.setProcessInstanceName(assetClassify.getYear() + "年" + assetClassify.getQuarter() + "季度资产五级分类风委会");
        }
        startProcessReq.setStartUserId(userId.toString());
        startProcessReq.setStartUserDeptId(this.getStartUserDeptId(userId));
        return flowProcessApiService.start(startProcessReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void endRiskMeeting(ProcessEndContext processEndContext) {
        Long assetClassifyId = Long.valueOf(processEndContext.getBusinessKey());
        boolean processPass = ProcessBusinessStatusEnum.success(processEndContext.getEndType());
        if (processPass) {
            // 更新节点状态
            assetClassifyNodeRecordService.updateNodeStatue(assetClassifyId, AssetClassifyBizNodeEnum.RISK_MEETING.name(), AssetClassifyStatusEnum.FINISH.name());
            // 判断是否要进入董事会 --> 不再进入董事会
//            if (!this.needBoardMeeting(assetClassifyId)) {
//                // 无需进入董事会的话，将本次资产五级分类置为结束
//                assetClassifyService.finish(assetClassifyId);
//            }
            assetClassifyService.finish(assetClassifyId);
            // 生成版本
            String version = assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId()), processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
            // 季中初分增加：风委会完成后保存客户数据,以供详情页查询分类历史数据
            assetClassifyClientLibService.saveList(version,assetClassifyId);
        } else {
            // 生成一个无效版本
            assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId()), processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            // 数据回退
            assetClassifyLibVersionService.reset(assetClassifyId);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public String startBoardMeeting(Long assetClassifyId) {
        Long userId = this.checkAuth();
        AssetClassify assetClassify = assetClassifyService.getById(assetClassifyId);
        Assert.notNull(assetClassify, () -> MithrasException.newException("资产五级分类记录不存在"));
        Assert.isTrue(Objects.equals(assetClassify.getFinish(), YesOrNoNumberEnum.NO.getCode()), () -> MithrasException.newException("资产五级分类已结束"));
        // 生成业务节点记录
        AssetClassifyNodeRecord assetClassifyNodeRecord = new AssetClassifyNodeRecord();
        assetClassifyNodeRecord.setAssetClassifyId(assetClassifyId);
        assetClassifyNodeRecord.setNodeName(AssetClassifyBizNodeEnum.BOARD_MEETING.name());
        assetClassifyNodeRecord.setStartTime(LocalDateTime.now());
        assetClassifyNodeRecord.setNodeStatue(AssetClassifyStatusEnum.PROCESS.name());
        assetClassifyNodeRecordService.save(assetClassifyNodeRecord);
        // 生成审批流
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.AssetClassifyBoardMeetingFlow.name());
        startProcessReq.setBusinessKey(assetClassifyId.toString());
        startProcessReq.setProcessInstanceName(assetClassify.getYear() + "年" + assetClassify.getQuarter() + "季度资产五级分类董事会");
        startProcessReq.setStartUserId(userId.toString());
        startProcessReq.setStartUserDeptId(this.getStartUserDeptId(userId));
        return flowProcessApiService.start(startProcessReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void endBoardMeeting(ProcessEndContext processEndContext) {
        Long assetClassifyId = Long.valueOf(processEndContext.getBusinessKey());
        boolean processPass = ProcessBusinessStatusEnum.success(processEndContext.getEndType());
        if (processPass) {
            // 更新节点状态
            assetClassifyNodeRecordService.updateNodeStatue(assetClassifyId, AssetClassifyBizNodeEnum.BOARD_MEETING.name(), AssetClassifyStatusEnum.FINISH.name());
            // 结束资产五级分类
            assetClassifyService.finish(assetClassifyId);
            // 生成版本
            assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId()),
                    processEndContext.getProcessInstanceId(), VersionTypeConstants.NORMAL);
        } else {
            // 生成一个无效版本
            assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.APPROVAL, Long.valueOf(processEndContext.getStartUserId()),
                    processEndContext.getProcessInstanceId(), VersionTypeConstants.INVALID);
            // 数据回退
            assetClassifyLibVersionService.reset(assetClassifyId);
        }
    }

    private Long checkAuth() {
        Long userId = AccountUtil.getLoginInfo().getId();
        List<String> jobList = sysUserService.queryUserJobList(userId);
        // 资产管理岗可发起
        boolean operation = false;
        for (String jobCode : jobList) {
            if (Objects.equals(JobEnum.assetmanagement.name(), jobCode)) {
                operation = true;
                break;
            }
        }
        Assert.isTrue(operation, () -> MithrasException.newException("仅资产管理岗可操作"));
        return userId;
    }

    private boolean needBoardMeeting(Long assetClassifyId) {
        List<AssetClassifyClient> assetClassifyClientList = assetClassifyClientService.listByAssetClassifyId(assetClassifyId);
        List<Long> needBoardMeetingIdList = new LinkedList<>();
        for (AssetClassifyClient assetClassifyClient : assetClassifyClientList) {
            // 存在风险敞口为2亿元（含2亿元）以上的租赁资产风险分类为不良类（即为次级类、可疑或损失类）
            // 或风险敞口为1亿元（含1亿元）以上的租赁资产风险分类为可疑类或损失类的
            if (Objects.isNull(assetClassifyClient.getStockRiskExposure())) {
                continue;
            }
            if (assetClassifyClient.getStockRiskExposure() >= 2000000000000L
                    && Arrays.asList(AssetClassifyResultEnum.SECONDARY.name(), AssetClassifyResultEnum.SUSPICIOUS.name(), AssetClassifyResultEnum.LOSS.name()).contains(assetClassifyClient.getClassifyResult())) {
                needBoardMeetingIdList.add(assetClassifyClient.getId());
            } else if (assetClassifyClient.getStockRiskExposure() >= 1000000000000L
                    && Arrays.asList(AssetClassifyResultEnum.SUSPICIOUS.name(), AssetClassifyResultEnum.LOSS.name()).contains(assetClassifyClient.getClassifyResult())) {
                needBoardMeetingIdList.add(assetClassifyClient.getId());
            }
        }
        if (CollectionUtil.isNotEmpty(needBoardMeetingIdList)) {
            assetClassifyClientService.joinBoardMeeting(needBoardMeetingIdList);
        }
        return needBoardMeetingIdList.size() > 0;
    }

    private String getStartUserDeptId(Long userId) {
        List<OrgDO> orgDOList = sysUserService.listOrgByJob(userId, JobEnum.assetmanagement.name());
        if (CollectionUtil.isEmpty(orgDOList)) {
            return null;
        }
        return orgDOList.get(0).getId().toString();
    }
}
