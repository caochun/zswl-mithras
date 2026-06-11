package cn.zswltech.mithras.others.hand.extract.contract;

import cn.zswltech.mithras.report.mapper.draft.CrAccountDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrActualRepayDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrClientDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrFiveClassDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrGuarantorDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrMortgageDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrOverdueRecordDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrPledgeDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrRepayPlanDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrSpecialTradeDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrActualRepayDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrFiveClassDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrGuarantorDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrMortgageDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrOverdueRecordDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrPledgeDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrRepayPlanDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrSpecialTradeDraft;
import cn.zswltech.mithras.report.mapper.formal.CrAccountMapper;
import cn.zswltech.mithras.report.mapper.formal.CrActualRepayMapper;
import cn.zswltech.mithras.report.mapper.formal.CrClientMapper;
import cn.zswltech.mithras.report.mapper.formal.CrFiveClassMapper;
import cn.zswltech.mithras.report.mapper.formal.CrGuarantorMapper;
import cn.zswltech.mithras.report.mapper.formal.CrMortgageMapper;
import cn.zswltech.mithras.report.mapper.formal.CrOverdueRecordMapper;
import cn.zswltech.mithras.report.mapper.formal.CrPledgeMapper;
import cn.zswltech.mithras.report.mapper.formal.CrRepayPlanMapper;
import cn.zswltech.mithras.report.mapper.formal.CrSpecialTradeMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrAccount;
import cn.zswltech.mithras.report.mapper.formal.model.CrActualRepay;
import cn.zswltech.mithras.report.mapper.formal.model.CrFiveClass;
import cn.zswltech.mithras.report.mapper.formal.model.CrGuarantor;
import cn.zswltech.mithras.report.mapper.formal.model.CrMortgage;
import cn.zswltech.mithras.report.mapper.formal.model.CrOverdueRecord;
import cn.zswltech.mithras.report.mapper.formal.model.CrPledge;
import cn.zswltech.mithras.report.mapper.formal.model.CrRepayPlan;
import cn.zswltech.mithras.report.mapper.formal.model.CrSpecialTrade;
import cn.zswltech.mithras.report.mapper.fullsnap.CrAccountFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrActualRepayFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrClientFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrFiveClassFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrGuarantorFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrMortgageFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrOverdueRecordFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrPledgeFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrRepayPlanFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrSpecialTradeFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrAccountFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrActualRepayFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrFiveClassFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrGuarantorFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrMortgageFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrOverdueRecordFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrPledgeFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrRepayPlanFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrSpecialTradeFullSnap;
import cn.zswltech.mithras.report.mapper.procsnap.CrAccountProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrActualRepayProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrClientProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrFiveClassProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrGuarantorProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrMortgageProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrOverdueRecordProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrPledgeProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrRepayPlanProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrSpecialTradeProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrAccountProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrActualRepayProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrFiveClassProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrGuarantorProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrMortgageProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrOverdueRecordProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrPledgeProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrRepayPlanProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrSpecialTradeProcSnap;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 征信报送修复
 *
 * @author wangchuanhao
 * @date 2023/2/7 10:02 AM
 */
@Component
public class CreditReportDataFix {

//    @Resource
//    private TransactionTemplate transactionTemplate;

    @Resource
    private CrAccountMapper crAccountMapper;
    @Resource
    private CrAccountDraftMapper crAccountDraftMapper;
    @Resource
    private CrAccountProcSnapMapper crAccountProcSnapMapper;
    @Resource
    private CrAccountFullSnapMapper crAccountFullSnapMapper;

    @Resource
    private CrClientMapper crClientMapper;
    @Resource
    private CrClientDraftMapper crClientDraftMapper;
    @Resource
    private CrClientProcSnapMapper crClientProcSnapMapper;
    @Resource
    private CrClientFullSnapMapper crClientFullSnapMapper;

    @Resource
    private CrPledgeMapper crPledgeMapper;
    @Resource
    private CrPledgeDraftMapper crPledgeDraftMapper;
    @Resource
    private CrPledgeProcSnapMapper crPledgeProcSnapMapper;
    @Resource
    private CrPledgeFullSnapMapper crPledgeFullSnapMapper;

    @Resource
    private CrGuarantorMapper crGuarantorMapper;
    @Resource
    private CrGuarantorDraftMapper crGuarantorDraftMapper;
    @Resource
    private CrGuarantorProcSnapMapper crGuarantorProcSnapMapper;
    @Resource
    private CrGuarantorFullSnapMapper crGuarantorFullSnapMapper;

