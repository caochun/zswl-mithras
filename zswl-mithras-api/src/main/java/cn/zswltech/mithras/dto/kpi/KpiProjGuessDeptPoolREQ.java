package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;

/**
 * @ClassName KpiProjGuessContractIndexREQ
 * @Description 项目绩效测算表-请求体
 * @Author jackerhe
 * @Date 2023/6/15 4:28 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessDeptPoolREQ extends PageReq {

    //所属部门
    private Long deptId;

    //分配比重类型
    private String divideType;

    //人员
    private Long divideTargetId;

    private Long calculateDateYear;

    private Long calculateDateMonth;

}
