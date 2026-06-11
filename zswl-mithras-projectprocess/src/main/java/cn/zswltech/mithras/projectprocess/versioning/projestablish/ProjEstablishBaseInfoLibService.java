package cn.zswltech.mithras.projectprocess.versioning.projestablish;

import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhaozhengkang
 * @date 2022/7/22 10:11 AM
 */
public interface ProjEstablishBaseInfoLibService extends IService<ProjEstablishBaseInfoLib> {

    List<ProjEstablishBaseInfoLib> allNewstEffectVersion();
}
