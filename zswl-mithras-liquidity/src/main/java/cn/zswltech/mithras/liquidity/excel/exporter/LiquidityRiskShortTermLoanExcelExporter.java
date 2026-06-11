package cn.zswltech.mithras.liquidity.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.liquidity.excel.model.LiquidityRiskShortTermLoanExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@Component
public class LiquidityRiskShortTermLoanExcelExporter extends AbstractSimpleExcelExporter<LiquidityRiskShortTermLoanExcelModel> {

    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Map<Integer, Integer> getColumnsWide(){
        HashMap<Integer, Integer> wideMap = new HashMap<>();
        wideMap.put(0, 40);
        wideMap.put(1, 40);
        wideMap.put(6, 40);
        return wideMap;
    }

    @Override
    protected Class<LiquidityRiskShortTermLoanExcelModel> modelClz() {
        return LiquidityRiskShortTermLoanExcelModel.class;
    }
}
