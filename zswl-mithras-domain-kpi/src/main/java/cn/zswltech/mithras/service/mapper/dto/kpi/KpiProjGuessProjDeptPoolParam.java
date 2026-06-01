package cn.zswltech.mithras.service.mapper.dto.kpi;

import lombok.Data;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessProjDeptPoolParam {


    //所属部门
    private Long deptId;

    //分配比重类型
    private String divideType;

    //人员
    private Long divideTargetId;

    private Long calculateDateYear;

    private Long calculateDateMonth;


}
