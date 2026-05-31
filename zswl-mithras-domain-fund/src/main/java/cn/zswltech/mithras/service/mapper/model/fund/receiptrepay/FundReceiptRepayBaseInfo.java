package cn.zswltech.mithras.service.mapper.model.fund.receiptrepay;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
@Data
public class FundReceiptRepayBaseInfo extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收付款编号
     */
    @TableField("receipt_repay_code")
    private String receiptRepayCode;

    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 融资类型 直融 or 间融
     */
    @TableField("financing_type")
    private String financingType;

    /**
     * 间融取自业务类型，直融取自项目类别
     */
    @TableField("financing_biz_type")
    private String financingBizType;

    @TableField("financing_amount")
    private Long financingAmount;

    @TableField("next_repay_date")
    private LocalDate nextRepayDate;

//    /**
//     * 融资机构id
//     */
//    @TableField("financing_org_id")
//    private String financingOrgId;
//
//    /**
//     * 融资机构名称
//     */
//    @TableField("financing_org_name")
//    private String financingOrgNames;

    /**
     * financing_version
     */
    @TableField("financing_version")
    private String financingVersion;

    @TableField("remark")
    @IncludeNull
    private String remark;

    /**
     * 收付款状态
     */
    @TableField("receipt_repay_state")
    private String receiptRepayState;

    /**
     * 流程状态
     */
    @TableField("process_state")
    private String processState;

    /**
     * 资金经理
     */
    @TableField("fund_manager")
    private Long fundManager;

    @TableField("financing_channel")
    private String financingChannel;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }

    public boolean isDirect() {
        return Objects.equals("DIRECT", financingType);
    }
}
