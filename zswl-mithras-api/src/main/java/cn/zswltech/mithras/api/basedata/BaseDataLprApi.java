package cn.zswltech.mithras.api.basedata;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.SingleFileREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataLprDetailRSP;
import cn.zswltech.mithras.dto.basedata.BaseDataLprSaveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
@Api(tags = "基础数据-LPR设置相关接口")
public interface BaseDataLprApi {
    @ApiOperation("保存LPR设置")
    @PostMapping("/basedata/lpr/save")
    R<Long> save(@RequestBody @Valid BaseDataLprSaveREQ baseDataLprSaveREQ);

    @ApiOperation("删除LPR设置")
    @PostMapping("/basedata/lpr/delete")
    R<Void> delete(@RequestBody @Valid SinglePkREQ singlePkREQ);

    @ApiOperation("批量导入LPR设置")
    @PostMapping("/basedata/lpr/import")
    R<Void> importExcel(@Valid SingleFileREQ singleFileREQ);

    @ApiOperation("LPR设置分页列表")
    @PostMapping("/basedata/lpr/pagelist")
    R<PageR<BaseDataLprDetailRSP>> pageList(@RequestBody @Valid PageReq pageReq);

    @ApiOperation("下载模板")
    @GetMapping("/basedata/lpr/template/download")
    R<String> downloadTemplate();

    @ApiOperation("获取最新一期的LPR信息")
    @PostMapping("/basedata/lpr/latest")
    R<BaseDataLprDetailRSP> getLatestLpr();
}
