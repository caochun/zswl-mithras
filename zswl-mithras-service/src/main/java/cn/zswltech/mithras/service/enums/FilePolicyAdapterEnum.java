package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.service.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/4/18/17:12
 * @description 文件策略适配器， 在协同流程中文件上传是使用
 */
@Getter
@AllArgsConstructor
public enum FilePolicyAdapterEnum {
    /**
     * 使用该通用方法的前提是材料的枚举的【描述属性】名称必须是 display ！！！
     * 否则不能使该通用方法
     */
    PROJ_ESTABLISH("项目立项资料", BusinessModuleEnum.PROJ_ESTABLISH, ProjEstablishMaterialsEnum.values()),
    PROJ_REVIEW("项目评审资料", BusinessModuleEnum.PROJ_REVIEW, ProjReviewMaterialsEnum.values()),
    CONTRACT("项目合同资料", BusinessModuleEnum.CONTRACT, ContractTypeEnum.values()),
    ;

    private final String display;
    private final BusinessModuleEnum businessModuleEnum;
    private final Enum[] businessEnum;

    public static Enum[] getFileTypeEnumByModuleEnum(BusinessModuleEnum businessModuleEnum){
        for (FilePolicyAdapterEnum adapterEnum : FilePolicyAdapterEnum.values()) {
            if(adapterEnum.businessModuleEnum.equals(businessModuleEnum)){
                return adapterEnum.businessEnum;
            }
        }
        return new Enum[0];
    }
}
