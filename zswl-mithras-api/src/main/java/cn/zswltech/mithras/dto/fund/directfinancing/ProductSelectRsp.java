package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 11:40
 */
@Data
public class ProductSelectRsp {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "证券简称")
    private String abbreviation;

}
