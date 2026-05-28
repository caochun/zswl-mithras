package cn.zswltech.mithras.service.service.margin;

import cn.zswltech.mithras.dto.margin.MarginwriteOffListREQ;
import cn.zswltech.mithras.dto.margin.MarginwriteOffListRSP;
import cn.zswltech.mithras.service.mapper.margin.MarginWriteOffRecordMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.margin.MarginWriteOffRecord;
import cn.zswltech.mithras.service.service.Id2NameService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @create: 2022-08-18
 **/
//@Slf4j
//@Service
public class MarginWriteOffRecordService {

//    @Resource
//    private MarginWriteOffRecordMapper marginWriteOffRecordMapper;
//    @Resource
//    private Id2NameService id2NameService;
//
//    public List<MarginwriteOffListRSP> writeoffList(MarginwriteOffListREQ req){
//        List<MarginWriteOffRecord> writeOffRecords = marginWriteOffRecordMapper.selectList(Wrappers.<MarginWriteOffRecord>lambdaQuery()
//                .eq(MarginWriteOffRecord::getMarginId, req.getId()).orderByDesc(BaseModel::getCreateTime));
//        List<Long> ids = writeOffRecords.stream().map(BaseModel::getCreateBy).distinct().collect(Collectors.toList());
//        Map<Long, String> userMap = id2NameService.sysUserId2Name(ids);
//        List<MarginwriteOffListRSP> rsps = new LinkedList<>();
//        for (MarginWriteOffRecord o : writeOffRecords) {
//            MarginwriteOffListRSP tmp = new MarginwriteOffListRSP();
//            tmp.setCreateTime(o.getCreateTime());
//            tmp.setCreateBy(userMap.get(o.getCreateBy()));
//            tmp.setOperate(o.getOperate());
//            tmp.setOperateDetails(o.getOperateInfo());
//            tmp.setMarginAmount(o.getMarginAmount());
//            rsps.add(tmp);
//        }
//        return rsps;
//    }
}
