package cn.zswltech.mithras.dto.log;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 操作日志记录
 * @author hspcadmin
 * @date 2025-09-07
 */
@Data
@ApiModel("操作日志记录删除-请求体")
public class SysOperLogRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
