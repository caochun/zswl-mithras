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
public class KpiProjGuessProjCompletionDetailDTO {


    /**
     * 分配类型 人/部门
     * 分配比重类型 {@link KpiProjectWeightTypeEnum#name()}
     **/
    private String divideType;

    private Long deptId;

    /**
     * 分配目标id
     */
    private Long divideTarget;

    //主办奖金-当期值
    private Long bonusCurrent;

    //投放值
    private Long paymentCurrent;

}
