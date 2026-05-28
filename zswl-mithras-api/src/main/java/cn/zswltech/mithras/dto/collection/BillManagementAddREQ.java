package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 票据管理表
 * @author vico
 * @date 2023-06-05
 */
@Data
@ApiModel("票据管理表新增-请求体")
public class BillManagementAddREQ {

    /**
    * 管理收付款主表id
    */
    @ApiModelProperty(value = "收款/付款明细ID")
    @NotNull(message = "收款/付款明细ID不能为空")
    private Long mainId;

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 票据类型 收款/付款
    */
    @ApiModelProperty(value = "票据类型 收款/付款 枚举-BillTypeEnum")
    @NotNull(message = "票据类型不能为空")
    private String billType;

    /**
    * 票据code
    */
    @ApiModelProperty(value = "票据code")
    @NotNull(message = "票据code不能为空")
    private String billCode;

    /**
    * 票据金额
    */
    @ApiModelProperty(value = "票据金额")
    @NotNull(message = "票据金额不能为空")
    private Long billAmount;

    /**
    * 票据到期日期
    */
    @ApiModelProperty(value = "票据到期日期")
    @NotNull(message = "票据到期日期不能为空")
    private LocalDate billExpireDate;

    /**
     * 票据买入价
     */
    @ApiModelProperty("票据买入价")
    //@NotNull(message = "票据买入价不能为空")
    private Long billBuyRate;


    /**
     * 票据买入价类型,0其他，1，同项目FTP
     */
    @ApiModelProperty("票据买入价类型,0其他，1，同项目FTP")
    private Integer billBuyRateType;

}
