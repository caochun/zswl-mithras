package cn.zswltech.mithras.dto.projreview.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("比较入参-请求体")
public class ProjReviewCompareREQ  {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("processInstanceId")
    private String processInstanceId;

}
