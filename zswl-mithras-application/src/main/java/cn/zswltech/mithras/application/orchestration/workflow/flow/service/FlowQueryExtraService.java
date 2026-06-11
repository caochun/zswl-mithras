package cn.zswltech.mithras.application.orchestration.workflow.flow.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.extension.event.ProcessStartEvent;
import cn.zswltech.flow.core.extension.event.context.ProcessStartContext;
import cn.zswltech.mithras.workflow.enums.trackevent.TrackTaskBizSourceEnum;
import cn.zswltech.mithras.workflow.mapper.FlowQueryExtraMapper;
import cn.zswltech.mithras.workflow.model.FlowQueryExtra;
import cn.zswltech.mithras.workflow.model.FlowQueryExtraMissing;
import cn.zswltech.mithras.afterlease.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRetreatInfo;
import cn.zswltech.mithras.filingmaterials.model.FilingMaterials;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistribution;
import cn.zswltech.mithras.financeprojectdistribution.mapper.model.FinanceProjectDistributionBaseInfo;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportBaseInfo;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportClientItem;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.workflow.model.TrackEventInfo;
import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.application.orchestration.afterlease.impl.AfterLeaseAdjustInfoServiceImpl;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractRetreatInfoService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.OtherFilingMaterialsService;
import cn.zswltech.mithras.financeprojectdistribution.service.impl.FinanceProjectDistributionBaseInfoService;
import cn.zswltech.mithras.financeprojectdistribution.service.impl.FinanceProjectDistributionService;
import cn.zswltech.mithras.creditreport.service.CreditReportBaseInfoService;
import cn.zswltech.mithras.creditreport.service.CreditReportClientItemService;
import cn.zswltech.mithras.application.orchestration.groupcredit.establish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.groupcredit.review.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.application.orchestration.workflow.trackevent.TrackEventService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.*;

/**
 * @author yibin
 * 审批流扩展表的数据生成
 * 基于此张表，满足更加复杂、离谱的查询
 */
@Slf4j
@Service
public class FlowQueryExtraService extends ServiceImpl<FlowQueryExtraMapper, FlowQueryExtra> {

