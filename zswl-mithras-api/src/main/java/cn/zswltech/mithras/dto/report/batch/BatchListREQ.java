package cn.zswltech.mithras.dto.report.batch;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 征信报送-批次查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/13 9:46 AM
 */
@Data
@ApiModel("征信报送-批次查询入参")
public class BatchListREQ extends PageReq {

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("报送时间")
    private LocalDateTime reportTimeFrom;

    @ApiModelProperty("报送时间到")
    private LocalDateTime reportTimeTo;

}
