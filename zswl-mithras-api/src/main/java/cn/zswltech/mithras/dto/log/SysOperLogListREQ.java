package cn.zswltech.mithras.dto.log;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
/**
 * @description 操作日志记录
 * @author hspcadmin
 * @date 2025-09-07
 */
@Data
@ApiModel("操作日志记录列表-请求体")
public class SysOperLogListREQ extends PageReq {

}
