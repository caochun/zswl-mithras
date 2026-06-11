package cn.zswltech.mithras.third.financialshare.application;

import cn.zswltech.mithras.third.financialshare.mapper.model.SyncCqRecord;
import cn.zswltech.mithras.third.financialshare.mapper.SyncCqRecordMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yibin
 */
@Service
public class SyncCqRecordService extends ServiceImpl<SyncCqRecordMapper, SyncCqRecord> {

    public List<SyncCqRecord> getLatestByReceiptCode(String receiptCode) {
        return baseMapper.selectList(Wrappers.<SyncCqRecord>lambdaQuery()
                .eq(SyncCqRecord::getSourcebillno, receiptCode)
                //.eq(SyncCqRecord::getChangeState, YesOrNoNumberEnum.NO.getCode())
                .orderByDesc(SyncCqRecord::getId));
    }
}
