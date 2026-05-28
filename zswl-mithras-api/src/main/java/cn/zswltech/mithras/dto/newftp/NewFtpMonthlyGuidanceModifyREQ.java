package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description ftp报价表
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("ftp报价表编辑-请求体")
public class NewFtpMonthlyGuidanceModifyREQ {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "ftp值")
    private Integer value;

}
