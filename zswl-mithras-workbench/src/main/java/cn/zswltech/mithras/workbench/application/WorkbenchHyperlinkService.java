package cn.zswltech.mithras.workbench.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkDto;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkListReq;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkModifyReq;
import cn.zswltech.mithras.workbench.mapper.model.WorkbenchHyperlink;
import cn.zswltech.mithras.workbench.mapper.WorkbenchHyperlinkMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 首页工作台-超链接
 * @date 2023-03-17
 */
@Service
public class WorkbenchHyperlinkService extends ServiceImpl<WorkbenchHyperlinkMapper, WorkbenchHyperlink> {

    @Transactional(rollbackFor = Throwable.class)
    public void modify(WorkbenchHyperlinkModifyReq req) {
        baseMapper.delete(Wrappers.<WorkbenchHyperlink>lambdaQuery().eq(WorkbenchHyperlink::getUserId, req.getUserId()));
        if (ObjectUtil.isNotEmpty(req.getModifyReq())) {
            List<WorkbenchHyperlink> entities = new ArrayList<>();
            for (WorkbenchHyperlinkDto dto : req.getModifyReq()) {
                WorkbenchHyperlink entity = toEntity(dto);
                entity.setUserId(req.getUserId());
                entities.add(entity);
            }
            saveBatch(entities);
        }
    }

    public List<WorkbenchHyperlinkDto> list(WorkbenchHyperlinkListReq req) {
        return baseMapper.selectList(Wrappers.<WorkbenchHyperlink>lambdaQuery()
                        .eq(WorkbenchHyperlink::getUserId, req.getUserId()))
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private WorkbenchHyperlink toEntity(WorkbenchHyperlinkDto dto) {
        WorkbenchHyperlink entity = new WorkbenchHyperlink();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setUserId(dto.getUserId());
        return entity;
    }

    private WorkbenchHyperlinkDto toDto(WorkbenchHyperlink workbenchHyperlink) {
        WorkbenchHyperlinkDto dto = new WorkbenchHyperlinkDto();
        dto.setId(workbenchHyperlink.getId());
        dto.setName(workbenchHyperlink.getName());
        dto.setAddress(workbenchHyperlink.getAddress());
        dto.setUserId(workbenchHyperlink.getUserId());
        return dto;
    }

}
