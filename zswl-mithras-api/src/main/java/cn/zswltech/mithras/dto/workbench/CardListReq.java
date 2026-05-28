package cn.zswltech.mithras.dto.workbench;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-10-21
 **/

@Data
public class CardListReq extends PageReq {
    @ApiModelProperty("本年 true/本月 false")
    private Boolean isYear;

    @ApiModelProperty("role")
    private String currentRoleCode;
}
