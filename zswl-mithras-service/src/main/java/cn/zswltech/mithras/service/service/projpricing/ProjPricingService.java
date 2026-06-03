package cn.zswltech.mithras.service.service.projpricing;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.projlifecycle.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.FlowEndEventProcessor;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.lib.projpricing.impl.ProjPricingVersionServiceImpl;
import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.impl.ProjPricingStateMachine;
import cn.zswltech.mithras.service.service.projlifecycle.ProjectLifecycleEventService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.zswltech.mithras.service.enums.common.ProjectBizType.*;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.NEW;
import static cn.zswltech.mithras.service.enums.common.RecordStatus.TAKE_EFFECT;
import static cn.zswltech.mithras.service.others.Util.missRequiredParam;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjPricingService implements FlowEndEventProcessor {
    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;
    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private ProjPricingVersionServiceImpl projPricingVersionService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private ProjectLifecycleEventService projectLifecycleEventService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjPricingPriceService priceService;
    @Resource
    private ProjPricingStateMachine stateMachine;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ProjPricingBaseInfoService baseInfoService;

    /**
     * 启动定价审批流程
     *
     * @param projPricingId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void effect(@NotNull Long projPricingId) {
        ProjPricingBaseInfo baseInfo = projPricingBaseInfoMapper.selectById(projPricingId);
        boolean isAdd = NEW.name().equals(baseInfo.getProjPricingStatus());
        if(!isAdd){
            ChangeDTO changeDTO = projPricingVersionService.checkPricingChange(projPricingId);
            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
                throw new MithrasException("报价方案和现金流表数据未变动，无需提交");
            }
        }
        //更新部门领导
        projPricingBaseInfoService.renewLeader(projPricingId);
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(isAdd ? ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name() : ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow.name());
        startProcessReq.setBusinessKey(String.valueOf(projPricingId));
        startProcessReq.setSubModule(baseInfo.getBizType());
        startProcessReq.setProcessInstanceName(baseInfo.getProjName());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId())
                .map(String::valueOf)
                .orElse(null));
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ?
                ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>());
        varMap.put("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ?
                ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>());
        varMap.put("riskControlManager", Objects.nonNull(baseInfo.getRiskControlManagerId()) ?
                ListUtil.toList(String.valueOf(baseInfo.getRiskControlManagerId())) : new ArrayList<>());
        startProcessReq.setVariables(varMap);
        String processInstanceId = processApiService.start(startProcessReq);
        // 记录客户id
        bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());
        String projType = baseInfo.getRelationDataType() == null ? ReviewRelationDataType.PROJ_ESTABLISH.name() : baseInfo.getRelationDataType();
        Long projId = baseInfo.getProjEstablishId();
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projType)) {
            projId = baseInfo.getId();
        }
        projectLifecycleEventService.add(isAdd ? "项目定价创建审批" : "项目定价变更审批", ProjLifecycleEventTypeEnum.APPROVAL.name(),  isAdd ? "提交审批" : "发起定价变更评审", projId, projType);
        stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.SUBMIT_APPROVAL, baseInfo.getProcessStatus()));

    }

    public List<ProcessResp> findRelatedProcesses(Long projPricingId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(10);
        processPageReq.setBusinessKey(String.valueOf(projPricingId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_PRICING.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(),
                ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents();
    }


    public ProcessResp findRelatedProcess(Long projPricingId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(projPricingId));
        processPageReq.setModelKeyList(BusinessModuleEnum.PROJ_PRICING.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public ProcessResp findProcessByProcessInstanceId(String instanceId) {
        return taskApiService.queryProcessById(instanceId);
    }


    public boolean canSave(Long projEstablishId) {
        AccountVO loginUser = AccountUtil.getLoginInfo();
        if (projEstablishId == null) {
            return false;
        }
        if (Objects.isNull(loginUser)) {
            return false;
        }
        ProcessResp processResp = findRelatedProcess(projEstablishId);
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
     * 设置风险敞口
     *
     * @param jsonInfo
     * @return
     */
    public String setExposureRisk(String jsonInfo) {
        if (StrUtil.isNotBlank(jsonInfo)) {
            List<ClientInfo> infos = JSON.parseArray(jsonInfo, ClientInfo.class);
            for (ClientInfo info : infos) {
                if (isNotNull(info.getClientId())) {
                    info.setStockRiskExposure(contractBaseInfoService.getStockRiskExposure(info.getClientId(), null, null));
                }
            }
            return JSONUtil.toJsonStr(infos);
        }
        return null;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long projectId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        ProjPricingBaseInfo baseInfo = projPricingBaseInfoMapper.selectById(projectId);
        if (processPass) {
            // 审批通过 新增版本
            stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.APPROVAL_PASS, baseInfo.getProcessStatus()));
        } else {
            if (TAKE_EFFECT.name().equals(baseInfo.getProjPricingStatus())) {
                if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_WITHDRAW, baseInfo.getProcessStatus()));
                }
                if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_REJECT, baseInfo.getProcessStatus()));
                }
            } else {
                if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_WITHDRAW, baseInfo.getProcessStatus()));
                }
                if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                    stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_REJECT, baseInfo.getProcessStatus()));
                }
            }
            //更新部门领导
            projPricingBaseInfoService.renewLeader(projectId);
        }
        // 流程快照需保留当时的风控行业分类
        CorpCommerceInfoLib corpCommerceInfoLib = SpringUtil.getBean(CorpCommerceInfoLibService.class).getNewestOne(baseInfo.getClientId());
        if (Objects.nonNull(corpCommerceInfoLib) && StrUtil.isNotBlank(corpCommerceInfoLib.getRiskControlIndustryClassify())) {
            LambdaUpdateWrapper<ProjPricingBaseInfo> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(ProjPricingBaseInfo::getRiskControlIndustryClassify, corpCommerceInfoLib.getRiskControlIndustryClassify());
            updateWrapper.eq(ProjPricingBaseInfo::getId, baseInfo.getId());
            projPricingBaseInfoService.update(updateWrapper);
        }
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        projPricingVersionService.recordVersion(projectId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);

        if (!processPass && TAKE_EFFECT.name().equals(baseInfo.getProjPricingStatus())) {
            projPricingVersionService.reset(projectId);
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_WITHDRAW, baseInfo.getProcessStatus()));
            }
            if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.MODIFY_REJECT, baseInfo.getProcessStatus()));
            }
        }


    }

    public void pricingApprovalCheck(Long projPricingId) {
        ProjPricingBaseInfo baseInfo = projPricingBaseInfoMapper.selectById(projPricingId);
        pricingApprovalCheck(baseInfo);
    }

    public void pricingApprovalCheck(ProjPricingBaseInfo baseInfo) {
        // 检查业务定价审批表 -> 不在检查
        //List<MaterialsList> materialsListList = materialsListService.list(BusinessModuleEnum.PROJ_REVIEW.name(), Collections.singletonList(ProjReviewMaterialsEnum.BUSINESS_PRICING_APPROVAL_FORM.name()), Collections.singletonList(baseInfo.getId()));
        //Assert.notEmpty(materialsListList, () -> MithrasException.newException("请先上传业务定价审批表"));
        missRequiredParam((ZL.name().equals(baseInfo.getBizType()) || ZZ.name().equals(baseInfo.getBizType()))
                && isBlank(baseInfo.getLesseeInfo()), "承租人");
        missRequiredParam((BL.name().equals(baseInfo.getBizType()) || ZR.name().equals(baseInfo.getBizType()))
                && isBlank(baseInfo.getCreditorInfo()), "债权人");
        // 如果FTP风控行业分类为公用事业类或民生消费类时，【项目管理层级】和【是否AAA评级】必填
        if (StrUtil.equalsAny(baseInfo.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_PUBLIC_UTILITIES.name(), FtpIndustryCategoryEnum.FTP_CIVIL_CONSUMPTION.name())) {
            Assert.notBlank(baseInfo.getProjectManageLevel(), () -> MithrasException.newException("<项目管理层级>不能为空"));
            Assert.notNull(baseInfo.getIsAAA(), () -> MithrasException.newException("<是否AAA评级>不能为空"));
        }
        //检查基本信息
        ProjPricingBaseInfoDetailRSP baseInfoDetailRSP = baseInfoService.detail(baseInfo.getId(), null);
        checkBaseInfo(baseInfoDetailRSP);
        //检查报价方案
        ProjPricingPriceDetailRSP priceDetail = priceService.detail(baseInfo.getId());
        checkBaseInfo(priceDetail);
    }

    private void checkBaseInfo(ProjPricingBaseInfoDetailRSP projPricingBaseInfoDetailRSP) {
        Boolean flag = false;
        StringBuilder builder = new StringBuilder();
        builder.append("请先完善基本信息：");
        if (projPricingBaseInfoDetailRSP == null) {
            throw new MithrasException("请填写新基本信息");
        }
        if ((ProjectBizType.ZL.name().equals(projPricingBaseInfoDetailRSP.getBizType()) || ProjectBizType.ZZ.name().equals(projPricingBaseInfoDetailRSP.getBizType())) && (projPricingBaseInfoDetailRSP.getLeaseTypes() == null || projPricingBaseInfoDetailRSP.getLeaseTypes().size() == 0)) {
            builder.append("租赁类型;");
            flag = true;
        }
        if (ProjectBizType.BL.name().equals(projPricingBaseInfoDetailRSP.getBizType()) && (projPricingBaseInfoDetailRSP.getFactoringTypes() == null || projPricingBaseInfoDetailRSP.getFactoringTypes().size() == 0)) {
            builder.append("保理类型;");
            flag = true;
        }
        if (ProjectBizType.ZR.name().equals(projPricingBaseInfoDetailRSP.getBizType()) && (projPricingBaseInfoDetailRSP.getZrTypes() == null || projPricingBaseInfoDetailRSP.getZrTypes().size() == 0)) {
            builder.append("转让类型;");
            flag = true;
        }
//        if (org.springframework.util.StringUtils.isEmpty(projPricingBaseInfoDetailRSP.getProjectType())) {
//            builder.append("项目类型;");
//            flag = true;
//        }
        if (org.springframework.util.StringUtils.isEmpty(projPricingBaseInfoDetailRSP.getRiskControlManagerName())) {
            builder.append("风控经理;");
            flag = true;
        }
        if (org.springframework.util.StringUtils.isEmpty(projPricingBaseInfoDetailRSP.getLegalManagerName())) {
            builder.append("法务经理;");
            flag = true;
        }
        if (Boolean.TRUE.equals(flag)) {
            throw new MithrasException(builder.toString());
        }
    }

    private void checkBaseInfo(ProjPricingPriceDetailRSP priceDetail) {
        Boolean flag = false;
        StringBuilder builder = new StringBuilder();
        builder.append("提交审批前请先完善报价方案：");
        if (priceDetail == null || (priceDetail.getAocPriceDetailRSP() == null && priceDetail.getLeasePriceDetailRSP() == null && priceDetail.getFactoringPriceDetailRSP() == null)) {
            throw new MithrasException("提交审批前请先填写报价方案");
        }
        Assert.notNull(priceDetail.getIrr(), () -> MithrasException.newException("IRR未保存"));
        //债权转让
        if (priceDetail.getAocPriceDetailRSP() != null) {
//            if (StringUtils.isEmpty(priceDetail.getAocPriceDetailRSP().getConsultingFee())) {
//                builder.append("服务费/咨询费;");
//                flag = true;
//            }
//            if (StringUtils.isEmpty(priceDetail.getAocPriceDetailRSP().getRepayType())) {
//                builder.append("还款方式;");
//                flag = true;
//            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getAocPriceDetailRSP().getRentalCalcType())) {
                builder.append("还款计算方式;");
                flag = true;
            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getAocPriceDetailRSP().getRepayRate())) {
                builder.append("还款频率;");
                flag = true;
            }
