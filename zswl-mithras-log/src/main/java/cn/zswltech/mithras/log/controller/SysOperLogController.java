package cn.zswltech.mithras.log.controller;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.api.log.SysOperLogApi;
import cn.zswltech.mithras.dto.log.SysOperLogAddREQ;
import cn.zswltech.mithras.dto.log.SysOperLogModifyREQ;
import cn.zswltech.mithras.dto.log.SysOperLogListREQ;
import cn.zswltech.mithras.dto.log.SysOperLogListRSP;
import cn.zswltech.mithras.dto.log.SysOperLogRemoveREQ;
import cn.zswltech.mithras.log.service.SysOperLogService;
import cn.zswltech.mithras.log.model.SysOperLog;

import java.util.List;

/**
* @description 操作日志记录
* @author hspcadmin
* @date 2025-09-07
*/
@RestController
public class SysOperLogController implements SysOperLogApi {

    @Resource
    private SysOperLogService sysOperLogService;

    @Override
    public R<Void> add(SysOperLogAddREQ req) {
        sysOperLogService.add(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(SysOperLogModifyREQ req){
        sysOperLogService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<SysOperLogListRSP>> list(SysOperLogListREQ req){
        Page<SysOperLog> data = sysOperLogService.list(req);
        List<SysOperLogListRSP> list = BeanUtil.copyToList(data.getRecords(), SysOperLogListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<Void> remove(SysOperLogRemoveREQ req){
        sysOperLogService.remove(req);
        return R.ok();
    }

}