package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/13 16:48
 */
@Data
@ApiModel("季度指导detail-返回体")
public class FtpQuarterlyGuidanceDetailRsp {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 年度
     */
    @ApiModelProperty("year")
    private Integer year;

    /**
     * 季度
     */
    @ApiModelProperty("quarter")
    private Integer quarter;

    /**
     * 审批状态
     */
    @ApiModelProperty("guidance_process_status")
    private String guidanceProcessStatus;

    @ApiModelProperty("guidance_record_status")
    private String guidanceRecordStatus;
}
