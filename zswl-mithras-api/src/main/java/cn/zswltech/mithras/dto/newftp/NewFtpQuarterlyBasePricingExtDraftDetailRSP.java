package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/7/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NewFtpQuarterlyBasePricingExtDraftDetailRSP extends ListBaseRSP {
    /**
     * 所属的主数据id
     */
    @ApiModelProperty(value = "所属的主数据id")
    private Long ftpId;

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
