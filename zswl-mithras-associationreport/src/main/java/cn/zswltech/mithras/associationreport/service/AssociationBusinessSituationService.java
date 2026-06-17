package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.associationreport.storedata.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationBusinessSituationModel;
import cn.zswltech.mithras.associationreport.support.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.associationreport.mapper.AssociationBusinessSituationMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBusinessSituation;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.associationreport.mapper.model.BasicAssociationReport;
import cn.zswltech.mithras.foundation.exception.MithrasException;
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

/**
* @description 业务情况表
* @author vico
* @date 2025-04-18
*/
@Service
public class AssociationBusinessSituationService extends ServiceImpl<AssociationBusinessSituationMapper, AssociationBusinessSituation> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportQueryService associationReportQueryService;

    @Resource
    private AssociationBusinessSituationMapper associationBusinessSituationMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationBusinessSituationAddREQ req) {
        AssociationBusinessSituation info = BeanUtil.copyProperties(req, AssociationBusinessSituation.class);
        associationBusinessSituationMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationBusinessSituationModifyREQ req) {
        req.setRowNum(1);
        AssociationBusinessSituation info = BeanUtil.copyProperties(req, AssociationBusinessSituation.class);
        if(info.getId() != null){//更新
            AssociationBusinessSituation originalInfo = this.baseMapper.selectById(req.getId());
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
            if(StringUtils.isBlank(info.getUnifSociCredCode())) {//如果统一社会信用代码为空，则取默认配置文件中的统一社会信用代码
                info.setUnifSociCredCode(this.zszlCreditCode);
            }
            info.setReportInstanceId(reportInstanceId);
            info.setReportInstancePeriod(period);
            info.setBatchNo(associationReport.getBatchNo());
            info.setWriteTime(LocalDateTime.now());
            info.setOp("insert");
            this.baseMapper.insert(info);
        }

    }

    public Page<AssociationBusinessSituation> list(AssociationBusinessSituationListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationBusinessSituationRemoveREQ req) {
        AssociationBusinessSituation originalInfo = associationBusinessSituationMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationBusinessSituationMapper.deleteById(req.getId());
    }

    public AssociationDetailBusinessSituationRSP getByReportInstanceId(String reportInstanceId) {
        AssociationBusinessSituation dbResult = this.getModelByReportInstanceId(reportInstanceId);
        return BeanUtil.copyProperties(dbResult, AssociationDetailBusinessSituationRSP.class);
    }

    public AssociationBusinessSituation getModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBusinessSituation> query = Wrappers.lambdaQuery();
        query.eq(AssociationBusinessSituation::getReportInstanceId, reportInstanceId);
        return this.getOne(query);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBusinessSituation> query = Wrappers.lambdaQuery();
        query.eq(AssociationBusinessSituation::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    public List<AssociationBusinessSituationModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBusinessSituation> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationBusinessSituation> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationBusinessSituationModel.class);
    }

}
