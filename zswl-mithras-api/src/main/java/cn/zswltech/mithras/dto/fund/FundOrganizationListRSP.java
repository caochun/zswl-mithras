package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description 资金管理-机构表
 * @date 2022-12-13
 */
@Data
@ApiModel("资金管理-机构表列表-返回体")
public class FundOrganizationListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机构简称")
    private String abbreviation;

    @ApiModelProperty(value = "机构名称")
    private String organizationName;

    @ApiModelProperty(value = "机构编号")
    private String organizationCode;

    @ApiModelProperty("原授信额度")
    private Long totalCreditAmount;

    @ApiModelProperty("已占用授信金额")
    private Long usedCreditAmount;

    @ApiModelProperty("剩余授信金额")
    private Long remainingCreditAmount;

    @ApiModelProperty(value = "机构类型 枚举")
    private String organizationType;

    @ApiModelProperty(value = "联系人名称")
    private String contactName;

    private Long createBy;
    
    @ApiModelProperty(value = "创建人")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("活期存款利率")
    private Long currentDepositRate;

    @ApiModelProperty("协定存款利率")
    private Long agreementDepositRate;

    @ApiModelProperty("协定存款利率到期日")
    private LocalDate agreementDepositRateDueTime;

}
