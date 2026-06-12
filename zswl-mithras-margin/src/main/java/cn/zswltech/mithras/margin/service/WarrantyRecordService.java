package cn.zswltech.mithras.margin.service;

import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.margin.persistence.mapper.MarginWriteOffRecordMapper;
import cn.zswltech.mithras.margin.persistence.mapper.WarrantyBaseInfoMapper;
import cn.zswltech.mithras.margin.persistence.mapper.WarrantyRecordInfoMapper;
import cn.zswltech.mithras.margin.persistence.model.MarginWriteOffRecord;
import cn.zswltech.mithras.margin.persistence.model.WarrantyBaseInfo;
import cn.zswltech.mithras.margin.persistence.model.WarrantyRecordInfo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @create: 2022-08-18
 **/
@Slf4j
@Service
public class WarrantyRecordService extends ServiceImpl<WarrantyRecordInfoMapper, WarrantyRecordInfo> {
    @Resource
    private WarrantyRecordInfoMapper warrantyRecordInfoMapper;
    @Resource
    private WarrantyBaseInfoMapper warrantyBaseInfoMapper;
    @Resource
    private MarginWriteOffRecordMapper marginWriteOffRecordMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void financialAdd(WarrantyRecordInfo info) {
        int count = warrantyRecordInfoMapper.selectCount(Wrappers.<WarrantyRecordInfo>lambdaQuery()
                .eq(WarrantyRecordInfo::getWarrantyId, info.getWarrantyId()).eq(WarrantyRecordInfo::getRecordType, info.getRecordType()));
        info.setSortId(String.valueOf(count + 1));
        warrantyRecordInfoMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void financialWriteOff(WarrantyRecordInfo recordInfo, String collectionCode, Long paidInAmount, LocalDate paidInDate) {
        WarrantyBaseInfo warrantyBaseInfo = warrantyBaseInfoMapper.selectById(recordInfo.getWarrantyId());
        MarginWriteOffRecord writeOffRecord = new MarginWriteOffRecord();
        writeOffRecord.setRecordId(warrantyBaseInfo.getId());
        writeOffRecord.setRecordId(recordInfo.getId());

        long warrantyAmount = LongUtil.null2zero(warrantyBaseInfo.getCollectionAmount());
        writeOffRecord.setOperate(recordInfo.getWriteOffStatus());
        Long collectionAmount = LongUtil.null2zero(recordInfo.getCollectionAmount());
        if (RecordTypeEnum.COLLECTION.name().equals(recordInfo.getRecordType())) {
            warrantyAmount = warrantyAmount + collectionAmount;
            warrantyBaseInfo.setCollectionAmount(warrantyAmount);
            warrantyBaseInfo.setCollectionDate(recordInfo.getCollectionDate());
        } else if (RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_WARRANTY.name().equals(recordInfo.getCollectionType())) {
            warrantyAmount = warrantyAmount - collectionAmount;
            warrantyBaseInfo.setCollectionAmount(warrantyAmount);
            warrantyBaseInfo.setBackAmount(LongUtil.null2zero(warrantyBaseInfo.getBackAmount()) + collectionAmount);
        }
        warrantyBaseInfoMapper.updateById(warrantyBaseInfo);
        writeOffRecord.setOperateInfo(String.valueOf(warrantyBaseInfo.getWarrantyCode()));
        writeOffRecord.setMarginAmount(warrantyAmount);
        marginWriteOffRecordMapper.insert(writeOffRecord);
    }

}
