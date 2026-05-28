package cn.zswltech.mithras.dto.contract.depost;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("保证金退抵-请求体")
public class ContractDeductRentInfoREQ {

    @ApiModelProperty("流水id")
    private Long id;

    @ApiModelProperty("所属的contractId")
    private Long retreatInfoId;


    @ApiModelProperty("合同编号")
    private String contractId;

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
