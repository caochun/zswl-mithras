package cn.zswltech.mithras.dto.fund;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 资金管理-机构表
 * @date 2022-12-13
 */
@Data
@ApiModel("资金管理-机构表新增-请求体")
public class FundOrganizationAddREQ {
    @ApiModelProperty(value = "机构简称")
    @NotNull(message = "机构简称为必填项")
    private String abbreviation;
    @ApiModelProperty(value = "机构名称")
    @NotNull(message = "机构名称为必填项")
    private String organizationName;
    @ApiModelProperty(value = "机构类型枚举")
    @NotNull(message = "机构类型为必填项")
    private String organizationType;
    @ApiModelProperty(value = "联系人")
    private ContactInfo contactInfo;
    @ApiModelProperty(value = "地址信息")
    private AddressInfo addressInfo;
    @ApiModelProperty(value = "银行联行号, 类型为银行时")
    private String interBankNo;
    @ApiModelProperty(value = "统一社会信用代码，类型为租赁公司时")
    private String uscCode;
    @ApiModelProperty(value = "账户信息")
    private List<AccountInfo> accountsInfo;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "机构代码")
    private String institutionCode;

    /**
     * 活期存款利率
     */
    @ApiModelProperty("活期存款利率")
    private Long currentDepositRate;

    /**
     * 协定存款利率
     */
    @ApiModelProperty("协定存款利率")
    private Long agreementDepositRate;

    /**
     * 协定存款利率到期日
     */
    @ApiModelProperty("协定存款利率到期日")
    private LocalDate agreementDepositRateDueTime;

}
