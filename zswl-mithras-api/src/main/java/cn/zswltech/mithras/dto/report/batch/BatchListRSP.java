package cn.zswltech.mithras.dto.report.batch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 征信报送-批次查询返回值
 *
 * @author wangchuanhao
 * @date 2023/1/13 9:46 AM
 */
@Data
@ApiModel("征信报送-批次查询返回值")
public class BatchListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("报送时间")
    private LocalDateTime reportTime;

    @ApiModelProperty("报送员id")
    private Long reportorId;

    @ApiModelProperty("报送员名称")
    private String reporterName;

}
