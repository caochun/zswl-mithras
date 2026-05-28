package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/20 10:59
 */
@ApiModel("收付款详情-请求体")
@Data
public class FundReceiptRepayBaseInfoDetailREQ extends VersionBaseREQ {
    @ApiModelProperty(value = "主键")
    private Long id;
}
