package cn.zswltech.mithras.contract.model.contract;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 合同-承租人表
 * @author vico
 * @date 2022-08-12
 */
@Data
public class ContractTenantry extends BaseModel implements Serializable, IEntity {

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
    * 承租人id
    */
    @TableField("lessee_id")
    private Long lesseeId;

    /**
    * 承租人类型,LesseeTypeEnum
    */
    @TableField("lessee_type")
    private String lesseeType;

    /**
    * 承租人名称
    */
    @TableField("lessee_name")
    private String lesseeName;

    /**
     * 租赁物文件类型
     */
    @TableField("lease_item_file_type")
    private String leaseItemFileType;

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
    * 存量风险敞口 由系统实时计算，在合同生效后，保存生效时的快照用
    */
    @TableField("stock_risk_exposure")
    private Long stockRiskExposure;

    /**
    * 指定联系人
    */
    @TableField("contact_id")
    @IncludeNull
    private Long contactId;

    /**
    * 是否上报征信 0不上报，1上报
    */
    @TableField("is_report")
    private Integer isReport;

    /**
     * 租金往来方
     */
    @TableField("rent_concat_account_id")
    private String rentConcatAccountId;

    /**
     * 租金往来方
     */
    @TableField("rent_concat_account_name")
    private String rentConcatAccountName;


    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
