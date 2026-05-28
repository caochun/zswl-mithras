package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RatingParamFieldRSP {

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

    @ApiModelProperty("字段分组编码")
    private String groupCode;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("计算公式")
    private String formal;

    @ApiModelProperty("枚举项[如果dataType是枚举/枚举集合时]")
    private List<DictionaryDTO> enumList;


    @Data
    public static class DictionaryDTO{
        private Object value;
        private String label;
        private String enLable;
        private Object extra;
    }

}
