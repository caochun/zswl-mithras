package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description ftp指导报价扩展表（下半部分）
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp指导报价扩展表（下半部分）编辑-请求体")
public class NewFtpMonthlyGuidanceExtDraftModifyREQ {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 一年期（含）以内
     */
    @ApiModelProperty(value = "一年期（含）以内")
    private Integer oneYear;

    /**
     * 一至三年期（含）
     */
    @ApiModelProperty(value = "一至三年期（含）")
    private Integer oneToThreeYear;

    /**
    * 三年以上
    */
    @ApiModelProperty(value = "三年以上")
    private Integer moreThanThreeYear;

    /**
    * 卖出价
    */
    @ApiModelProperty(value = "卖出价")
    private Integer sellingPrice;

    /**
     * 买入价
     */
    @ApiModelProperty(value = "买入价")
    private String buyingPrice;

}
