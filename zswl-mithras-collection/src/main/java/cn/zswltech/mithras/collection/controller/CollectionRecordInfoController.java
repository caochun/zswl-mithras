package cn.zswltech.mithras.collection.controller;

import cn.zswltech.mithras.api.collection.CollectionRecordInfoApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.collection.service.CollectionRecordInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @create: 2022-08-17
 **/
@RestController
public class CollectionRecordInfoController implements CollectionRecordInfoApi {

    @Resource
    private CollectionRecordInfoApplicationService collectionRecordInfoService;
//    @Resource
//    private CollectionWriteOffRecordService collectionWriteOffRecordService;

    @Override
    public R<CollectionRecordListRSP> recordList(@Valid CollectionRecordListREQ req) {
        return R.ok(collectionRecordInfoService.list(req));
    }

//    @Override
//    public R<List<CollectionwriteOffListRSP>> writeoffList(@Valid CollectionwriteOffListREQ req) {
//        return R.ok(collectionWriteOffRecordService.writeoffList(req));
//    }

    @Override
    public R<CollectionRecordDetailRSP> recordDetail(@Valid CollectionRecordDetailREQ req) {
        return R.ok(collectionRecordInfoService.detail(req));
    }


//    @Override
    public R<String> addRecord(@Valid CollectionRecordAddREQ req) {
        return collectionRecordInfoService.addFinancialRecord();
    }

//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = "PAYMENT")
//    public R<String> addRecord(@Valid CollectionRecordAddREQ req,MultipartFile file) {
//        if (req.getCollectionAmount() == 0 || req.getCollectionAmount() == null){
//            return R.fail("实付金额不能为0！");
//        }
//        CollectionRecordInfo info = new CollectionRecordInfo();
//        BeanUtil.copyProperties(req,info);
//        info.setDataSource(String.valueOf(AccountUtil.getLoginInfo().getId()));
//        info.setSourceFlag(1);
//        return collectionRecordInfoService.add(info,file);
//    }
//
//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = "PAYMENT")
//    public R<String> modifyRecord(@Valid CollectionRecordAddREQ req, MultipartFile file) {
//
//        return collectionRecordInfoService.modify(req,file);
//    }
//
//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = "PAYMENT")
//    public R<String> updateRecord(@Valid CollectionRecordUpdateREQ req) {
//        return collectionRecordInfoService.writeOff(req);
//    }
//
//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = "PAYMENT")
//    public R<Void> off(@Valid CollectionWriteOffReq req) {
//        collectionRecordInfoService.writeOff(req);
//        return R.ok();
//    }

//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = "PAYMENT")
//    public R<Void> undooff(@Valid CollectionUnWriteOffReq req) {
//        collectionRecordInfoService.undoWriteOff(req.getId());
//        return R.ok();
//    }



}