    public void process(ProcessStartEvent event) {
        try {
            ProcessStartContext context = event.getProcessStartContext();
            String instanceId = context.getProcessInstanceId();
            String modelKey = context.getModelKey();
            String businessKey = context.getBusinessKey();
            //预算
            if (equalsAny(modelKey, BudgetPlanPayWeeklyFlow.name(), YearHalfOtherPlanEventFlow.name(), MonthPlanEventFlow.name(), BudgetExamineFlow.name(), FinalPlanEventFlow.name())) {
                return;
            }

            Long bizId = isBlank(businessKey) ? null : Long.valueOf(businessKey);

            //客户
            if (equalsAny(modelKey, ClientModifyFlow.name())) {
                Client client = getBean(ClientService.class).getById(bizId);
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId).setClientName(client.getClientName()));

            }
            //立项
            if (equalsAny(modelKey,
                    ProjEstablishCreateFlow.name(), ProjEstablishModifyFlow.name())) {
                ProjEstablishBaseInfo info = getBean(ProjEstablishBaseInfoService.class).getById(bizId);
                Client client = getBean(ClientService.class).getById(info.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId)
                                .setClientName(client.getClientName()).setProjName(info.getProjName()).setProjCode(info.getProjCode()));
            }
            //评审
            if (equalsAny(modelKey,
                    ProjReviewPricingApprovalFlow.name(), ProjReviewPricingModifyApprovalFlow.name())) {
                ProjPricingBaseInfo info = getBean(ProjPricingBaseInfoService.class).getById(bizId);
                Client client = getBean(ClientService.class).getById(info.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId)
                                .setClientName(client.getClientName()).setInstanceId(instanceId).setProjName(info.getProjName()).setProjCode(info.getProjCode()));
            }
            //评审
            if (equalsAny(modelKey,
                    ProjReviewCreateFlow.name(), ProjReviewModifyFlow.name())) {
                ProjReviewBaseInfo info = getBean(ProjReviewBaseInfoService.class).getById(bizId);
                Client client = getBean(ClientService.class).getById(info.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId)
                                .setClientName(client.getClientName()).setInstanceId(instanceId).setProjName(info.getProjName()).setProjCode(info.getProjCode()));
            }
            //合同
            if (equalsAny(modelKey,
                    ContractCreateFlow.name(), ContractModifyFlow.name(), ContractStartRentFlow.name(), ContractAddNewReceiptFlow.name(), ContractEarlySettleFlow.name(),
                    ContractNormalSettleFlow.name(), ContractLPRChangeFlow.name(), ContractExtensionFlow.name(), ContractEarlyRepayFlow.name(), ContractChangeRepayPlanFlow.name(),
                    ContractStartRentAutoFlow.name(), ContractAddNewReceiptAutoFlow.name(), ContractEarlySettleConfirmFlow.name())) {
                ContractBaseInfo info = getBean(ContractBaseInfoService.class).getById(bizId);
                ProjReviewBaseInfo reviewInfo = getBean(ProjReviewBaseInfoService.class).getById(info.getProjReviewId());
                Client client = getBean(ClientService.class).getById(reviewInfo.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId).setProjName(reviewInfo.getProjName())
                                .setClientName(client.getClientName()).setProjCode(reviewInfo.getProjCode()).setContractCode(info.getContractCode()));
            }
            //付款
            if (equalsAny(modelKey, PaymentCreateFlow.name(), PaymentActualDetailFlow.name(), PaymentReviewInAdvancedFlow.name())) {
                PaymentBaseInfo info = getBean(PaymentBaseInfoService.class).getById(bizId);
                ContractBaseInfo contractInfo = getBean(ContractBaseInfoService.class).getById(info.getContractId());
                ProjReviewBaseInfo reviewInfo = getBean(ProjReviewBaseInfoService.class).getById(contractInfo.getProjReviewId());
                Client client = getBean(ClientService.class).getById(reviewInfo.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId).setProjName(reviewInfo.getProjName())
                                .setClientName(client.getClientName()).setProjCode(reviewInfo.getProjCode()).setContractCode(contractInfo.getContractCode()));
            }
            //租后调整
            if (equalsAny(modelKey, AfterLeaseExtendFlow.name(), AfterLeaseRepaymentFlow.name())) {
                AfterLeaseAdjustInfo info = getBean(AfterLeaseAdjustInfoServiceImpl.class).getById(bizId);
                Client client = getBean(ClientService.class).getById(info.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId)
                                .setClientName(client.getClientName()).setProjName(info.getProjName()).setProjCode(info.getProjCode()));
            }
            //授信立项
            if (equalsAny(modelKey, GroupCreditEstablishCreateFlow.name(), GroupCreditEstablishModifyFlow.name())) {
                GroupCreditEstablishBaseInfo info = getBean(GroupCreditEstablishBaseInfoService.class).getById(bizId);
                Client client = getBean(ClientService.class).getById(info.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId)
                                .setClientName(client.getClientName()).setProjName(info.getProjName()).setProjCode(info.getProjCode()));
            }
            //授信评审
            if (equalsAny(modelKey, GroupCreditReviewCreateFlow.name(), GroupCreditReviewModifyFlow.name())) {
                GroupCreditReviewBaseInfo info = getBean(GroupCreditReviewBaseInfoService.class).getById(bizId);
                Client client = getBean(ClientService.class).getById(info.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId)
                                .setClientName(client.getClientName()).setProjName(info.getProjName()).setProjCode(info.getProjCode()));
            }
            //租赁审核
            if (equalsAny(modelKey, LeaseCreateFlow.name(), LeaseModifyFlow.name())) {
                LeaseItemInfo leaseItemInfo = getBean(LeaseItemInfoService.class).getById(bizId);
                ProjReviewBaseInfo info = getBean(ProjReviewBaseInfoService.class).getById(leaseItemInfo.getProjReviewId());
                Client client = getBean(ClientService.class).getById(leaseItemInfo.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId)
                                .setClientName(client.getClientName()).setProjName(leaseItemInfo.getProjName()).setProjCode(info.getProjCode()));
            }
            //舆情处理
            if (equalsAny(modelKey, RiskControlOpinionHandleFlow.name(), RiskControlOpinionHandleAfterLaunchFlow.name())) {
                RiskControlOpinionMonitor riskControlOpinionMonitor = getBean(RiskControlOpinionMonitorService.class).getById(bizId);
                //找到对应客户
                if (ObjectUtil.isNotEmpty(riskControlOpinionMonitor)) {
                    Client client = getBean(ClientService.class).getOne(Wrappers.<Client>lambdaQuery()
                            .eq(Client::getUscCode, riskControlOpinionMonitor.getCreditCode())
                            .or(w -> w.eq(Client::getCertNumber, riskControlOpinionMonitor.getCreditCode()))
                            .last(StringUtil.mysqlLimitOne()));
                    if (ObjectUtil.isNotEmpty(client)) {
                        //查找关联项目
                        List<ProjReviewBaseInfo> projReviewBaseInfos = getBean(ProjReviewBaseInfoService.class).listRelationByClients(client.getId());
                        if (CollectionUtil.isNotEmpty(projReviewBaseInfos)) {
                            //保存关联信息
                            getBean(FlowQueryExtraService.class)
                                    .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId)
                                            .setClientName(client.getClientName()).setProjNameInfo(JSON.toJSONString(projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getProjName).collect(Collectors.toList()))));
                        }
                    }

                }
            }
            // 跟踪事项
            if (equalsAny(modelKey, TrackEventCreateFlow.name())) {
                TrackEventInfo info = getBean(TrackEventService.class).getById(bizId);
                Long trackEventBizId = info.getBizId();
                TrackTaskBizSourceEnum bizSourceEnum = TrackTaskBizSourceEnum.find(info.getBizSource());
                if (bizSourceEnum != null) {
                    FlowQueryExtra flowQueryExtra = new FlowQueryExtra();
                    switch (bizSourceEnum) {
                        case CONTRACT:
                            ContractBaseInfo byId = getBean(ContractBaseInfoService.class).getById(trackEventBizId);
                            if (byId != null) {
                                Client client = Optional.ofNullable(getBean(ClientService.class).getById(byId.getClientId())).orElse(new Client());
                                flowQueryExtra.setProjCode(byId.getProjCode()).setProjName(byId.getProjName())
                                        .setContractCode(byId.getContractCode()).setClientName(client.getClientName());
                            }
                            break;
                        case PROJ_REVIEW:
                            ProjReviewBaseInfo projReview = getBean(ProjReviewBaseInfoService.class).getById(trackEventBizId);
                            if (projReview != null) {
                                Client client = Optional.ofNullable(getBean(ClientService.class).getById(projReview.getClientId())).orElse(new Client());
                                flowQueryExtra.setProjCode(projReview.getProjCode()).setProjName(projReview.getProjName()).setClientName(client.getClientName());
                            }
                            break;
                        case AFTER_LEASE:
                            AfterLeaseAdjustInfo afterLease = getBean(AfterLeaseAdjustInfoService.class).getById(trackEventBizId);
                            if (afterLease != null) {
                                Client client = Optional.ofNullable(getBean(ClientService.class).getById(afterLease.getClientId())).orElse(new Client());
                                flowQueryExtra.setProjCode(afterLease.getProjCode()).setProjName(afterLease.getProjName()).setClientName(client.getClientName());
                            }
                            break;
                        case PAYMENT:
                            PaymentBaseInfo payment = getBean(PaymentBaseInfoService.class).getById(trackEventBizId);
                            if (payment != null) {
                                ContractBaseInfo contractBaseInfo = Optional.ofNullable(getBean(ContractBaseInfoService.class).getById(payment.getContractId())).orElse(new ContractBaseInfo());
                                Client client = Optional.ofNullable(getBean(ClientService.class).getById(contractBaseInfo.getClientId())).orElse(new Client());
                                flowQueryExtra.setProjName(contractBaseInfo.getProjName()).setProjCode(contractBaseInfo.getProjCode())
                                        .setClientName(client.getClientName()).setContractCode(payment.getContractCode());
                            }
                            break;
                    }
                    getBean(FlowQueryExtraService.class)
                            .save(flowQueryExtra.setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId));
                }
            }
            //征信查询
            if (equalsAny(modelKey, CreditReportSelectFlow.name())) {
                CreditReportBaseInfo reportDO = getBean(CreditReportBaseInfoService.class).getById(bizId);
                List<CreditReportClientItem> clientItems = getBean(CreditReportClientItemService.class).listByBaseInfoId(bizId);
                getBean(FlowQueryExtraService.class).save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId).setInstanceId(instanceId).setProjName(reportDO.getProjName())
                        .setClientName(clientItems == null ? null : clientItems.get(0).getClientName()).setProjCode(reportDO.getProjCode()));
            }


            //项目资料归档
            if (equalsAny(modelKey, FilingMaterialsApplyFlow.name())) {
                FilingMaterials info = getBean(FilingMaterialsService.class).getById(bizId);
                ContractBaseInfo contractBaseInfo = getBean(ContractBaseInfoService.class).getById(info.getContractId());
                Client client = getBean(ClientService.class).getById(contractBaseInfo.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId)
                                .setClientName(client.getClientName()).setInstanceId(instanceId)
                                .setProjName(contractBaseInfo.getProjName()).setProjCode(info.getProjCode())
                        .setContractCode(contractBaseInfo.getContractCode()));
            }
            //财务-项目利润分配
            if (equalsAny(modelKey, ProjectProfitSharingFlow.name())) {
                FinanceProjectDistribution info = getBean(FinanceProjectDistributionService.class).getById(bizId);
                ContractBaseInfo contractBaseInfo = getBean(ContractBaseInfoService.class).getById(info.getContractId());
                FinanceProjectDistributionBaseInfo baseInfo = getBean(FinanceProjectDistributionBaseInfoService.class).getOneByProjectDistributionId(info.getId());

                Client client = getBean(ClientService.class).getById(contractBaseInfo.getClientId());
                FlowQueryExtra flowQueryExtra = new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId)
                        .setClientName(client.getClientName()).setInstanceId(instanceId)
                        .setProjName(baseInfo.getProjName()).setProjCode(baseInfo.getProjCode())
                        .setContractCode(baseInfo.getContractCode());
                getBean(FlowQueryExtraService.class).save(flowQueryExtra);
            }

            // 保证金退抵
            if (equalsAny(modelKey, MarginFlowAuto.name(), MarginFlowManually.name())) {
                ContractRetreatInfo info = getBean(ContractRetreatInfoService.class).getById(bizId);
                ContractBaseInfo contractBaseInfo = getBean(ContractBaseInfoService.class).getById(info.getContractId());
                Client client = getBean(ClientService.class).getById(contractBaseInfo.getClientId());
                FlowQueryExtra flowQueryExtra = new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId)
                        .setClientName(client.getClientName()).setInstanceId(instanceId)
                        .setProjName(contractBaseInfo.getProjName()).setProjCode(contractBaseInfo.getProjCode())
                        .setContractCode(contractBaseInfo.getContractCode());
                getBean(FlowQueryExtraService.class).save(flowQueryExtra);

            }
            //其他资料归档
            if (equalsAny(modelKey, OtherFilingMaterialsApplyFlow.name())) {
                FilingMaterials info = getBean(OtherFilingMaterialsService.class).getById(bizId);
                ProjReviewBaseInfo projReviewBaseInfo = getBean(ProjReviewBaseInfoService.class).getById(info.getObjectId());
                Client client = getBean(ClientService.class).getById(projReviewBaseInfo.getClientId());
                getBean(FlowQueryExtraService.class)
                        .save(new FlowQueryExtra().setFlowKey(modelKey).setBizId(bizId)
                                .setClientName(client.getClientName()).setInstanceId(instanceId)
                                .setProjName(projReviewBaseInfo.getProjName()).setProjCode(info.getProjCode()));
            }
        } catch (NullPointerException npe) {
            log.warn("额外加工处理新流程开始时间数据未找到. event.context:{}", toJsonStr(event.getProcessStartContext()));

        } catch (Exception e) {
            log.error("额外加工处理新流程开始事件失败. event.context:{}", toJsonStr(event.getProcessStartContext()), e);
        }
    }

    /**
     * 兜底修复process失败的情况；定时执行
     */
    @XxlJob("FixBrokenFlowExtra")
    public void fixBrokenExtra() {
        try {
            log.info("开始：兜底修复process失败的情况");
            int pageSize = 200;
            int maxTimes = 50;
            int index = 1;
            List<FlowQueryExtraMissing> list;
            do {
                List<String> flowKeys = ListUtil.of(ClientModifyFlow.name(), ProjEstablishCreateFlow.name(), ProjEstablishModifyFlow.name(),
                        ProjReviewCreateFlow.name(), ProjReviewModifyFlow.name(), ProjReviewPricingApprovalFlow.name(), ProjReviewPricingModifyApprovalFlow.name(),
                        ContractCreateFlow.name(), ContractModifyFlow.name(), ContractStartRentFlow.name(), ContractAddNewReceiptFlow.name(), ContractEarlySettleFlow.name(),
                        ContractNormalSettleFlow.name(), ContractLPRChangeFlow.name(), ContractExtensionFlow.name(), ContractEarlyRepayFlow.name(), ContractChangeRepayPlanFlow.name(),
                        PaymentCreateFlow.name(), AfterLeaseExtendFlow.name(), AfterLeaseRepaymentFlow.name(), GroupCreditEstablishCreateFlow.name(), GroupCreditEstablishModifyFlow.name(),
                        GroupCreditReviewCreateFlow.name(), GroupCreditReviewModifyFlow.name(), RiskControlOpinionHandleFlow.name(), RiskControlOpinionHandleAfterLaunchFlow.name()
                );
                list = getBaseMapper().selectMissingFlow(new Page<>(1, pageSize), flowKeys);
                for (FlowQueryExtraMissing missing : list) {
                    process(new ProcessStartEvent(this, copyProperties(missing, ProcessStartContext.class)));
                }
            } while (list.size() == pageSize && index++ <= maxTimes);
        } catch (Exception e) {
            log.error("定时兜底修复flow extra失败", e);
        }
    }

    //维护客户和项目评审之间的关系
    public void saveClientProjReviewRelation(Set<String> clientNames, String projName) {
        try {
            if (CollectionUtil.isEmpty(clientNames) || ObjectUtil.isEmpty(projName)) {
                return;
            }
            List<FlowQueryExtra> flowQueryExtras = this.baseMapper.selectList(Wrappers.<FlowQueryExtra>lambdaQuery()
                    .in(FlowQueryExtra::getClientName, clientNames));
            if (CollectionUtil.isNotEmpty(flowQueryExtras)) {
                List<FlowQueryExtra> updateFlowQueryExtras = new ArrayList<>();
                flowQueryExtras.forEach(base -> {
                    List<String> projNames = JSON.parseArray(base.getProjNameInfo(), String.class);
                    if (CollectionUtil.isEmpty(projNames)) {
                        projNames = new ArrayList<>();
                    }
                    if (!projNames.contains(projName)) {
                        projNames.add(projName);
                        base.setProjNameInfo(JSON.toJSONString(projNames));
                        updateFlowQueryExtras.add(base);
                    }
                });
                if (updateFlowQueryExtras.size() > 0) {
                    getBean(FlowQueryExtraService.class).updateBatchById(updateFlowQueryExtras);
                }
            }
        } catch (Exception e) {
            log.error("FlowQueryExtraService saveClientProjReviewRelation error", e);
        }
    }

    public static String format(String content) {
        return content.replace("（", "(").replace("）", ")").replaceAll("\\s+", "").toUpperCase();
    }
}
