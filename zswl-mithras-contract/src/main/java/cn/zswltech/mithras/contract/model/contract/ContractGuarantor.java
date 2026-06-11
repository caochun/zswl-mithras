package cn.zswltech.mithras.contract.model.contract;

import cn.zswltech.mithras.contract.enums.contract.JointGuaranteeMarkEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @description 合同-担保措施表
 * @author vico
 * @date 2022-08-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractGuarantor extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     *合同编号
     **/
    @Transient
    @TableField(exist = false)
    private String contractCode;

    /**
     * 保证合同编号
     */
    @TableField("guarantor_contract_code")
    private String guarantorContractCode;

    /**
    * 关联合同code
    */
    @TableField("relat_contracts")
    @IncludeNull
    private String relatContracts;
    /**
     * 担保人类型
     */
    @TableField("guarantor_type")
    private String guarantorType;

    /**
     * 决议类型：股东会决议、董事会决议、股东决定、执行董事决定
     */
    @TableField("resolution_type")
    private String resolutionType;

    /**
     * 决议文件
     */
    @TableField("resolution_file_id")
    private String resolutionFileId;


    /**
     * 担保人id
     */
    @TableField("guarantor_ids")
    private String guarantorIds;

    /**
    * 担保方式-连带责任担保、一般担保
    */
    @TableField("guarantee_method")
    private String guaranteeMethod;

    /**
     * 联保标志 {@link JointGuaranteeMarkEnum#name()}
     */
    @TableField("joint_guarantee_mark")
    private String jointGuaranteeMark;

    /**
     * 担保金额，当联保标志为单人保证或联保是该字段有值
     */
    @TableField(value = "guarantee_amount_single", updateStrategy = FieldStrategy.IGNORED)
    private Long guaranteeAmountSingle;

    /**
     * 担保金额，当担保标志为多人分保时该字段有值
     * 字符串为JSON数组
     * eg: [{"clientId":1, "amount":100000}, {"clientId":2, "amount":200000}]
     * 可使用 {@link GuaranteeMultipleJsonWrapper} 进行反序列化
     */
    @TableField(value = "guarantee_amount_multiple", updateStrategy = FieldStrategy.IGNORED)
    private String guaranteeAmountMultiple;

    /**
    * 是否上报征信 0不上报，1上报
    */
    @TableField("is_report")
    private Integer isReport;

    /**
     * 指定联系人id
     */
    @IncludeNull
    @TableField("contact_id")
    private Long contactId;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return this.contractId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GuaranteeMultipleJsonWrapper {
        private Long clientId;
        private Long amount;
    }
}
