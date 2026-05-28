package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/5 19:03
 */
@Data
public class WithdrawalRatioWrapper {

    @ApiModelProperty("借据id")
    private Long receiptId;
    @ApiModelProperty("借据编号")
    private String receiptCode;
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("计提比例")
    private Integer withdrawalRatio;
}
