package cn.zswltech.mithras.service.service.lib.projestablish;

import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhaozhengkang
 * @date 2022/7/22 10:11 AM
 */
public interface ProjEstablishBaseInfoLibService extends IService<ProjEstablishBaseInfoLib> {

    List<ProjEstablishBaseInfoLib> allNewstEffectVersion();
}
