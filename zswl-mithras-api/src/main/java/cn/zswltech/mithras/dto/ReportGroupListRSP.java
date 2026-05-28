package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author luyi
 */
@Data
@ApiModel("下拉数据-返回体")
@AllArgsConstructor
@NoArgsConstructor
public class ReportGroupListRSP {
    private String key;
    @ApiModelProperty("报表名称")
    private String title;
    @ApiModelProperty("报表地址")
    private String href;
    @ApiModelProperty("报表是否可刷新")
    private Boolean hasRefresh;
    @ApiModelProperty("是否子节点")
    private Boolean isLeaf;
    @ApiModelProperty(value = "子集")
    private List<ReportGroupListRSP> children;
}
