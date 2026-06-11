package cn.zswltech.mithras.riskcontrol.excel.model;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/17 09:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RiskControlFieldInitExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "客户名称")
    private String clientName;

    @SimpleExcelHeader(headerName = "年度风险策略限额行业类别")
    private String classifyDisplay;
}
