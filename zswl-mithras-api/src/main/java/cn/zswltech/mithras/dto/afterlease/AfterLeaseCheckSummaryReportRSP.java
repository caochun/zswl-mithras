package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.CommonFileSortWeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/11/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后管理-租后检查总结报告-返回体")
public class AfterLeaseCheckSummaryReportRSP extends CommonFileSortWeight {
    @ApiModelProperty("文件id")
    private Long fileId;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("上传人")
    private String creator;

    @ApiModelProperty("上传时间")
    private String createTime;

    @ApiModelProperty("上传时间戳")
    private long createTimestamp;

    @Override
    protected String sortKey() {
        return this.fileName;
    }

    @Override
    public long createTimestamp() {
        return this.createTimestamp;
    }
}
