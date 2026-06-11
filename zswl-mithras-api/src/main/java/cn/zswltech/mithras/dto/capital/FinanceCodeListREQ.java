package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author yangxiong
 * @date 2024/5/19/18:07
 * @description
 */
@Data
public class FinanceCodeListREQ {

    @ApiModelProperty(value = "机构名称")
    private String orgName;

}
