package cn.zswltech.mithras.service.service.log;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.log.SysOperLogAddREQ;
import cn.zswltech.mithras.dto.log.SysOperLogListREQ;
import cn.zswltech.mithras.dto.log.SysOperLogModifyREQ;
import cn.zswltech.mithras.dto.log.SysOperLogRemoveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.event.OperLogEvent;
import cn.zswltech.mithras.service.mapper.log.SysOperLogMapper;
import cn.zswltech.mithras.service.model.log.SysOperLog;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.AddressUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
* @description 操作日志记录
* @author hspcadmin
* @date 2025-09-07
*/
@Service
public class SysOperLogService {

    @Resource
    private SysOperLogMapper sysOperLogMapper;

    /**
     * 操作日志记录
     *
     * @param operLogEvent 操作日志事件
     */
    @Async
    @EventListener
    public void recordOper(OperLogEvent operLogEvent) {
        SysOperLog operLog = BeanUtil.toBean(operLogEvent, SysOperLog.class);
        // 远程查询操作地点
        operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
        operLog.setOperTime(LocalDateTime.now());
        sysOperLogMapper.insert(operLog);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(SysOperLogAddREQ req) {
        SysOperLog info = BeanUtil.copyProperties(req, SysOperLog.class);
        sysOperLogMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(SysOperLogModifyREQ req) {
        SysOperLog originalInfo = sysOperLogMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        SysOperLog info = BeanUtil.copyProperties(req, SysOperLog.class);
        sysOperLogMapper.updateById(info);
    }

    public Page<SysOperLog> list(SysOperLogListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(SysOperLogRemoveREQ req) {
        SysOperLog originalInfo = sysOperLogMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        sysOperLogMapper.deleteById(req.getId());
    }

}