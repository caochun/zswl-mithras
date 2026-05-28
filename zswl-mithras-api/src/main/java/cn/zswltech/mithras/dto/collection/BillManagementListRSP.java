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
@ApiModel("票据管理表列表-返回体")
public class BillManagementListRSP {

    /**
    * 票据id
    */
    @ApiModelProperty(value = "票据id")
    private Long id;

    /**
    * 管理收付款主表id
    */
    @ApiModelProperty(value = "管理收付款主表id")
    private Long mainId;

    /**
    * 票据类型 收款/付款
    */
    @ApiModelProperty(value = "票据类型 收款/付款")
    private String billType;

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
