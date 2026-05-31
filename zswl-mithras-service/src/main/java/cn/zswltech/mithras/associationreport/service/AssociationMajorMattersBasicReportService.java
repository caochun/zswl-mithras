package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationMajorMattersBasicReportModel;
import cn.zswltech.mithras.associationreport.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationMajorMattersBasicReportMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersBasicReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
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

/**
* @description 重大事项报告表-基本信息
* @author hspcadmin
* @date 2025-08-27
*/
@Service
public class AssociationMajorMattersBasicReportService extends ServiceImpl<AssociationMajorMattersBasicReportMapper, AssociationMajorMattersBasicReport> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportQueryService associationReportQueryService;

    @Resource
    private AssociationMajorMattersBasicReportMapper associationMajorMattersBasicReportMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationMajorMattersBasicReportAddREQ req) {
        AssociationMajorMattersBasicReport info = BeanUtil.copyProperties(req, AssociationMajorMattersBasicReport.class);
        associationMajorMattersBasicReportMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationMajorMattersBasicReportModifyREQ req) {
        req.setRowNum(1);
        AssociationMajorMattersBasicReport info = BeanUtil.copyProperties(req, AssociationMajorMattersBasicReport.class);
        if(info.getId() != null){//更新
            AssociationMajorMattersBasicReport originalInfo = this.baseMapper.selectById(req.getId());
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

    public Page<AssociationMajorMattersBasicReport> list(AssociationMajorMattersBasicReportListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationMajorMattersBasicReportRemoveREQ req) {
        AssociationMajorMattersBasicReport originalInfo = associationMajorMattersBasicReportMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationMajorMattersBasicReportMapper.deleteById(req.getId());
    }

    public AssociationDetailMajorMattersBasicReportRSP getByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMajorMattersBasicReport> query = Wrappers.lambdaQuery();
        query.eq(AssociationMajorMattersBasicReport::getReportInstanceId, reportInstanceId);
        AssociationMajorMattersBasicReport dbResult = this.getOne(query);
        return BeanUtil.copyProperties(dbResult, AssociationDetailMajorMattersBasicReportRSP.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMajorMattersBasicReport> query = Wrappers.lambdaQuery();
        query.eq(AssociationMajorMattersBasicReport::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    public List<AssociationMajorMattersBasicReportModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationMajorMattersBasicReport> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationMajorMattersBasicReport> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationMajorMattersBasicReportModel.class);
    }

}
