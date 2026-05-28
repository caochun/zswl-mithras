package cn.zswltech.mithras.service.mapper.model.assetclassify;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyReviewStatusEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifySuggestEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("asset_classify_client")
public class AssetClassifyClient extends BaseModel implements IEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主表id
     */
    @TableField("asset_classify_id")
    private Long assetClassifyId;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 投放金额
     */
    @TableField("amount")
    private Long amount;

    /**
     * 剩余租期
     */
    @TableField("remaining_term")
    private Integer remainingTerm;

    /**
     * 定性调整 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("qualitative_adjust")
    private Integer qualitativeAdjust;

    /**
     * 上次分类结果 {@link AssetClassifyResultEnum#name()}
     */
    @TableField("last_classify_result")
    private String lastClassifyResult;

    /**
     * 初分分类结果 {@link AssetClassifyResultEnum#name()}
     */
    @TableField("init_classify_result")
    private String initClassifyResult;

    /**
     * 本次分类结果 {@link AssetClassifyResultEnum#name()}
     */
    @TableField("classify_result")
    private String classifyResult;

    /**
     *计提比例
     **/
    @TableField("award_ratio")
    private Long awardRatio;

    /**
     *逾期天数
     **/
    @TableField("overdue_days")
    private Integer overdueDays;

    /**
     *逾期金额 该季度该客户的逾期金额
     **/
    @TableField("overdue_amount")
    private Long overdueAmount;

    /**
     *剩余风险敞口 所有剩余的本金+利息
     **/
    /*@TableField("residual_exposure")
    private Long residualExposure;*/

    /**
     *存量风险敞口
     **/
    @TableField(value = "stock_risk_exposure")
    private Long stockRiskExposure;

    /**
     *资产余额（亿元）
     **/
    @TableField(value = "asset_balance")
    private Long assetBalance;

    /**
     *合同到期日
     **/
    @TableField("contract_expiration_date")
    private LocalDate contractExpirationDate;

    /**
     * 在租合同编号
     **/
    @TableField("start_rent_contract_codes")
    private String startRentContractCodes;

    /**
     * 在租合同剩余本金 与在租合同编号顺序一一对应
     **/
    @TableField("start_rent_contractRemainingPrincipal")
    private String startRentContractRemainingPrincipal;

    /**
     *{@link YesOrNoNumberEnum }
     * 1.调整 0不调整
     **/
    @TableField("suggest_flag")
    private int suggestFlag;

    /**
     * 建议分类结果 {@link AssetClassifySuggestEnum#name()}
     */
    @TableField("suggest_result")
    private String suggestResult;

    /**
     * 建议原因
     **/
    @TableField("suggest_reason")
    private String suggestReason;

    /**
     * 备注
     **/
    @TableField("remark")
    private String remark;

    /**
     * 复核状态 {@link AssetClassifyReviewStatusEnum#name()}
     * 修改为新状态
     */
    @TableField("review_status")
    private String reviewStatus;

    /**
     * 复核通过时间 --> 生效时间
     */
    @TableField(value = "review_pass_time", updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime reviewPassTime;

    /**
     * 客户归属部门id
     */
    @TableField("belong_dept_id")
    private Long belongDeptId;

    /**
     * 客户归属主办id
     */
    @TableField("belong_sponsor_id")
    private Long belongSponsorId;

    /**
     * 是否经过董事会流程 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("board_meeting")
    private Integer boardMeeting;

    @TableField("provisions")
    private String provisions;

    @TableField("risk_factor")
    private String riskFactor;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }

    @Data
    public static class ProvisionData {
        private Long contractId;
        private Long receiptId;
        private String receiptCode;
        private Integer withdrawalRatio;
    }
}
