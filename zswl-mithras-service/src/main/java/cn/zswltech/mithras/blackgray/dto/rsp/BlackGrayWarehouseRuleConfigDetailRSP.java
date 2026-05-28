package cn.zswltech.mithras.blackgray.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单库-入库原因参数配置
 * @author 
 * @date 2024-01-18
 */
@Data
@ApiModel("黑灰名单库-入库原因参数配置详情-返回体")
public class BlackGrayWarehouseRuleConfigDetailRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;
    /**
    * 维护机构
    */
    @ApiModelProperty(value = "维护机构")
    private String orgCode;
    /**
    * 规则编号
    */
    @ApiModelProperty(value = "规则编号")
    private String ruleNumber;
    /**
    * 递增序列
    */
    @ApiModelProperty(value = "递增序列")
    private Integer ruleSequence;
    /**
    * 规则名称
    */
    @ApiModelProperty(value = "规则名称")
    private String ruleName;
    /**
    * 层级 0金控定义
    */
    @ApiModelProperty(value = "层级 0金控定义")
    private Integer level;
    /**
    * 父id
    */
    @ApiModelProperty(value = "父id")
    private Long parentId;
    /**
    * 所属金控类型主id，即一级id
    */
    @ApiModelProperty(value = "所属金控类型主id，即一级id")
    private Long mainId;
    /**
    * 状态 0启用，1禁用
    */
    @ApiModelProperty(value = "状态 1启用，0禁用")
    private Integer status;
    /**
    * 黑灰标识
    */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部")
    private String source;

    @ApiModelProperty(value = "适用业务类型")
    private List<String> suitBusiness;
    /**
     * 适用机构
     */
    @ApiModelProperty(value = "适用机构")
    private List<String> suitOrg;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;
    /**
    * 更新时间
    */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date updateTime;
    /**
    * 创建人、发起人
    */
    @ApiModelProperty(value = "创建人、发起人")
    private Long createBy;
    /**
    * 最后更新人id
    */
    @ApiModelProperty(value = "最后更新人id")
    private Long updateBy;
}
