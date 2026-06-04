package cn.zswltech.mithras.projectprocess.service.lib.projestablish;

import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishFactoringPriceLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @author zhaozhengkang
 * @date 2022/7/22 10:11 AM
 */
public interface ProjEstablishFactoringPriceLibService extends IService<ProjEstablishFactoringPriceLib> {

    List<ProjEstablishFactoringPriceLib> listNewestByProjEstablishIds(Set<Long> projEstablishIds);
}
