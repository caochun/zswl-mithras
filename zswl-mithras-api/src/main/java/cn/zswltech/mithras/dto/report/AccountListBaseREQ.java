package cn.zswltech.mithras.dto.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户表 及其 子表的通用搜索条件
 *
 * @author wangchuanhao
 * @date 2023/1/11 2:11 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountListBaseREQ extends ReportListBaseREQ {

    @ApiModelProperty("借据编号")
    private String paymentApplyCode;

    @ApiModelProperty("业务标识")
    private String businessKey;

}
