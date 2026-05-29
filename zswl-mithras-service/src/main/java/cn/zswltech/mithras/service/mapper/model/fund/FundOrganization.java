package cn.zswltech.mithras.service.mapper.model.fund;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 资金管理-机构表
 * @date 2022-12-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FundOrganization extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 机构简称
     */
    @TableField("abbreviation")
    private String abbreviation;

    /**
     * 机构名称
     */
    @TableField("organization_name")
    private String organizationName;

    /**
     * 机构编号
     */
    @TableField("organization_code")
    private String organizationCode;

    /**
     * 机构类型 枚举
     */
    @TableField("organization_type")
    private String organizationType;

    /**
     * 联系人 json
     */
    @TableField("contact_info")
    private String contactInfo;

    /**
     * 地址信息
     */
    @TableField("address_info")
    private String addressInfo;

    /**
     * 银行联行号
     */
    @TableField("inter_bank_no")
    private String interBankNo;

    /**
     * 统一社会信用代码
     */
    @TableField("usc_code")
    private String uscCode;

    /**
     * 账户信息 list json
     */
    @TableField("accounts_info")
    private String accountsInfo;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 机构代码
     */
    @TableField("institution_code")
    private String institutionCode;

    /**
     * 活期存款利率
     */
    @TableField("current_deposit_rate")
    private Long currentDepositRate;

    /**
     * 协定存款利率
     */
    @TableField("agreement_deposit_rate")
    private Long agreementDepositRate;

    /**
     * 协定存款利率到期日
     */
    @TableField("agreement_deposit_rate_due_time")
    private LocalDate agreementDepositRateDueTime;

}
