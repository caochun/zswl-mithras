package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationBalanceSheetPartialModel;
import cn.zswltech.mithras.associationreport.storedata.AbstractDataStore;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationBalanceSheetPartialMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBalanceSheetPartial;
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
* @description 资产负债表
* @author vico
* @date 2025-04-18
*/
@Service
public class AssociationBalanceSheetPartialService extends ServiceImpl<AssociationBalanceSheetPartialMapper, AssociationBalanceSheetPartial> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportService associationReportService;

    @Resource
    private AssociationBalanceSheetPartialMapper associationBalanceSheetPartialMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationBalanceSheetPartialAddREQ req) {
        AssociationBalanceSheetPartial info = BeanUtil.copyProperties(req, AssociationBalanceSheetPartial.class);
        associationBalanceSheetPartialMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationBalanceSheetPartialModifyREQ req) {
        req.setRowNum(1);
        AssociationBalanceSheetPartial info = BeanUtil.copyProperties(req, AssociationBalanceSheetPartial.class);
        if(info.getId() != null){//更新
            AssociationBalanceSheetPartial originalInfo = this.baseMapper.selectById(req.getId());
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
            AssociationReport associationReport = associationReportService.getOne(associationReportLambdaQueryWrapper);
            if (ObjectUtil.isNull(associationReport)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }

            String period = AbstractDataStore.generatePeriod(associationReport.getReportPeriodCategory(), associationReport.getReportPeriod(), associationReport.getReportYear());
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

    public Page<AssociationBalanceSheetPartial> list(AssociationBalanceSheetPartialListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationBalanceSheetPartialRemoveREQ req) {
        AssociationBalanceSheetPartial originalInfo = associationBalanceSheetPartialMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationBalanceSheetPartialMapper.deleteById(req.getId());
    }

    public AssociationDetailBalanceSheetPartialRSP getByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBalanceSheetPartial> query = Wrappers.lambdaQuery();
        query.eq(AssociationBalanceSheetPartial::getReportInstanceId, reportInstanceId);
        AssociationBalanceSheetPartial dbResult = this.getOne(query);
        return BeanUtil.copyProperties(dbResult, AssociationDetailBalanceSheetPartialRSP.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBalanceSheetPartial> query = Wrappers.lambdaQuery();
        query.eq(AssociationBalanceSheetPartial::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    public void createReportFile(Integer rowNum) {
        List<AssociationBalanceSheetPartial> associationBalanceSheetPartials = associationBalanceSheetPartialMapper.selectList(Wrappers.<AssociationBalanceSheetPartial>lambdaQuery()
                .eq(AssociationBalanceSheetPartial::getRowNum, rowNum));
        if (ObjectUtil.isEmpty(associationBalanceSheetPartials)) {
            return;
        }

    }

    public List<AssociationBalanceSheetPartialModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationBalanceSheetPartial> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationBalanceSheetPartial> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationBalanceSheetPartialModel.class);
    }

}