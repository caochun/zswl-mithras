package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/7/16
 * @description
 */
@Data
public class NewFtpQuarterlyBasePricingExtDraftModifyREQ {
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 3年内（含）
     */
    @ApiModelProperty(value = "3年内（含）")
    private Integer threeYear;

    /**
     * 3-5年（含）
     */
    @ApiModelProperty(value = "3-5年（含）")
    private Integer threeToFiveYear;

    /**
     * 5年以上
     */
    @ApiModelProperty(value = "5年以上")
    private Integer moreThanFiveYear;
}
