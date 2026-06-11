package cn.zswltech.mithras.third.baorong.application;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.capital.BrFlowRecordCountRSP;
import cn.zswltech.mithras.dto.capital.BrFlowRecordListREQ;
import cn.zswltech.mithras.dto.capital.BrFlowRecordRemoveREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.third.baorong.model.BrFlowRecord;
import cn.zswltech.mithras.third.baorong.mapper.BrFlowRecordMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author vico
 * 保融流水表
 * @date 2024-06-17
 */
@Service
public class BrFlowRecordService extends ServiceImpl<BrFlowRecordMapper, BrFlowRecord> {

    @Resource
    private BrFlowRecordMapper brFlowRecordMapper;

    public Page<BrFlowRecord> list(BrFlowRecordListREQ req) {
        return baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<BrFlowRecord>lambdaQuery()
                .in(BrFlowRecord::getIgnoreFlag, ListUtil.toList(YesOrNoNumberEnum.YES.getCode(), YesOrNoNumberEnum.NO.getCode()))
                .like(ObjectUtil.isNotEmpty(req.getBruid()), BrFlowRecord::getBruid, req.getBruid())
                .like(ObjectUtil.isNotEmpty(req.getOrgName()), BrFlowRecord::getOrgName, req.getOrgName())
                .like(ObjectUtil.isNotEmpty(req.getAccountnumber()), BrFlowRecord::getAccountnumber, req.getAccountnumber())
                .like(ObjectUtil.isNotEmpty(req.getOppositeaccountnumber()), BrFlowRecord::getOppositeaccountnumber, req.getOppositeaccountnumber())
                .like(ObjectUtil.isNotEmpty(req.getOppositeaccountname()), BrFlowRecord::getOppositeaccountname, req.getOppositeaccountname()));
    }

    public BrFlowRecordCountRSP listCount() {
        BrFlowRecordCountRSP rsp = new BrFlowRecordCountRSP();
        rsp.setNotIgnoredCount(baseMapper.selectCount(Wrappers.<BrFlowRecord>lambdaQuery()
                .eq(BrFlowRecord::getIgnoreFlag, YesOrNoNumberEnum.NO.getCode())
        ));
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(BrFlowRecordRemoveREQ req) {
        BrFlowRecord originalInfo = brFlowRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaUpdateWrapper<BrFlowRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(BrFlowRecord::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(BrFlowRecord::getId, req.getId());
        brFlowRecordMapper.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void ignore(BrFlowRecordRemoveREQ req) {
        BrFlowRecord originalInfo = brFlowRecordMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaUpdateWrapper<BrFlowRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(BrFlowRecord::getIgnoreFlag, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(BrFlowRecord::getId, req.getId());
        brFlowRecordMapper.update(null, updateWrapper);
    }

    //同步苍穹流水到宝融
    @Transactional(rollbackFor = Throwable.class)
    public void doSyncCqFlow() {
        brFlowRecordMapper.syncCQFlow();
    }


}