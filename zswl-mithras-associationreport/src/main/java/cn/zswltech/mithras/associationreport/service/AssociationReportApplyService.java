package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyRemoveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.associationreport.mapper.AssociationReportApplyMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReportApply;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @description 金融局报表申请表
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationReportApplyService extends ServiceImpl<AssociationReportApplyMapper, AssociationReportApply> {

    @Resource
    private AssociationReportApplyMapper associationReportApplyMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationReportApplyAddREQ req) {
        AssociationReportApply info = BeanUtil.copyProperties(req, AssociationReportApply.class);
        associationReportApplyMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationReportApplyModifyREQ req) {
        AssociationReportApply originalInfo = associationReportApplyMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationReportApply info = BeanUtil.copyProperties(req, AssociationReportApply.class);
        associationReportApplyMapper.updateById(info);
    }

    public Page<AssociationReportApply> list(AssociationReportApplyListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationReportApplyRemoveREQ req) {
        AssociationReportApply originalInfo = associationReportApplyMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationReportApplyMapper.deleteById(req.getId());
    }




}