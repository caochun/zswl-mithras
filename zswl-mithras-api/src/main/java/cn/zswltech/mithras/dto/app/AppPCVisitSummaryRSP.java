package cn.zswltech.mithras.dto.app;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 生效
 *
 * @author zhouning
 * @date 2024/10/14 11:56 PM
 */
@Data
@ApiModel("融租易访客管理拜访汇总-返回体")
public class AppPCVisitSummaryRSP {


    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("拜访总次数")
    private ValueUnitDTO visitCount;

    @ApiModelProperty("拜访总家数")
    private ValueUnitDTO clientCount;

    @ApiModelProperty("拜访人信息")
    private List<ObjectInfo> objectInfoList;

    @Data
    public static class ObjectInfo {
        @ApiModelProperty("拜访人id")
        private Long userId;

        @ApiModelProperty("拜访人名字")
        private String createdName;

        @ApiModelProperty("拜访总次数")
        private Integer visitCount;

        @ApiModelProperty("拜访总家数")
        private Integer clientCount;
    }

}
