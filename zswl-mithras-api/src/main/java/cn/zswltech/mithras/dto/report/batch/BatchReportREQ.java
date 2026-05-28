package cn.zswltech.mithras.dto.report.batch;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yangxiong
 * @date 2024/4/7/14:20
 * @description
 */
@Data
public class BatchReportREQ implements Serializable {
    private static final long serialVersionUID = 9203627188738428790L;

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "报送说明")
    private String reportDescription;
}
