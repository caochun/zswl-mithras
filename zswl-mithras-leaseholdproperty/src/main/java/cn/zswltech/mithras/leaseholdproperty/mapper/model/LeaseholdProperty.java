package cn.zswltech.mithras.leaseholdproperty.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 租赁物类型表
 *
 * @author yangxiong
 * @TableName leasehold_property
 */
@TableName(value = "leasehold_property")
@Data
@Builder
public class LeaseholdProperty implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 创建人、发起人
     */
    @ApiModelProperty(value = "创建人、发起人")
    private Long createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 最后更新人id
     */
    @ApiModelProperty(value = "最后更新人id")
    private Long updateBy;

    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}