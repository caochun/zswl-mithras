package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.receipt.ContractReceiptQueryActualTaxREQ;
import cn.zswltech.mithras.dto.contract.receipt.ContractReceiptUpdateStartDateREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractReceiptComputeActualTaxRSP;
import cn.zswltech.mithras.service.mapper.model.contract.ContractReceipt;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
public interface ContractReceiptService extends IService<ContractReceipt> {
    void removeReceiptById(Long receiptId);

    Map<Long, ContractReceipt> getMapByIds(List<Long> ids, String version);

    Map<Long, Boolean> checkCanRemove(Long contractId);

    List<ContractReceipt> listByContractId(Long contractId);

    Integer getNextSequenceByContractId(Long contractId);

    @Deprecated
    ContractReceipt getOneByPaymentCode(String paymentCode);

    void checkActualIrrByContractId(Long contractId);

    R<List<ContractReceiptComputeActualTaxRSP>> computeFinancialCosts(ContractReceiptQueryActualTaxREQ req, boolean flag);

    R<Void> generate(ContractReceiptQueryActualTaxREQ req);

    /**
     * 编辑区-更新实际起租日期
     * @param req 请求参数
     * @return
     */
    void updateActualStartDate(ContractReceiptUpdateStartDateREQ req);

    /**
     * 流程中-更新实际起租日期
     * @param req 请求参数
     * @return
     */
    void updateActualLeaseDate(ContractReceiptUpdateStartDateREQ req);

    ContractReceipt getOntByReceiptCode(String receiptCode);

    BigDecimal calculateCombinedIRR(Long contractId, String version);
}
