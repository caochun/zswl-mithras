package cn.zswltech.mithras.api.log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.log.SysOperLogAddREQ;
import cn.zswltech.mithras.dto.log.SysOperLogModifyREQ;
import cn.zswltech.mithras.dto.log.SysOperLogListREQ;
import cn.zswltech.mithras.dto.log.SysOperLogListRSP;
import cn.zswltech.mithras.dto.log.SysOperLogRemoveREQ;

/**
* @description 操作日志记录
* @author hspcadmin
* @date 2025-09-07
*/
@Api(tags = "操作日志记录-接口")
public interface SysOperLogApi {

    @ApiOperation("新增操作日志记录")
    @PostMapping("/sys/oper/log/add")
    R<Void> add(@RequestBody @Valid SysOperLogAddREQ req);

    @ApiOperation("修改操作日志记录")
    @PostMapping("/sys/oper/log/modify")
    R<Void> modify(@RequestBody @Valid SysOperLogModifyREQ req);

    @ApiOperation("操作日志记录列表")
    @PostMapping("/sys/oper/log/list")
    R<PageR<SysOperLogListRSP>> list(@RequestBody @Valid SysOperLogListREQ req);

    @ApiOperation("删除操作日志记录")
    @PostMapping("/sys/oper/log/remove")
    R<Void> remove(@RequestBody @Valid SysOperLogRemoveREQ req);

}