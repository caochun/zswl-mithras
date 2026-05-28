package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/19/18:07
 * @description
 */
@Data
public class FinanceCodeListRSP {

    @ApiModelProperty(value = "idKey")
    private String idKey;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

}
