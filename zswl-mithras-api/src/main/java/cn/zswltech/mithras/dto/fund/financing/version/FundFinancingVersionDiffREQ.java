package cn.zswltech.mithras.dto.fund.financing.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/2/24 2:30 下午
 **/
@ApiModel("融资管理版本差异比较-入参")
@Data
public class FundFinancingVersionDiffREQ {
    @ApiModelProperty("版本id")
    @NotNull
    private Long id;
}
