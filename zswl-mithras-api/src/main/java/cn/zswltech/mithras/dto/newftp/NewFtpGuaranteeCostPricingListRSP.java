package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 担保成本定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("担保成本定价列表-返回体")
public class NewFtpGuaranteeCostPricingListRSP extends ListBaseRSP {

    /**
    * month
    */
    @ApiModelProperty(value = "month")
    private LocalDate month;

    /**
    * 当期均值
    */
    @ApiModelProperty(value = "当期均值")
    private Integer currentAverage;

}
