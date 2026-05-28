package cn.zswltech.mithras.dto.associationreport;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AssociationReportListREQ extends PageReq {

    @ApiModelProperty("报表名称")
    private String reportCategoryName;

    @ApiModelProperty("报表实例周期类型")
    private List<String> reportPeriodCategoryList;

    @ApiModelProperty("报送状态")
    private List<String> reportStatusList;

    @ApiModelProperty("报表类型code")
    private List<String> reportCategoryCodeList;

    @ApiModelProperty("流程状态")
    private List<String> processStatusList;

    @ApiModelProperty("报表实例年份")
    private Integer reportYear;

    @ApiModelProperty("报表实例周期类型")
    private String reportPeriodCategory;

    @ApiModelProperty("报表实例周期")
    private Integer reportPeriod;

    @ApiModelProperty("来源页面 query:普遍查询列表页面 apply:上报申请列表页面 reported：已报送列表页面")
    private String refereePage;

    @ApiModelProperty("上报时间-起")
    private LocalDate reportTimeFrom;

    @ApiModelProperty("上报时间-止")
    private LocalDate reportTimeTo;
}
