package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

/**
 * @description LPR定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("LPR定价列表-返回体")
public class NewFtpLprPricingListRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty(value = "LPR报价日期")
    private LocalDate lprDate;

    @ApiModelProperty("1年期LPR")
    private Integer oneYearLpr;

    @ApiModelProperty("5年期LPR")
    private Integer fiveYearLpr;

    @ApiModelProperty("1年期LPR差额")
    private Integer oneYearLprDiff;

    @ApiModelProperty("5年期LPR差额")
    private Integer fiveYearLprDiff;

//    private Map<String, NewFtpLprBody> bodyMap;
//
//    @Data
//    public static class NewFtpLprBody{
//        /**
//         * id
//         */
//        @ApiModelProperty(value = "id")
//        private Long id;
//
//        @ApiModelProperty(value = "term_range")
//        private String termRange;
//
//        /**
//         * lpr
//         */
//        @ApiModelProperty(value = "lpr")
//        private Integer lpr;
//
//
//        @ApiModelProperty(value = "lpr_pricing")
//        private Integer lprPricing;
//
//    }

}
