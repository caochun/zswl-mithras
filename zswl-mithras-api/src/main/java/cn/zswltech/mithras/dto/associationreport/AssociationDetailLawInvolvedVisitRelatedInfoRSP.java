package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailLawInvolvedVisitRelatedInfoRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private String onum;

    /**
     * 信息类别
     */
    @ApiModelProperty(value = "信息类别")
    private String caseClasCode;

    @ApiModelProperty(value = "信息类别Display")
    private String caseClasDisplay;

    /**
     * 合同名称
     */
    @ApiModelProperty(value = "合同名称")
    private String agmtName;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String agmtNo;

    /**
     * 涉及金额(元)
     */
    @ApiModelProperty(value = "涉及金额(元)")
    private BigDecimal invlAmt;

    /**
     * 是否销号
     */
    @ApiModelProperty(value = "是否销号")
    private String canbFlag;
}
