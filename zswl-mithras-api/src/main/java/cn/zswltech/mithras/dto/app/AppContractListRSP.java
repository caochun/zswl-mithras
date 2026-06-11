package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author junke
 */
@ApiModel("融租易合同信息-返回体")
@Data
public class AppContractListRSP {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "客户id")
    private Long clientId;
    @ApiModelProperty(value = "客户Name")
    private String clientName;
    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;
    @ApiModelProperty(value = "合同编号")
    private String contractCode;
    @ApiModelProperty(value = "项目名称")
    private String projName;
    @ApiModelProperty(value = "项目编号")
    private String projCode;
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;
    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private String leaseType;
    /**
     * 概算起租日
     **/
    @ApiModelProperty(value = "estimated_lease_date")
    private LocalDate estimatedLeaseDate;
    @ApiModelProperty(value = "实际起租日")
    private LocalDate actualLeaseDate;
    @ApiModelProperty("合同状态")
    private String contractStatus;
    @ApiModelProperty(value = "起租日期")
    private LocalDate leaseDate;
    @ApiModelProperty(value = "放款状态")
    private String paymentStatus;
    @ApiModelProperty(value = "合同金额")
    private Long contractAmount;
    @ApiModelProperty("合同剩余本金")
    private Long contractRemaining;
}
