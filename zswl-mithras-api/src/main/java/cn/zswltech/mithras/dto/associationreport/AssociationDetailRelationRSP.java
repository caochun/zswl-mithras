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
public class AssociationDetailRelationRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("序号")
    private String onum;

    @ApiModelProperty("关联方名称")
    private String relpName;

    @ApiModelProperty("是否为本公司股东关联方")
    private String corpShahRelpFlag;

    @ApiModelProperty("是否为本公司股东关联方")
    private String corpShahRelpFlagDisplay;

    @ApiModelProperty("本公司股东名称")
    private String corpShahName;

    @ApiModelProperty("表内业务-关联方租赁余额_单一关联方")
    private BigDecimal onblRelpLeasBalSrlp;

    @ApiModelProperty("表内业务-占净资产比例_单一关联方")
    private BigDecimal onblOnarSrlp;

    @ApiModelProperty("表外业务-担保_单一关联方")
    private BigDecimal ofblGuarSrlp;

    @ApiModelProperty("表外业务-其他_单一关联方")
    private BigDecimal ofblOthSrlp;

    @ApiModelProperty("扣减项-合格质物_单一关联方")
    private BigDecimal deitQulfSbimSrlp;

    @ApiModelProperty("扣减项-合格保证_单一关联方")
    private BigDecimal deitQulfAsueSrlp;

    @ApiModelProperty("扣减项-其他_单一关联方")
    private BigDecimal deitOthSrlp;

    @ApiModelProperty("信用风险敞口_单一关联方")
    private BigDecimal credExpsSrlp;

    @ApiModelProperty("所在集团名称_关联方所在集团")
    private String grlpName;

    @ApiModelProperty("表内业务-关联方租赁余额_关联方所在集团")
    private BigDecimal onblRelpLeasBalGrlp;

    @ApiModelProperty("表内业务-占净资产比例_关联方所在集团")
    private BigDecimal onblOnarGrlp;

    @ApiModelProperty("表外业务-担保_关联方所在集团")
    private BigDecimal ofblGuarGrlp;

    @ApiModelProperty("表外业务-其他_关联方所在集团")
    private BigDecimal ofblOthGrlp;

    @ApiModelProperty("扣减项-合格质物_关联方所在集团")
    private BigDecimal deitQulfSbimGrlp;

    @ApiModelProperty("扣减项-合格保证_关联方所在集团")
    private BigDecimal deitQulfAsueGrlp;

    @ApiModelProperty("扣减项-其他_关联方所在集团")
    private BigDecimal deitOthGrlp;

    @ApiModelProperty("信用风险敞口_关联方所在集团")
    private BigDecimal credExpsGrlp;
}
