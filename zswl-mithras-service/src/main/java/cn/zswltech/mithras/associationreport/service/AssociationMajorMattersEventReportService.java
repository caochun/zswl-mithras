package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationMajorMattersEventReportModel;
import cn.zswltech.mithras.associationreport.storedata.AbstractDataStore;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationMajorMattersEventReportMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersEventReport;
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
import java.util.stream.Collectors;

/**
* @description 重大事项报告表-重大事项报告情况
* @author hspcadmin
* @date 2025-08-27
*/
@Service
public class AssociationMajorMattersEventReportService extends ServiceImpl<AssociationMajorMattersEventReportMapper, AssociationMajorMattersEventReport> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportService associationReportService;
    public List<AssociationDetailMajorMattersEventReportRSP> listByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMajorMattersEventReport> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationMajorMattersEventReport> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailMajorMattersEventReportRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailMajorMattersEventReportRSP.class);
        return result;
    }

    public List<AssociationMajorMattersEventReportModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMajorMattersEventReport> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationMajorMattersEventReport> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationMajorMattersEventReportModel.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMajorMattersEventReport> query = Wrappers.lambdaQuery();
        query.eq(AssociationMajorMattersEventReport::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationMajorMattersEventReportAddREQ req) {
        AssociationMajorMattersEventReport info = BeanUtil.copyProperties(req, AssociationMajorMattersEventReport.class);
        this.baseMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationMajorMattersEventReportModifyREQ req) {
        AssociationMajorMattersEventReport originalInfo = this.baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationMajorMattersEventReport info = BeanUtil.copyProperties(req, AssociationMajorMattersEventReport.class);
        this.baseMapper.updateById(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<AssociationMajorMattersEventReportModifyREQ> req){
        //行号自动生成
        for(int rowNum = 1; rowNum < req.size()+1; rowNum++){
            req.get(rowNum-1).setRowNum(rowNum);
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

        LambdaQueryWrapper<AssociationMajorMattersEventReport> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationMajorMattersEventReport> oldDbResult = this.list(query);

        List<AssociationMajorMattersEventReport> newValList = req.stream().map(item -> {
            AssociationMajorMattersEventReport newVal = BeanUtil.copyProperties(item, AssociationMajorMattersEventReport.class);
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, AssociationMajorMattersEventReport::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(AssociationMajorMattersEventReport::getId).collect(Collectors.toList())),
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

    public Page<AssociationMajorMattersEventReport> list(AssociationMajorMattersEventReportListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationMajorMattersEventReportRemoveREQ req) {
        AssociationMajorMattersEventReport originalInfo = this.baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        this.baseMapper.deleteById(req.getId());
    }

}