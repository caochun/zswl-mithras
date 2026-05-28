package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailMajorMattersEventReportRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 事项名称
     */
    @ApiModelProperty(value = "事项名称")
    private String piecName;

    /**
     * 重大事项说明
     */
    @ApiModelProperty(value = "重大事项说明")
    private String imprPiecExpl;
}
