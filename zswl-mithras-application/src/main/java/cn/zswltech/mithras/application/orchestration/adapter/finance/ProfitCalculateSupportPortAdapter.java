package cn.zswltech.mithras.application.orchestration.adapter.finance;

import cn.zswltech.mithras.collection.model.BillManagement;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.finance.service.profitcalculate.ProfitCalculateSupportPort;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.kpi.enums.KpiProjectClassifyEnum;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.application.orchestration.collection.BillManagementService;
import cn.zswltech.mithras.application.orchestration.finance.FinanceProjectProfitDetailService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestDetailRecordService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Component
public class ProfitCalculateSupportPortAdapter implements ProfitCalculateSupportPort {
    @Resource
    private KpiProjectDistributionBaseInfoService kpiProjectDistributionBaseInfoService;
    @Resource
    private FinanceProjectProfitDetailService financeProjectProfitDetailService;
    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private FtpInterestDetailRecordService ftpInterestDetailRecordService;
    @Resource
    private BillManagementService billManagementService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public KpiProjectClassifyEnum ensureProjClassify(String clientRiskControlIndustryClassify) {
        return kpiProjectDistributionBaseInfoService.ensureProjClassify(clientRiskControlIndustryClassify);
    }

    @Override
    public FinanceProjectProfitDetail getLatestProfitDetailByContractId(Long contractId) {
        return financeProjectProfitDetailService.getLatestOneByContractId(contractId);
    }

    @Override
    public FinanceProjectProfitDetail getLastYearProfitDetail(Long contractId) {
        return financeProjectProfitDetailService.getOne(Wrappers.<FinanceProjectProfitDetail>lambdaQuery()
                .eq(FinanceProjectProfitDetail::getContractId, contractId)
                .eq(FinanceProjectProfitDetail::getYear, LocalDate.now().getYear() - 1)
                .eq(FinanceProjectProfitDetail::getMonth, 12));
    }

    @Override
    public FtpInterestBaseInfo getFtpInterestByReceiptId(Long receiptId) {
        return ftpInterestBaseInfoService.getOneByReceiptId(receiptId);
    }

    @Override
    public FtpInterestDetailRecord getLatestFtpInterestRecord(Long ftpInterestId, LocalDate interestDate) {
        return ftpInterestDetailRecordService.getLatestRecord(ftpInterestId, interestDate);
    }

    @Override
    public List<BillManagement> listBillsByReceiptId(Long receiptId) {
        return billManagementService.listByReceiptId(receiptId);
    }

    @Override
    public PaymentActualDetail getEarliestPayment(Long contractId) {
        return paymentActualDetailService.getEarliestPayment(contractId);
    }

    @Override
    public List<PaymentBaseInfo> listEffectPaymentByContractId(Long contractId) {
        return paymentBaseInfoService.listEffectPaymentByContractId(contractId);
    }

    @Override
    public Long getMarginCollectionAmountByContractId(Long contractId) {
        return marginBaseInfoService.getLatestCollectionAmountByContractId(contractId);
    }
}
