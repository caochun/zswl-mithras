package cn.zswltech.mithras.blackgray.service.external;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 风险画像-单一客户-关联关系数据返回
 * @Author:fengming.dai
 */
@ApiModel
@Data
@Accessors(chain = true)
public class ImageRelationDTO {

    @ApiModelProperty("所属集团")
    String group;

    @ApiModelProperty("集团主企业")
    String controllerName;

    /*List<MemberInfo> list;

    public void emptyData() {
        list = Lists.newArrayList();
    }*/
}
