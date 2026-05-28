package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@ApiModel("金融局报表-获取报表上报列表请求体")
public class AssociationReportApplyREQ  {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id,对应工作流中的businessKey的值")
    @NotNull(message = "id为空")
    private Long id;

    @ApiModelProperty("报表类型code")
    private List<String> reportCategoryCodeList;

    @ApiModelProperty("报表实例周期类型")
    private List<String> reportPeriodCategoryList;


    private List<String> reportInstanceIdList;

    @ApiModelProperty("报表实例年份")
    private Integer reportYear;

    @ApiModelProperty("报表实例周期类型")
    private String reportPeriodCategory;

    @ApiModelProperty("报表实例周期")
    private Integer reportPeriod;


}
