package cn.zswltech.mithras.dto.riskcontrol.opinion;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class RiskWarnMonitorOpinionListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;

    @ApiModelProperty("链接地址")
    private String linkAddress;

    @ApiModelProperty("客户名称")
    private String chiName;

    @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]，这里显示成五角星个数")
    private Integer warnStar;

    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯],这里显示成红绿灯的图标")
    private Integer warnLevel;

    @ApiModelProperty("信息发布日期")
    private LocalDateTime infoPublDate;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("是否可操作，0不可， 1 可以")
    private Integer operableFlag;

    //数据来源: FHC-金控, XINSIGHT-慧眼
    @TableField("data_source")
    private String dataSource;

    //新闻链接地址
    @TableField("news_url")
    private String newsUrl;

    //关联关系类型
    @TableField("relation_type")
    private String relationType;

    //关联关系
    @TableField("relation_type_name")
    private String relationTypeName;

    //关联客户
    @TableField("relate_company_name")
    private String relateCompanyName;

    // 新增字段:关联主体统一社会信用代码
    @TableField("relation_company_code")
    private String relateCompanyCode;

    // 新增字段:描述说明
    @TableField("relation_description")
    private String relationDescription;

    @ApiModelProperty("来源类型: 系统推送, 人工录入")
    private String opinionType;

    @ApiModelProperty("创建人id")
    private Long createBy;

    @ApiModelProperty("创建人姓名")
    private String createName;

}
