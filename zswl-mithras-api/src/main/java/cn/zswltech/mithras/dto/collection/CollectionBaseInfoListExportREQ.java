package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2022-08-15
 **/
@Data
@ApiModel("收款核销列表-导出excel-请求题")
public class CollectionBaseInfoListExportREQ {

    @ApiModelProperty("需要导出的数据id列表")
    @NotEmpty
    private List<Long> exportIdList;

}
