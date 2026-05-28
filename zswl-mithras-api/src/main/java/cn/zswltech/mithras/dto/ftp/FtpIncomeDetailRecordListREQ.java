package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 资金管理-融资管理-ftp收益记录表
 * @author vico
 * @date 2025-07-15
 */
@Data
@ApiModel("资金管理-融资管理-ftp收益记录表列表-请求体")
public class FtpIncomeDetailRecordListREQ {

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    @NotNull(message = "融资信息不能为空")
    private Long fundFinancingId;

    /**
     * 融资类型 直融 or 间融 financingtypeenum
     */
    @ApiModelProperty("融资类型")
    private String financingType;

}
