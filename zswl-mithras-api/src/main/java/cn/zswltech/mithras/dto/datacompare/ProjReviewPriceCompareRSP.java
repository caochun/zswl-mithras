package cn.zswltech.mithras.dto.datacompare;

import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("报价方案表详情-对比返回体")
public class ProjReviewPriceCompareRSP {
    @ApiModelProperty("租赁报价方案")
    private Map<String, DiffValue> leasePriceDetailRSP;
    @ApiModelProperty("保理报价方案")
    private Map<String, DiffValue> factoringPriceDetailRSP;
    @ApiModelProperty("债权转让报价方案")
    private Map<String, DiffValue> aocPriceDetailRSP;
}
