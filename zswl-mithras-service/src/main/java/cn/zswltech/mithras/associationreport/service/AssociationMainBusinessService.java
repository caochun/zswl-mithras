package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationMainBusinessModel;
import cn.zswltech.mithras.associationreport.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailMainBusinessRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationMainBusinessModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationMainBusinessMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMainBusiness;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.DateUtil;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@Slf4j
@Service
public class AssociationMainBusinessService extends ServiceImpl<AssociationMainBusinessMapper, AssociationMainBusiness> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportService associationReportService;
    public List<AssociationDetailMainBusinessRSP> listByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMainBusiness> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationMainBusiness> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailMainBusinessRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailMainBusinessRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailMainBusinessRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getAgmtTypeCode())) {
                rsp.setAgmtTypeDisplay(dictMap.get(AssociationDictionaryCategoryEnum.DIMLS079.name()).get(rsp.getAgmtTypeCode()));
            }
            if (StrUtil.isNotBlank(rsp.getProjIndtClasCode())) {
                rsp.setProjIndtClasDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PUB00234.name()).get(rsp.getProjIndtClasCode()));
            }
            if (StrUtil.isNotBlank(rsp.getCustScalCode())) {
                rsp.setCustScalDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00019.name()).get(rsp.getCustScalCode()));
            }
            if (StrUtil.isNotBlank(rsp.getUdpnSituCode())) {
                rsp.setUdpnSituDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00212.name()).get(rsp.getUdpnSituCode()));
            }
            if (StrUtil.isNotBlank(rsp.getOvduDaysCode())) {
                rsp.setOvduDaysDisplay(dictMap.get(AssociationDictionaryCategoryEnum.EVT00051.name()).get(rsp.getOvduDaysCode()));
            }
            if (StrUtil.isNotBlank(rsp.getNpFlag())) {
                rsp.setNpFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getNpFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null));
            }
        }
        return result;
    }

    public List<AssociationMainBusinessModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMainBusiness> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationMainBusiness> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationMainBusinessModel.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMainBusiness> query = Wrappers.lambdaQuery();
        query.eq(AssociationMainBusiness::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<AssociationMainBusinessModifyREQ> req){
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

        LambdaQueryWrapper<AssociationMainBusiness> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationMainBusiness> oldDbResult = this.list(query);

        List<AssociationMainBusiness> newValList = req.stream().map(item -> {
            AssociationMainBusiness newVal = BeanUtil.copyProperties(item, AssociationMainBusiness.class);
            newVal.setAgmtMatuDate(DateUtil.parseDateTime(item.getAgmtMatuDate()));
            newVal.setAgmtSignDate(DateUtil.parseDateTime(item.getAgmtSignDate()));
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, AssociationMainBusiness::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(AssociationMainBusiness::getId).collect(Collectors.toList())),
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
