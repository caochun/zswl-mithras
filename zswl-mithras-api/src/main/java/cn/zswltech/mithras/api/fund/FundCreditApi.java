package cn.zswltech.mithras.api.fund;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.fund.*;
import cn.zswltech.mithras.dto.materialsfile.FundMaterialListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description fund_credit
 * @date 2022-12-13
 */
@Api(tags = "授信管理-接口")
public interface FundCreditApi {

    @ApiOperation("新增授信")
    @PostMapping("/fund/credit/add")
    R<Long> add(@RequestBody @Valid FundCreditAddREQ req);

    @ApiOperation("修改授信")
    @PostMapping("/fund/credit/modify")
    R<Void> modify(@RequestBody @Valid FundCreditModifyREQ req);

    @ApiOperation("授信列表")
    @PostMapping("/fund/credit/list")
    R<FundCreditListRSP> list(@RequestBody @Valid FundCreditListREQ req);

    @ApiOperation("授信详情")
    @PostMapping("/fund/credit/detail")
    R<FundCreditDetailRSP> detail(@RequestBody @Valid FundCreditDetailREQ req);

    @ApiOperation("删除授信")
    @PostMapping("/fund/credit/remove")
    R<Void> remove(@RequestBody @Valid FundCreditRemoveREQ req);

    @ApiOperation("根据机构查询授信额度")
    @PostMapping("/fund/credit/limit")
    R<CreditLimitRsp> limit(@RequestBody @Valid CreditLimitReq req);


    @ApiOperation("文件列表")
    @PostMapping("/fund/credit/file/list")
    R<List<FundMaterialListRSP>> fileList(@RequestBody @Valid FundCreditDetailREQ req);

    @ApiOperation("文件上传")
    @PostMapping("/fund/credit/file/upload")
    R<Void> fileUpload(@RequestParam MultipartFile file,
                       @RequestParam Long belongId);

    @ApiOperation("文件删除")
    @PostMapping("/fund/credit/file/remove")
    R<Void> fileRemove(@RequestBody @Valid FundCreditRemoveREQ req);

    @ApiOperation("文件下载")
    @GetMapping("/fund/credit/file/download")
    R<FileListRSP> fileDownload(@Valid FundCreditRemoveREQ req) throws IOException;

    @ApiOperation("获取机构下的有效授信")
    @PostMapping(path = "/fund/credit/effect/list")
    R<List<FundCreditListRSP.FundCreditList>> listEffect(@RequestBody @Valid FundCreditListREQ req);

    @ApiOperation("授信失效")
    @PostMapping(path = "/fund/credit/invalid")
    R<Void> invalid(@RequestBody @Valid FundCreditSingletonIdREQ req);

    @ApiOperation("使用详情")
    @PostMapping(path = "/fund/credit/limitDetail")
    R<FundCreditLimitDetailRSP> limitDetail(@RequestBody @Valid FundCreditSingletonIdREQ req);
}