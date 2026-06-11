package cn.zswltech.mithras.kpi.application.projguess;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiFinanceProjectProfitRecordRemoveREQ;
import cn.zswltech.mithras.kpi.mapper.KpiFinanceProjectProfitRecordMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiFinanceProjectProfitRecord;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 绩效考核-项目利润明细-记录表
* @author vico
* @date 2024-09-25
*/
@Service
public class KpiFinanceProjectProfitRecordService extends ServiceImpl<KpiFinanceProjectProfitRecordMapper, KpiFinanceProjectProfitRecord> {

    @Resource
    private KpiFinanceProjectProfitRecordMapper kpiFinanceProjectProfitRecordMapper;


    @Transactional(rollbackFor = Throwable.class)
    public void modify(KpiFinanceProjectProfitRecordModifyREQ req) {
        KpiFinanceProjectProfitRecord originalInfo = kpiFinanceProjectProfitRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        KpiFinanceProjectProfitRecord info = BeanUtil.copyProperties(req, KpiFinanceProjectProfitRecord.class);
        kpiFinanceProjectProfitRecordMapper.updateById(info);
    }

    public Page<KpiFinanceProjectProfitRecord> list(KpiFinanceProjectProfitRecordListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(KpiFinanceProjectProfitRecordRemoveREQ req) {
        KpiFinanceProjectProfitRecord originalInfo = kpiFinanceProjectProfitRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        kpiFinanceProjectProfitRecordMapper.deleteById(req.getId());
    }

    public List<KpiFinanceProjectProfitRecord> lastByYearAndMonth(Integer year, Integer month, List<Long> contractId) {
        KpiFinanceProjectProfitRecord kpiFinanceProjectProfitRecord = kpiFinanceProjectProfitRecordMapper.selectOne(Wrappers.<KpiFinanceProjectProfitRecord>lambdaQuery()
                .eq(KpiFinanceProjectProfitRecord::getYear, year)
                .eq(KpiFinanceProjectProfitRecord::getMonth, month)
                .in(ObjectUtil.isNotEmpty(contractId), KpiFinanceProjectProfitRecord::getContractId, contractId)
                .orderByDesc(KpiFinanceProjectProfitRecord::getBatchNumber)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(kpiFinanceProjectProfitRecord)) {
            return null;
        }
        return kpiFinanceProjectProfitRecordMapper.selectList(Wrappers.<KpiFinanceProjectProfitRecord>lambdaQuery()
                .eq(KpiFinanceProjectProfitRecord::getYear, year)
                .eq(KpiFinanceProjectProfitRecord::getMonth, month)
                .eq(KpiFinanceProjectProfitRecord::getBatchNumber, kpiFinanceProjectProfitRecord.getBatchNumber()));
    }

}