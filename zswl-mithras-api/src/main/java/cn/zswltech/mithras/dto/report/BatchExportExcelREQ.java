package cn.zswltech.mithras.dto.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author bigbear
 * @date 2025/5/27 09:51
 * @description
 */
@Data
public class BatchExportExcelREQ {

    @ApiModelProperty("查询渠道：EDIT编辑区；PROC审批流里；PROC_BATCH批次查询；EFFECT已报送账户维度穿透")
    @NotBlank(message = "查询渠道不能为空")
    private String channel;

    @ApiModelProperty("批次id 导出批次的时候必传")
    private Long batchId;


}
