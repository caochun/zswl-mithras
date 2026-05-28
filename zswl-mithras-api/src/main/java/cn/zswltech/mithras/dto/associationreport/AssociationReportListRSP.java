package cn.zswltech.mithras.dto.associationreport;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationReportListRSP {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("报表编码")
    private String reportCategoryCode;

    @ApiModelProperty("报表名称")
    private String reportCategoryName;

    @ApiModelProperty("是否为重报数据，0-否，1-是")
    private Integer isRetry;

    @ApiModelProperty("报表实例编号")
    private String reportInstanceId;

    @ApiModelProperty("报表实例年份")
    private Integer reportYear;

    @ApiModelProperty("报表实例周期类型")
    private String reportPeriodCategory;

    @ApiModelProperty("报表实例周期")
    private Integer reportPeriod;

    @ApiModelProperty("数据来源（创建方式）")
    private String dataSource;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("上报时间")
    private LocalDateTime reportTime;

    @ApiModelProperty("上报状态")
    private String reportStatus;

    @ApiModelProperty("流程状态")
    private String processStatus;

    @ApiModelProperty("上报流程状态")
    private String pushProcessStatus;

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
