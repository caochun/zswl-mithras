package cn.zswltech.mithras.dto.ftp;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 月度指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
@ApiModel("月度指导列表-返回体")
public class FtpMonthlyGuidanceDetailRsp extends ListBaseRSP {

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "月度")
    private Integer month;

    @ApiModelProperty(value = "审批状态")
    private String guidanceProcessStatus;

    @ApiModelProperty(value = "一年期ftp收益指导报价")
    private Integer oneYearEarningsGuidance;

    @ApiModelProperty(value = "1-3年期ftp收益指导报价")
    private Integer oneToThreeEarningsGuidance;

    @ApiModelProperty(value = "3年以上ftp收益指导报价")
    private Integer moreThanThreeEarningsGuidance;

    @ApiModelProperty(value = "卖出价")
    private Integer sellingPrice;

    @ApiModelProperty(value = "买入价")
    private Integer buyingPrice;

}
