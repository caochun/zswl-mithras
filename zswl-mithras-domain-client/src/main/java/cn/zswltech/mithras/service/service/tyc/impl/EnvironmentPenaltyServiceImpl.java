package cn.zswltech.mithras.service.service.tyc.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyAddREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyModifyREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRemoveREQ;
import cn.zswltech.mithras.service.mapper.corp.EnvironmentPenaltyMapper;
import cn.zswltech.mithras.service.mapper.model.client.EnvironmentPenalty;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.tyc.EnvironmentPenaltyService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;

/**
 * 中登网
 *
 * @author wangchuanhao
 * @date 2022/6/21 3:00 PM
 */
@Service
public class EnvironmentPenaltyServiceImpl extends ServiceImpl<EnvironmentPenaltyMapper, EnvironmentPenalty> implements EnvironmentPenaltyService {

    @Resource
    private EnvironmentPenaltyMapper environmentPenaltyMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(EnvironmentPenaltyAddREQ req) {
        EnvironmentPenalty entity = BeanUtil.copyProperties(req, EnvironmentPenalty.class);
        environmentPenaltyMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(EnvironmentPenaltyModifyREQ req) {
        EnvironmentPenalty originalInfo = environmentPenaltyMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        EnvironmentPenalty entity = BeanUtil.copyProperties(req, EnvironmentPenalty.class);
        environmentPenaltyMapper.updateAnnotationIncludeNullById(entity);
    }

    @Override
    public Page<EnvironmentPenalty> list(ExternalPageREQ req) {
        return environmentPenaltyMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<EnvironmentPenalty>lambdaQuery().eq(EnvironmentPenalty::getClientId, req.getClientId()));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(EnvironmentPenaltyRemoveREQ req) {
        EnvironmentPenalty originalInfo = environmentPenaltyMapper.selectById(req.getId());
        if (isNull(originalInfo)) {
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        environmentPenaltyMapper.deleteById(req.getId());
    }

}
