package cn.zswltech.mithras.service.excel.exporter.kpi;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.kpi.KpiProjGuessDeptListExcelModel;
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
public class KpiProjGuessDeptListExcelExporter extends AbstractSimpleExcelExporter<KpiProjGuessDeptListExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Map<Integer, Integer> getColumnsWide(){
        HashMap<Integer, Integer> wideMap = new HashMap<>();
        wideMap.put(0, 40);
        return wideMap;
    }

    @Override
    protected Class<KpiProjGuessDeptListExcelModel> modelClz() {
        return KpiProjGuessDeptListExcelModel.class;
    }
}
