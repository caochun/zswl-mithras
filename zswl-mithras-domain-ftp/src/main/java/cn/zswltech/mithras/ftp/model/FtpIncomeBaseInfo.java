package cn.zswltech.mithras.ftp.model;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description 资金管理-融资管理-ftp收益表
 * @author vico
 * @date 2025-07-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FtpIncomeBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 融资id
    */
    @TableField("fund_financing_id")
    private Long fundFinancingId;

    /**
    * 融资类型 直融 or 间融 financingtypeenum
    */
    @TableField("financing_type")
    private String financingType;

    /**
    * 融资编号
    */
    @TableField("financing_code")
    private String financingCode;

    /**
    * 直融保存产品信息
    */
    @TableField("financing_product_id")
    private Long financingProductId;

    /**
    * 证券简称
    */
    @TableField("abbreviation")
    private String abbreviation;

    /**
    * 融资金额
    */
    @TableField("financing_amount")
    private Long financingAmount;

    /**
    * ftp收益率
    */
    @TableField("ftp_yield_rate")
    private Integer ftpYieldRate;

    /**
     * 产品ftp收益率
     */
    @TableField("product_ftp_yield_rate")
    private Integer productFtpYieldRate;

    /**
    * 资金经理id
    */
    @TableField("fund_manager_id")
    private Long fundManagerId;

}
