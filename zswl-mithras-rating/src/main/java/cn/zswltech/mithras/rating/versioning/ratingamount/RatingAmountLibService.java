package cn.zswltech.mithras.rating.versioning.ratingamount;

import cn.zswltech.mithras.dto.rating.ratingamount.RatingAmountDetailLibRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingClientDetailLibRSP;
import cn.zswltech.mithras.rating.model.RatingAmountLib;
import cn.zswltech.mithras.rating.model.RatingClientLib;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @create: 2023-06-18
 **/

public interface RatingAmountLibService extends IService<RatingAmountLib> {

    RatingAmountDetailLibRSP detail(Long id);
}
