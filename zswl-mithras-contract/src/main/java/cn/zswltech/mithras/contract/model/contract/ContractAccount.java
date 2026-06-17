package cn.zswltech.mithras.contract.model.contract;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import lombok.EqualsAndHashCode;

/**
 * @description 合同-收款账户表
 * @author vico
 * @date 2022-08-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractAccount extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 方案id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
    * 预留-客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 客户名称
    */
    @TableField("client_name")
    private String clientName;

    /**
    * 账户名称
    */
    @TableField("account_name")
    private String accountName;

    /**
    * 银行账号
    */
    @TableField("account_num")
    private String accountNum;

    /**
    * 开户行
    */
    @TableField("account_address")
    private String accountAddress;

    /**
     * 回款方式编码
     */
    @TableField("repay_way")
    private String repayWay;

    /**
     * 账号用途 {@link ContractAccountUseEnum#name()}
     */
    @TableField("account_use")
    private String accountUse;

    /**
     * 收款方类型 {@link ContractAccountPayeeTypeEnum#name()}
     */
    @TableField("payee_type")
    private String payeeType;

    /**
     * 我方账户id
     */
    @TableField("bank_account_id")
    private Long bankAccountId;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
