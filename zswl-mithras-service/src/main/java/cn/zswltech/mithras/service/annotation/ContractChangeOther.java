package cn.zswltech.mithras.service.annotation;

import cn.zswltech.mithras.service.enums.contract.ContractChangeTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ContractChangeOther {

    String contractId() default "contractId";

    ContractChangeTypeEnum twoStatus() default ContractChangeTypeEnum.OTHER;


    /**
     * 参数下标
     * @return
     */
    int paramIndex() default 0;

}

