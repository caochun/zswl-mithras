package cn.zswltech.mithras.contract.mapper.model.contract;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.MortgageItemTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 合同-抵押措施
 * @author vico
 * @date 2022-08-12
 */
@Data
public class ContractMortgage extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 抵押合同编号
     */
    @TableField("mortgage_contract_code")
    private String mortgageContractCode;

    /**
     *抵押物清单id
     **/
    @TableField("file_id")
    private Long fileId;

    /**
     * 所属合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 关联合同code
     */
    @TableField("relat_contracts")
    @IncludeNull
    private String relatContracts;

    /**
     * 抵押合同类型
     */
    @TableField("contract_mortgage_type")
    private String contractMortgageType;

    /**
     * 质押人类型
     */
    @TableField("mortgage_type")
    private String mortgageType;

    /**
    * 抵押人id
    */
    @TableField("mortgage_ids")
    private String mortgageIds;

    /**
    * 抵押物描述
    */
    @TableField("mortgage_describe")
    @IncludeNull
    private String mortgageDescribe;

    /**
     * 是否评估 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("assess")
    private Integer assess;

    /**
     * 评估日期
     */
    @TableField("assess_date")
    private LocalDate assessDate;

    /**
    * 评估公司
    */
    @TableField("appraisal_company")
    @IncludeNull
    private String appraisalCompany;

    /**
    * 评估编号
    */
    @TableField("appraisal_code")
    @IncludeNull
    private String appraisalCode;

    /**
     * 是否最高额标识 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("highest")
    private Integer highest;

    /**
     * 抵押物类型 {@link MortgageItemTypeEnum#name()}
     */
    @TableField("mortgage_item_type")
    private String mortgageItemType;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
