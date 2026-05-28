package cn.zswltech.mithras.dto.report.account;

import cn.zswltech.mithras.dto.report.AccountListBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 征信报送-账户表查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-账户表查询入参")
@EqualsAndHashCode(callSuper = true)
public class AccountListREQ extends AccountListBaseREQ {

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;

    @ApiModelProperty("只查询待报送的数据")
    private Integer onlyToBeReportFlag = 1;

}
