package cn.zswltech.mithras.dto.contract.mortgage;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @description 合同-抵押措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-抵押措施列表-返回体")
public class ContractMortgageItemListRSP extends ListBaseRSP {

    @ApiModelProperty("序号")
    private Integer sequence;

    @ApiModelProperty("种类")
    private String category;

    @ApiModelProperty("识别号类型")
    private String uniqueIdentifyCodeType;

    @ApiModelProperty("唯一识别号")
    private String uniqueIdentifyCode;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("供应商")
    private String supplier;

    @ApiModelProperty("数量")
    private String quantity;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("购置日期")
    private String purchaseDate;

    @ApiModelProperty("账面原值")
    private Long originalBookValue;

    @ApiModelProperty("账面净值")
    private Long originalBookNetValue;

    @ApiModelProperty("评估原值")
    private Long assessedValue;

    @ApiModelProperty("评估净值")
    private Long assessedNetValue;

    @ApiModelProperty("发票号")
    private String invoiceCode;

    @ApiModelProperty("存放地点")
    private String storagePlace;

}
