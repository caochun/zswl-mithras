package cn.zswltech.mithras.dto.flow.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 董事会成员表单返回值
 *
 * @author wangchuanhao
 * @date 2022/8/8 2:44 PM
 */
@Data
public class ShowDirectorsFormRSP {

    @ApiModelProperty("董事会成员列表")
    private List<UserRSP> userList;

    @Data
    public static class UserRSP {

        @ApiModelProperty("董事会成员id")
        private Long userId;

        @ApiModelProperty("董事会成员名称")
        private String userName;

    }

}
