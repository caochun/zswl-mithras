package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @ClassName CollectionReconciliationLetterREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/28 9:31 上午
 * @Version 1.0
 **/
@Data
@ApiModel("对账函-请求体")
public class CollectionReconciliationLetterREQ {
    @NotNull(message = "对账时间不能为空")
    private LocalDate date;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("用户电话")
    private String userPhone;

    @ApiModelProperty("地址")
    private String address;
}
