package cn.zswltech.mithras.api.margin;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.margin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2022-08-15
 **/
@Api(tags = "保证金信息-接口")
public interface MarginBaseInfoApi {

//    @ApiOperation("保证金新增")
//    @PostMapping("/margin/add")
//    R<String> add(@RequestBody @Valid MarginBaseInfoAddREQ req);

    @ApiOperation("保证金列表")
    @PostMapping("/margin/list")
    R<PageR<MarginBaseInfoListRSP>> list(@RequestBody @Valid MarginBaseInfoListREQ req);

    @ApiOperation("保证金明细")
    @PostMapping("/margin/detail")
    R<MarginBaseInfoRSP> detail(@RequestBody @Valid MarginBaseInfoDetailREQ req);

    @ApiOperation("记录列表")
    @PostMapping("/margin/record/list")
    R<List<MarginRecordListRSP>> collectionList(@RequestBody @Valid MarginRecordListREQ req);

//    @ApiOperation("核销记录")
//    @PostMapping("/margin/writeoff/list")
//    R<List<MarginwriteOffListRSP>> writeoffList(@RequestBody @Valid MarginwriteOffListREQ req);

    @ApiOperation("收款记录详情")
    @PostMapping("/margin/collection/detail")
    R<MarginRecordDetailRSP> collectionDetail(@RequestBody @Valid MarginRecordDetailREQ req);


    @ApiOperation("退款记录详情")
    @PostMapping("/margin/record/detail")
    R<MarginRecordDetailRSP> recordDetail(@RequestBody @Valid MarginRecordDetailREQ req);

    @ApiOperation("保证金抵扣记录详情")
    @PostMapping("/margin/deduct/detail")
    R<MarginDeductDetailRSP> deductDetail(@RequestBody @Valid MarginRecordDetailREQ req);

//    @ApiOperation("记录新增")
//    @PostMapping("/margin/add/record")
//    R<String> addRecord(@Valid MarginRecordAddREQ req,@RequestParam(value = "file",required = false) MultipartFile file);

//    @ApiOperation("新增退款")
//    @PostMapping("/margin/add/back/record")
//    R<String> addBackRecord(@Valid MarginRecordBackAddREQ req,@RequestParam(value = "file",required = false) MultipartFile file);
//
//    @ApiOperation("新增抵扣")
//    @PostMapping("/margin/add/deduct/record")
//    R<String> addDeductRecord(@RequestBody @Valid MarginRecordDeductAddREQ req);

//    @ApiOperation("记录修改")
//    @PostMapping("/margin/update/record")
//    R<String> updateRecord(@RequestBody @Valid MarginRecordUpdateREQ req);

//    @ApiOperation("选择抵扣期项")
//    @PostMapping("/margin/phase/select")
//    R<List<MarginCashSelectRSP>> phaseSelect(@RequestBody @Valid CollectionSelectREQ req);

//    @ApiOperation("抵扣期项详情")
//    @PostMapping("/margin/phase/info")
//    R<MarginCashInfoRSP> phaseInfo(@RequestBody @Valid MarginCashInfoREQ req);

    @ApiOperation("保证金列表（excel导出）")
    @PostMapping("/margin/list/export")
//    R<Void> exportMarginList(@RequestBody @Valid MarginListExportREQ req);
    R<Void> exportMarginList(@RequestBody @Valid MarginBaseInfoListREQ req);

}
