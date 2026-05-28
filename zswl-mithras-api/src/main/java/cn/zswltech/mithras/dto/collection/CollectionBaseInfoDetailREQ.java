package cn.zswltech.mithras.dto.collection;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @create: 2022-08-15
 **/

@Data
public class CollectionBaseInfoDetailREQ {


    @NotNull
    @ApiModelProperty("收款核销id")
    private Long id;

}
