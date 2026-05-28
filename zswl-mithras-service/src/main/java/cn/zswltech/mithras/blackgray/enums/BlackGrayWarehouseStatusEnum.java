package cn.zswltech.mithras.blackgray.enums;/*
package cn.zswltech.mithras.biz.blackgray.enums;

import cn.zswltech.mithras.common.dict.DictionaryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

*/
/**
 * @ClassName BlackgrayBusinessENUM
 * @Description
 * @Date 2023/11/29 4:56 下午
 * @Version 1.0
 **//*

@Getter
@AllArgsConstructor
public enum BlackGrayWarehouseStatusEnum {
    DRAFT("草稿"),
    TASK_TO_BE_ASSIGNED( "任务待分配"),
    BUSINESS_PENDING_REVIEW( "业务待审核"),
    RISK_PENDING_REVIEW( "风险待审核"),
    IN_APPROVAL( "审批中"),
    IN_FINAL_JUDGMENT( "终审中"),
    PASS( "已通过"),
    RETURNED( "已退回"),
    ;

    private final String desc;


    public static List<DictionaryDTO> toDictionaryDTO(){
        return Arrays.stream(values()).map(t ->
                DictionaryDTO.builder()
                        .value(t.name())
                        .label(t.getDesc())
                        .build()
        ).collect(Collectors.toList());
    }

}
*/
