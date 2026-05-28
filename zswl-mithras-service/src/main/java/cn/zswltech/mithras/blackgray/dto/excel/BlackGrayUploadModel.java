package cn.zswltech.mithras.blackgray.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;


@Data
public class BlackGrayUploadModel {

    @ExcelProperty(index = 0)
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    /**
     * 建议禁止/受限制准入业务及原因
     */
    @ExcelProperty(index = 1)
    @NotBlank(message = "建议禁止/受限制准入业务及原因不能为空")
    private String applyReasonType;

    /**
     * 入库时间
     */
    @ExcelProperty(index = 2)
    //@DateTimeFormat(value = "yyyy-MM-dd", use1904windowing = BooleanEnum.FALSE, t)
    private String warehouseTime;

    /**
     * 风险规模（万元）
     */
    @ExcelProperty(index = 3)
    private BigDecimal riskScale;

    @ExcelProperty(index = 4)
    private String reportFlag;

    private String unifiedSocialCreditCode;

    private String blackGrayType;

    /**
     * 业务类型
     *
     */
    private String businessType;

    private String applyReasonName;



}
