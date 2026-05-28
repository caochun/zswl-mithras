package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractIndexREQ
 * @Description 项目绩效测算表-请求体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessProjManagerDetailREQ extends PageReq {


    /**
     * 分配年
     */
    @ApiModelProperty("分配年")
    private Integer calculateDateYear;

    /**
     * 分配月
     */
    @ApiModelProperty("分配月")
    private Integer calculateDateMonth;

    //合同编号
    private String contractCode;

    //项目名称
    private String projName;

    //所属部门
    @ApiModelProperty("所属部门")
    private Long deptId;

    //分配比重类型
    @ApiModelProperty("分配比重类型 KpiProjectWeightTypeEnum")
    private String divideType;

    //人员
    private Long divideTargetId;

    @ApiModelProperty("项目类别 KpiProjectClassifyEnum#name()")
    private String projClassify;

    //主办
    private Long sponsorUserId;

}
