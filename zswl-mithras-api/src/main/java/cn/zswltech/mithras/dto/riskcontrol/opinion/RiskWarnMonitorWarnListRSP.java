package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控列表-返回体")
public class RiskWarnMonitorWarnListRSP {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("预警编号")
    private String warnCode;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("客户名称")
    private String chiName;

    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯],这里显示成红绿灯的图标")
    private Integer warnLevel;

    @ApiModelProperty("链接地址")
    private String linkAddress;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;

    @ApiModelProperty("信息发布日期")
    private LocalDateTime dataTime;

    @ApiModelProperty("主体机构代码")
    private String majorOrgCode;

    @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]，这里显示成五角星个数")
    private Integer warnStar;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("所属部门")
    private Long belongDeptId;

    @ApiModelProperty("所属部门名称")
    private String belongDeptName;

    @ApiModelProperty("是否可操作，0不可， 1 可以")
    private Integer operableFlag;

    // 对接慧眼新增字段
    //慧眼数据唯一标识
    private Long sourceXinsightId;

    //数据来源: FHC-金控, XINSIGHT-慧眼
    @ApiModelProperty("数据来源: FHC-金控, XINSIGHT-慧眼")
    private String dataSource;

}
