package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@Data
@ApiModel("基础数据-乙方账户列表-返回体")
public class ContractAccountPayListRSP {

    @ApiModelProperty("账户名称")
    private String accountName;

}
