package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RatingParamFieldApprovalRSP {

    @ApiModelProperty("字段标识")
    private String fieldName;

    @ApiModelProperty("字段显示名")
    private String fieldComment;

    @ApiModelProperty("数据类型")
    private String dataType;

    @ApiModelProperty("取数方式 RatingFetchMethodEnum")
    private String fetchMethod;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("指标值")
    private Object fieldValue;

    @ApiModelProperty("字段分组编码")
    private String groupCode;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("计算公式")
    private String formal;

    @ApiModelProperty("枚举项[如果dataType是枚举/枚举集合时]")
    private List<DictionaryDTO> enumList;

    @ApiModelProperty("审批状态")
    private Boolean approvalStatus;

    @ApiModelProperty("数据是否变化-流程中被退回，再发起时返回")
    private Boolean isChange = false;

    @ApiModelProperty("审批意见")
    private String approvalOpinion;

    @ApiModelProperty("是否为系统取数的区域模型指标")
    private Boolean isAreaModelIndex = false;

    @ApiModelProperty("业务数据id")
    private Long bizId;

    @ApiModelProperty("定量指标是否可编辑")
    private Boolean canEdit = false;


    @Data
    public static class DictionaryDTO{
        private Object value;
        private String label;
        private String enLable;
        private Object extra;
        private List<DictionaryDTO> enumList;
    }

}
