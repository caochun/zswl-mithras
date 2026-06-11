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
public class RiskControlOpinionMonitorListRSP {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("标题跳转链接,点击标题时跳转到该地址，新标签页")
    private String titleTarget;

    @ApiModelProperty("客户名称")
    private String chiName;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;

    @ApiModelProperty("信息发布日期")
    private LocalDateTime infoPublDate;

    @ApiModelProperty("链接地址")
    private String linkAddress;

    @ApiModelProperty("主体机构代码")
    private String majorOrgCode;

    @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]，这里显示成五角星个数")
    private Integer warnStar;

    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯],这里显示成红绿灯的图标")
    private Integer warnLevel;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("处置意见")
    private String advisement;

    @ApiModelProperty(value = "舆情类型[1:基础舆情,2:工商舆情]")
    private Integer riskType;

    @ApiModelProperty(value = "工商舆情类型")
    private String newTypeOpinion;

    @ApiModelProperty("处置方式 0 处理， 1 关闭")
    private Integer handleResult;

    // 慧眼新增字段
    @ApiModelProperty("慧眼数据ID")
    private String sourceXinsightId;

    @ApiModelProperty("数据来源: FHC-金控, XINSIGHT-慧眼")
    private String dataSource;

    @ApiModelProperty("新闻链接地址")
    private String newsUrl;

    @ApiModelProperty("来源类型: 系统推送, 人工录入")
    private String opinionType;

    @ApiModelProperty("创建人id")
    private Long createBy;

    @ApiModelProperty("创建人姓名")
    private String createName;

    @ApiModelProperty("是否可操作，0不可， 1 可以")
    private Integer operableFlag;

    //关联关系类型
    @ApiModelProperty("关联关系类型: 枚举值")
    private String relationType;

    //关联关系
    @ApiModelProperty("关联关系描述: 中文名称")
    private String relationTypeName;

    //关联客户
    @ApiModelProperty("关联主体")
    private String relateCompanyName;

    // 新增字段
    @ApiModelProperty("关联主体统一社会信用代码")
    private String relateCompanyCode;

    // 新增字段
    @ApiModelProperty("描述说明")
    private String relationDescription;
}
