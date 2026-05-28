package cn.zswltech.mithras.dto.ftp;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description ftp_quarterly_enterprise_pricing
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
@ApiModel("企业计价-返回体")
public class FtpQuarterlyEnterprisePricingRsp extends ListBaseRSP {

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
    * 项目分类
    */
    @ApiModelProperty(value = "项目分类")
    private String projectClassify;

    /**
    * credit_term
    */
    @ApiModelProperty(value = "credit_term")
    private String creditTerm;

    /**
    * 利率值
    */
    @ApiModelProperty(value = "利率值")
    private Integer percentValue;

}
