package cn.zswltech.mithras.service.mapper.model.associationreport;

import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description 金融协会报送-主要业务清单表实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("association_main_business")
public class AssociationMainBusiness extends BasicAssociationReport implements Serializable, IEntity {
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
     * 合同名称
     */
    @TableField("agmt_name")
    private String agmtName;

    /**
     * 合同编号
     */
    @TableField("agmt_no")
    private String agmtNo;

    /**
     * 合同签订日期
     */
    @TableField("agmt_sign_date")
    private LocalDate agmtSignDate;

    /**
     * 协议到期日期
     */
    @TableField("agmt_matu_date")
    private LocalDate agmtMatuDate;

    /**
     * 合同类型
     */
    @TableField("agmt_type_code")
    private String agmtTypeCode;

    /**
     * 租赁物类型
     */
    @TableField("lasd_type")
    private String lasdType;

    /**
     * 项目行业分类
     */
    @TableField("proj_indt_clas_code")
    private String projIndtClasCode;

    /**
     * 客户姓名
     */
    @TableField("cust_name")
    private String custName;

    /**
     * 客户证件号码
     */
    @TableField("cust_cert_num")
    private String custCertNum;

    /**
     * 客户规模
     */
    @TableField("cust_scal_code")
    private String custScalCode;

    /**
     * 融资租赁投放额
     */
    @TableField("fnl_rels")
    private BigDecimal fnlRels;

    /**
     * 收回本金
     */
    @TableField("wthd_prin")
    private BigDecimal wthdPrin;

    /**
     * 租金余额
     */
    @TableField("rent_bal")
    private BigDecimal rentBal;

    /**
     * 综合融资成本
     */
    @TableField("cmph_fin_cost")
    private BigDecimal cmphFinCost;

    /**
     * 增信情况
     */
    @TableField("udpn_situ_code")
    private String udpnSituCode;

    /**
     * 增信方
     */
    @TableField("udpn")
    private String udpn;

    /**
     * 逾期租金
     */
    @TableField("ovdu_rent")
    private BigDecimal ovduRent;

    /**
     * 逾期天数
     */
    @TableField("ovdu_days_code")
    private String ovduDaysCode;

    /**
     * 逾期处置情况
     */
    @TableField("ovdu_dsps_prog")
    private String ovduDspsProg;

    /**
     * 是否纳入不良
     */
    @TableField("np_flag")
    private String npFlag;

    /**
     * 不良余额
     */
    @TableField("np_bal")
    private BigDecimal npBal;

    /**
     * 客户数量
     */
    @TableField("cust_vol")
    private Integer custVol;

    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
