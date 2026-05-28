package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class RatingParamInfoDuoApprovalRSP {

    @ApiModelProperty("问卷信息")
    private Map<String,Map<String,List<RatingParamFieldApprovalRSP>>> info;

    @ApiModelProperty("试算次数")
    private int executeCount;

    @ApiModelProperty("试算最大次数")
    private int executeCountLimit = 3;

    @ApiModelProperty("答题结果,有草稿时返回")
    private Collection<RatingParamRSP> ratingParam;



}
