package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zswl
 */
@Data
@ApiModel("基础数据-我方账户-列表-请求体")
public class BaseDataBankAccountQueryREQ {
    @ApiModelProperty("银行名称")
    private String accountBank;

    @ApiModelProperty("银行账户")
    private String accountNumber;

    @ApiModelProperty("账户性质")
    private String accountType;

}
