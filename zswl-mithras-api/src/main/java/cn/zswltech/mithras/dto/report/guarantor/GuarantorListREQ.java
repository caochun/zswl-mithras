package cn.zswltech.mithras.dto.report.guarantor;

import cn.zswltech.mithras.dto.report.AccountListBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 征信报送-保证表查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-保证表查询入参")
public class GuarantorListREQ extends AccountListBaseREQ {

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("业务标识")
    private String businessKey;

}
