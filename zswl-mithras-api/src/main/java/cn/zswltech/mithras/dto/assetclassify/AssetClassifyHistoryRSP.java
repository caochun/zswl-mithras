package cn.zswltech.mithras.dto.assetclassify;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/1/8 1:46 下午
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("风控管理-资产分类-检查内容/总结-返回体")
public class AssetClassifyHistoryRSP extends ListBaseRSP {

    @ApiModelProperty("分类时间")
    private LocalDate createTime;

    @ApiModelProperty("存量风险敞口")
    private Long stockRiskExposure;

    @ApiModelProperty("逾期金额")
    private Long overdueAmount;

    @ApiModelProperty("逾期天数")
    private Integer overdueDays;

    @ApiModelProperty("定性调整")
    private Integer qualitativeAdjust;

    @ApiModelProperty("分类结果")
    private String classifyResult;
}
