package cn.zswltech.mithras.kpi.application.distribution;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRecordRemoveREQ;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionWeightRecordMapper;
import cn.zswltech.mithras.kpi.model.KpiProjectDistributionWeightRecord;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @description 绩效考核-项目分配表-分配比重信息记录表
* @author vico
* @date 2024-09-27
*/
@Service
public class KpiProjectDistributionWeightRecordService extends ServiceImpl<KpiProjectDistributionWeightRecordMapper, KpiProjectDistributionWeightRecord> {

    @Resource
    private KpiProjectDistributionWeightRecordMapper kpiProjectDistributionWeightRecordMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(KpiProjectDistributionWeightRecordAddREQ req) {
        KpiProjectDistributionWeightRecord info = BeanUtil.copyProperties(req, KpiProjectDistributionWeightRecord.class);
        kpiProjectDistributionWeightRecordMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(KpiProjectDistributionWeightRecordModifyREQ req) {
        KpiProjectDistributionWeightRecord originalInfo = kpiProjectDistributionWeightRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        KpiProjectDistributionWeightRecord info = BeanUtil.copyProperties(req, KpiProjectDistributionWeightRecord.class);
        kpiProjectDistributionWeightRecordMapper.updateById(info);
    }

    public Page<KpiProjectDistributionWeightRecord> list(KpiProjectDistributionWeightRecordListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(KpiProjectDistributionWeightRecordRemoveREQ req) {
        KpiProjectDistributionWeightRecord originalInfo = kpiProjectDistributionWeightRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        kpiProjectDistributionWeightRecordMapper.deleteById(req.getId());
    }

}