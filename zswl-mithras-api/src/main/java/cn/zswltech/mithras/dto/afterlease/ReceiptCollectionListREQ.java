package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 借据卡逾期汇总-请求体
 *
 * @author jackerhe
 * @date 2022/11/17 3:09 PM
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("借据卡逾期汇总请求体")
@Data
public class ReceiptCollectionListREQ extends PageReq {

    @ApiModelProperty("借据卡编号")
    @NotNull(message = "借据编号不能为空")
    private String paymentCode;

}