//            if (ObjectUtil.isEmpty(priceDetail.getAocPriceDetailRSP().getPlannedStartingDate())) {
//                builder.append("计划起租日;");
//                flag = true;
//            }
        }
        //租赁
        if (priceDetail.getLeasePriceDetailRSP() != null) {
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getLeasePriceDetailRSP().getConsultingFee())) {
                builder.append("服务费/咨询费;");
                flag = true;
            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getLeasePriceDetailRSP().getNominalPrice())) {
                builder.append("名义价款;");
                flag = true;
            }
            if (ObjectUtil.isEmpty(priceDetail.getLeasePriceDetailRSP().getPlannedStartingDate())) {
                builder.append("计划起租日;");
                flag = true;
            }
            if (ObjectUtil.isEmpty(priceDetail.getLeasePriceDetailRSP().getRepayRate())) {
                builder.append("还款频率;");
                flag = true;
            }
        }
        //保理
        if (priceDetail.getFactoringPriceDetailRSP() != null) {
//            if (StringUtils.isEmpty(priceDetail.getFactoringPriceDetailRSP().getConsultingFee())) {
//                builder.append("服务费/咨询费;");
//                flag = true;
//            }
//            if (StringUtils.isEmpty(priceDetail.getFactoringPriceDetailRSP().getRepayType())) {
//                builder.append("还款方式;");
//                flag = true;
//            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getFactoringPriceDetailRSP().getRentalCalcType())) {
                builder.append("还款计算方式;");
                flag = true;
            }
            if (org.springframework.util.StringUtils.isEmpty(priceDetail.getFactoringPriceDetailRSP().getRepayRate())) {
                builder.append("还款频率;");
                flag = true;
            }
//            if (ObjectUtil.isEmpty(priceDetail.getFactoringPriceDetailRSP().getPlannedStartingDate())) {
//                builder.append("计划起租日;");
//                flag = true;
//            }
        }
        if (Boolean.TRUE.equals(flag)) {
            throw new MithrasException(builder.toString());
        }
    }

}
