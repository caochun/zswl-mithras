package cn.zswltech.mithras.service.mapper.model.associationreport;

import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description 金融协会报送-关联方信息汇总表实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("association_relation")
public class AssociationRelation extends BasicAssociationReport implements Serializable, IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 序号
     */
    @TableField("onum")
    private String onum;

    /**
     * 关联方名称
     */
    @TableField("relp_name")
    private String relpName;

    /**
     * 是否为本公司股东关联方
     */
    @TableField("corp_shah_relp_flag")
    private String corpShahRelpFlag;

    /**
     * 本公司股东名称
     */
    @TableField("corp_shah_name")
    private String corpShahName;

    /**
     * 表内业务-关联方租赁余额_单一关联方
     */
    @TableField("onbl_relp_leas_bal_srlp")
    private BigDecimal onblRelpLeasBalSrlp;

    /**
     * 表内业务-占净资产比例_单一关联方
     */
    @TableField("onbl_onar_srlp")
    private BigDecimal onblOnarSrlp;

    /**
     * 表外业务-担保_单一关联方
     */
    @TableField("ofbl_guar_srlp")
    private BigDecimal ofblGuarSrlp;

    /**
     * 表外业务-其他_单一关联方
     */
    @TableField("ofbl_oth_srlp")
    private BigDecimal ofblOthSrlp;

    /**
     * 扣减项-合格质物_单一关联方
     */
    @TableField("deit_qulf_sbim_srlp")
    private BigDecimal deitQulfSbimSrlp;

    /**
     * 扣减项-合格保证_单一关联方
     */
    @TableField("deit_qulf_asue_srlp")
    private BigDecimal deitQulfAsueSrlp;

    /**
     * 扣减项-其他_单一关联方
     */
    @TableField("deit_oth_srlp")
    private BigDecimal deitOthSrlp;

    /**
     * 信用风险敞口_单一关联方
     */
    @TableField("cred_exps_srlp")
    private BigDecimal credExpsSrlp;

    /**
     * 所在集团名称_关联方所在集团
     */
    @TableField("grlp_name")
    private String grlpName;

    /**
     * 表内业务-关联方租赁余额_关联方所在集团
     */
    @TableField("onbl_relp_leas_bal_grlp")
    private BigDecimal onblRelpLeasBalGrlp;

    /**
     * 表内业务-占净资产比例_关联方所在集团
     */
    @TableField("onbl_onar_grlp")
    private BigDecimal onblOnarGrlp;

    /**
     * 表外业务-担保_关联方所在集团
     */
    @TableField("ofbl_guar_grlp")
    private BigDecimal ofblGuarGrlp;

    /**
     * 表外业务-其他_关联方所在集团
     */
    @TableField("ofbl_oth_grlp")
    private BigDecimal ofblOthGrlp;

    /**
     * 扣减项-合格质物_关联方所在集团
     */
    @TableField("deit_qulf_sbim_grlp")
    private BigDecimal deitQulfSbimGrlp;

    /**
     * 扣减项-合格保证_关联方所在集团
     */
    @TableField("deit_qulf_asue_grlp")
    private BigDecimal deitQulfAsueGrlp;

    /**
     * 扣减项-其他_关联方所在集团
     */
    @TableField("deit_oth_grlp")
    private BigDecimal deitOthGrlp;

    /**
     * 信用风险敞口_关联方所在集团
     */
    @TableField("cred_exps_grlp")
    private BigDecimal credExpsGrlp;

    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
