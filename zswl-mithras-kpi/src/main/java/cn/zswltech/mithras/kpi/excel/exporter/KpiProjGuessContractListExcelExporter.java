package cn.zswltech.mithras.kpi.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.kpi.excel.model.KpiProjGuessContractListExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName KpiProjGuessContractListExcelExporter
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/7/4 3:34 下午
 * @Version 1.0
 **/
@Component
public class KpiProjGuessContractListExcelExporter extends AbstractSimpleExcelExporter<KpiProjGuessContractListExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Map<Integer, Integer> getColumnsWide(){
        HashMap<Integer, Integer> wideMap = new HashMap<>();
        wideMap.put(0, 40);
        wideMap.put(1, 40);
        wideMap.put(2, 40);
        wideMap.put(3, 40);
        wideMap.put(5, 40);
        return wideMap;
    }

    @Override
    protected Class<KpiProjGuessContractListExcelModel> modelClz() {
        return KpiProjGuessContractListExcelModel.class;
    }
}
