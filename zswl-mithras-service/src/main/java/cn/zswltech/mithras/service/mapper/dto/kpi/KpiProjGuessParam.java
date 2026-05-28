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
public class KpiProjGuessParam {

    //合同编号
    private String contractCode;

    //项目名称
    private String projName;

    //所属部门
    private Long deptId;

    //核算月份
    private Integer calculateDateYear;

    //核算月份
    private Integer calculateDateMonth;

    //人员
    private Long userId;

    //主办
    private Long sponsorUserId;

}
