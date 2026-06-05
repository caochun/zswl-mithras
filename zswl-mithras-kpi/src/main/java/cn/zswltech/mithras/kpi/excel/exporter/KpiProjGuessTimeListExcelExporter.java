package cn.zswltech.mithras.kpi.excel.exporter;

import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.kpi.excel.model.KpiProjGuessTimeListExcelModel;
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
public class KpiProjGuessTimeListExcelExporter extends AbstractSimpleExcelExporter<KpiProjGuessTimeListExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Map<Integer, Integer> getColumnsWide(){
        return new HashMap<>();
    }

    @Override
    protected Class<KpiProjGuessTimeListExcelModel> modelClz() {
        return KpiProjGuessTimeListExcelModel.class;
    }
}
