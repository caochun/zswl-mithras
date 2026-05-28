package cn.zswltech.mithras.dto.contract.depost;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("保证金退抵-返回体")
public class ContractDeductRentInfoRSP {

    @ApiModelProperty("所属的retreatInfoId")
    private Long retreatInfoId;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("现金流编号")
    private String code;

    @ApiModelProperty("日期")
    private LocalDate planCollectionDate;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("租金")
    private Long planCollectionAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("已收租金")
    private Long collectionAmount;

    @ApiModelProperty("未收租金")
    private Long restAmount;
}
