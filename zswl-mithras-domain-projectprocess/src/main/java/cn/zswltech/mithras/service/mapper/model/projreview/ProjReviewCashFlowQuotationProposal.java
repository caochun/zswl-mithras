package cn.zswltech.mithras.service.mapper.model.projreview;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author
 * 报价方案-现金流计划表
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjReviewCashFlowQuotationProposal extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = 8944536070896554147L;
    /**
     * 现金流量明细表主键id
     */
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目评审记录id
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 日期
     */
    @TableField("cash_flow_date")
    private LocalDate cashFlowDate;

    /**
     * 期项
     */
    @TableField("cash_flow_phase")
    private Integer cashFlowPhase;

    /**
     * 现金流金额
     */
    @TableField("cash_flow_amount")
    private Long cashFlowAmount;

    /**
     * 租金(租赁、转租赁)/应收保理款(保理)/回收款(债权转让)
     */
    @TableField("rent")
    private Long rent;

    /**
     * 本金
     */
    @TableField("principal")
    private Long principal;

    /**
     * 利息
     */
    @TableField("interest")
    private Long interest;

    /**
     * 剩余本金
     */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    @Override
    public void setMainId(Long id) {
        this.projectId = id;
    }

    @Override
    public Long getMainId() {
        return this.projectId;
    }
}
