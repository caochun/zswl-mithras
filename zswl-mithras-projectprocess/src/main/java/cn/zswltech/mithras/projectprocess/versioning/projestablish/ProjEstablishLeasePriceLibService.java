package cn.zswltech.mithras.projectprocess.versioning.projestablish;

import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePriceLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @author zhaozhengkang
 * @date 2022/7/22 10:11 AM
 */
public interface ProjEstablishLeasePriceLibService extends IService<ProjEstablishLeasePriceLib> {

    List<ProjEstablishLeasePriceLib> listNewestByProjEstablishIds(Set<Long> projEstablishIds);
}
