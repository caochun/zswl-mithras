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
public class RatingSnapshotDetailDuoRSP {

    /**
     * 评分模型快照 key: dataType
     */
    @ApiModelProperty(value = "评分模型快照")
    private Map<String ,Map<String, List<RatingParamFieldRSP>>> contentRsp;

    private List<RatingParamFieldRSP> contentList;

    /**
     * 用户选择
     */
    @ApiModelProperty(value = "用户选择")
    private Map<String, RatingParamRSP> resultRsp;

    /**
     * 评分结果
     */
    @ApiModelProperty(value = "评分结果")
    private DecisionExecuteResult scoreRsp;

    @ApiModelProperty("试算次数")
    private int executeCount;

    public RatingSnapshotDetailDuoRSP(Map<String ,Map<String, List<RatingParamFieldRSP>>> contentRsp, Map<String, RatingParamRSP> resultRsp, DecisionExecuteResult scoreRsp) {
        this.contentRsp = contentRsp;
        this.resultRsp = resultRsp;
        this.scoreRsp = scoreRsp;
    }
}
