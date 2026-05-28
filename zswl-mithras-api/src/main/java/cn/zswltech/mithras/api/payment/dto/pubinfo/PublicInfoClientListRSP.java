package cn.zswltech.mithras.api.payment.dto.pubinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/10 11:10
 * @description
 */
@Data
@ApiModel(value = "公开信息-客户列表查询响应体")
public class PublicInfoClientListRSP {

    @ApiModelProperty(value = "客户ID")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "客户类型")
    private String clientType;

    // 客户类型
    @ApiModelProperty(value = "原始客户类型")
    private String originClientType;

    @ApiModelProperty(value = "是否存在必填未填")
    private Boolean isExistRequiredNotFill;

    @ApiModelProperty(value = "查询区间列表")
    private List<PublicInfoClientListRSP.QueryIntervalListRSP> queryIntervalList;

    @Data
    public static class QueryIntervalListRSP {

        @ApiModelProperty(value = "查询区间ID")
        private Long id;

        @ApiModelProperty(value = "是否存在必填未填")
        private Boolean isExistRequiredNotFill;

        @ApiModelProperty(value = "查询开始时间")
        private String queryFrom;

        @ApiModelProperty(value = "查询结束时间")
        private String queryTo;
    }
}
