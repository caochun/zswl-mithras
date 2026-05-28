package cn.zswltech.mithras.dto.projreview.price;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("报价方案详情-请求体")
@AllArgsConstructor
@NoArgsConstructor
public class ProjReviewPriceDetailREQ extends VersionBaseREQ {
    @ApiModelProperty("所属的projectId")
    private Long id;

    @ApiModelProperty("流程id")
    private String processInstanceId;
}
