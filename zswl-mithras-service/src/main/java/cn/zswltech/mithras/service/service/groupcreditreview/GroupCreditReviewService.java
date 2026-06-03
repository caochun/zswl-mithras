package cn.zswltech.mithras.service.service.groupcreditreview;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.credit.domain.groupcredit.review.enums.GroupCreditReviewProcessStatus;
import cn.zswltech.mithras.service.enums.projreview.MeetMinuteStatuesEnum;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.groupcreditreview.handler.impl.GroupCreditReviewBaseInfoLibHandler;
import cn.zswltech.mithras.credit.application.groupcredit.review.service.impl.GroupCreditReviewVersionServiceImpl;
import cn.zswltech.mithras.service.service.lib.projreview.handler.impl.ProjReviewBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewMeetMinuteBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.others.Util.missRequiredParam;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Service
public class GroupCreditReviewService {
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;
    @Resource
    private GroupCreditReviewVersionServiceImpl groupCreditReviewVersionService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private GroupCreditReviewBaseInfoLibHandler groupCreditReviewBaseInfoLibHandler;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibHandler projReviewBaseInfoLibHandler;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;

    @Transactional(rollbackFor = Throwable.class)
    public void effect(@NotNull Long groupCreditReviewId) {
        GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(groupCreditReviewId);
        //授信立项非失效状态
        if (SpringContextHolder.getBean(GroupCreditEstablishBaseInfoService.class).count(Wrappers.<GroupCreditEstablishBaseInfo>lambdaQuery()
                .eq(GroupCreditEstablishBaseInfo::getId, baseInfo.getGroupCreditEstablishId())
                .eq(GroupCreditEstablishBaseInfo::getGroupCreditEstablishStatus, RecordStatus.EXPIRE.name())) > 0) {
            throw new MithrasException("此授信立项已失效！流程无法提交");
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        // 判断使用创建流程还是修改流程
        if (RecordStatus.NEW.name().equals(baseInfo.getGroupCreditReviewStatus())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name());
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name());
        }
        // 判断客户风控行业分类
        Client client = clientService.getById(baseInfo.getClientId());
        Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(client);
        Assert.notNull(corpCommerceInfoLib, () -> MithrasException.newException("无生效的客户工商信息数据"));
        Assert.notBlank(corpCommerceInfoLib.getRiskControlIndustryClassify(), () -> MithrasException.newException(String.format("请先至客户模块维护<%s>的“风控行业分类”，否则无法判断该项目是否需经董事会审议！", client.getClientName())));
        startProcessReq.setVariables(MapUtil.of(
            Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>()),
            Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>()),
            Pair.of("riskControlManager", Objects.nonNull(baseInfo.getRiskControlManagerId()) ? ListUtil.toList(String.valueOf(baseInfo.getRiskControlManagerId())) : new ArrayList<>()),
            Pair.of("legalManagerUser", Objects.nonNull(baseInfo.getLegalManagerUserId()) ? ListUtil.toList(String.valueOf(baseInfo.getLegalManagerUserId())) : new ArrayList<>())
        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(groupCreditReviewId));
        startProcessReq.setSubModule("DEFAULT");
        startProcessReq.setProcessInstanceName(baseInfo.getProjName());
        startProcessReq.setCcUserIdList(StringUtils.isBlank(baseInfo.getProjCosponsorUserIds()) ? new ArrayList<>() : JSONUtil.parseArray(baseInfo.getProjCosponsorUserIds()).toList(String.class));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId()).map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());

        recordReviewStatus(groupCreditReviewId, null, RecordStatus.NEW.name().equals(baseInfo.getGroupCreditReviewStatus())
                ? GroupCreditReviewProcessStatus.NEW_UNDER_APPROVAL : GroupCreditReviewProcessStatus.CHANGING_UNDER_APPROVAL);

       /* if (ObjectUtil.equals(ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name(), startProcessReq.getModelKey())) {
            SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).initChangeReviewMeetMinute(baseInfo.getId(), processInstanceId);
        }*/
    }

    public ProcessResp findRelatedProcess(Long groupCreditReviewId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(groupCreditReviewId));
        processPageReq.setModelKeyList(BusinessModuleEnum.GROUP_CREDIT_REVIEW.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void processEnd(String modelKey, Long groupCreditReviewId, Integer endType, Long startUserId, String processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(groupCreditReviewId);
        // 填充风险敞口
        baseInfo.setClientRiskExposure(contractBaseInfoService.getGroupCreditStockRiskExposure(baseInfo.getClientId()));
        // 填充最新的客户风控行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibService.getNewestOne(baseInfo.getClientId());
        baseInfo.setRiskControlIndustryClassify(Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getRiskControlIndustryClassify).orElse(null));
        // 更新风险敞口和客户风控行业分类
        groupCreditReviewBaseInfoMapper.updateById(baseInfo);
        // 记录版本前要先更新状态
        GroupCreditReviewProcessStatus reviewProcessStatus = null;
        RecordStatus reviewStatus = null;
        if (processPass) {
            // 审批通过
            reviewProcessStatus = RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getGroupCreditReviewStatus())
                    ? GroupCreditReviewProcessStatus.CHANGING_APPROVAL_PASS : GroupCreditReviewProcessStatus.NEW_APPROVAL_PASS;
            reviewStatus = RecordStatus.TAKE_EFFECT;
            SpringContextHolder.getBean(ProjReviewMeetMinuteBaseInfoService.class).modifyStatus(processInstanceId, groupCreditReviewId, MeetMinuteStatuesEnum.EFFECT.name());
        } else {
            if (RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getGroupCreditReviewStatus())) {
                // 变更审批
                reviewProcessStatus = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)
                        ? GroupCreditReviewProcessStatus.CANCEL_CHANGE : GroupCreditReviewProcessStatus.CHANGE_REJECT;
            } else {
                // 新建审批
                reviewProcessStatus = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)
                        ? GroupCreditReviewProcessStatus.CANCEL_NEW : GroupCreditReviewProcessStatus.NEW_REJECT;
            }
        }
        recordReviewStatus(groupCreditReviewId, reviewStatus, reviewProcessStatus);
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        groupCreditReviewVersionService.recordVersion(groupCreditReviewId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);

        if (!processPass && RecordStatus.TAKE_EFFECT.name().equals(baseInfo.getGroupCreditReviewStatus())) {
            // 变更审批 且 未审批通过 回滚
            groupCreditReviewVersionService.reset(groupCreditReviewId);
            recordReviewStatus(groupCreditReviewId, null, reviewProcessStatus);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recordReviewStatus(Long establishId, RecordStatus establishStatus,
                                      GroupCreditReviewProcessStatus establishProcessStatus) {
        if (establishId == null || (establishStatus == null && establishProcessStatus == null)) {
            return;
        }
        LambdaUpdateWrapper<GroupCreditReviewBaseInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GroupCreditReviewBaseInfo::getId, establishId);
        if (establishStatus != null) {
            updateWrapper.set(GroupCreditReviewBaseInfo::getGroupCreditReviewStatus, establishStatus.name());
        }
        if (establishProcessStatus != null) {
            updateWrapper.set(GroupCreditReviewBaseInfo::getGroupCreditReviewProcessStatus, establishProcessStatus.name());
        }
        updateWrapper.set(GroupCreditReviewBaseInfo::getUpdateTime, LocalDateTime.now());
        groupCreditReviewBaseInfoMapper.update(null, updateWrapper);
    }

    public boolean canSave(Long groupCreditEstablishId) {
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (groupCreditEstablishId == null) {
            return false;
        }
        if (Objects.isNull(loginUser)) {
            return false;
        }
        ProcessResp processResp = findRelatedProcess(groupCreditEstablishId);
        if (processResp == null) {
            // 运行中流程为空 可以保存
            return true;
        }
        if (!FlowConstants.START_USER_TASK.equals(processResp.getCurTaskActivityIds())) {
            // 有运行中流程 不在发起人节点 不能保存
            return false;
        }
        if (!Objects.equals(String.valueOf(loginUser.getId()), processResp.getStartUserId())) {
            // 在发起人节点 不是发起人 不能保存
            return false;
        }
        return true;
    }

    /**
     * 获取剩余可用授信金额
     * @param groupCreditReviewId
     * @return
     */
    public Long getRemainCreditAmount(Long groupCreditReviewId) {
        if (Objects.isNull(groupCreditReviewId)) {
            return 0L;
        }
        // 找到最新版本的授信评审，拿到授信金额
        GroupCreditReviewBaseInfoLib reviewBaseInfoLib = groupCreditReviewBaseInfoLibHandler.queryLatestDataByOriginId(groupCreditReviewId);
        if (Objects.isNull(reviewBaseInfoLib) || Objects.isNull(reviewBaseInfoLib.getApplyCreditAmount())) {
            // 没生效
            return 0L;
        }
        // 找和该授信评审关联的评审
        // 剩余授信额度(元)=【授信评审】中的“授信额度”-与该授信关联的【项目评审】中审批状态！=“审批拒绝”，项目状态！=“关闭”的项目中的“申报授信金额”之和
        List<ProjReviewBaseInfo> relatedProjReviewList = projReviewBaseInfoService.getBaseMapper().selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getRelationDataType, ReviewRelationDataType.GROUP_CREDIT_REVIEW.name())
                .eq(ProjReviewBaseInfo::getGroupCreditReviewId, groupCreditReviewId)
                // 未关闭
                .notIn(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name())
                // 流程状态 不是 新建审批拒绝
                .ne(ProjReviewBaseInfo::getProjReviewProcessStatus, ProjProcessState.NEW_REJECT.name())
        );
        BigDecimal remainCreditAmountDecimal = new BigDecimal(reviewBaseInfoLib.getProjectApprovalAmount());
        for (ProjReviewBaseInfo rp : relatedProjReviewList) {
            if (RecordStatus.TAKE_EFFECT.name().equals(rp.getProjReviewStatus())) {
                // 生效了 找最新版本
                ProjReviewBaseInfoLib projReviewlatestVersionData = projReviewBaseInfoLibHandler.queryLatestDataByOriginId(rp.getId());
                remainCreditAmountDecimal = remainCreditAmountDecimal.subtract(Optional.ofNullable(projReviewlatestVersionData).map(p -> p.getDeclaredAmount()).map(BigDecimal::new).orElse(new BigDecimal(0L)));
            } else {
                // 没生效 找编辑区
                remainCreditAmountDecimal = remainCreditAmountDecimal.subtract(Optional.ofNullable(rp.getDeclaredAmount()).map(BigDecimal::new).orElse(new BigDecimal(0L)));
            }
        }
        return remainCreditAmountDecimal.longValue();
    }

    public void effectCheck(Long groupCreditReviewId) {
        GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(groupCreditReviewId);
        effectCheck(baseInfo);
    }

    public void effectCheck(GroupCreditReviewBaseInfo baseInfo) {
        // 此条判断的作用是判断下有没有编辑过 如果只新增没编辑，有些必填字段在接口层得不到校验
        missRequiredParam(isNull(baseInfo.getLegalManagerUserId()), "法务经理");
    }

}
