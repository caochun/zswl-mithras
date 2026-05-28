package cn.zswltech.mithras.dto.version;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
public class CommonVersionListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("主数据id")
    private Long mainId;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("版本类型")
    private Integer type;

    @ApiModelProperty("是否可和上版本比较")
    private Integer canCompare;

    @ApiModelProperty("业务模块枚举")
    private String module;

    private LocalDateTime createTime;

    private Long createBy;

    private LocalDateTime updateTime;

    private Long updateBy;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;
}
