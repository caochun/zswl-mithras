package cn.zswltech.mithras.contract.excel.exporter;

import cn.zswltech.mithras.foundation.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.contract.excel.model.ContractMatcherResultModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/5 14:25
 */
@Primary
@Component
public class ContractMatcherResultExporter extends AbstractSimpleExcelExporter<ContractMatcherResultModel> {
    @Override
    protected void customStrategy(Workbook workbook) {

    }

    @Override
    protected Class<ContractMatcherResultModel> modelClz() {
        return ContractMatcherResultModel.class;
    }
}
