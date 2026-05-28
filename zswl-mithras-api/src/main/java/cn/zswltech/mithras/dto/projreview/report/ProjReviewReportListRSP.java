package cn.zswltech.mithras.dto.projreview.report;

import cn.zswltech.mithras.dto.CommonFileSortWeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Data
@ApiModel("报告清单列表-返回体")
public class ProjReviewReportListRSP extends CommonFileSortWeight {
    @ApiModelProperty("报告文件id")
    private Long id;
    @ApiModelProperty("报告名称")
    private String typeName;
    @ApiModelProperty("材料类型")
    private String materialsType;
    @ApiModelProperty("材料子类型")
    private String materialsSubType;
    @ApiModelProperty("文档名称")
    private String fileName;
    @ApiModelProperty("上传人")
    private String creator;
    @ApiModelProperty("上传时间")
    private String createTime;
    @ApiModelProperty("上传时间戳")
    private long createTimestamp;
    @ApiModelProperty("排序优先级")
    private int sort;

    @Override
    protected String sortKey() {
        return this.fileName;
    }

    @Override
    public long createTimestamp() {
        return this.createTimestamp;
    }
}
