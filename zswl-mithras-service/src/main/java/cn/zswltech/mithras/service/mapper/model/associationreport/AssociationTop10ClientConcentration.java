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
 * @description 金融协会报送-最大10家客户（含集团）集中度统计表实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("association_top10_client_concentration")
public class AssociationTop10ClientConcentration extends BasicAssociationReport implements Serializable, IEntity {
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
     * 客户姓名
     */
    @TableField("cust_name")
    private String custName;

    /**
     * 表内业务-前十大客户租赁余额
     */
    @TableField("onbl_topt_cust_leas_bal")
    private BigDecimal onblToptCustLeasBal;

    /**
     * 表内业务-占净资产比例
     */
    @TableField("onbl_onar")
    private BigDecimal onblOnar;

    /**
     * 表外业务-担保
     */
    @TableField("ofbl_guar")
    private BigDecimal ofblGuar;

    /**
     * 表外业务-其他
     */
    @TableField("ofbl_oth")
    private BigDecimal ofblOth;

    /**
     * 扣减项-合格质物
     */
    @TableField("deit_qulf_sbim")
    private BigDecimal deitQulfSbim;

    /**
     * 扣减项-合格保证
     */
    @TableField("deit_qulf_asue")
    private BigDecimal deitQulfAsue;

    /**
     * 扣减项-其他
     */
    @TableField("deit_oth")
    private BigDecimal deitOth;

    /**
     * 信用风险敞口
     */
    @TableField("cred_exps")
    private BigDecimal credExps;

    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
