package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.associationreport.storedata.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationCompanyProfitStatementModel;
import cn.zswltech.mithras.associationreport.support.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.associationreport.mapper.AssociationCompanyProfitStatementMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationCompanyProfitStatement;
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
* @description 公司利润表数据表
* @author vico
* @date 2025-04-18
*/
@Service
public class AssociationCompanyProfitStatementService extends ServiceImpl<AssociationCompanyProfitStatementMapper, AssociationCompanyProfitStatement> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportQueryService associationReportQueryService;

    @Resource
    private AssociationCompanyProfitStatementMapper associationCompanyProfitStatementMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationCompanyProfitStatementAddREQ req) {
        AssociationCompanyProfitStatement info = BeanUtil.copyProperties(req, AssociationCompanyProfitStatement.class);
        associationCompanyProfitStatementMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationCompanyProfitStatementModifyREQ req) {
        req.setRowNum(1);
        AssociationCompanyProfitStatement info = BeanUtil.copyProperties(req, AssociationCompanyProfitStatement.class);
        if(info.getId() != null){//更新
            AssociationCompanyProfitStatement originalInfo = this.baseMapper.selectById(req.getId());
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

    public Page<AssociationCompanyProfitStatement> list(AssociationCompanyProfitStatementListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationCompanyProfitStatementRemoveREQ req) {
        AssociationCompanyProfitStatement originalInfo = associationCompanyProfitStatementMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationCompanyProfitStatementMapper.deleteById(req.getId());
    }

    public AssociationDetailProfitRSP getByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationCompanyProfitStatement> query = Wrappers.lambdaQuery();
        query.eq(AssociationCompanyProfitStatement::getReportInstanceId, reportInstanceId);
        AssociationCompanyProfitStatement dbResult = this.getOne(query);
        return BeanUtil.copyProperties(dbResult, AssociationDetailProfitRSP.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationCompanyProfitStatement> query = Wrappers.lambdaQuery();
        query.eq(AssociationCompanyProfitStatement::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    public List<AssociationCompanyProfitStatementModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationCompanyProfitStatement> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationCompanyProfitStatement> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationCompanyProfitStatementModel.class);
    }

    public AssociationCompanyProfitStatement getModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationCompanyProfitStatement> query = Wrappers.lambdaQuery();
        query.eq(AssociationCompanyProfitStatement::getReportInstanceId, reportInstanceId);
        return this.getOne(query);
    }

}
