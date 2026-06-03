package cn.zswltech.mithras.kpi.mapper.dto;

import cn.zswltech.mithras.kpi.enums.KpiProjectClassifyEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectWeightTypeEnum;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessProjCompletionDetailParam {

    /**
     * 分配年
     */
    private Integer calculateDateYear;

    /**
     * 分配月
     */
    private Integer calculateDateMonth;

    //合同编号
    private String contractCode;

    //项目名称
    private String projName;

    //所属部门
    private Long deptId;

    //人员
    private Long divideTargetId;


    /**
     * 项目类别 {@link KpiProjectClassifyEnum#name()}
     */
    private String projClassify;



    /**
     * 项目来源 {@link KpiProjectSourceDistributionEnum#name()}
     */
    private String projSource;


    /**
     * 分配类型 人/部门
     * 分配比重类型 {@link KpiProjectWeightTypeEnum#name()}
     **/
    private String divideType;

    //主办
    private Long sponsorUserId;

}
