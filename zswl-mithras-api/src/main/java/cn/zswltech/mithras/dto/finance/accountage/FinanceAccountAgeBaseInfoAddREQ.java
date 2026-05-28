package cn.zswltech.mithras.dto.finance.accountage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 帐龄主表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄主表新增-请求体")
public class FinanceAccountAgeBaseInfoAddREQ {

    /**
    * 截止日期
    */
    @ApiModelProperty(value = "截止日期")
    @NotNull(message = "截止日期不能为空")
    private LocalDate deadline;

    /**
    * 核算组织编码 默认 10000396
    */
    @ApiModelProperty(value = "核算组织编码 默认 10000396")
    private String accountancyOrganizationNumber;

    /**
    * 核算组织名称 默认 浙江浙商融资租赁有限公司
    */
    @ApiModelProperty(value = "核算组织名称 默认 浙江浙商融资租赁有限公司")
    private String accountancyOrganizationName;

    /**
    * 状态
    */
    @ApiModelProperty(value = "状态")
    private String status;

}
