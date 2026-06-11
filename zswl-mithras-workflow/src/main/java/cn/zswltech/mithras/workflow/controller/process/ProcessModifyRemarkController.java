package cn.zswltech.mithras.workflow.controller.process;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.ProcessModifyRemarkApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.process.modify.remark.*;
import cn.zswltech.mithras.workflow.mapper.model.ProcessModifyRemark;
import cn.zswltech.mithras.workflow.mapper.model.ProcessModifyRemarkLib;
import cn.zswltech.mithras.workflow.process.ProcessModifyRemarkLibService;
import cn.zswltech.mithras.workflow.process.ProcessModifyRemarkService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.json.JSONUtil.toBean;
import static cn.hutool.json.JSONUtil.toJsonStr;

/**
 * @author yibin
 */
@Slf4j
@RestController
public class ProcessModifyRemarkController implements ProcessModifyRemarkApi {
    @Resource
    private ProcessModifyRemarkService remarkService;
    @Resource
    private ProcessModifyRemarkLibService remarkLibService;

    @Override
    public R<Void> add(ProcessModifyRemarkAddREQ req) {
        remarkService.save(BeanUtil.copyProperties(req, ProcessModifyRemark.class));
        return R.ok();
    }

    @Override
    public R<Void> modify(ProcessModifyRemarkModifyREQ req) {
        remarkService.updateById(BeanUtil.copyProperties(req, ProcessModifyRemark.class));
        return R.ok();
    }

    @Override
    public R<ProcessModifyRemarkDetailRSP> detail(ProcessModifyRemarkDetailREQ req) {
        ProcessModifyRemark one = remarkService.getOne(Wrappers.<ProcessModifyRemark>lambdaQuery()
                .eq(ProcessModifyRemark::getMainId, req.getMainId())
                .eq(ProcessModifyRemark::getRemarkType, req.getRemarkType())
                .eq(ProcessModifyRemark::getModuleType, req.getModuleType()));
        return R.ok(BeanUtil.copyProperties(one, ProcessModifyRemarkDetailRSP.class));
    }

    @Override
    public R<ProcessModifyRemarkAllRSP> all(ProcessModifyRemarkAllREQ req) {
        ProcessModifyRemark one = remarkService.getOne(Wrappers.<ProcessModifyRemark>lambdaQuery()
                .eq(ProcessModifyRemark::getMainId, req.getMainId())
                .eq(ProcessModifyRemark::getRemarkType, req.getRemarkType())
                .eq(ProcessModifyRemark::getModuleType, req.getModuleType()));
        List<ProcessModifyRemarkLib> libList = remarkLibService.list(Wrappers.<ProcessModifyRemarkLib>lambdaQuery()
                .eq(ProcessModifyRemarkLib::getMainId, req.getMainId())
                .eq(ProcessModifyRemarkLib::getVersionType, 1)
                .eq(ProcessModifyRemarkLib::getRemarkType, req.getRemarkType())
                .eq(ProcessModifyRemarkLib::getModuleType, req.getModuleType()));

        ProcessModifyRemarkAllRSP rsp = BeanUtil.copyProperties(req, ProcessModifyRemarkAllRSP.class);
        //主表数据
        if (isNotNull(one)) {
            ProcessModifyObjDTO dto = toBean(toJsonStr(one.getRemarkJson()), ProcessModifyObjDTO.class);
            dto.setCreateTime(one.getCreateTime());
            dto.setIsUpdate(true);
            rsp.setId(one.getId());
            rsp.getRemarkJsonList().add(dto);
            rsp.setModuleType(one.getModuleType());
            rsp.setRemarkType(one.getRemarkType());
        }

        //版本表数据
        if (!libList.isEmpty() && CollectionUtils.isEmpty(rsp.getRemarkJsonList())) {
            rsp.setId(libList.get(0).getId());
            rsp.setModuleType(libList.get(0).getModuleType());
            rsp.setRemarkType(libList.get(0).getRemarkType());
            rsp.setMainId(libList.get(0).getMainId());
        }
        for (ProcessModifyRemark remark : libList) {
            ProcessModifyObjDTO dto = toBean(toJsonStr(remark.getRemarkJson()), ProcessModifyObjDTO.class);
            dto.setCreateTime(remark.getCreateTime());
            rsp.getRemarkJsonList().add(dto);
        }

        //根据时间排序
        rsp.getRemarkJsonList().sort(Comparator.comparing(ProcessModifyObjDTO::getCreateTime));
        return R.ok(rsp);
    }

}
