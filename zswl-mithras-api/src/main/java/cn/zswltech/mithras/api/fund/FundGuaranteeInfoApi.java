package cn.zswltech.mithras.api.fund;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_info
 * @date 2022-12-13
 */
@Api(tags = "担保信息-接口")
public interface FundGuaranteeInfoApi {

    @ApiOperation("新增担保信息")
    @PostMapping("/fund/guarantee/info/add")
    R<Void> add(@RequestParam MultipartFile[] files, @Valid FundGuaranteeInfoAddREQ req);

    @ApiOperation("修改担保信息")
    @PostMapping("/fund/guarantee/info/modify")
    R<Void> modify(@RequestParam MultipartFile[] addFiles, @Valid FundGuaranteeInfoModifyREQ req);

    @ApiOperation("担保信息列表")
    @PostMapping("/fund/guarantee/info/list")
    R<PageR<FundGuaranteeInfoListRSP>> list(@RequestBody @Valid FundGuaranteeInfoListREQ req);

    @ApiOperation("担保信息详情")
    @PostMapping("/fund/guarantee/info/detail")
    R<FundGuaranteeInfoDetailRSP> detail(@RequestBody @Valid FundGuaranteeInfoDetailREQ req);

    @ApiOperation("删除担保信息")
    @PostMapping("/fund/guarantee/info/remove")
    R<Void> remove(@RequestBody @Valid FundGuaranteeInfoRemoveREQ req);

}