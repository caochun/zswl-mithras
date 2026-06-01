package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 集团在库明细列表出参
 */
@Data
@ApiModel
public class GroupInStockListRSP {
    private Long id;
    @ApiModelProperty(value = "所属机构code")
    private String applyOrganization;
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "入库原因")
    private String applyReason;

    @ApiModelProperty(value = "入库日期")
    private Date warehouseTime;

    @ApiModelProperty(value = "计划出库日期")
    private Date planOutboundTime;

    @ApiModelProperty(value = "风险规模（万元）")
    private BigDecimal riskScale;

    @ApiModelProperty(value = "名单来源 blackGraySourceEnum字典")
    private String source;
}
