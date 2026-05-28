package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
public class MaterialsListIdType extends VersionBaseREQ {

    public static final Integer EDIT_AREA = 1;
    public static final Integer VERSIONED = 2;

    ///**
    //     * 之前的预览是是编辑区id和版本表的version组合作为参数，进行预览的
    //     * 现在有些情况是直接传版本表的id进来。所以区分下id类型
    //     * idType:1:编辑；2：版本表
    //     */
    @ApiModelProperty
    private Integer idType;
}
