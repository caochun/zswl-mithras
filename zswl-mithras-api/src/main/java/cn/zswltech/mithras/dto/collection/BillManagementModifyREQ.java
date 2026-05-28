package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
/**
 * @description 票据管理表
 * @author vico
 * @date 2023-06-05
 */
@Data
@ApiModel("票据管理表编辑-请求体")
public class BillManagementModifyREQ {

    /**
    * 票据id
    */
    @ApiModelProperty(value = "票据id")
    private Long id;

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 票据code
    */
    @ApiModelProperty(value = "票据code")
    private String billCode;

    /**
    * 票据金额
    */
    @ApiModelProperty(value = "票据金额")
    private Long billAmount;

    /**
    * 票据到期日期
    */
    @ApiModelProperty(value = "票据到期日期")
    private LocalDate billExpireDate;

    /**
     * 票据买入价
     */
    @ApiModelProperty("票据买入价")
    private Long billBuyRate;


    /**
     * 票据买入价类型,0其他，1，同项目FTP
     */
    @ApiModelProperty("票据买入价类型,0其他，1，同项目FTP")
    private Long billBuyRateType;

}
