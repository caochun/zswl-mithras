package cn.zswltech.mithras.afterlease.interfaces;

import cn.zswltech.mithras.api.afterlease.RentCollectionDetailApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.rentcollection.*;
import cn.zswltech.mithras.afterlease.application.RentCollectionDetailService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2022-11-18
 **/
@RestController
public class RentCollectionDetailController implements RentCollectionDetailApi {

    @Resource
    private RentCollectionDetailService rentCollectionDetailService;


    @Override
    public R<RentDetailInfoRSP> rentDetail(@Valid RentDetailInfoREQ req) {
        return rentCollectionDetailService.rentDetail(req);
    }

    @Override
    public R<OverdueDetailInfoRSP> overdueDetail(@Valid RentDetailInfoREQ req) {
        return rentCollectionDetailService.overdueDetail(req);
    }

//    @Override
//    public R<ProjEstablishBaseInfoListRSP> projDetail(@Valid ProjDetailInfoREQ req) {
//        return null;
//    }

    @Override
    public R<List<OverdueRentListInfoRSP>> overdueRentList(@Valid ProjDetailInfoREQ req) {
        return rentCollectionDetailService.overdueRentList(req);
    }
}
