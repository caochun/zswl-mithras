package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.AssociationReportDateUtils;
import cn.zswltech.mithras.associationreport.excel.AssociationBasicSituationModel;
import cn.zswltech.mithras.associationreport.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.AssociationBasicSituationMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBasicSituation;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.associationreport.mapper.model.BasicAssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
* @description 基本情况统计表
* @author hspcadmin
* @date 2025-08-22
*/
@Service
public class AssociationBasicSituationService extends ServiceImpl<AssociationBasicSituationMapper, AssociationBasicSituation> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportQueryService associationReportQueryService;

    @Resource
    private AssociationBasicSituationMapper associationBasicSituationMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationBasicSituationAddREQ req) {
        AssociationBasicSituation info = BeanUtil.copyProperties(req, AssociationBasicSituation.class);
        associationBasicSituationMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationBasicSituationModifyREQ req) {
        req.setRowNum(1);
        AssociationBasicSituation info = BeanUtil.copyProperties(req, AssociationBasicSituation.class);
        info.setSetpDate(AssociationReportDateUtils.parseDateTime(req.getSetpDate()));
        if(StringUtils.isBlank(info.getUnifSociCredCode())) {//如果统一社会信用代码为空，则取默认配置文件中的统一社会信用代码
            info.setUnifSociCredCode(this.zszlCreditCode);
        }
        if(info.getId() != null){//更新
            AssociationBasicSituation originalInfo = this.baseMapper.selectById(req.getId());
            if (ObjectUtil.isNull(originalInfo)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            this.baseMapper.updateById(info);
        }else{//新增
            String reportInstanceId = req.getReportInstanceId();
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
            info.setReportInstanceId(reportInstanceId);
            info.setReportInstancePeriod(period);
            info.setBatchNo(associationReport.getBatchNo());
            info.setWriteTime(LocalDateTime.now());
            info.setOp("insert");
            this.baseMapper.insert(info);
        }
    }

    public Page<AssociationBasicSituation> list(AssociationBasicSituationListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationBasicSituationRemoveREQ req) {
        AssociationBasicSituation originalInfo = associationBasicSituationMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationBasicSituationMapper.deleteById(req.getId());
    }

    public AssociationDetailBasicSituationRSP getByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBasicSituation> query = Wrappers.lambdaQuery();
        query.eq(AssociationBasicSituation::getReportInstanceId, reportInstanceId);
        AssociationBasicSituation dbResult = this.getOne(query);
        AssociationDetailBasicSituationRSP rsp = BeanUtil.copyProperties(dbResult, AssociationDetailBasicSituationRSP.class);
        if (ObjectUtil.isNull(dbResult)) {
            rsp.setUnifSociCredCode(this.zszlCreditCode);
        }
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        if (StrUtil.isNotBlank(rsp.getEconClasCode())) {
            rsp.setEconClasDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00003.name()).get(rsp.getEconClasCode()));//经济成分
        }

        if (StrUtil.isNotBlank(rsp.getCtarCorpHoldFlag())) {
            rsp.setCtarCorpHoldFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getCtarCorpHoldFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null)); //是否中央企业控股
        }

        if (StrUtil.isNotBlank(rsp.getLcalSoeHoldFlag())) {
            rsp.setLcalSoeHoldFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getLcalSoeHoldFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null)); //是否地方国企控股
        }

        if (StrUtil.isNotBlank(rsp.getCorpClasCode())) {
            rsp.setCorpClasDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00221.name()).get(rsp.getCorpClasCode()));//企业类别
        }
        return rsp;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBasicSituation> query = Wrappers.lambdaQuery();
        query.eq(AssociationBasicSituation::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    public List<AssociationBasicSituationModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBasicSituation> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationBasicSituation> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationBasicSituationModel.class);
    }

}
