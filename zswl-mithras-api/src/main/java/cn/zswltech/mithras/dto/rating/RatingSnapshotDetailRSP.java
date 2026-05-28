package cn.zswltech.mithras.dto.rating;

import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingSnapshotDetailRSP {

    /**
     * 评分模型快照 key: dataType
     */
    @ApiModelProperty(value = "评分模型快照")
    private Map<String, List<RatingParamFieldRSP>> contentRsp;

    /**
     * 用户选择
     */
    @ApiModelProperty(value = "用户选择")
    private Map<String, RatingParamRSP> resultRsp;

    /**
     * 上一次的用户选择
     */
    @ApiModelProperty(value = "用户选择")
    private Map<String, RatingParamRSP> lastResultRsp;

    /**
     * 评分结果
     */
    @ApiModelProperty(value = "评分结果")
    private DecisionExecuteResult scoreRsp;

    @ApiModelProperty("试算次数")
    private int executeCount;

    public RatingSnapshotDetailRSP(Map<String, List<RatingParamFieldRSP>> contentRsp, Map<String, RatingParamRSP> resultRsp, DecisionExecuteResult scoreRsp) {
        this.contentRsp = contentRsp;
        this.resultRsp = resultRsp;
        this.scoreRsp = scoreRsp;
    }
}
