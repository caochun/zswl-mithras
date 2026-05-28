package cn.zswltech.mithras.dto.groupcreditestablish.baseinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
  * @Description 评级id
  * @ClassName GroupCreditEstalishBaseInfoUpdateRatingREQ.java
  * @author tangxh
  * @Date 16:59
  * @Version 1.0
  **/
@NoArgsConstructor
@Data
public class GroupCreditEstalishBaseInfoUpdateRatingREQ {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    public GroupCreditEstalishBaseInfoUpdateRatingREQ(Long id) {
        this.id = id;
    }
}
