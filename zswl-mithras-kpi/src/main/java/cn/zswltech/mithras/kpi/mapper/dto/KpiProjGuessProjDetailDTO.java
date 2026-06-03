package cn.zswltech.mithras.kpi.mapper.dto;

import lombok.Data;

/**
 * @ClassName KpiProjGuessContractDTO
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/6/15 7:44 下午
 * @Version 1.0
 **/
@Data
public class KpiProjGuessProjDetailDTO {

    //考核部门
    private Long deptId;

    //分配比重类型
    private String divideType;

    //人员
    private Long divideTargetId;

    //奖金-当期值
    private Long bonusCurrent;

    //奖金-累计值
    private Long paymentCurrent;

    //协办奖金-当期值
    private Long bonusCurrentDeputy;

    //协办奖金-累计值
    private Long paymentCurrentDeputy;

    //推荐人奖金-当期值
    private Long bonusCurrentReference;

    //推荐人奖金-累计值
    private Long paymentCurrentReference;

}