    @Resource
    private CrMortgageMapper crMortgageMapper;
    @Resource
    private CrMortgageDraftMapper crMortgageDraftMapper;
    @Resource
    private CrMortgageProcSnapMapper crMortgageProcSnapMapper;
    @Resource
    private CrMortgageFullSnapMapper crMortgageFullSnapMapper;

    @Resource
    private CrRepayPlanMapper crRepayPlanMapper;
    @Resource
    private CrRepayPlanDraftMapper crRepayPlanDraftMapper;
    @Resource
    private CrRepayPlanProcSnapMapper crRepayPlanProcSnapMapper;
    @Resource
    private CrRepayPlanFullSnapMapper crRepayPlanFullSnapMapper;

    @Resource
    private CrActualRepayMapper crActualRepayMapper;
    @Resource
    private CrActualRepayDraftMapper crActualRepayDraftMapper;
    @Resource
    private CrActualRepayProcSnapMapper crActualRepayProcSnapMapper;
    @Resource
    private CrActualRepayFullSnapMapper crActualRepayFullSnapMapper;

    @Resource
    private CrOverdueRecordMapper crOverdueRecordMapper;
    @Resource
    private CrOverdueRecordDraftMapper crOverdueRecordDraftMapper;
    @Resource
    private CrOverdueRecordProcSnapMapper crOverdueRecordProcSnapMapper;
    @Resource
    private CrOverdueRecordFullSnapMapper crOverdueRecordFullSnapMapper;

    @Resource
    private CrSpecialTradeMapper crSpecialTradeMapper;
    @Resource
    private CrSpecialTradeDraftMapper crSpecialTradeDraftMapper;
    @Resource
    private CrSpecialTradeProcSnapMapper crSpecialTradeProcSnapMapper;
    @Resource
    private CrSpecialTradeFullSnapMapper crSpecialTradeFullSnapMapper;

