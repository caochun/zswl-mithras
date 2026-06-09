package cn.zswltech.mithras.system.audit;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.SystemUserOperateLogREQ;
import cn.zswltech.mithras.dto.SystemUserOperateLogRSP;
import cn.zswltech.mithras.system.mapper.SystemUserOperateLogMapper;
import cn.zswltech.mithras.system.mapper.model.SystemUserOperateLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/9/10
 * @description
 */
@Service
public class SystemUserOperateLogService extends ServiceImpl<SystemUserOperateLogMapper, SystemUserOperateLog> {
    public PageR<SystemUserOperateLogRSP> pageList(SystemUserOperateLogREQ req) {
        Page<SystemUserOperateLog> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<SystemUserOperateLog> conditionQuery = Wrappers.lambdaQuery();
        if (Objects.nonNull(req.getUserId())) {
            conditionQuery.eq(SystemUserOperateLog::getUserId, req.getUserId());
        }
        conditionQuery.orderByDesc(SystemUserOperateLog::getId);
        Page<SystemUserOperateLog> pageResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(pageResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<SystemUserOperateLogRSP> list = pageResult.getRecords().stream().map(e -> {
            SystemUserOperateLogRSP rsp = new SystemUserOperateLogRSP();
            rsp.setUserId(e.getUserId());
            rsp.setUserName(e.getUserName());
            rsp.setUrl(e.getUrl());
            rsp.setOperateTime(e.getCreateTime());
            rsp.setReqData(e.getReqData());
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(list, pageResult.getTotal(), req.getPage(), req.getPageSize());
    }
}
