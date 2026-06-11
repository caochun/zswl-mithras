package cn.zswltech.mithras.finance.mapper.model.finance;

import cn.zswltech.mithras.finance.enums.third.FinancialAccountAgeRecordStatus;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 帐龄主表
 * @author vico
 * @date 2024-09-10
 */
@Data
public class FinanceAccountAgeBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 截止日期
    */
    @TableField("deadline")
    private LocalDate deadline;

    /**
    * 核算组织编码 默认 10000396
    */
    @TableField("accountancy_organization_number")
    private String accountancyOrganizationNumber;

    /**
    * 核算组织名称 默认 浙江浙商融资租赁有限公司
    */
    @TableField("accountancy_organization_name")
    private String accountancyOrganizationName;

    /**
    * 状态
     * {@link FinancialAccountAgeRecordStatus#name()}
    */
    @TableField("status")
    private String status;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @TableField("deleted")
    private Integer deleted;

}
