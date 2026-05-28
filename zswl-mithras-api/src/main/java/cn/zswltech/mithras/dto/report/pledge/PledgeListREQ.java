package cn.zswltech.mithras.dto.report.pledge;

import cn.zswltech.mithras.dto.report.AccountListBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 征信报送-质押表查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-质押表查询入参")
public class PledgeListREQ extends AccountListBaseREQ {

    @ApiModelProperty("客户名称")
    private String clientName;

}
