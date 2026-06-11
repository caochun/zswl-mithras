package cn.zswltech.mithras.others.service.common;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.mapper.BizProcessDataMapper;
import cn.zswltech.mithras.afterlease.mapper.AfterLeaseAdjustInfoMapper;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckExternalQueryMapper;
import cn.zswltech.mithras.afterlease.mapper.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.afterlease.mapper.CollectionPenaltyReductionInfoMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.workflow.model.BizProcessData;
import cn.zswltech.mithras.afterlease.mapper.model.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.mapper.model.CollectionPenaltyReductionInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.review.mapper.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * TODO
 *
 * @author wangchuanhao
 * @date 2022/12/9 2:21 PM
 */
public class BizProcessDataFix extends ApplicationTest {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;
    @Resource
    private HistoryService historyService;
    @Resource
    private BizProcessDataMapper bizProcessDataMapper;
    @Resource
    private AfterLeaseAdjustInfoMapper afterLeaseAdjustInfoMapper;
    @Resource
    private NewAfterLeaseCheckExternalQueryMapper newAfterLeaseCheckExternalQueryMapper;
    @Resource
    private NewAfterLeaseCheckPlanClientMapper newAfterLeaseCheckPlanClientMapper;
    @Resource
    private CollectionPenaltyReductionInfoMapper collectionPenaltyReductionInfoMapper;

    @Test
    public void fix() {
        List<HistoricProcessInstance> processInstanceList = historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance processInstance : processInstanceList) {
            ProcessModelTypeEnum modelTypeEnum = ProcessModelTypeEnum.getByName(processInstance.getProcessDefinitionKey());
            if (Objects.isNull(modelTypeEnum)) {
                continue;
            }
            Long clientId = null;
            switch (modelTypeEnum) {
                case ClientModifyFlow:
                    clientId = Long.valueOf(processInstance.getBusinessKey());
                    break;
                case ProjEstablishCreateFlow:
                case ProjEstablishModifyFlow:
                    ProjEstablishBaseInfo projEstablishBaseInfo = projEstablishBaseInfoMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(projEstablishBaseInfo).map(ProjEstablishBaseInfo::getClientId).orElse(null);
                    break;
                case ProjReviewCreateFlow:
                case ProjReviewModifyFlow:
                case ProjReviewPricingApprovalFlow:
                    ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(projReviewBaseInfo).map(ProjReviewBaseInfo::getClientId).orElse(null);
                    break;
                case ContractCreateFlow:
                case ContractModifyFlow:
                case ContractStartRentFlow:
                case ContractAddNewReceiptFlow:
                case ContractEarlySettleFlow:
                case ContractNormalSettleFlow:
                case ContractLPRChangeFlow:
                case ContractExtensionFlow:
                case ContractEarlyRepayFlow:
                case ContractChangeRepayPlanFlow:
                    ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(contractBaseInfo).map(ContractBaseInfo::getClientId).orElse(null);
                    break;
                case PaymentCreateFlow:
                    PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(paymentBaseInfo).map(PaymentBaseInfo::getClientId).orElse(null);
                    break;
                case AfterLeaseExtendFlow:
                case AfterLeaseRepaymentFlow:
                    AfterLeaseAdjustInfo afterLeaseAdjustInfo = afterLeaseAdjustInfoMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(afterLeaseAdjustInfo).map(AfterLeaseAdjustInfo::getClientId).orElse(null);
                    break;
                case GroupCreditEstablishCreateFlow:
                case GroupCreditEstablishModifyFlow:
                    GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = groupCreditEstablishBaseInfoMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(groupCreditEstablishBaseInfo).map(GroupCreditEstablishBaseInfo::getClientId).orElse(null);
                    break;
                case GroupCreditReviewCreateFlow:
                case GroupCreditReviewModifyFlow:
                    GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(groupCreditReviewBaseInfo).map(GroupCreditReviewBaseInfo::getClientId).orElse(null);
                    break;
                case NewAfterLeaseCheckExternalQueryFlow:
                    NewAfterLeaseCheckExternalQuery query = newAfterLeaseCheckExternalQueryMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(query).map(NewAfterLeaseCheckExternalQuery::getClientId).orElse(null);
                    break;
                case NewAfterLeaseCheckReportFlow:
                    NewAfterLeaseCheckPlanClient checkPlanClient = newAfterLeaseCheckPlanClientMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    clientId = Optional.ofNullable(checkPlanClient).map(NewAfterLeaseCheckPlanClient::getClientId).orElse(null);
                    break;
                case RentCollectionExemptionFlow:
                    CollectionPenaltyReductionInfo collectionPenaltyReductionInfo = collectionPenaltyReductionInfoMapper.selectById(Long.valueOf(processInstance.getBusinessKey()));
                    if (Objects.nonNull(collectionPenaltyReductionInfo)) {
                        ContractBaseInfo cb = contractBaseInfoMapper.selectById(collectionPenaltyReductionInfo.getContractId());
                        clientId = Optional.ofNullable(cb).map(ContractBaseInfo::getClientId).orElse(null);
                    }
                    break;
            }
            if (clientId != null) {
                bizProcessDataMapper.update(null, new LambdaUpdateWrapper<BizProcessData>().eq(BizProcessData::getProcessInstanceId, processInstance.getId()).set(BizProcessData::getClientId, clientId));
            }
        }
    }

}
