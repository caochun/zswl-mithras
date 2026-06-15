package cn.zswltech.mithras.finance.service.profitcalculate;

import cn.zswltech.mithras.collection.model.BillManagement;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.kpi.enums.KpiProjectClassifyEnum;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;

import java.time.LocalDate;
import java.util.List;

public interface ProfitCalculateSupportPort {

    KpiProjectClassifyEnum ensureProjClassify(String clientRiskControlIndustryClassify);

    FinanceProjectProfitDetail getLatestProfitDetailByContractId(Long contractId);

    FinanceProjectProfitDetail getLastYearProfitDetail(Long contractId);

    FtpInterestBaseInfo getFtpInterestByReceiptId(Long receiptId);

    FtpInterestDetailRecord getLatestFtpInterestRecord(Long ftpInterestId, LocalDate interestDate);

    List<BillManagement> listBillsByReceiptId(Long receiptId);

    PaymentActualDetail getEarliestPayment(Long contractId);

    List<PaymentBaseInfo> listEffectPaymentByContractId(Long contractId);

    Long getMarginCollectionAmountByContractId(Long contractId);
}
