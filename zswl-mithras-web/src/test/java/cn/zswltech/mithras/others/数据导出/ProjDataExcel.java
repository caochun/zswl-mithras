package cn.zswltech.mithras.others.数据导出;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/3/13/16:45
 * @description
 */
@Data
public class ProjDataExcel {
    @ExcelProperty("项目名称")
    private String projName;
    @ExcelProperty("客户类型")
    private String clientType;
    @ExcelProperty("客户名称")
    private String clientName;
    @ExcelProperty("社会信用代码/身份证号")
    private String code;
    @ExcelProperty("报表数据是否完整")
    private String isComplete;
    @ExcelProperty("解决方案")
    private String method;
}
