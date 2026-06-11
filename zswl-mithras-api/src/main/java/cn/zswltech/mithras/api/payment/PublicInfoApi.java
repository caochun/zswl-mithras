package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.pubinfo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/10 10:57
 * @description 文件上传和删除的接口使用通用的API，切记需要添加FunctionCode!!!
 */
@Api(tags = "公开信息-API")
@RequestMapping(path = "/public/info")
public interface PublicInfoApi {

    @ApiOperation(value = "公开信息-客户列表查询")
    @PostMapping(path = "/client/list")
    R<List<PublicInfoClientListRSP>> clientList(@RequestBody @Valid PublicInfoClientListREQ req);

    @ApiOperation(value = "公开信息-项目经理提交前校验")
    @PostMapping(path = "/submit/check")
    R<Boolean> queryTableCheck(@RequestBody @Valid PublicInfoSubmitCheckREQ req);

    @ApiOperation(value = "公开信息-查询指定区间表格")
    @PostMapping(path = "/query/interval/table")
    R<PublicInfoQueryRSP> queryTableResult(@RequestBody @Valid PublicInfoQueryREQ req);

    @ApiOperation(value = "公开信息-修改表格内容")
    @PostMapping(path = "/modify/table/content")
    R<Void> modifyTableContent(@RequestBody @Valid PublicInfoModifyContentREQ req);

    @ApiOperation(value = "公开信息-新建")
    @PostMapping(path = "/create/interval/table")
    R<Long> createIntervalTable(@RequestBody @Valid PublicInfoCreateREQ req);

    @ApiOperation(value = "公开信息-导出")
    @PostMapping(path = "/export")
    R<Void> export(@RequestBody @Valid PublicInfoExportREQ req);

    @ApiOperation(value = "公开信息-删除指定区间表格")
    @PostMapping(path = "/delete/interval/table")
    R<Void> deleteIntervalTable(@RequestBody @Valid PublicInfoDeleteREQ req);

    @ApiOperation(value = "公开信息-检查是否存在客户没有维护公开信息(返回空数组即校验通过)")
    @PostMapping(path = "/check")
    R<List<String>> check(@RequestBody @Valid PublicInfoCheckREQ req);
}
