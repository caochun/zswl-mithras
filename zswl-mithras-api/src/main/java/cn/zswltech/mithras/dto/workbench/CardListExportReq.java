package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/4/1
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CardListExportReq extends CardListReq {
    @ApiModelProperty("是否下载全量，1-是，0-否")
    private Integer isAll;
}
