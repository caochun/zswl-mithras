package cn.zswltech.mithras.dto.fund.receiptrepay;
import cn.zswltech.mithras.dto.ListBaseRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 资金管理-融资管理-对方收款账户
 * @author zhaozhengkang
 * @date 2023-02-22
 */
@Data
@ApiModel("资金管理-融资管理-对方收款账户列表-返回体")
public class FundReceiptAccountListRSP extends ListBaseRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 融资id
    */
    @ApiModelProperty(value = "融资id")
    private Long financingId;

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

}
