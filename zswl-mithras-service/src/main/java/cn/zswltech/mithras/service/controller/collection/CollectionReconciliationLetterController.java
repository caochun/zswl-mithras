package cn.zswltech.mithras.service.controller.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.collection.CollectionReconciliationLetterApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.CollectionReconciliationLetterREQ;
import cn.zswltech.mithras.dto.collection.CollectionReconciliationLetterRSP;
import cn.zswltech.mithras.collection.service.bo.ReconciliationLetterBO;
import cn.zswltech.mithras.service.service.collection.ReconciliationLetterService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * @ClassName CollectionReconciliationLetterController
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/27 10:52 上午
 * @Version 1.0
 **/
@RestController
public class CollectionReconciliationLetterController implements CollectionReconciliationLetterApi {

    @Resource
    private ReconciliationLetterService reconciliationLetterService;
    @Resource
    private HttpServletResponse httpServletResponse;

    @SneakyThrows
    @Override
    public void exportList(CollectionReconciliationLetterREQ req) {
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("对账函清单汇总.xlsx" , StandardCharsets.UTF_8.name()));
        reconciliationLetterService.exportExcel(req, httpServletResponse.getOutputStream());
    }

    @Override
    public R<CollectionReconciliationLetterRSP> list(@Valid CollectionReconciliationLetterREQ req) {
        Map<Long, ReconciliationLetterBO> longReconciliationLetterBOMap = reconciliationLetterService.statisticsClientsLetter(req.getDate());
        CollectionReconciliationLetterRSP collectionReconciliationLetterRSP = new CollectionReconciliationLetterRSP();
        Collection<ReconciliationLetterBO> values = longReconciliationLetterBOMap.values();
        if(ObjectUtil.isNotEmpty(values)){
            collectionReconciliationLetterRSP.setReconciliationLetterBOS(BeanUtil.copyToList(values,
                    CollectionReconciliationLetterRSP.ReconciliationLetterBO.class));

        values.forEach(base -> {
            collectionReconciliationLetterRSP.setRentAndNominalPriceTotal(add(collectionReconciliationLetterRSP.getRentAndNominalPriceTotal(),
                    base.getRentAndNominalPrice()));
            collectionReconciliationLetterRSP.setMarginTotal(add(collectionReconciliationLetterRSP.getMarginTotal(),
                    base.getMargin()));
            collectionReconciliationLetterRSP.setBlPrincipalAndInterestTotal(add(collectionReconciliationLetterRSP.getBlPrincipalAndInterestTotal(), base.getBlPrincipalAndInterest()));
        });
        }
        return R.ok(collectionReconciliationLetterRSP);
    }

    private BigDecimal add(BigDecimal old, BigDecimal add){
        if(ObjectUtil.isEmpty(old)){
            old = new BigDecimal(0L);
        }
        if(ObjectUtil.isEmpty(add)){
            add = new BigDecimal(0L);
        }
        return old.add(add).setScale(2, RoundingMode.HALF_UP);
    }
}