    @Resource
    private CrFiveClassMapper crFiveClassMapper;
    @Resource
    private CrFiveClassDraftMapper crFiveClassDraftMapper;
    @Resource
    private CrFiveClassProcSnapMapper crFiveClassProcSnapMapper;
    @Resource
    private CrFiveClassFullSnapMapper crFiveClassFullSnapMapper;

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    /**
     * 修复以下合同的征信报送数据对应的 编号
     * @param contractIdList
     */
    public void fixCode(Collection<Long> contractIdList) {
        if (CollectionUtils.isEmpty(contractIdList)) {
            return;
        }
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getContractId, contractIdList)
        );
        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
            fixAccountCode(paymentBaseInfo);
            fixPledgeCode(paymentBaseInfo);
            fixGuarantorCode(paymentBaseInfo);
            fixMortgageCode(paymentBaseInfo);
            fixRepayPlanCode(paymentBaseInfo);
            fixActualRepayCode(paymentBaseInfo);
            fixOverdueRecordCode(paymentBaseInfo);
            fixSpecialTradeCode(paymentBaseInfo);
            fixFiveClassCode(paymentBaseInfo);
        }
    }

    private void fixAccountCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrAccount> accountWrapper = new LambdaUpdateWrapper<>();
        accountWrapper.eq(CrAccount::getPaymentId, paymentBaseInfo.getId());
        accountWrapper.set(CrAccount::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crAccountMapper.update(null, accountWrapper);
        LambdaUpdateWrapper<CrAccountDraft> accountDraftWrapper = new LambdaUpdateWrapper<>();
        accountDraftWrapper.eq(CrAccountDraft::getPaymentId, paymentBaseInfo.getId());
        accountDraftWrapper.set(CrAccountDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crAccountDraftMapper.update(null, accountDraftWrapper);
        LambdaUpdateWrapper<CrAccountProcSnap> accountProcSnapWrapper = new LambdaUpdateWrapper<>();
        accountProcSnapWrapper.eq(CrAccountProcSnap::getPaymentId, paymentBaseInfo.getId());
        accountProcSnapWrapper.set(CrAccountProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crAccountProcSnapMapper.update(null, accountProcSnapWrapper);
        LambdaUpdateWrapper<CrAccountFullSnap> accountFullSnapWrapper = new LambdaUpdateWrapper<>();
        accountFullSnapWrapper.eq(CrAccountFullSnap::getPaymentId, paymentBaseInfo.getId());
        accountFullSnapWrapper.set(CrAccountFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crAccountFullSnapMapper.update(null, accountFullSnapWrapper);
    }

    private void fixPledgeCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrPledge> pledgeWrapper = new LambdaUpdateWrapper<>();
        pledgeWrapper.eq(CrPledge::getPaymentId, paymentBaseInfo.getId());
        pledgeWrapper.set(CrPledge::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crPledgeMapper.update(null, pledgeWrapper);
        LambdaUpdateWrapper<CrPledgeDraft> pledgeDraftWrapper = new LambdaUpdateWrapper<>();
        pledgeDraftWrapper.eq(CrPledgeDraft::getPaymentId, paymentBaseInfo.getId());
        pledgeDraftWrapper.set(CrPledgeDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crPledgeDraftMapper.update(null, pledgeDraftWrapper);
        LambdaUpdateWrapper<CrPledgeProcSnap> pledgeProcSnapWrapper = new LambdaUpdateWrapper<>();
        pledgeProcSnapWrapper.eq(CrPledgeProcSnap::getPaymentId, paymentBaseInfo.getId());
        pledgeProcSnapWrapper.set(CrPledgeProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crPledgeProcSnapMapper.update(null, pledgeProcSnapWrapper);
        LambdaUpdateWrapper<CrPledgeFullSnap> pledgeFullSnapWrapper = new LambdaUpdateWrapper<>();
        pledgeFullSnapWrapper.eq(CrPledgeFullSnap::getPaymentId, paymentBaseInfo.getId());
        pledgeFullSnapWrapper.set(CrPledgeFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crPledgeFullSnapMapper.update(null, pledgeFullSnapWrapper);


    }

    private void fixGuarantorCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrGuarantor> guarantorWrapper = new LambdaUpdateWrapper<>();
        guarantorWrapper.eq(CrGuarantor::getPaymentId, paymentBaseInfo.getId());
        guarantorWrapper.set(CrGuarantor::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crGuarantorMapper.update(null, guarantorWrapper);
        LambdaUpdateWrapper<CrGuarantorDraft> guarantorDraftWrapper = new LambdaUpdateWrapper<>();
        guarantorDraftWrapper.eq(CrGuarantorDraft::getPaymentId, paymentBaseInfo.getId());
        guarantorDraftWrapper.set(CrGuarantorDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crGuarantorDraftMapper.update(null, guarantorDraftWrapper);
        LambdaUpdateWrapper<CrGuarantorProcSnap> guarantorProcSnapWrapper = new LambdaUpdateWrapper<>();
        guarantorProcSnapWrapper.eq(CrGuarantorProcSnap::getPaymentId, paymentBaseInfo.getId());
        guarantorProcSnapWrapper.set(CrGuarantorProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crGuarantorProcSnapMapper.update(null, guarantorProcSnapWrapper);
        LambdaUpdateWrapper<CrGuarantorFullSnap> guarantorFullSnapWrapper = new LambdaUpdateWrapper<>();
        guarantorFullSnapWrapper.eq(CrGuarantorFullSnap::getPaymentId, paymentBaseInfo.getId());
        guarantorFullSnapWrapper.set(CrGuarantorFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crGuarantorFullSnapMapper.update(null, guarantorFullSnapWrapper);
    }

    private void fixMortgageCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrMortgage> mortgageWrapper = new LambdaUpdateWrapper<>();
        mortgageWrapper.eq(CrMortgage::getPaymentId, paymentBaseInfo.getId());
        mortgageWrapper.set(CrMortgage::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crMortgageMapper.update(null, mortgageWrapper);
        LambdaUpdateWrapper<CrMortgageDraft> mortgageDraftWrapper = new LambdaUpdateWrapper<>();
        mortgageDraftWrapper.eq(CrMortgageDraft::getPaymentId, paymentBaseInfo.getId());
        mortgageDraftWrapper.set(CrMortgageDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crMortgageDraftMapper.update(null, mortgageDraftWrapper);
        LambdaUpdateWrapper<CrMortgageProcSnap> mortgageProcSnapWrapper = new LambdaUpdateWrapper<>();
        mortgageProcSnapWrapper.eq(CrMortgageProcSnap::getPaymentId, paymentBaseInfo.getId());
        mortgageProcSnapWrapper.set(CrMortgageProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crMortgageProcSnapMapper.update(null, mortgageProcSnapWrapper);
        LambdaUpdateWrapper<CrMortgageFullSnap> mortgageFullSnapWrapper = new LambdaUpdateWrapper<>();
        mortgageFullSnapWrapper.eq(CrMortgageFullSnap::getPaymentId, paymentBaseInfo.getId());
        mortgageFullSnapWrapper.set(CrMortgageFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crMortgageFullSnapMapper.update(null, mortgageFullSnapWrapper);
    }

    private void fixRepayPlanCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrRepayPlan> repayPlanWrapper = new LambdaUpdateWrapper<>();
        repayPlanWrapper.eq(CrRepayPlan::getPaymentId, paymentBaseInfo.getId());
        repayPlanWrapper.set(CrRepayPlan::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crRepayPlanMapper.update(null, repayPlanWrapper);
        LambdaUpdateWrapper<CrRepayPlanDraft> repayPlanDraftWrapper = new LambdaUpdateWrapper<>();
        repayPlanDraftWrapper.eq(CrRepayPlanDraft::getPaymentId, paymentBaseInfo.getId());
        repayPlanDraftWrapper.set(CrRepayPlanDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crRepayPlanDraftMapper.update(null, repayPlanDraftWrapper);
        LambdaUpdateWrapper<CrRepayPlanProcSnap> repayPlanProcSnapWrapper = new LambdaUpdateWrapper<>();
        repayPlanProcSnapWrapper.eq(CrRepayPlanProcSnap::getPaymentId, paymentBaseInfo.getId());
        repayPlanProcSnapWrapper.set(CrRepayPlanProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crRepayPlanProcSnapMapper.update(null, repayPlanProcSnapWrapper);
        LambdaUpdateWrapper<CrRepayPlanFullSnap> repayPlanFullSnapWrapper = new LambdaUpdateWrapper<>();
        repayPlanFullSnapWrapper.eq(CrRepayPlanFullSnap::getPaymentId, paymentBaseInfo.getId());
        repayPlanFullSnapWrapper.set(CrRepayPlanFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crRepayPlanFullSnapMapper.update(null, repayPlanFullSnapWrapper);
    }

    private void fixActualRepayCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrActualRepay> actualRepayWrapper = new LambdaUpdateWrapper<>();
        actualRepayWrapper.eq(CrActualRepay::getPaymentId, paymentBaseInfo.getId());
        actualRepayWrapper.set(CrActualRepay::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crActualRepayMapper.update(null, actualRepayWrapper);
        LambdaUpdateWrapper<CrActualRepayDraft> actualRepayDraftWrapper = new LambdaUpdateWrapper<>();
        actualRepayDraftWrapper.eq(CrActualRepayDraft::getPaymentId, paymentBaseInfo.getId());
        actualRepayDraftWrapper.set(CrActualRepayDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crActualRepayDraftMapper.update(null, actualRepayDraftWrapper);
        LambdaUpdateWrapper<CrActualRepayProcSnap> actualRepayProcSnapWrapper = new LambdaUpdateWrapper<>();
        actualRepayProcSnapWrapper.eq(CrActualRepayProcSnap::getPaymentId, paymentBaseInfo.getId());
        actualRepayProcSnapWrapper.set(CrActualRepayProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crActualRepayProcSnapMapper.update(null, actualRepayProcSnapWrapper);
        LambdaUpdateWrapper<CrActualRepayFullSnap> actualRepayFullSnapWrapper = new LambdaUpdateWrapper<>();
        actualRepayFullSnapWrapper.eq(CrActualRepayFullSnap::getPaymentId, paymentBaseInfo.getId());
        actualRepayFullSnapWrapper.set(CrActualRepayFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crActualRepayFullSnapMapper.update(null, actualRepayFullSnapWrapper);
    }

    private void fixOverdueRecordCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrOverdueRecord> overdueRecordWrapper = new LambdaUpdateWrapper<>();
        overdueRecordWrapper.eq(CrOverdueRecord::getPaymentId, paymentBaseInfo.getId());
        overdueRecordWrapper.set(CrOverdueRecord::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crOverdueRecordMapper.update(null, overdueRecordWrapper);
        LambdaUpdateWrapper<CrOverdueRecordDraft> overdueRecordDraftWrapper = new LambdaUpdateWrapper<>();
        overdueRecordDraftWrapper.eq(CrOverdueRecordDraft::getPaymentId, paymentBaseInfo.getId());
        overdueRecordDraftWrapper.set(CrOverdueRecordDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crOverdueRecordDraftMapper.update(null, overdueRecordDraftWrapper);
        LambdaUpdateWrapper<CrOverdueRecordProcSnap> overdueRecordProcSnapWrapper = new LambdaUpdateWrapper<>();
        overdueRecordProcSnapWrapper.eq(CrOverdueRecordProcSnap::getPaymentId, paymentBaseInfo.getId());
        overdueRecordProcSnapWrapper.set(CrOverdueRecordProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crOverdueRecordProcSnapMapper.update(null, overdueRecordProcSnapWrapper);
        LambdaUpdateWrapper<CrOverdueRecordFullSnap> overdueRecordFullSnapWrapper = new LambdaUpdateWrapper<>();
        overdueRecordFullSnapWrapper.eq(CrOverdueRecordFullSnap::getPaymentId, paymentBaseInfo.getId());
        overdueRecordFullSnapWrapper.set(CrOverdueRecordFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crOverdueRecordFullSnapMapper.update(null, overdueRecordFullSnapWrapper);
    }

    private void fixSpecialTradeCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrSpecialTrade> specialTradeWrapper = new LambdaUpdateWrapper<>();
        specialTradeWrapper.eq(CrSpecialTrade::getPaymentId, paymentBaseInfo.getId());
        specialTradeWrapper.set(CrSpecialTrade::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crSpecialTradeMapper.update(null, specialTradeWrapper);
        LambdaUpdateWrapper<CrSpecialTradeDraft> specialTradeDraftWrapper = new LambdaUpdateWrapper<>();
        specialTradeDraftWrapper.eq(CrSpecialTradeDraft::getPaymentId, paymentBaseInfo.getId());
        specialTradeDraftWrapper.set(CrSpecialTradeDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crSpecialTradeDraftMapper.update(null, specialTradeDraftWrapper);
        LambdaUpdateWrapper<CrSpecialTradeProcSnap> specialTradeProcSnapWrapper = new LambdaUpdateWrapper<>();
        specialTradeProcSnapWrapper.eq(CrSpecialTradeProcSnap::getPaymentId, paymentBaseInfo.getId());
        specialTradeProcSnapWrapper.set(CrSpecialTradeProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crSpecialTradeProcSnapMapper.update(null, specialTradeProcSnapWrapper);
        LambdaUpdateWrapper<CrSpecialTradeFullSnap> specialTradeFullSnapWrapper = new LambdaUpdateWrapper<>();
        specialTradeFullSnapWrapper.eq(CrSpecialTradeFullSnap::getPaymentId, paymentBaseInfo.getId());
        specialTradeFullSnapWrapper.set(CrSpecialTradeFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crSpecialTradeFullSnapMapper.update(null, specialTradeFullSnapWrapper);
    }

    private void fixFiveClassCode(PaymentBaseInfo paymentBaseInfo) {
        LambdaUpdateWrapper<CrFiveClass> fiveClassWrapper = new LambdaUpdateWrapper<>();
        fiveClassWrapper.eq(CrFiveClass::getPaymentId, paymentBaseInfo.getId());
        fiveClassWrapper.set(CrFiveClass::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crFiveClassMapper.update(null, fiveClassWrapper);
        LambdaUpdateWrapper<CrFiveClassDraft> fiveClassDraftWrapper = new LambdaUpdateWrapper<>();
        fiveClassDraftWrapper.eq(CrFiveClassDraft::getPaymentId, paymentBaseInfo.getId());
        fiveClassDraftWrapper.set(CrFiveClassDraft::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crFiveClassDraftMapper.update(null, fiveClassDraftWrapper);
        LambdaUpdateWrapper<CrFiveClassProcSnap> fiveClassProcSnapWrapper = new LambdaUpdateWrapper<>();
        fiveClassProcSnapWrapper.eq(CrFiveClassProcSnap::getPaymentId, paymentBaseInfo.getId());
        fiveClassProcSnapWrapper.set(CrFiveClassProcSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crFiveClassProcSnapMapper.update(null, fiveClassProcSnapWrapper);
        LambdaUpdateWrapper<CrFiveClassFullSnap> fiveClassFullSnapWrapper = new LambdaUpdateWrapper<>();
        fiveClassFullSnapWrapper.eq(CrFiveClassFullSnap::getPaymentId, paymentBaseInfo.getId());
        fiveClassFullSnapWrapper.set(CrFiveClassFullSnap::getPaymentApplyCode, paymentBaseInfo.getPaymentCode());
        crFiveClassFullSnapMapper.update(null, fiveClassFullSnapWrapper);
    }

}
