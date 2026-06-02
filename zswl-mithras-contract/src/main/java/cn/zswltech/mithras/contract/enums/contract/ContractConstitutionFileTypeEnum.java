package cn.zswltech.mithras.contract.enums.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author yupengfei
 * @date 2024/4/18 10:14
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ContractConstitutionFileTypeEnum {

    TENANT("承租人"),
    GUARANTOR("担保措施"),
    PLEDGE("质押措施"),
    MORTGAGE("抵押措施"),
    ;

    String display;
}
