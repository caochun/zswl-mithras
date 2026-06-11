package cn.zswltech.mithras.dto.datacompare;

import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName ContractPriceModifyREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/8/16 2:04 下午
 * @Version 1.0
 **/
@Data
@ApiModel("合同报价方案更新-对比返回体")
public class ContractPriceDetailCompareRSP {

    @ApiModelProperty("租赁报价方案返回体")
    private Map<String, DiffValue> leasePriceModifyRSP;

    @ApiModelProperty("债权转让请求体")
    private Map<String, DiffValue> aocPriceRSP;

    @ApiModelProperty("保理方案请求体")
    private Map<String, DiffValue> factoringPriceRSP;

}
