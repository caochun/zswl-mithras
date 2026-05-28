package cn.zswltech.mithras.dto.contract.account;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 合同-收款账户表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-收款账户表列表-返回体")
public class ContractAccountListRSP extends ListBaseRSP {

    /**
    * 方案id
    */
    @ApiModelProperty(value = "方案id")
    private Long id;

    /**
    * 所属合同id
    */
    @ApiModelProperty(value = "所属合同id")
    private Long contractId;

    /**
    * 预留-客户id
    */
    @ApiModelProperty(value = "预留-客户id")
    private Long clientId;

    /**
    * 客户名称
    */
    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
    * 账户名称
    */
    @ApiModelProperty(value = "账户名称")
    private String accountName;

    /**
    * 银行账号
    */
    @ApiModelProperty(value = "银行账号")
    private String accountNum;

    /**
    * 开户行
    */
    @ApiModelProperty(value = "开户行")
    private String accountAddress;

    /**
     * 还款方式
     */
    @ApiModelProperty(value = "还款方式")
    private String repayWay;

    /**
     * 账号用途
     */
    @ApiModelProperty(value = "账号用途")
    private String accountUse;

    @ApiModelProperty("收款方类型")
    private String payeeType;

    @ApiModelProperty("我方账户id")
    private Long bankAccountId;

}
