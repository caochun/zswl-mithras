package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author luyi
 */
@Data
@ApiModel("下拉数据-返回体")
public class ReportSelectRSP {
    private Long id;
    @ApiModelProperty("管报类型")
    private String reportType;
    private String reportTypeName;
    private String reportSource;
    private String reportKey;
    @ApiModelProperty("下拉展示文本")
    private String label;
    @ApiModelProperty("下拉选项值")
    private String value;
    @ApiModelProperty("排序字段")
    private Integer sortNum;
    @ApiModelProperty(value = "子集")
    private List<ReportSelectRSP> children;
    @ApiModelProperty("子枚举的名称")
    private String childSelectName;


    public ReportSelectRSP(Long id, String reportType, String reportTypeName, String label, String value, Integer sortNum) {
        this.id = id;
        this.reportType = reportType;
        this.reportTypeName = reportTypeName;
        this.label = label;
        this.value = value;
        this.sortNum = sortNum;
    }

    public ReportSelectRSP() {
    }
}
