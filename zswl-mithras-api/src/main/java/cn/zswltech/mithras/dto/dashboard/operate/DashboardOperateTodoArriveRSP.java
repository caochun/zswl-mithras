package cn.zswltech.mithras.dto.dashboard.operate;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
public class DashboardOperateTodoArriveRSP{

    @ApiModelProperty("流程key")
    private List<String> modelKeyList;

    @ApiModelProperty("流程名称")
    private String modelName;

    @ApiModelProperty("内容")
    private List<content> content;

    @ApiModelProperty("流程id")
    private List<String> processInstanceIdList;

    @Data
    public static class content{

        @ApiModelProperty("流程id")
        private List<String> processInstanceIdList;

        @ApiModelProperty("节点display")
        private String activityDisplay;

        @ApiModelProperty("数量")
        private Integer count;

    }

}
