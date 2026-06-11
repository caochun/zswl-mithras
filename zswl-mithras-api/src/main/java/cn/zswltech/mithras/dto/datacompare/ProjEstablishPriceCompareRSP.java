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
@ApiModel("报价方案复合查询列表-对比返回体")
public class ProjEstablishPriceCompareRSP {

    @ApiModelProperty(value = "租赁报价方案")
    private Map<String, DiffValue> leasePriceRSP;

    @ApiModelProperty(value = "债权转让报价方案")
    private Map<String, DiffValue> aocPriceRSP;

    @ApiModelProperty(value = "保理报价方案")
    private Map<String, DiffValue> factoringPriceRSP;

}
