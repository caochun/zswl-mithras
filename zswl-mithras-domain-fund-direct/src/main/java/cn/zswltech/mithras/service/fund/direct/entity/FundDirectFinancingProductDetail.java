package cn.zswltech.mithras.service.fund.direct.entity;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 直接融资-产品明细
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
public class FundDirectFinancingProductDetail extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("financing_id")
    private Long financingId;

    /**
     * 证券代码
     */
    @TableField("securities_code")
    private String securitiesCode;

    /**
     * 证券简称
     */
    @TableField("abbreviation")
    private String abbreviation;

    /**
     * 发行金额（万元）
     */
    @TableField("issuance_amount")
    private Long issuanceAmount;

    /**
     * 分层占比（%）
     */
    @TableField("layered_proportion")
    private Long layeredProportion;

    /**
    * 还本方式
    */
    @TableField("repayment_method")
    private String repaymentMethod;

    /**
     * 发行利率
     */
    @TableField("issuance_rate")
    private Long issuanceRate;

    /**
     * 年付息次数
     */
    @TableField("annual_pay_count")
    private Long annualPayCount;

    /**
     * 起息日
     */
    @TableField("value_date")
    private LocalDate valueDate;

    /**
     * 预计到期日
     */
    @TableField("expected_expiration_date")
    private LocalDate expectedExpirationDate;

    /**
    * 剩余本金余额（万元）
    */
    @TableField("remaining_principal")
    private String remainingPrincipal;

    /**
     * FTP收益率
     */
    @TableField("ftp_yield_rate")
    private Integer ftpYieldRate;

    /**
    * 评级
    */
    @TableField("rating")
    private String rating;

    /**
    * 备注
    */
    @TableField("remark")
    private String remark;

}
