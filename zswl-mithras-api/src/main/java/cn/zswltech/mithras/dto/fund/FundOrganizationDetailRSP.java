package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 13:37
 */
@ApiModel("机构详情响应体")
@Data
public class FundOrganizationDetailRSP {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "机构简称")
    private String abbreviation;
    @ApiModelProperty(value = "机构名称")
    private String organizationName;
    @ApiModelProperty(value = "机构编号")
    private String organizationCode;
    @ApiModelProperty(value = "机构类型 枚举")
    private String organizationType;
    @ApiModelProperty(value = "联系人 json")
    private ContactInfo contactInfo;
    @ApiModelProperty(value = "地址信息")
    private AddressInfo addressInfo;
    @ApiModelProperty(value = "银行联行号")
    private String interBankNo;
    @ApiModelProperty(value = "统一社会信用代码")
    private String uscCode;
    @ApiModelProperty(value = "账户信息 ")
    private List<AccountInfo> accountsInfo;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "机构代码")
    private String institutionCode;
    @ApiModelProperty("活期存款利率")
    private Long currentDepositRate;
    @ApiModelProperty("协定存款利率")
    private Long agreementDepositRate;
    @ApiModelProperty("协定存款利率到期日")
    private LocalDate agreementDepositRateDueTime;

}
