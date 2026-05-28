package cn.zswltech.mithras.dto.fund.receiptrepay.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 批量界面详情返回值
 *
 * @author wangchuanhao
 * @date 2023/2/18 5:26 PM
 */
@ApiModel("批量界面详情返回值")
@Data
public class BatchDetailRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("备注")
    private String remark;

}
