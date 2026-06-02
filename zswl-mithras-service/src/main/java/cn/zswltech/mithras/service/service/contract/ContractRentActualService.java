package cn.zswltech.mithras.service.service.contract;

import cn.zswltech.mithras.dto.contract.rent.ContractRentActualExportREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualImportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
public interface ContractRentActualService extends IService<ContractRentActual> {
    void importExcel(ContractRentActualImportREQ req) throws Exception;

    void importExcelBySystem(ContractRentActualImportREQ req, List<CashFlowExcelModel> actualRentList, ContractBaseInfo contractBaseInfo);

    Map<Long, List<ContractRentActual>> getMapGroupByReceipt(Long contractId, String version);

    List<ContractRentActual> listByContract(Long contractId);

    //获取合同下第一张有效的租金表
    List<ContractRentActual> firstRentByContract(Long contractId);

    List<ContractRentActual> listByReceipt(Long receiptId);

    List<ContractRentActual> listByReceipts(Collection<Long> receiptIds);

    List<ContractRentActual> listVerifiedRentActual(Long contractId);

    void remove(Long contractId, Long receipt);

    void exportExcel(boolean isHistory, ContractRentActualExportREQ req, OutputStream outputStream) throws Exception;

    void exportRichExcel(boolean isHistory, ContractRentActualExportREQ req, OutputStream outputStream) throws Exception;

    List<ContractRentActual> listByContractIds(Collection<Long> contractIds);

    /**
     *查找合同下实际租金表最晚计划收取时间
     **/
    LocalDate getContractRentExpirationDate(Long contractId);

    IRRCalculateResultRSP calculateIRR(Long receiptId, boolean showDetailExcel);

    ContractReceipt doContractReceipt(ContractRentActualImportREQ req, ContractBaseInfo contractBaseInfo);
}
