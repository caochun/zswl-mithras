package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yangxiong
 * @since 2023-08-14
 * @description 租赁物列表信息实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class LeaseholdPropertyRSP implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "创建人、发起人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "最后更新人id")
    private String updateBy;

    @ApiModelProperty(value = "最后更新时间")
    private String updateTime;
}
