package cn.zswltech.mithras.service.service.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.domain.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.domain.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.excel.importer.EclExecutePredictRecordImporter;
import cn.zswltech.mithras.service.excel.model.EclExecutePredictRecordExcelModel;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.EclExecutePredictRecordMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.EclExecutePredictBaseInfo;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.EclExecutePredictRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @description 资产减值预测详情记录表
* @author vico
* @date 2025-10-14
*/
@Service
public class EclExecutePredictRecordService extends ServiceImpl<EclExecutePredictRecordMapper, EclExecutePredictRecord> {

    @Resource
    private EclExecutePredictRecordMapper eclExecutePredictRecordMapper;
    @Resource
    private EclExecutePredictRecordImporter eclExecutePredictRecordImporter;

    @Transactional(rollbackFor = Throwable.class)
    public void add(EclExecutePredictRecordAddREQ req) {
        checkPredict(req.getExecutePredictId());
        checkContract(Collections.singletonList(req.getContractCode()));
        EclExecutePredictBaseInfo eclExecutePredictBaseInfo = SpringContextHolder.getBean(EclExecutePredictBaseInfoService.class).getById(req.getExecutePredictId());
        //检验是否重复
        EclExecutePredictRecord one = this.getOne(Wrappers.<EclExecutePredictRecord>lambdaQuery()
                .eq(EclExecutePredictRecord::getExecutePredictId, req.getExecutePredictId())
                .eq(EclExecutePredictRecord::getReceiptCode, req.getReceiptCode())
        .eq(EclExecutePredictRecord::getOverdueFlag, YesOrNoNumberEnum.NO.getCode()));
        EclExecutePredictRecord info = BeanUtil.copyProperties(req, EclExecutePredictRecord.class);
        if (ObjectUtil.isNotEmpty(one)) {
            info.setId(one.getId());
        }
        info.setCalculationDate(eclExecutePredictBaseInfo.getPredictDataBegan());
        info.setSourceType(YesOrNoNumberEnum.YES.getCode());
        this.saveOrUpdate(info);
        //调用计算
        SpringContextHolder.getBean(EclExecutePredictBaseInfoService.class).calculation(info.getExecutePredictId(), Collections.singletonList(info.getId()));
    }

    public String addCheck(EclExecutePredictRecordAddREQ req) {
        checkPredict(req.getExecutePredictId());
        checkContract(Collections.singletonList(req.getContractCode()));
        if (this.count(Wrappers.<EclExecutePredictRecord>lambdaQuery()
                .eq(EclExecutePredictRecord::getExecutePredictId, req.getExecutePredictId())
                .eq(EclExecutePredictRecord::getContractCode, req.getContractCode())) > 0) {
            return req.getContractCode();
        }
        return null;
    }

    public void importFile(EclExecutePredictRecordImportREQ req) {
        checkPredict(req.getExecutePredictId());
        List<EclExecutePredictRecordExcelModel> parse;
        try {
            parse = eclExecutePredictRecordImporter.parse(req.getFile().getInputStream());
        } catch (IOException e) {
            throw new MithrasException("文件解析异常");
        }
        if (CollectionUtil.isEmpty(parse)) {
            throw new MithrasException("未解析到数据");
        }
        checkContract(parse.stream().map(EclExecutePredictRecordExcelModel::getContractCode).collect(Collectors.toList()));
        EclExecutePredictBaseInfo eclExecutePredictBaseInfo = SpringContextHolder.getBean(EclExecutePredictBaseInfoService.class).getById(req.getExecutePredictId());
        List<EclExecutePredictRecord> records = BeanUtil.copyToList(parse, EclExecutePredictRecord.class);
        records.forEach(e -> {
            e.setExecutePredictId(req.getExecutePredictId());
            e.setCalculationDate(eclExecutePredictBaseInfo.getPredictDataBegan());
            e.setSourceType(YesOrNoNumberEnum.YES.getCode());
        });
        this.saveBatch(records);

        //调用计算
        SpringContextHolder.getBean(EclExecutePredictBaseInfoService.class).calculation(req.getExecutePredictId(), records.stream().map(EclExecutePredictRecord::getId).collect(Collectors.toList()));

    }

