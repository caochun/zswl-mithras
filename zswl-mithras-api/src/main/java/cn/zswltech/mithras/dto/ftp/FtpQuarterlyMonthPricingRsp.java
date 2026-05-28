package cn.zswltech.mithras.dto.ftp;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description ftp_quarterly_month_pricing
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
@ApiModel("月度计价-返回体")
public class FtpQuarterlyMonthPricingRsp extends ListBaseRSP {
    @ApiModelProperty(value = "所属指引id")
    private Long guidanceId;

    @ApiModelProperty(value = "项目分类")
    private String projectClassify;

    @ApiModelProperty(value = "孟月、仲月、季月、均值")
    private String monthType;

    @ApiModelProperty(value = "credit_term")
    private String creditTerm;

    @ApiModelProperty(value = "利率值")
    private Integer percentValue;

}
