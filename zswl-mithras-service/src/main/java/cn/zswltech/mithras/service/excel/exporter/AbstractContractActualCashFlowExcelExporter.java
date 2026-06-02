package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.service.convert.contract.ContractRentConvert;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceiptLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.service.bo.ContractActualCashFlowExporterBO;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.contract.service.lib.contract.ContractRentActualLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/28
 * @description
 */
@Component
public abstract class AbstractContractActualCashFlowExcelExporter extends AbstractCashFlowExcelExporter<ContractActualCashFlowExporterBO> {
    @Resource
    protected ContractRentActualService contractRentActualService;
    @Resource
    protected ContractRentActualLibService contractRentActualLibService;

    @Override
    protected CashFlowRichExcelModel prepare(ContractActualCashFlowExporterBO contractActualCashFlowExporterBO) {
        CashFlowRichExcelModel cashFlowRichExcelModel = new CashFlowRichExcelModel();
        // 填充一些特有数据
        if (contractActualCashFlowExporterBO.isHistory()) {
            ContractReceiptLib contractReceiptLib = contractActualCashFlowExporterBO.getContractReceiptLib();
            List<ContractRentActual> contractRentActualList = contractRentActualLibService.listByReceiptVersion(contractReceiptLib.getOriginId(), contractActualCashFlowExporterBO.getVersion());
            if (CollectionUtil.isNotEmpty(contractRentActualList)) {
                List<CashFlowExcelModel> cashFlowExcelModelList = contractRentActualList.stream().map(ContractRentConvert::toContractRentActualExcelModel).collect(Collectors.toList());
                cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            }
            cashFlowRichExcelModel.setStartRentDate(contractActualCashFlowExporterBO.getContractBaseInfoLib().getEstimatedLeaseDate());
        } else {
            ContractReceipt contractReceipt = contractActualCashFlowExporterBO.getContractReceipt();
            List<ContractRentActual> contractRentActualList = contractRentActualService.listByReceipt(contractReceipt.getId());
            if (CollectionUtil.isNotEmpty(contractRentActualList)) {
                List<CashFlowExcelModel> cashFlowExcelModelList = contractRentActualList.stream().map(ContractRentConvert::toContractRentActualExcelModel).collect(Collectors.toList());
                cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            }
            cashFlowRichExcelModel.setStartRentDate(contractActualCashFlowExporterBO.getContractBaseInfo().getEstimatedLeaseDate());
        }
        if (CollectionUtil.isNotEmpty(cashFlowRichExcelModel.getCashFlowList())) {
            cashFlowRichExcelModel.getCashFlowList().sort(Comparator.comparing(CashFlowExcelModel::getCashFlowPhase));
        }
        return cashFlowRichExcelModel;
    }
}
