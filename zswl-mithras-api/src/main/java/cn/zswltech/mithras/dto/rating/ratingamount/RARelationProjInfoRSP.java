package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class RARelationProjInfoRSP {

    @ApiModelProperty("租赁类型")
    private String leaseTypes;

    @ApiModelProperty("评估主体ID")
    private Long evaluationSubjectId;

    @ApiModelProperty("评估主体名称")
    private String evaluationSubjectName;

    @ApiModelProperty(value = "评估主体区域")
    private String evaluationSubjectAreaName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("行业分类")
    private String projectClassify;

    @ApiModelProperty("地区分类")
    private String regionalProjectClassify;

    @ApiModelProperty("资金用途")
    private String fundsPurpose;

    @ApiModelProperty("评估主体营业收入")
    private Long evaluationSubjectOperatingIncome;



}
