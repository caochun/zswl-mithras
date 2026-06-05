package cn.zswltech.mithras.kpi.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.kpi.excel.model.KpiProjGuessPeopleDetailExcelModel;
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
public class KpiProjGuessPeopleDetailExcelExporter extends AbstractSimpleExcelExporter<KpiProjGuessPeopleDetailExcelModel> {
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
    protected Class<KpiProjGuessPeopleDetailExcelModel> modelClz() {
        return KpiProjGuessPeopleDetailExcelModel.class;
    }
}
