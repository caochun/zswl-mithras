package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.foundation.annotation.NotCompareColumn;
import cn.zswltech.mithras.projectprocess.enums.projestablish.FactoringType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.ZrType;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vico
 * 合同基本信息表
 * @date 2022-08-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SponsorField(value = "projSponsorUserId", belongDeptField = "bizDeptId")
public class ContractBaseInfo extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户id
     **/
    @TableField("client_id")
    private Long clientId;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 咨询合同编号
     */
    @TableField("consulting_contract_code")
    private String consultingContractCode;

    /**
     * 剩余可用额度(元)
     */
    @TableField("remain_available_quota")
    private Long remainAvailableQuota;

    /**
     * 项目名称
     */
    @TableField("proj_name")
    private String projName;

    /**
     * 项目编号
     */
    @TableField("proj_code")
    private String projCode;

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
     * 保理类型 {@link FactoringType#name()}
     */
    @TableField("factoring_type")
    private String factoringType;

    /**
     * 转让类型 {@link ZrType#name()}
     */
    @TableField("zr_type")
    private String zrType;

    /**
     * 转让方
     */
    @IncludeNull
    @TableField("assignor")
    private String assignor;

    /**
     * 项目类型：公共事业类、省内国（央）企、其他
     *
     * @deprecated 项目评审该字段作废
     */
    @Deprecated
    @TableField("project_type")
    private String projectType;

    /**
     * todo 风险等级->项目类型
     */
    @TableField("risk_level")
    private String riskLevel;

    /**
     * 项目来源：存量翻单、渠道介绍、自主开发
     */
    @TableField("proj_source")
    private String projSource;

    /**
     * 资金用途
     */
    @TableField("funds_purpose")
    private String fundsPurpose;

    /**
     * 项目背景
     */
    @TableField("proj_background")
    private String projBackground;

    /**
     * 项目主办用户id
     */
    @TableField("proj_sponsor_user_id")
    private Long projSponsorUserId;

    /**
     * 项目协办方用户id列表
     */
    @TableField("proj_cosponsor_user_ids")
    private String projCosponsorUserIds;

    /**
     * 业务部门id
     */
    @TableField("biz_dept_id")
    private Long bizDeptId;

    /**
     * 业务部门负责人id
     */
    @TableField("biz_dept_leader_id")
    private Long bizDeptLeaderId;

    /**
     * 业务分管领导id
     */
    @TableField("biz_division_leader_id")
    private Long bizDivisionLeaderId;

    /**
     * 关联的评审id
     */
    @TableField("proj_review_id")
    private Long projReviewId;

    /**
     * 合同状态
     */
    @TableField("contract_status")
    private String contractStatus;

    /**
     * 流程状态
     */
    @NotCompareColumn
    @TableField("contract_process_status")
    private String contractProcessStatus;

    /**
     * 流程变更子类型状态
     */
    @TableField(value = "contract_process_change_status", updateStrategy = FieldStrategy.IGNORED)
    private String contractProcessChangeStatus;

    /**
     * 概算起租日
     **/
    @TableField("estimated_lease_date")
    private LocalDate estimatedLeaseDate;

    /**
     * 实际起租日
     **/
    @TableField("actual_lease_date")
    private LocalDate actualLeaseDate;

    /**
     * 实际结束日
     */
    @TableField("actual_finish_date")
    private LocalDate actualFinishDate;

    /**
     * 计划付款时间-合同生效时间
     **/
    @TableField("payment_plan_date")
    private LocalDate paymentPlanDate;

    /**
     * 计划付款金额-合同金额
     */
    @TableField("apply_credit_amount")
    private Long applyCreditAmount;

    /**
     * 支付申请次数
     **/
    @TableField("payment_count")
    private Long paymentCount;

    @TableField("risk_control_manager_id")
    private Long riskControlManagerId;

    /**
     * 项目分类
     **/
    @TableField("proj_item")
    private String projItem;

    /**
     * 额度是否可循环
     */
    @TableField("credit_amount_loop")
    private Integer creditAmountLoop;

    /**
     * 本年度合同序号
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 合同创建年限
     */
    @TableField("contract_year")
    private Integer contractYear;

    /**
     * 合同结清时间
     */
    @TableField("settle_time")
    private LocalDateTime settleTime;

    /**
     * 逾期催收状态 0可催收，1不可催收
     **/
    @TableField("overdue_collection_flag")
    private Long overdueCollectionFlag;

    @TableField("remark")
    private String remark;

    /**
     * 变更说明
     */
    @Deprecated
    @IncludeNull
    @TableField("change_remark")
    private String changeRemark;

    /**
     * 风控行业分类
     */
    @TableField(value = "risk_control_industry_classify")
    private String riskControlIndustryClassify;

    @TableField(value = "item_list_header")
    private String itemListHeader;

//    @Deprecated
//    @TableField(value = "item_total_amount")
//    private Long itemTotalAmount;


    @TableField(value = "lease_item_info_id")
    private Long leaseItemInfoId;

    @TableField(value = "adjust_remark")
    private String adjustRemark;

    @TableField(value = "income_confirm_type")
    private String incomeConfirmType;

    /**
     * 是否签约
     */
    @TableField(value = "is_signed")
    private Integer isSigned;

    /**
     * 是否中登初始登记保存
     */
    @TableField("is_save_register")
    private Integer isSaveRegister;

    /**
     * 非数据库字段，用于选择性生成合同的
     */
    @TableField(exist = false)
    private List<String> generateContractTypeList;

    /**
     * 租赁物类型
     **/
    @TableField("lease_item_types")
    private String leaseItemTypes;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}
