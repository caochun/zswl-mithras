package cn.zswltech.mithras.report.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.report.excel.model.BoardProjInfoExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 看板需要提供的项目信息
 *
 */
@Component
public class BoardProjInfoExcelExporter extends AbstractSimpleExcelExporter<BoardProjInfoExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<BoardProjInfoExcelModel> modelClz() {
        return BoardProjInfoExcelModel.class;
    }

    @Override
    protected boolean writeHead() {
        return true;
    }
}
