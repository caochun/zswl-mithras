package cn.zswltech.mithras.dto.ftp;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description ftp_quarterly_customer_principal_pricing
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
@ApiModel("客户主体计价-返回体")
public class FtpQuarterlyCustomerPrincipalPricingRsp extends ListBaseRSP {

    /**
    * 所属指引id
    */
    @ApiModelProperty(value = "所属指引id")
    private Long guidanceId;

    /**
    * 国有、其他
    */
    @ApiModelProperty(value = "国有、其他")
    private String enterpriseType;

    /**
    * 1年期、1-3年、3年以上
    */
    @ApiModelProperty(value = "1年期、1-3年、3年以上")
    private String creditTerm;

    /**
    * 利率值
    */
    @ApiModelProperty(value = "利率值")
    private Integer percentValue;

}
