package cn.zswltech.mithras.others.service;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.projectprocess.excel.exporter.IRRCalculateExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.model.IRRCalculateExcelModel;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowIRRBO;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/12/20
 * @description
 */
public class IRRCalculateTest extends ApplicationTest {
    @Resource
    private IRRCalculateExcelExporter excelExporter;

    @Test
    public void irrTest() {
        RepayRateEnum repayRateEnum = RepayRateEnum.QUARTER;
        List<CashFlowBO> list = new ArrayList<>();
        LocalDate zeroStandardDate = LocalDate.of(2022, 9, 15);
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2022, 9, 10)).setCashFlowPhase(0).setCashFlowAmount(-96000000000L).setStandardCashFlowDate(zeroStandardDate));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2022, 12, 1)).setCashFlowPhase(1).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2023, 3, 15)).setCashFlowPhase(2).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2023, 6, 20)).setCashFlowPhase(3).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2023, 9, 1)).setCashFlowPhase(4).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2023, 12, 1)).setCashFlowPhase(5).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2024, 3, 1)).setCashFlowPhase(6).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2024, 6, 1)).setCashFlowPhase(7).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2024, 9, 1)).setCashFlowPhase(8).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2024, 12, 1)).setCashFlowPhase(9).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2025, 3, 1)).setCashFlowPhase(10).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2025, 6, 1)).setCashFlowPhase(11).setCashFlowAmount(9167999300L));
        list.add(new CashFlowBO().setCashFlowDate(LocalDate.of(2025, 9, 30)).setCashFlowPhase(12).setCashFlowAmount(8168999300L));
        CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(36, repayRateEnum, list);
        IRRCalculateExcelModel excelModel = new IRRCalculateExcelModel();
        excelModel.setIrrPerPhase(cashFlowIRRBO.getIrrPerPhase());
        excelModel.setIrr(cashFlowIRRBO.getIrrPerPhase().multiply(BigDecimal.valueOf(FinancialUtil.getRepayTimesInYear(repayRateEnum))));
        excelModel.setCashFlowAdjustExcelModelList(cashFlowIRRBO.getCashFlowAdjustList().stream().map(item -> {
            IRRCalculateExcelModel.CashFlowAdjustExcelModel model = new IRRCalculateExcelModel.CashFlowAdjustExcelModel();
            model.setCashFlowDate(item.getCashFlowDate());
            model.setAdjustCashFlowDate(item.getAdjustCashFlowDate());
            model.setCashFlowPhase(item.getCashFlowPhase());
            model.setAdjustCashFlowPhase(item.getAdjustCashFlowPhase());
            model.setCashFlowAmount(BigDecimal.valueOf(item.getCashFlowAmount()));
            model.setAdjustCashFlowAmount(item.getAdjustCashFlowAmount());
            return model;
        }).collect(Collectors.toList()));
        excelExporter.export(FileUtil.getOutputStream("/Users/mockorz/test.xlsx"), excelModel);
    }
}
