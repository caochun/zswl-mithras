package cn.zswltech.mithras.dto.report.batch;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yangxiong
 * @date 2024/4/9/18:51
 * @description
 */
@Data
public class BatchHeadRSP implements Serializable {
    private static final long serialVersionUID = -3435557619812509417L;

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "报送说明")
    private String reportDescription;

}
