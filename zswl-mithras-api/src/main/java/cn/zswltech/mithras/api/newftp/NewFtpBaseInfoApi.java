package cn.zswltech.mithras.api.newftp;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.*;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author zhaozhengkang
 * @description ftp主表
 * @date 2023-05-21
 */
@Api(tags = "ftp主表-接口")
public interface NewFtpBaseInfoApi {

    @ApiOperation("新增ftp主表")
    @PostMapping("/new/ftp/base/info/add")
    R<Long> add(@RequestBody @Valid NewFtpBaseInfoAddREQ req);

    @ApiOperation("主表信息")
    @PostMapping("/new/ftp/base/info/detail")
    R<NewFtpBaseInfoDetailRSP> detail(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("ftp计算")
    @PostMapping("/new/ftp/base/info/calculate")
    R<Void> calculate(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("ftp主表列表")
    @PostMapping("/new/ftp/base/info/list")
    R<PageR<NewFtpBaseInfoListRSP>> list(@RequestBody @Valid NewFtpBaseInfoListREQ req);

    @ApiOperation("ftp文字描述信息列表")
    @PostMapping("/new/ftp/base/info/desclist")
    R<List<NewFtpDescriptionTextListRsp>> listDesc(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("ftp文字描述信息更新")
    @PostMapping("/new/ftp/base/info/descmodify")
    R<Void> modifDesc(@RequestBody @Valid NewFtpDescriptionTextModifyReq req);

    @ApiOperation("提交审批")
    @PostMapping("/new/ftp/base/info/submit")
    R<Void> submit(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("版本日志")
    @PostMapping("/new/ftp/base/info/versions")
    R<PageR<CommonVersionListRSP>> versions(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("版本比对")
    @PostMapping("/new/ftp/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid ProjReviewVersionDiffREQ req);


    @ApiOperation("流程详情页面内比对接口-月度指导")
    @PostMapping("/new/ftp/flow/detail/compare/monthly")
    R<Map<String, DiffValue>> compareMonthly(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("流程详情页面内比对接口-季度指导")
    @PostMapping("/new/ftp/flow/detail/compare/quarterly")
    R<Map<String, DiffValue>> compareQuarterly(@RequestBody @Valid NewFtpDetailReq req);

}