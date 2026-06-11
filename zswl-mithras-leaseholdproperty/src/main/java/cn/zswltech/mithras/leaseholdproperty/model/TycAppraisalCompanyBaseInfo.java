package cn.zswltech.mithras.leaseholdproperty.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 天眼查-评估机构主表
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tyc_appraisal_company_base_info")
public class TycAppraisalCompanyBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机构名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @TableField("credit_code")
    private String creditCode;

    /**
     * 营业许可证到期日
     */
    @TableField("biz_license_end_date")
    private LocalDate bizLicenseEndDate;

    /**
     * 营业许可证是否为长期
     */
    @TableField("biz_licence_long_term")
    private Boolean bizLicenceLongTerm;

    /**
     * 业务范围
     */
    @TableField("biz_scope")
    private String bizScope;

    /**
     * 成立日期
     */
    @TableField("establish_date")
    private LocalDate establishDate;

    /**
     * 逻辑删除
     */
    @TableField("deleted")
    private Integer deleted;



}
