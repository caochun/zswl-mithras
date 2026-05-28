package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description ftp指导报价扩展表（下半部分）
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp指导报价扩展表（下半部分）列表-返回体")
public class NewFtpMonthlyGuidanceExtDraftDetailRSP extends ListBaseRSP {

    /**
     * 所属的主数据id
     */
    @ApiModelProperty(value = "所属的主数据id")
    private Long ftpId;

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

    public Integer getFtpYieldRate (Integer month) {
        if (month == null) {
            return 0;
        } else if (month <= 12) {
            return oneYear;
        } else if (month <= 36) {
            return oneToThreeYear;
        } else {
            return moreThanThreeYear;
        }
    }


}
