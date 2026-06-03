package cn.zswltech.mithras.service.controller.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.collection.CollectionRecordInfoApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewSubAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionWriteOffRecordService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2022-08-17
 **/
@RestController
public class CollectionRecordInfoController implements CollectionRecordInfoApi {

    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
//    @Resource
//    private CollectionWriteOffRecordService collectionWriteOffRecordService;

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.COLLECTION)
    public R<CollectionRecordListRSP> recordList(@Valid CollectionRecordListREQ req) {
        return R.ok(collectionRecordInfoService.list(req));
    }

//    @Override
//    public R<List<CollectionwriteOffListRSP>> writeoffList(@Valid CollectionwriteOffListREQ req) {
//        return R.ok(collectionWriteOffRecordService.writeoffList(req));
//    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonViewSubAuthCheckerNew.class, mapperClass = CollectionRecordInfoMapper.class, businessModule = BusinessModuleEnum.COLLECTION)
    public R<CollectionRecordDetailRSP> recordDetail(@Valid CollectionRecordDetailREQ req) {
        return R.ok(collectionRecordInfoService.detail(req));
    }


//    @Override
    public R<String> addRecord(@Valid CollectionRecordAddREQ req) {
        CollectionRecordInfo info = new CollectionRecordInfo();
        info.setDataSource("财务系统");
        info.setSourceFlag(0);
        return collectionRecordInfoService.addRecord(info);
    }

//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
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
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
//    public R<String> modifyRecord(@Valid CollectionRecordAddREQ req, MultipartFile file) {
//
//        return collectionRecordInfoService.modify(req,file);
//    }
//
//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
//    public R<String> updateRecord(@Valid CollectionRecordUpdateREQ req) {
//        return collectionRecordInfoService.writeOff(req);
//    }
//
//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
//    public R<Void> off(@Valid CollectionWriteOffReq req) {
//        collectionRecordInfoService.writeOff(req);
//        return R.ok();
//    }

//    @Override
//    @DataAuthCheck(checkerClass = CollectionWriteOffAuthChecker.class, businessModule = BusinessModuleEnum.PAYMENT)
//    public R<Void> undooff(@Valid CollectionUnWriteOffReq req) {
//        collectionRecordInfoService.undoWriteOff(req.getId());
//        return R.ok();
//    }



}
