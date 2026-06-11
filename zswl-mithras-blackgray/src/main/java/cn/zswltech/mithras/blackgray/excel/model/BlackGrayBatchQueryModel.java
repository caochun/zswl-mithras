package cn.zswltech.mithras.blackgray.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BlackGrayBatchQueryModel {

    @ExcelProperty(index = 0)
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    @ExcelProperty(index = 1)
    @NotBlank(message = "统一社会信用代码不能为空")
    private String unifiedSocialCreditCode;

}
