package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationRelationModel;
import cn.zswltech.mithras.associationreport.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailRelationRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationRelationModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationRelationMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationRelation;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@Slf4j
@Service
public class AssociationRelationService extends ServiceImpl<AssociationRelationMapper, AssociationRelation> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportQueryService associationReportQueryService;

    public List<AssociationDetailRelationRSP> listByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationRelation> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(AssociationRelation::getRowNum);
        List<AssociationRelation> dbResult = this.list(query);
        List<AssociationDetailRelationRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailRelationRSP.class);
        if (CollectionUtil.isEmpty(result)) {
            return Collections.emptyList();
        }
        for (AssociationDetailRelationRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getCorpShahRelpFlag())) {
                rsp.setCorpShahRelpFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getCorpShahRelpFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null));
            }
        }
        return result;
    }

    public List<AssociationRelationModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationRelation> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(AssociationRelation::getRowNum);
        List<AssociationRelation> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationRelationModel.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationRelation> query = Wrappers.lambdaQuery();
        query.eq(AssociationRelation::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<AssociationRelationModifyREQ> req){
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
        AssociationReport associationReport = associationReportQueryService.findByReportInstanceId(reportInstanceId);
        if (ObjectUtil.isNull(associationReport)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        String period = AssociationReportPeriodUtils.generatePeriod(associationReport.getReportPeriodCategory(), associationReport.getReportPeriod(), associationReport.getReportYear());

        LambdaQueryWrapper<AssociationRelation> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationRelation> oldDbResult = this.list(query);

        List<AssociationRelation> newValList = req.stream().map(item -> {
            AssociationRelation newVal = BeanUtil.copyProperties(item, AssociationRelation.class);
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, AssociationRelation::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(AssociationRelation::getId).collect(Collectors.toList())),
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
