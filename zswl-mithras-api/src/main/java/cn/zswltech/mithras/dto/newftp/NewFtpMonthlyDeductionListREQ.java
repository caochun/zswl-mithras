package cn.zswltech.mithras.dto.newftp;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 月度计价指导
 * @date 2023-05-21
 */
@Data
@ApiModel("月度计价指导列表-请求体")
public class NewFtpMonthlyDeductionListREQ extends VersionBaseREQ {

    @ApiModelProperty("主表id")
    private Long mainId;
}
