package cn.zswltech.mithras.contract.enums.overdue;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.Getter;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/6 15:47
 */
public enum LitigationFileType implements PullDown, IMaterialsTypeConvert {
    /**
     * 一审相关材料
     */
    FIRST_AUDIT_RELATED("一审相关材料",1),
    /**
     * 二审相关材料
     */
    SECOND_AUDIT_RELATED("二审相关材料",2),
    /**
     * 再审相关材料
     */
    THIRD_AUDIT_RELATED("再审相关材料",3),
    /**
     * 执行相关材料
     */
    EXECUTION_RELATED("执行相关材料",4),
    /**
     * 保全相关材料
     */
    GUARANTEE_RELATED("保全相关材料",5),
    /**
     *  其他
     */
    OTHER("其他",6);
    ;

    private final String display;
    @Getter
    private final int sort;

    LitigationFileType(String name,int sort) {
        this.display = name;
        this.sort = sort;
    }

    @Override
    public String businessModule() {
        return "LITIGATION_REGISTRATION";
    }

    @Override
    public String display() {
        return display;
    }

}
