package cn.zswltech.mithras.collection.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.collection.excel.model.ContractcpCashDetailExcelModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 合同收付款 详情列表
 *
 * @author wangchuanhao
 * @date 2022/8/22 4:12 PM
 */
@Component
public class ContractcpCashDetailExcelExporter extends AbstractSimpleExcelExporter<ContractcpCashDetailExcelModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ContractcpCashDetailExcelModel> modelClz() {
        return ContractcpCashDetailExcelModel.class;
    }
}
