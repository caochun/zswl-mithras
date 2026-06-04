package cn.zswltech.mithras.associationreport.service;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.associationreport.mapper.AssociationMainBusinessLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMainBusinessLib;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMajorMattersBasicReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.associationreport.mapper.AssociationMajorMattersBasicReportLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMajorMattersBasicReportLib;

/**
* @description 重大事项报告表-基本信息(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationMajorMattersBasicReportLibService extends ServiceImpl<AssociationMajorMattersBasicReportLibMapper, AssociationMajorMattersBasicReportLib> {

    @Resource
    private AssociationMajorMattersBasicReportLibMapper associationMajorMattersBasicReportLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationMajorMattersBasicReportLibAddREQ req) {
        AssociationMajorMattersBasicReportLib info = BeanUtil.copyProperties(req, AssociationMajorMattersBasicReportLib.class);
        associationMajorMattersBasicReportLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationMajorMattersBasicReportLibModifyREQ req) {
        AssociationMajorMattersBasicReportLib originalInfo = associationMajorMattersBasicReportLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationMajorMattersBasicReportLib info = BeanUtil.copyProperties(req, AssociationMajorMattersBasicReportLib.class);
        associationMajorMattersBasicReportLibMapper.updateById(info);
    }

    public Page<AssociationMajorMattersBasicReportLib> list(AssociationMajorMattersBasicReportLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationMajorMattersBasicReportLibRemoveREQ req) {
        AssociationMajorMattersBasicReportLib originalInfo = associationMajorMattersBasicReportLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationMajorMattersBasicReportLibMapper.deleteById(req.getId());
    }

    public AssociationDetailMajorMattersBasicReportRSP getByReportInstanceIdAndVersion(String reportInstanceId,String version) {
        LambdaQueryWrapper<AssociationMajorMattersBasicReportLib> query = Wrappers.lambdaQuery();
        query.eq(AssociationMajorMattersBasicReportLib::getReportInstanceId, reportInstanceId);
        query.eq(AssociationMajorMattersBasicReportLib::getVersion, version);
        query.last("LIMIT 1");
        AssociationMajorMattersBasicReport dbResult = this.baseMapper.selectOne(query);
        return BeanUtil.copyProperties(dbResult, AssociationDetailMajorMattersBasicReportRSP.class);
    }

}