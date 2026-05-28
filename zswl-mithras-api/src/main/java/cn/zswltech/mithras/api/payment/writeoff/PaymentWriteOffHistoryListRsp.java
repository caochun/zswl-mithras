package cn.zswltech.mithras.api.payment.writeoff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 14:05
 */
@ApiModel("核销记录查询-出参")
@Data
public class PaymentWriteOffHistoryListRsp {

    @ApiModelProperty("paymentid")
    private Long paymentid;
    @ApiModelProperty("operate_time")
    private LocalDateTime operateTime;
    @ApiModelProperty("operate_person_id")
    private Long operatePersonId;
    @ApiModelProperty("operate_person_name")
    private String operatePersonName;
    @ApiModelProperty("operate_data_type")
    private String operateDataType;
    @ApiModelProperty("operate_data_code")
    private String operateDataCode;
    @ApiModelProperty("operation")
    private String operation;
    @ApiModelProperty("data_status")
    private String dataStatus;
}
