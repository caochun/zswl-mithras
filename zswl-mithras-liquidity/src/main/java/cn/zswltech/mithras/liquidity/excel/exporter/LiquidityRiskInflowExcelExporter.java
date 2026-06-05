package cn.zswltech.mithras.liquidity.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.liquidity.excel.model.LiquidityRiskInflowExcelModel;
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
public class LiquidityRiskInflowExcelExporter extends AbstractSimpleExcelExporter<LiquidityRiskInflowExcelModel> {

    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Map<Integer, Integer> getColumnsWide(){
        HashMap<Integer, Integer> wideMap = new HashMap<>();
        wideMap.put(0, 40);
        wideMap.put(1, 40);
        wideMap.put(8, 40);
        wideMap.put(10, 30);
        return wideMap;
    }

    @Override
    protected Class<LiquidityRiskInflowExcelModel> modelClz() {
        return LiquidityRiskInflowExcelModel.class;
    }
}
