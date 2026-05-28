package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractIndexREQ
 * @Description 项目绩效测算表-详情-请求体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessDetailREQ extends PageReq {
    //合同id
    private Long contractId;

    //核算年份
    private Integer calculateDateYear;

    //核算月份
    private Integer calculateDateMonth;

    //核算月份部门ID
    private Long deptId;

    //分配比重类型
    @ApiModelProperty("分配比重类型 KpiProjectWeightTypeEnum")
    private String divideType;

    //分配目标id
    private Long divideTargetId;
}
