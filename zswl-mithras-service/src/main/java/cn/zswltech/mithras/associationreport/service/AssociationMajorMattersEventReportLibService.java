package cn.zswltech.mithras.associationreport.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationMajorMattersBasicReportLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersBasicReportLib;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersEventReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationMajorMattersEventReportLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersEventReportLib;

import java.util.Collections;
import java.util.List;

/**
* @description 重大事项报告表-重大事项报告情况(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationMajorMattersEventReportLibService extends ServiceImpl<AssociationMajorMattersEventReportLibMapper, AssociationMajorMattersEventReportLib> {

    @Resource
    private AssociationMajorMattersEventReportLibMapper associationMajorMattersEventReportLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationMajorMattersEventReportLibAddREQ req) {
        AssociationMajorMattersEventReportLib info = BeanUtil.copyProperties(req, AssociationMajorMattersEventReportLib.class);
        associationMajorMattersEventReportLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationMajorMattersEventReportLibModifyREQ req) {
        AssociationMajorMattersEventReportLib originalInfo = associationMajorMattersEventReportLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationMajorMattersEventReportLib info = BeanUtil.copyProperties(req, AssociationMajorMattersEventReportLib.class);
        associationMajorMattersEventReportLibMapper.updateById(info);
    }

    public Page<AssociationMajorMattersEventReportLib> list(AssociationMajorMattersEventReportLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationMajorMattersEventReportLibRemoveREQ req) {
        AssociationMajorMattersEventReportLib originalInfo = associationMajorMattersEventReportLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationMajorMattersEventReportLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailMajorMattersEventReportRSP> listByReportInstanceIdAndVersion(String reportInstanceId, String version) {
        LambdaQueryWrapper<AssociationMajorMattersEventReportLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationMajorMattersEventReportLib> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailMajorMattersEventReportRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailMajorMattersEventReportRSP.class);
        return result;
    }

}