package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationLawInvolvedVisitRelatedInfoModel;
import cn.zswltech.mithras.associationreport.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationLawInvolvedVisitRelatedInfoMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationLawInvolvedVisitRelatedInfo;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
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
* @description 涉法涉讼涉访信息表
* @author hspcadmin
* @date 2025-08-27
*/
@Service
public class AssociationLawInvolvedVisitRelatedInfoService extends ServiceImpl<AssociationLawInvolvedVisitRelatedInfoMapper, AssociationLawInvolvedVisitRelatedInfo> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportService associationReportService;
    public List<AssociationDetailLawInvolvedVisitRelatedInfoRSP> listByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationLawInvolvedVisitRelatedInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationLawInvolvedVisitRelatedInfo> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailLawInvolvedVisitRelatedInfoRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailLawInvolvedVisitRelatedInfoRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailLawInvolvedVisitRelatedInfoRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getCaseClasCode())) {
                rsp.setCaseClasDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PUB00250.name()).get(rsp.getCaseClasCode()));
            }
            //是否销号

        }
        return result;
    }

    public List<AssociationLawInvolvedVisitRelatedInfoModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationLawInvolvedVisitRelatedInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationLawInvolvedVisitRelatedInfo> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationLawInvolvedVisitRelatedInfoModel.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationLawInvolvedVisitRelatedInfo> query = Wrappers.lambdaQuery();
        query.eq(AssociationLawInvolvedVisitRelatedInfo::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationLawInvolvedVisitRelatedInfoAddREQ req) {
        AssociationLawInvolvedVisitRelatedInfo info = BeanUtil.copyProperties(req, AssociationLawInvolvedVisitRelatedInfo.class);
        this.baseMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationLawInvolvedVisitRelatedInfoModifyREQ req) {
        AssociationLawInvolvedVisitRelatedInfo originalInfo = this.baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationLawInvolvedVisitRelatedInfo info = BeanUtil.copyProperties(req, AssociationLawInvolvedVisitRelatedInfo.class);
        this.baseMapper.updateById(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<AssociationLawInvolvedVisitRelatedInfoModifyREQ> req){
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

        String period = AssociationReportPeriodUtils.generatePeriod(associationReport.getReportPeriodCategory(), associationReport.getReportPeriod(), associationReport.getReportYear());

        LambdaQueryWrapper<AssociationLawInvolvedVisitRelatedInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationLawInvolvedVisitRelatedInfo> oldDbResult = this.list(query);

        List<AssociationLawInvolvedVisitRelatedInfo> newValList = req.stream().map(item -> {
            AssociationLawInvolvedVisitRelatedInfo newVal = BeanUtil.copyProperties(item, AssociationLawInvolvedVisitRelatedInfo.class);
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, AssociationLawInvolvedVisitRelatedInfo::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(AssociationLawInvolvedVisitRelatedInfo::getId).collect(Collectors.toList())),
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

    public Page<AssociationLawInvolvedVisitRelatedInfo> list(AssociationLawInvolvedVisitRelatedInfoListREQ req) {
        return null;
    }


    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationLawInvolvedVisitRelatedInfoRemoveREQ req) {
        AssociationLawInvolvedVisitRelatedInfo originalInfo = this.baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        this.baseMapper.deleteById(req.getId());
    }



}
