package cn.zswltech.mithras.api.collection;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailOperateReq;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffReq;
import cn.zswltech.mithras.dto.collection.*;
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

@Api(tags = "收款核销记录-接口")
public interface CollectionRecordInfoApi {

    @ApiOperation("收款记录")
    @PostMapping("/collection/record/list")
    R<CollectionRecordListRSP> recordList(@RequestBody @Valid CollectionRecordListREQ req);


//    @ApiOperation("核销记录")
//    @PostMapping("/collection/writeoff/list")
//    R<List<CollectionwriteOffListRSP>> writeoffList(@RequestBody @Valid CollectionwriteOffListREQ req);

    @ApiOperation("收款记录详情")
    @PostMapping("/collection/record/detail")
    R<CollectionRecordDetailRSP> recordDetail(@RequestBody @Valid CollectionRecordDetailREQ req);

//    @ApiOperation("收款记录新增")
//    @PostMapping("/collection/add/record")
//    R<String> addRecord(@Valid CollectionRecordAddREQ req,@RequestParam(value = "file",required = false) MultipartFile file);

//    @ApiOperation("收款记录编辑")
//    @PostMapping("/collection/modify/record")
//    R<String> modifyRecord(@Valid CollectionRecordAddREQ req,@RequestParam(value = "file",required = false) MultipartFile file);


//    @ApiOperation("单项核销")
//    @PostMapping("/collection/update/record")
//    R<String> updateRecord(@RequestBody @Valid CollectionRecordUpdateREQ req);

//    @ApiOperation("核销收款申请")
//    @PostMapping("/collection/writeoff")
//    R<Void> off(@RequestBody @Valid CollectionWriteOffReq req);

//    @ApiOperation("撤销核销收款申请")
//    @PostMapping("/collection/undowriteoff")
//    R<Void> undooff(@RequestBody @Valid CollectionUnWriteOffReq req);



}
