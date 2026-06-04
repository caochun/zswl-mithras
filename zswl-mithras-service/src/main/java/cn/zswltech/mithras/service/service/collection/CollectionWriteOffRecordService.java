package cn.zswltech.mithras.service.service.collection;

import cn.zswltech.mithras.dto.collection.CollectionwriteOffListREQ;
import cn.zswltech.mithras.dto.collection.CollectionwriteOffListRSP;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionWriteOffRecordMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.collection.mapper.model.CollectionWriteOffRecord;
import cn.zswltech.mithras.system.service.Id2NameService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @create: 2022-08-18
 **/
//@Slf4j
//@Service
public class CollectionWriteOffRecordService {

//    @Resource
//    private CollectionWriteOffRecordMapper collectionWriteOffRecordMapper;
//    @Resource
//    private Id2NameService id2NameService;

//    public List<CollectionwriteOffListRSP> writeoffList(CollectionwriteOffListREQ req){
//        List<CollectionWriteOffRecord> writeOffRecords = collectionWriteOffRecordMapper.selectList(Wrappers.<CollectionWriteOffRecord>lambdaQuery()
//                .eq(CollectionWriteOffRecord::getCollectionId, req.getId()).orderByDesc(BaseModel::getCreateTime));
//        List<Long> ids = writeOffRecords.stream().map(BaseModel::getCreateBy).distinct().collect(Collectors.toList());
//        Map<Long, String> userMap = id2NameService.sysUserId2Name(ids);
//        List<CollectionwriteOffListRSP> rsps = new LinkedList<>();
//        for (CollectionWriteOffRecord o : writeOffRecords) {
//            CollectionwriteOffListRSP tmp = new CollectionwriteOffListRSP();
//            tmp.setCreateTime(o.getCreateTime());
//            tmp.setCreateBy(userMap.get(o.getCreateBy()));
//            tmp.setOperate(o.getOperate());
//            tmp.setOperateDetails(o.getOperateInfo());
//            tmp.setStatus(Optional.ofNullable(o.getReceiptStatus()).map(CollectionWriteOffStatusEnum::of).map(CollectionWriteOffStatusEnum::display).orElse(null));
//            rsps.add(tmp);
//        }
//        return rsps;
//    }
}
