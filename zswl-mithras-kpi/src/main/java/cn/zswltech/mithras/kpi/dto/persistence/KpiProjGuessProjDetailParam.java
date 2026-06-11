package cn.zswltech.mithras.kpi.dto.persistence;

import lombok.Data;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessProjDetailParam {

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

    //分配比重类型
    private String divideType;

    //人员
    private Long divideTargetId;

    private String projClassify;

    //主办
    private Long sponsorUserId;

}
