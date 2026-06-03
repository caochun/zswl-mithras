package cn.zswltech.mithras.kpi.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionRecordRemoveREQ;
import cn.zswltech.mithras.kpi.bo.KpiProjectDistributionRecordBo;
import cn.zswltech.mithras.kpi.bo.KpiProjectDistributionWeightInfoRecordBo;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionRecordMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionRecord;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionWeightRecord;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionWeightLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.kpi.KpiProjectDistributionWeightLibService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 绩效考核-项目分配记录表
 * @date 2024-09-27
 */
@Service
public class KpiProjectDistributionRecordService extends ServiceImpl<KpiProjectDistributionRecordMapper, KpiProjectDistributionRecord> {

    @Resource
    private KpiProjectDistributionRecordMapper kpiProjectDistributionRecordMapper;
    @Resource
    private KpiProjectDistributionWeightRecordService kpiProjectDistributionWeightRecordService;
    @Resource
    private KpiProjectDistributionWeightLibService kpiProjectDistributionWeightLibService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(KpiProjectDistributionRecordAddREQ req) {
        KpiProjectDistributionRecord info = BeanUtil.copyProperties(req, KpiProjectDistributionRecord.class);
        kpiProjectDistributionRecordMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(KpiProjectDistributionRecordModifyREQ req) {
        KpiProjectDistributionRecord originalInfo = kpiProjectDistributionRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        KpiProjectDistributionRecord info = BeanUtil.copyProperties(req, KpiProjectDistributionRecord.class);
        kpiProjectDistributionRecordMapper.updateById(info);
    }

    public Page<KpiProjectDistributionRecord> list(KpiProjectDistributionRecordListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(KpiProjectDistributionRecordRemoveREQ req) {
        KpiProjectDistributionRecord originalInfo = kpiProjectDistributionRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        kpiProjectDistributionRecordMapper.deleteById(req.getId());
    }

    public List<KpiProjectDistributionRecordBo> getLastByDate(LocalDate localDate, List<Long> contractIds) {
        KpiProjectDistributionRecord kpiProjectDistributionRecord = kpiProjectDistributionRecordMapper.selectOne(Wrappers.<KpiProjectDistributionRecord>lambdaQuery()
                .eq(KpiProjectDistributionRecord::getCalculateDate, localDate)
                .orderByDesc(KpiProjectDistributionRecord::getBatchNumber)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(kpiProjectDistributionRecord)) {
            return null;
        }
        List<KpiProjectDistributionRecord> kpiProjectDistributionRecords = kpiProjectDistributionRecordMapper.selectList(Wrappers.<KpiProjectDistributionRecord>lambdaQuery()
                .eq(KpiProjectDistributionRecord::getCalculateDate, localDate)
                .in(ObjectUtil.isNotEmpty(contractIds), KpiProjectDistributionRecord::getContractId, contractIds)
                .eq(KpiProjectDistributionRecord::getBatchNumber, kpiProjectDistributionRecord.getBatchNumber()));
        Map<Long, List<KpiProjectDistributionWeightRecord>> weightRecordMap = kpiProjectDistributionWeightRecordService.list(Wrappers.<KpiProjectDistributionWeightRecord>lambdaQuery()
                .in(KpiProjectDistributionWeightRecord::getProjectDistributionRecordId, kpiProjectDistributionRecords.stream().map(KpiProjectDistributionRecord::getId).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(KpiProjectDistributionWeightRecord::getProjectDistributionRecordId));
        List<KpiProjectDistributionRecordBo> bos = new ArrayList<>();
        kpiProjectDistributionRecords.forEach(record -> {
            KpiProjectDistributionRecordBo bo = BeanUtil.copyProperties(record, KpiProjectDistributionRecordBo.class);
            List<KpiProjectDistributionWeightRecord> kpiProjectDistributionWeightRecords = weightRecordMap.get(record.getId());
            if (ObjectUtil.isNotEmpty(kpiProjectDistributionWeightRecords)) {
                bo.setWeightInfoList(BeanUtil.copyToList(kpiProjectDistributionWeightRecords, KpiProjectDistributionWeightInfoRecordBo.class));
            }
            bos.add(bo);
        });
        return bos;
    }

    public void record(List<KpiProjectDistributionBaseInfoLib> distributionListRSP, LocalDate date, int batchNumber) {
        if (ObjectUtil.isEmpty(distributionListRSP)) {
            return;
        }
        Map<String, Long> contractCode2Id = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractCode, distributionListRSP.stream().map(KpiProjectDistributionBaseInfoLib::getContractCode).collect(Collectors.toList()))
        .notIn(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()))
                .stream().collect(Collectors.toMap(ContractBaseInfo::getContractCode, ContractBaseInfo::getId, (a, b) -> a));

        List<KpiProjectDistributionRecord> records = new ArrayList<>();
        distributionListRSP.forEach(e -> {
            KpiProjectDistributionRecord record = BeanUtil.copyProperties(e, KpiProjectDistributionRecord.class);
            record.setKpiProjectDistributionVersion(e.getVersion());
            record.setBatchNumber(batchNumber);
            record.setCalculateDate(date);
            record.setId(null);
            record.setContractId(contractCode2Id.get(e.getContractCode()));
            records.add(record);
        });
        Map<Long, List<KpiProjectDistributionWeightLib>> id2LibMap = kpiProjectDistributionWeightLibService.listByKpiProjectDistributionBaseInfoLibs(distributionListRSP).stream().collect(Collectors.groupingBy(KpiProjectDistributionWeightLib::getProjectDistributionId));
        this.saveBatch(records);
        Map<Long, Long> distributionId2RecordId = records.stream().collect(Collectors.toMap(KpiProjectDistributionRecord::getProjectDistributionId, KpiProjectDistributionRecord::getId, (a, b) -> b));
        List<KpiProjectDistributionWeightRecord> weightRecords = new ArrayList<>();
        distributionListRSP.forEach(e -> {
                    List<KpiProjectDistributionWeightLib> kpiProjectDistributionWeightLibs = id2LibMap.get(e.getProjectDistributionId());
                    if (ObjectUtil.isNotEmpty(kpiProjectDistributionWeightLibs)) {
                        List<KpiProjectDistributionWeightRecord> tempRecords = BeanUtil.copyToList(kpiProjectDistributionWeightLibs, KpiProjectDistributionWeightRecord.class);
                        if (ObjectUtil.isNotEmpty(tempRecords)) {
                            tempRecords.forEach(record -> {
                                record.setId(null);
                                record.setProjectDistributionRecordId(distributionId2RecordId.get(e.getProjectDistributionId()));
                                record.setProjectDistributionId(e.getProjectDistributionId());
                                record.setCreateTime(null);
                                record.setUpdateTime(null);
                                weightRecords.add(record);
                            });
                        }
                    }
                }
        );
        if (ObjectUtil.isNotEmpty(weightRecords)) {
            kpiProjectDistributionWeightRecordService.saveBatch(weightRecords);
        }
    }

}
