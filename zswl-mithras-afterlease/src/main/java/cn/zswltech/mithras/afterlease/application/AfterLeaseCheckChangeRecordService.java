package cn.zswltech.mithras.afterlease.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckChangeRecordListREQ;
import cn.zswltech.mithras.afterlease.mapper.AfterLeaseCheckChangeRecordMapper;
import cn.zswltech.mithras.afterlease.mapper.model.AfterLeaseCheckChangeRecord;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @description 租后检查计划-基本信息表
* @author vico
* @date 2024-04-22
*/
@Service
public class AfterLeaseCheckChangeRecordService extends ServiceImpl<AfterLeaseCheckChangeRecordMapper, AfterLeaseCheckChangeRecord> {

    @Resource
    private AfterLeaseCheckChangeRecordMapper afterLeaseCheckChangeRecordMapper;

    public Page<AfterLeaseCheckChangeRecord> list(AfterLeaseCheckChangeRecordListREQ req) {
        return afterLeaseCheckChangeRecordMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<AfterLeaseCheckChangeRecord>lambdaQuery()
                .eq(AfterLeaseCheckChangeRecord::getPlanId, req.getPlanId())
                .eq(ObjectUtil.isNotEmpty(req.getCheckPlanClientId()), AfterLeaseCheckChangeRecord::getCheckPlanClientId, req.getCheckPlanClientId()));
    }
}