package cn.zswltech.mithras.projectprocess.application.lib.projestablish.impl;

import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.application.lib.projestablish.ProjEstablishFactoringPriceLibService;
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
public class ProjEstablishFactoringPriceLibServiceImpl
        extends ServiceImpl<ProjEstablishFactoringPriceLibMapper, ProjEstablishFactoringPriceLib>
        implements ProjEstablishFactoringPriceLibService {
    @Override
    public List<ProjEstablishFactoringPriceLib> listNewestByProjEstablishIds(Set<Long> projEstablishIds) {
        return baseMapper.listNewestByProjEstablishIds(projEstablishIds);
    }
}
