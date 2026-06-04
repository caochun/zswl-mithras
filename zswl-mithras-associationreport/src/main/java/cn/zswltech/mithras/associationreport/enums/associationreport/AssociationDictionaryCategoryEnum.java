package cn.zswltech.mithras.associationreport.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/4/19
 * @description
 */
@AllArgsConstructor
@Getter
public enum AssociationDictionaryCategoryEnum {
    // 以下是外部约定枚举
    PTY00221("企业类别代码"),
    PTY00003("经济成分类别代码"),
    PTY00021("股东性质代码"),
    PUB00247("现任职务代码"),
    DIMLS803("学历学位代码"),
    PTY00019("规模代码"),
    EVT00051("逾期天数代码"),
    DIMLS079("合同类型代码"),
    PUB00234("行业分类代码"),
    PTY00212("增信情况代码"),
    EVT00052("融资业务类型代码"),
    PUB00250("案件类别代码"),
    // 以下是内部自定义枚举
    LasdType("自定义租赁物类型映射")
    ;

    private final String display;
}
