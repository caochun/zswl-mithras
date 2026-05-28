package cn.zswltech.mithras.service.service.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkDto;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkListReq;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkModifyReq;
import cn.zswltech.mithras.service.convert.workbench.WorkbenchHyperlinkConverter;
import cn.zswltech.mithras.service.mapper.model.workbench.WorkbenchHyperlink;
import cn.zswltech.mithras.service.mapper.workbench.WorkbenchHyperlinkMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    @Resource
    private WorkbenchHyperlinkConverter baseConverter;

    @Transactional(rollbackFor = Throwable.class)
    public void modify(WorkbenchHyperlinkModifyReq req) {
        baseMapper.delete(Wrappers.<WorkbenchHyperlink>lambdaQuery().eq(WorkbenchHyperlink::getUserId, req.getUserId()));
        if (ObjectUtil.isNotEmpty(req.getModifyReq())) {
            List<WorkbenchHyperlink> entities = new ArrayList<>();
            for (WorkbenchHyperlinkDto dto : req.getModifyReq()) {
                WorkbenchHyperlink entity = baseConverter.dto2Entity(dto);
                entity.setUserId(req.getUserId());
                entities.add(entity);
            }
            saveBatch(entities);
        }
    }

    public List<WorkbenchHyperlinkDto> list(WorkbenchHyperlinkListReq req) {
        return baseMapper.selectList(Wrappers.<WorkbenchHyperlink>lambdaQuery()
                        .eq(WorkbenchHyperlink::getUserId, req.getUserId()))
                .stream().map(workbenchHyperlink -> baseConverter.entity2Dto(workbenchHyperlink)).collect(Collectors.toList());
    }

}