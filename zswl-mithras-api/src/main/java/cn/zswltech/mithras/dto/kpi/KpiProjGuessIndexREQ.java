package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName KpiProjGuessContractIndexREQ
 * @Description 项目绩效测算表-请求体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessIndexREQ extends PageReq {

    //合同编号
    private String contractCode;

    //项目名称
    private String projName;

    //所属部门
    private Long deptId;

    //核算月份
    private LocalDate calculateDate;

    //分配比重类型
    @ApiModelProperty("分配比重类型 KpiProjectWeightTypeEnum")
    private String divideType;

    //人员
    private Long divideTargetId;

    @ApiModelProperty("核算年份")
    private Integer calculateDateYear;

    @ApiModelProperty("核算月份")
    private Integer calculateDateMonth;

}
