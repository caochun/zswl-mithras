package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/12/13
 * @description
 */
@Data
public class YunYingDaiBanDetailREQ {
    public static final String QUERY_JOB_YYJB = "YYJB";
    public static final String QUERY_JOB_YYFH = "YYFH";
    public static final String QUERY_JOB_YYFZR = "YYFZR";

    @ApiModelProperty("流程实例ID")
    private List<String> processInstanceIdList;

    // 查询参数作废，不再使用
    @Deprecated
    @ApiModelProperty("岗位，运营经办-YYJB，运营复核-YYFH，运营负责人-YYFZR")
    private String job;
}
