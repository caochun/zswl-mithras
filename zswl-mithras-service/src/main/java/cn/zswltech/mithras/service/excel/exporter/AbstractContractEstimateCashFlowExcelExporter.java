package cn.zswltech.mithras.service.excel.exporter;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.service.convert.contract.ContractRentConvert;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.CashFlowRichExcelModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.contract.application.dto.ContractEstimateCashFlowExporterBO;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
public abstract class AbstractContractEstimateCashFlowExcelExporter extends AbstractCashFlowExcelExporter<ContractEstimateCashFlowExporterBO> {
    @Override
    protected CashFlowRichExcelModel prepare(ContractEstimateCashFlowExporterBO contractEstimateCashFlowExporterBO) {
        ContractBaseInfo contractBaseInfo = contractEstimateCashFlowExporterBO.getContractBaseInfo();
        CashFlowRichExcelModel cashFlowRichExcelModel = new CashFlowRichExcelModel();
        // 填充一些特有数据
        cashFlowRichExcelModel.setStartRentDate(contractBaseInfo.getEstimatedLeaseDate());
        // 查询概算租金表
        List<ContractRentEstimate> contractRentEstimateList = contractEstimateCashFlowExporterBO.getContractRentEstimateList();
        if (CollectionUtil.isNotEmpty(contractRentEstimateList)) {
            List<CashFlowExcelModel> cashFlowExcelModelList = contractRentEstimateList.stream().map(ContractRentConvert::toCashFlowExcelModel).collect(Collectors.toList());
            cashFlowRichExcelModel.setCashFlowList(new LinkedList<>(cashFlowExcelModelList));
            cashFlowRichExcelModel.getCashFlowList().sort(Comparator.comparing(CashFlowExcelModel::getCashFlowPhase));
        }
        return cashFlowRichExcelModel;
    }
}
