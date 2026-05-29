package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.MaterialsType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租赁物资料清单枚举
 */
@Getter
@AllArgsConstructor
public enum LeaseFileTypeEnums implements PullDown, IMaterialsTypeConvert {

    PROJECT_MANAGER_UPLOAD_OWNERSHIP("项目经理上传-权属文件", 1),
    PROJECT_MANAGER_UPLOAD_DUPLICATE_CHECK("项目经理上传-查重文件", 2),
    PROJECT_MANAGER_UPLOAD_OTHER("项目经理上传-其他文件", 3),
    OPERATION_MANAGER_UPLOAD_OWNERSHIP("运营经理上传-权属文件", 10),
    OPERATION_MANAGER_UPLOAD_DUPLICATE_CHECK("运营经理上传-查重文件", 11),
    OPERATION_MANAGER_UPLOAD_OTHER("运营经理上传-其他文件", 12),
    OPERATION_MANAGER_REVIEW_SUBMISSION("租赁物审核意见书", 13),
    LEASE_INSERT("租赁物创建一租赁物审核意见书", 14),
    LEASE_UPDATE("租赁物变更一租赁物审核意见书", 15),
    LEASE_VAT_INVOICE("项目经理上传-增值税发票", 16),
    ;

    public final String display;
    public final Integer order;

    public static MaterialsType of(String code) {
        for (MaterialsType value : MaterialsType.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String businessModule() {
        return BusinessModuleEnum.LEASE_DATA_LIST.name();
    }

    @Override
    public String display() {
        return display;
    }
}
