package cn.zswltech.mithras.kpi.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordRemoveREQ;
import cn.zswltech.mithras.kpi.mapper.KpiPaymentAmountRecordMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiPaymentAmountRecord;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
* @description 绩效考核-投放信息记录表
* @author vico
* @date 2024-09-27
*/
@Service
public class KpiPaymentAmountRecordService extends ServiceImpl<KpiPaymentAmountRecordMapper, KpiPaymentAmountRecord> {

    @Resource
    private KpiPaymentAmountRecordMapper kpiPaymentAmountRecordMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(KpiPaymentAmountRecordAddREQ req) {
        KpiPaymentAmountRecord info = BeanUtil.copyProperties(req, KpiPaymentAmountRecord.class);
        kpiPaymentAmountRecordMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(KpiPaymentAmountRecordModifyREQ req) {
        KpiPaymentAmountRecord originalInfo = kpiPaymentAmountRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        KpiPaymentAmountRecord info = BeanUtil.copyProperties(req, KpiPaymentAmountRecord.class);
        kpiPaymentAmountRecordMapper.updateById(info);
    }

    public Page<KpiPaymentAmountRecord> list(KpiPaymentAmountRecordListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(KpiPaymentAmountRecordRemoveREQ req) {
        KpiPaymentAmountRecord originalInfo = kpiPaymentAmountRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        kpiPaymentAmountRecordMapper.deleteById(req.getId());
    }

    public List<KpiPaymentAmountRecord> lastByYearAndMonth(LocalDate date, List<Long> contractIds) {
        KpiPaymentAmountRecord kpiPaymentAmountRecord = kpiPaymentAmountRecordMapper.selectOne(Wrappers.<KpiPaymentAmountRecord>lambdaQuery()
                .eq(KpiPaymentAmountRecord::getEffectMonth, date)
                .in(ObjectUtil.isNotEmpty(contractIds), KpiPaymentAmountRecord::getContractId, contractIds)
                .orderByDesc(KpiPaymentAmountRecord::getBatchNumber)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(kpiPaymentAmountRecord)) {
            return null;
        }
        return kpiPaymentAmountRecordMapper.selectList(Wrappers.<KpiPaymentAmountRecord>lambdaQuery()
                .eq(KpiPaymentAmountRecord::getEffectMonth, date)
                .eq(KpiPaymentAmountRecord::getBatchNumber, kpiPaymentAmountRecord.getBatchNumber()));
    }

}