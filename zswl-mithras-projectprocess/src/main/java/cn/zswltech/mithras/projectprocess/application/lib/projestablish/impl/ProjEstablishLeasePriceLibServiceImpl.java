package cn.zswltech.mithras.projectprocess.application.lib.projestablish.impl;

import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishLeasePriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishLeasePriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.ProjEstablishLeasePriceLibService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Service
public class ProjEstablishLeasePriceLibServiceImpl
        extends ServiceImpl<ProjEstablishLeasePriceLibMapper, ProjEstablishLeasePriceLib>
        implements ProjEstablishLeasePriceLibService {

    @Override
    public List<ProjEstablishLeasePriceLib> listNewestByProjEstablishIds(Set<Long> projEstablishIds) {
        return baseMapper.listNewestByProjEstablishIds(projEstablishIds);
    }
}
