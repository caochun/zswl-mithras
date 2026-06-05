package cn.zswltech.mithras.projectprocess.convert.projestablish;

import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishAocPriceLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishLeasePriceLib;
import org.mapstruct.Mapper;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/9 09:57
 */
@Mapper(componentModel = "spring")
public interface ProjEstablishPriceConverter {

    ProjEstablishAocPriceRSP esAocPriceLibToRsp(ProjEstablishAocPriceLib lib);

    ProjEstablishFactoringPriceRSP esFactoringPriceLibToRsp(ProjEstablishFactoringPriceLib lib);

    ProjEstablishLeasePriceRSP esLeasePriceLibToRsp(ProjEstablishLeasePriceLib lib);
}
