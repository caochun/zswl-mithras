package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckPlanProcessStatusEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.lib.afterlease.AfterLeaseCheckPlanVersionService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName RentPaymentNotifyFlowHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class NewAfterLeaseCheckPlanPublishCreateFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private AfterLeaseCheckPlanVersionService afterLeaseCheckPlanVersionService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        log.info("NewAfterLeaseCheckPlanPublishCreateFlowHandle commit {}", prepare);
        if (CharSequenceUtil.isBlank(prepare.getBusinessData())) {
            throw MithrasException.newException("请先保存【基本信息】模块！");
        }
        NewAfterLeaseCheckPlanBase planBase = afterLeaseCheckPlanBaseService.getById(Long.valueOf(prepare.getBusinessData()));
        if (ObjectUtil.isEmpty(prepare.getBusinessData()) || ObjectUtil.isEmpty(planBase)) {
            throw new MithrasException("未保存计划");
        }
        if (StrUtil.equalsAny(planBase.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.PUBLISH.name(), AfterLeaseCheckPlanStatusEnum.FINISH.name())) {
            throw new MithrasException("计划已发布");
        }

        CommonProcessPrepare toUpdate = CommonProcessPrepare.builder().id(prepare.getId()).isAssetConfirm("已确认").build();
        commonProcessPrepareService.updateById(toUpdate);

//        NewAfterLeaseCheckPlanBase newPlan = new NewAfterLeaseCheckPlanBase();
//        //发布任务
//        newPlan.setId(planBase.getId());
//        newPlan.setApprovalStatus(AfterLeaseCheckPlanProcessStatusEnum.PLAN_ACK_PASS.name());
//        newPlan.setPlanStatus(AfterLeaseCheckPlanStatusEnum.PUBLISH.name());
//        newPlan.setUpdateTime(LocalDateTimeUtil.now());
//        afterLeaseCheckPlanBaseService.updateById(newPlan);
//        afterLeaseCheckPlanVersionService.recordVersion(newPlan.getId(), VersionTypeEnum.EFFECT, null,
//                null, VersionTypeConstants.NORMAL);
//        //发布
//        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).listBy(planBase.getId());
//        for (NewAfterLeaseCheckPlanClient checkPlanClient : checkPlanClientList) {
//            List<CorpCommerceInfo> byClientId = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(checkPlanClient.getClientId());
//            if (ObjectUtil.isEmpty(byClientId)) {
//                continue;
//            }
//            CorpCommerceInfo corpCommerceInfo = byClientId.get(0);
//            if (RiskControlIndustryClassify.PUBLIC_UTILITIES.name().equals(corpCommerceInfo.getRiskControlIndustryClassify())
//                    || RiskControlIndustryClassify.CIVIL_CONSUMPTION.name().equals(corpCommerceInfo.getRiskControlIndustryClassify())) {
//                afterLeaseCheckPlanBaseService.startChecking(planBase.getId(), AfterLeaseCheckReportTypeEnum.PUBLIC);
//            } else {
//                afterLeaseCheckPlanBaseService.startChecking(planBase.getId(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC);
//            }
//        }
        return null;
    }

    @Override
    public void afterDiscard(CommonProcessPrepare prepare) {
        if (StrUtil.isNotBlank(prepare.getBusinessData())) {
            SpringUtil.getBean(AfterLeaseCheckPlanBaseService.class).close(Long.valueOf(prepare.getBusinessData()));
        }
    }
}
