package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationTop10ClientConcentrationModel;
import cn.zswltech.mithras.associationreport.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailTop10ClientConcentrationRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationTop10ClientConcentrationModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationTop10ClientConcentrationMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationTop10ClientConcentration;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.UpdateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@Slf4j
@Service
public class AssociationTop10ClientConcentrationService extends ServiceImpl<AssociationTop10ClientConcentrationMapper, AssociationTop10ClientConcentration> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportService associationReportService;

    public List<AssociationDetailTop10ClientConcentrationRSP> listByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationTop10ClientConcentration> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationTop10ClientConcentration> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationDetailTop10ClientConcentrationRSP.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationTop10ClientConcentration> query = Wrappers.lambdaQuery();
        query.eq(AssociationTop10ClientConcentration::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    public List<AssociationTop10ClientConcentrationModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationTop10ClientConcentration> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationTop10ClientConcentration> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationTop10ClientConcentrationModel.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<AssociationTop10ClientConcentrationModifyREQ> req){
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

        LambdaQueryWrapper<AssociationTop10ClientConcentration> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationTop10ClientConcentration> oldDbResult = this.list(query);

        List<AssociationTop10ClientConcentration> newValList = req.stream().map(item -> {
            AssociationTop10ClientConcentration newVal = BeanUtil.copyProperties(item, AssociationTop10ClientConcentration.class);
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, AssociationTop10ClientConcentration::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(AssociationTop10ClientConcentration::getId).collect(Collectors.toList())),
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
