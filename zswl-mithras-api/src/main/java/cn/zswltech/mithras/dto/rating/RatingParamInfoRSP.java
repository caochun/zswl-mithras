package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class RatingParamInfoRSP {

    @ApiModelProperty("问卷信息")
    private Map<String,List<RatingParamFieldRSP>> info;

    @ApiModelProperty("试算次数")
    private int executeCount;

    @ApiModelProperty("答题结果,有草稿时返回")
    private Collection<RatingParamRSP> ratingParam;



}
