package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

/**
 * @description 融资成本定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("融资成本定价列表-返回体")
public class NewFtpFinancingCostPricingListRSP extends ListBaseRSP {

    /**
     * month
     */
    @ApiModelProperty(value = "month")
    private LocalDate month;

    private Map<String, NewFtpFinancingCostPricingListRSP.NewFtpFinancingBody> bodyMap;

    @Data
    public class NewFtpFinancingBody{
        /**
         * id
         */
        @ApiModelProperty(value = "id")
        private Long id;

        /**
         * 类型
         * {@link TermRange#name()}
         */
        @ApiModelProperty(value = "term_range")
        private String termRange;

        /**
         * 当期均值
         */
        @ApiModelProperty(value = "当期均值")
        private Integer currentAverage;

        /**
         * 当年均值
         */
        @ApiModelProperty(value = "当年均值")
        private Integer annualAverage;

        /**
         * 当季均值
         */
        @ApiModelProperty(value = "当季均值")
        private Integer currentQuarterAverage;

        /**
         * ftp_pricing
         */
        @ApiModelProperty(value = "ftp定价")
        private Integer ftpPricing;

    }

}
