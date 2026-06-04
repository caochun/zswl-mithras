package cn.zswltech.mithras.associationreport.service;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.associationreport.mapper.AssociationShahChangeInfoLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationShahChangeInfoLib;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationTop10ClientConcentration;
import cn.zswltech.mithras.associationreport.mapper.model.BasicAssociationReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.service.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.associationreport.mapper.AssociationTop10ClientConcentrationLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationTop10ClientConcentrationLib;

import java.util.List;

/**
* @description 金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationTop10ClientConcentrationLibService extends ServiceImpl<AssociationTop10ClientConcentrationLibMapper, AssociationTop10ClientConcentrationLib> {

    @Resource
    private AssociationTop10ClientConcentrationLibMapper associationTop10ClientConcentrationLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationTop10ClientConcentrationLibAddREQ req) {
        AssociationTop10ClientConcentrationLib info = BeanUtil.copyProperties(req, AssociationTop10ClientConcentrationLib.class);
        associationTop10ClientConcentrationLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationTop10ClientConcentrationLibModifyREQ req) {
        AssociationTop10ClientConcentrationLib originalInfo = associationTop10ClientConcentrationLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationTop10ClientConcentrationLib info = BeanUtil.copyProperties(req, AssociationTop10ClientConcentrationLib.class);
        associationTop10ClientConcentrationLibMapper.updateById(info);
    }

    public Page<AssociationTop10ClientConcentrationLib> list(AssociationTop10ClientConcentrationLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationTop10ClientConcentrationLibRemoveREQ req) {
        AssociationTop10ClientConcentrationLib originalInfo = associationTop10ClientConcentrationLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationTop10ClientConcentrationLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailTop10ClientConcentrationRSP> listByReportInstanceIdAndVersion(String reportInstanceId, String version) {
        LambdaQueryWrapper<AssociationTop10ClientConcentrationLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationTop10ClientConcentrationLib> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationDetailTop10ClientConcentrationRSP.class);
    }

}