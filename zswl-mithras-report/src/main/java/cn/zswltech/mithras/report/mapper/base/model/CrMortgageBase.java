package cn.zswltech.mithras.report.mapper.base.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.*;

import java.time.LocalDate;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;

/**
 * @description 征信报送-抵押表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrMortgageBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 借据编号
     */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
     * 借据本金(单位：0.0001元)
     */
    @TableField(value = "apply_payment_amount")
    private Long applyPaymentAmount;

    /**
    * 抵押合同编号
    */
    @TableField(value = "mortgage_contract_code", updateStrategy = FieldStrategy.IGNORED)
    private String mortgageContractCode;

    /**
     * 抵押合同编号
     */
    @TableField(value = "mortgage_contract_code_2", updateStrategy = FieldStrategy.IGNORED)
    private String mortgageContractCode2;

    /**
    * 最高额担保标识（0否，1是）
    */
    @TableField(value = "max_flag", updateStrategy = FieldStrategy.IGNORED)
    private Integer maxFlag;

    /**
    * 序号
    */
    @TableField(value = "sequence", updateStrategy = FieldStrategy.IGNORED)
    private String sequence;

    /**
    * 抵押物种类
    */
    @TableField(value = "type", updateStrategy = FieldStrategy.IGNORED)
    private String type;

    /**
    * 抵押物识别号类型
    */
    @TableField(value = "model_type", updateStrategy = FieldStrategy.IGNORED)
    private String modelType;

    /**
    * 抵押物唯一识别号
    */
    @TableField(value = "model", updateStrategy = FieldStrategy.IGNORED)
    private String model;

    /**
    * 评估价值（单位：0.0001元）
    */
    @TableField(value = "assessed_value", updateStrategy = FieldStrategy.IGNORED)
    private Long assessedValue;

    /**
    * 评估机构类型
    */
    @TableField(value = "appraisal_company_type", updateStrategy = FieldStrategy.IGNORED)
    private String appraisalCompanyType;

    /**
    * 评估日期
    */
    @TableField(value = "assessed_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate assessedDate;

    /**
    * 抵押人类型
    */
    @TableField(value = "mortgage_type", updateStrategy = FieldStrategy.IGNORED)
    private String mortgageType;

    /**
    * 抵押人名称
    */
    @TableField(value = "mortgage_name", updateStrategy = FieldStrategy.IGNORED)
    private String mortgageName;

    /**
    * 抵押人身份标识类型
    */
    @TableField(value = "mortgage_id_type", updateStrategy = FieldStrategy.IGNORED)
    private String mortgageIdType;

    /**
    * 抵押人身份标识号码
    */
    @TableField(value = "mortgage_id", updateStrategy = FieldStrategy.IGNORED)
    private String mortgageId;

    /**
    * 抵押物描述
    */
    @TableField(value = "mortgage_describe", updateStrategy = FieldStrategy.IGNORED)
    private String mortgageDescribe;

    /**
     * 付款id
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    public String genBusinessKey(Long contractMortgageItemId) {
        return String.format("%s_%s_%s", paymentApplyCode, paymentId, contractMortgageItemId);
    }

}
