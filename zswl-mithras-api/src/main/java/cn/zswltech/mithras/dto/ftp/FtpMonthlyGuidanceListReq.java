package cn.zswltech.mithras.dto.ftp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;

import java.time.LocalDate;
import java.util.List;

/**
 * @description 月度指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
@ApiModel("月度指导列表-请求体")
public class FtpMonthlyGuidanceListReq extends PageReq {
    @ApiModelProperty("创建人id")
    private Long createBy;
    @ApiModelProperty("年份")
    private Integer year;
    @ApiModelProperty("月份")
    private Integer month;
    @ApiModelProperty("流程状态")
    private String processStatus;
    @ApiModelProperty("创建日期from")
    private LocalDate createDateFrom;
    @ApiModelProperty("创建日期to")
    private LocalDate createDateTo;
    @ApiModelProperty("更新日期from")
    private LocalDate updateDateFrom;
    @ApiModelProperty("更新日期to")
    private LocalDate updateDateTo;

}
