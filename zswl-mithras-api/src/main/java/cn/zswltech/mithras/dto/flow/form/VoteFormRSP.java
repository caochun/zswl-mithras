package cn.zswltech.mithras.dto.flow.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 投票结果统计表单
 *
 * @author wangchuanhao
 * @date 2022/8/8 2:55 PM
 */
@Data
public class VoteFormRSP {

    @ApiModelProperty("投票结果列表")
    private List<VoteRSP> voteRSPList;

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class VoteRSP {

        @ApiModelProperty("投票人id")
        private Long handlerId;

        @ApiModelProperty("投票人名称")
        private String handlerName;

        @ApiModelProperty("任务id")
        private String taskId;

        @ApiModelProperty("投票时间")
        private LocalDateTime voteTime;

        @ApiModelProperty("投票备注")
        private String message;

        @ApiModelProperty("投票类型")
        private String type;

        @ApiModelProperty("投票类型名称")
        private String typeName;
    }

}
