package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationExternalFinancingModel;
import cn.zswltech.mithras.associationreport.storedata.AbstractDataStore;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationExternalFinancingMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationExternalFinancing;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.UpdateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 金融局报送-对外融资信息清单表
* @author vico
* @date 2025-04-18
*/
@Service
public class AssociationExternalFinancingService extends ServiceImpl<AssociationExternalFinancingMapper, AssociationExternalFinancing> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportService associationReportService;

    @Resource
    private AssociationExternalFinancingMapper associationExternalFinancingMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationExternalFinancingAddREQ req) {
        AssociationExternalFinancing info = BeanUtil.copyProperties(req, AssociationExternalFinancing.class);
        associationExternalFinancingMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationExternalFinancingModifyREQ req) {
        AssociationExternalFinancing originalInfo = associationExternalFinancingMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationExternalFinancing info = BeanUtil.copyProperties(req, AssociationExternalFinancing.class);
        associationExternalFinancingMapper.updateById(info);
    }

    public Page<AssociationExternalFinancing> list(AssociationExternalFinancingListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationExternalFinancingRemoveREQ req) {
        AssociationExternalFinancing originalInfo = associationExternalFinancingMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationExternalFinancingMapper.deleteById(req.getId());
    }

    public List<AssociationDetailExternalFinancingRSP> listByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationExternalFinancing> query = Wrappers.lambdaQuery();
        query.eq(AssociationExternalFinancing::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationExternalFinancing> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailExternalFinancingRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailExternalFinancingRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailExternalFinancingRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getFinBusiTypeCode())) {
                rsp.setFinBusiTypeCodeDisplay(dictMap.get(AssociationDictionaryCategoryEnum.EVT00052.name()).get(rsp.getFinBusiTypeCode()));
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationExternalFinancing> query = Wrappers.lambdaQuery();
        query.eq(AssociationExternalFinancing::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    public List<AssociationExternalFinancingModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationExternalFinancing> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationExternalFinancing> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationExternalFinancingModel.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<AssociationExternalFinancingModifyREQ> req){
        //行号自动生成
        for(int rowNum = 1; rowNum < req.size()+1; rowNum++){
            req.get(rowNum-1).setRowNum(rowNum);
            req.get(rowNum-1).setOnum(String.valueOf(rowNum));
        }
        String reportInstanceId = req.get(0).getReportInstanceId();
        if(StringUtils.isBlank(reportInstanceId)){
            throw new MithrasException(ResultMsg.REPORT_INSTANCE_ID_NULL);
        }

        LambdaQueryWrapper<AssociationReport> associationReportLambdaQueryWrapper = Wrappers.lambdaQuery();
        associationReportLambdaQueryWrapper.eq(AssociationReport::getReportInstanceId, reportInstanceId);
        AssociationReport associationReport = associationReportService.getOne(associationReportLambdaQueryWrapper);
        if (ObjectUtil.isNull(associationReport)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        String period = AbstractDataStore.generatePeriod(associationReport.getReportPeriodCategory(), associationReport.getReportPeriod(), associationReport.getReportYear());

        LambdaQueryWrapper<AssociationExternalFinancing> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationExternalFinancing> oldDbResult = this.list(query);

        List<AssociationExternalFinancing> newValList = req.stream().map(item -> {
            AssociationExternalFinancing newVal = BeanUtil.copyProperties(item, AssociationExternalFinancing.class);
            newVal.setFinLoanDate(DateUtil.parseDateTime(item.getFinLoanDate()));
            newVal.setFinMatuDate(DateUtil.parseDateTime(item.getFinMatuDate()));
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, AssociationExternalFinancing::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(AssociationExternalFinancing::getId).collect(Collectors.toList())),
                addList -> {
                    addList.forEach(e -> {
                        if(StringUtils.isBlank(e.getUnifSociCredCode())) {//如果统一社会信用代码为空，则取默认配置文件中的统一社会信用代码
                            e.setUnifSociCredCode(this.zszlCreditCode);
                        }
                        e.setReportInstanceId(reportInstanceId);
                        e.setReportInstancePeriod(period);
                        e.setBatchNo(associationReport.getBatchNo());
                        e.setWriteTime(LocalDateTime.now());
                        e.setOp("insert");
                    });
                    this.saveBatch(addList);
                }
        );
        
    }
    
}