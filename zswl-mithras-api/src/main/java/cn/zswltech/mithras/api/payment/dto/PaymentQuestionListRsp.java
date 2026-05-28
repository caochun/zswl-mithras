package cn.zswltech.mithras.api.payment.dto;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 11:11
 */
@ApiModel("问卷调查-出参")
@Data
public class PaymentQuestionListRsp extends ListBaseRSP {
    @ApiModelProperty("paymentId")
    private Long paymentId;
    @ApiModelProperty("问题id")
    private Long questionId;
    @ApiModelProperty("序号")
    private String seqCode;
    @ApiModelProperty("问题类型")
    private String questionType;
    @ApiModelProperty("问题")
    private String question;
    @ApiModelProperty("是否满足")
    private String questionAnswer;
    @ApiModelProperty("备注")
    private String remarks;
}
