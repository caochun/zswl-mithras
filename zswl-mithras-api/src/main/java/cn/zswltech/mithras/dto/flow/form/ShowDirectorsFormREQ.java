package cn.zswltech.mithras.dto.flow.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 董事会成员表单入参
 *
 * @author wangchuanhao
 * @date 2022/8/8 2:44 PM
 */
@Data
public class ShowDirectorsFormREQ {

    @ApiModelProperty("董事会成员id列表")
    private List<Long> userIdList;

}
