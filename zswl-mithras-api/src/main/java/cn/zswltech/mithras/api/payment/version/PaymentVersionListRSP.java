package cn.zswltech.mithras.api.payment.version;

import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 立项信息版本表
 * @author zhaozhengkang
 * @date 2022-07-19
 */
@Data
@ApiModel("付款版本表列表-返回体")
public class PaymentVersionListRSP extends CommonVersionListRSP {

    @ApiModelProperty("申请付款金额")
    private Long applyPaymentAmount;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("变更时间")
    private LocalDateTime gmtModify;

}
