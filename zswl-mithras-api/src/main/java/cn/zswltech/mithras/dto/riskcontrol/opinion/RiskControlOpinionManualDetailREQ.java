package cn.zswltech.mithras.dto.riskcontrol.opinion;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("人工录入舆情-保存按钮-请求体")
public class RiskControlOpinionManualDetailREQ {

    @ApiModelProperty("舆情id")
    private Long id;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("链接地址")
    private String linkAddress;

    @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]，这里显示成五角星个数")
    private Integer warnStar;

    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯],这里显示成红绿灯的图标")
    private Integer warnLevel;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("主体机构代码")
    private String majorOrgCode;

    @ApiModelProperty("信息发布日期")
    private String infoPublDate;

    @ApiModelProperty("处置方式 0 处理， 1 关闭")
    private Integer handleResult;

    @ApiModelProperty("处置意见")
    private String advisement;

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
