package cn.zswltech.mithras.service.mapper.model.kpi;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 绩效-拨备表
 * @author vico
 * @date 2023-06-19
 */
@Data
public class KpiProvisionDetail extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     *拨备id
     **/
    @TableField("provision_id")
    private Long provisionId;

    /**
     * 借据id
     */
    @TableField("receipt_id")
    private Long receiptId;

    /**
     * 借据编号
     */
    @TableField("receipt_code")
    private String receiptCode;

    /**
    * 业务类型。租赁、保理、转租赁
    */
    @TableField("biz_type")
    private String bizType;

    /**
    * 租赁类型。直租、回租、经营性租赁
    */
    @TableField("lease_type")
    private String leaseType;

    /**
    * 项目类别
    */
    @TableField("proj_classify")
    private String projClassify;

    /**
    * 利润所属部门id
    */
    @TableField("profit_belong_dept_id")
    private Long profitBelongDeptId;

    /**
    * 项目主办id
    */
    @TableField("sponsor_user_id")
    private Long sponsorUserId;

    @TableField("client_id")
    private Long clientId;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
    * 合同编号
    */
    @TableField("contract_code")
    private String contractCode;

    /**
    * 到期日
    */
    @TableField("end_date")
    private LocalDate endDate;

    /**
    * 剩余本金
    */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    /**
    * 保证金余额
    */
    @TableField("earnest_balance")
    private Long earnestBalance;

    /**
    * 敞口
    */
    @TableField("exposure")
    private Long exposure;

    /**
    * 风险等级
    */
    @TableField("risk_level")
    private String riskLevel;

    /**
     * 应计利息
     */
    @TableField("accrued_interest")
    private Long accruedInterest;

    /**
     * 下期租金
     */
    @TableField("next_rent")
    private Long nextRent;


    /**
    * 计提比例
    */
    @TableField(value = "withdrawal_ratio", updateStrategy = FieldStrategy.IGNORED)
    private Long withdrawalRatio;

    /**
    * 计提比例-配置
    */
    @TableField("withdrawal_ratio_config")
    private String withdrawalRatioConfig;

    /**
    * 计提比例-手动修改
    */
    @TableField("withdrawal_ratio_hand")
    private Long withdrawalRatioHand;

    /**
    * 本月风险余额
    */
    @TableField("profit_current")
    private Long profitCurrent;

    /**
    * 上月风险余额
    */
    @TableField(value = "profit_total", updateStrategy = FieldStrategy.IGNORED)
    private Long profitTotal;

    /**
    * 本月风险金计提/转回
    */
    @TableField("bonus_current")
    private Long bonusCurrent;

    /**
     * 拨备月份
     */
    @TableField("provision_date")
    private LocalDate provisionDate;

    /**
     * 备注
     */
    @TableField(value = "remark", updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    /**
     * 来源 0 系统, 1 添加
     */
    @TableField("source_type")
    private Integer sourceType;
}
