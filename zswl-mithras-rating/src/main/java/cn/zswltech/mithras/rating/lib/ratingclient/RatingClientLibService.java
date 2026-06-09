package cn.zswltech.mithras.rating.lib.ratingclient;

import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDetailLibRSP;
import cn.zswltech.mithras.rating.model.RatingClientLib;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @create: 2023-06-18
 **/

public interface RatingClientLibService extends IService<RatingClientLib> {

    RatingClientDetailLibRSP detail(Long id);
}