    public Set<String> importFileCheck(EclExecutePredictRecordImportREQ req) {
        checkPredict(req.getExecutePredictId());
        List<EclExecutePredictRecordExcelModel> parse;
        try {
            parse = eclExecutePredictRecordImporter.parse(req.getFile().getInputStream());
        } catch (IOException e) {
            throw new MithrasException("文件解析异常");
        }
        if (CollectionUtil.isEmpty(parse)) {
            throw new MithrasException("未解析到数据");
        }
        checkContract(parse.stream().map(EclExecutePredictRecordExcelModel::getContractCode).collect(Collectors.toList()));
        List<EclExecutePredictRecord> records = BeanUtil.copyToList(parse, EclExecutePredictRecord.class);
        //合同号相同的需覆盖
        if (ObjectUtil.isEmpty(records)) {
            return new HashSet<>();
        }
        List<String> contractCodes = records.stream().filter(e -> ObjectUtil.isNotEmpty(e.getContractCode())).map(EclExecutePredictRecord::getContractCode).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(contractCodes)) {
            throw new MithrasException("未获取到合同信息");
        }
        List<EclExecutePredictRecord> oldExecuteRecord = this.list(Wrappers.<EclExecutePredictRecord>lambdaQuery()
                .eq(EclExecutePredictRecord::getExecutePredictId, req.getExecutePredictId())
                .in(CollectionUtil.isNotEmpty(contractCodes), EclExecutePredictRecord::getContractCode, contractCodes));
        if (ObjectUtil.isNotEmpty(oldExecuteRecord)) {
            Set<String> contractCodeList = oldExecuteRecord.stream().filter(ObjectUtil::isNotEmpty).map(EclExecutePredictRecord::getContractCode).collect(Collectors.toSet());
            if(CollectionUtil.isNotEmpty(contractCodeList)) {
                return contractCodeList;
            }
        }
        return new HashSet<>();
    }

    //检查是否可编辑
    private void checkPredict(Long executePredictId) {
        EclExecutePredictBaseInfo eclExecutePredictBaseInfo = SpringContextHolder.getBean(EclExecutePredictBaseInfoService.class).getById(executePredictId);
        if (ObjectUtil.isEmpty(eclExecutePredictBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ObjectUtil.equals(eclExecutePredictBaseInfo.getSource(), YesOrNoNumberEnum.YES.getCode())) {
            throw new MithrasException("系统生成计划，不允许变动数据");
        }
    }

    /**
     * 判断合同是否重复
     **/
    private void checkContract(List<String> contractCodes) {
        if (CollectionUtil.isEmpty(contractCodes)) {
            return;
        }
        List<EclExecutePredictRecord> list = this.list(Wrappers.<EclExecutePredictRecord>lambdaQuery()
                .eq(EclExecutePredictRecord::getSourceType, YesOrNoNumberEnum.NO.getCode())
                .in(EclExecutePredictRecord::getContractCode, contractCodes));
        if (CollectionUtil.isNotEmpty(list)) {
            throw new MithrasException(String.format("新增项目与存量项目%s重复，请核对后重新上传", list.stream().map(EclExecutePredictRecord::getContractCode).collect(Collectors.toSet())));
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(EclExecutePredictRecordModifyREQ req) {
        EclExecutePredictRecord originalInfo = eclExecutePredictRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        EclExecutePredictRecord info = BeanUtil.copyProperties(req, EclExecutePredictRecord.class);
        eclExecutePredictRecordMapper.updateById(info);
        //调用计算
        SpringUtil.getBean(EclExecutePredictBaseInfoService.class).calculation(originalInfo.getExecutePredictId(), Collections.singletonList(originalInfo.getId()));
    }

    public Page<EclExecutePredictRecord> list(EclExecutePredictRecordListREQ req) {
        LocalDateTime localDateTime = null;
        if (ObjectUtil.isNotEmpty(req.getUpdateTimeTo())){
            localDateTime = req.getUpdateTimeTo().atTime(23, 59, 59);
        }
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<EclExecutePredictRecord>lambdaQuery()
                .eq(EclExecutePredictRecord::getExecutePredictId, req.getExecutePredictId())
                .like(ObjectUtil.isNotEmpty(req.getClientName()), EclExecutePredictRecord::getClientName, req.getClientName())
                .like(ObjectUtil.isNotEmpty(req.getContractCode()), EclExecutePredictRecord::getContractCode, req.getContractCode())
                .eq(ObjectUtil.isNotEmpty(req.getInnerMdLevel()), EclExecutePredictRecord::getInnerMdLevel, req.getInnerMdLevel())
                .eq(ObjectUtil.isNotEmpty(req.getGroup()), EclExecutePredictRecord::getGroup, req.getGroup())
                .eq(ObjectUtil.isNotEmpty(req.getClassify()), EclExecutePredictRecord::getClassify, req.getClassify())
                .eq(ObjectUtil.isNotEmpty(req.getEclStep()), EclExecutePredictRecord::getEclStep, req.getEclStep())
                .eq(ObjectUtil.isNotEmpty(req.getLeaseType()), EclExecutePredictRecord::getLeaseType, req.getLeaseType())
                .ge(ObjectUtil.isNotEmpty(req.getUpdateTimeFrom()), EclExecutePredictRecord::getUpdateTime, req.getUpdateTimeFrom())
                .le(ObjectUtil.isNotEmpty(req.getUpdateTimeTo()), EclExecutePredictRecord::getUpdateTime, localDateTime)
                .orderByDesc(EclExecutePredictRecord::getUpdateTime));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(EclExecutePredictRecordRemoveREQ req) {
        EclExecutePredictRecord originalInfo = eclExecutePredictRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        checkPredict(originalInfo.getExecutePredictId());
        LambdaUpdateWrapper<EclExecutePredictRecord> updateWrapper = new LambdaUpdateWrapper<EclExecutePredictRecord>();
        updateWrapper.set(EclExecutePredictRecord::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(EclExecutePredictRecord::getId, req.getId());
        eclExecutePredictRecordMapper.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void removeByExecutePredictId(Long executePredictId) {
        LambdaUpdateWrapper<EclExecutePredictRecord> updateWrapper = new LambdaUpdateWrapper<EclExecutePredictRecord>();
        updateWrapper.set(EclExecutePredictRecord::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(EclExecutePredictRecord::getExecutePredictId, executePredictId);
        eclExecutePredictRecordMapper.update(null, updateWrapper);
    }

}