package cn.zswltech.mithras.service.controller.basedata;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.api.basedata.BaseDataSpecialDateApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.basedata.BaseDataSpecialDateInitREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataSpecialDateSaveREQ;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataSpecialDate;
import cn.zswltech.mithras.service.service.basedata.BaseDataSpecialDateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/4
 * @description
 */
@Slf4j
@RestController
public class BaseDataSpecialDateController implements BaseDataSpecialDateApi {
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;

    @Override
    public R<Void> save(@Valid BaseDataSpecialDateSaveREQ req) {
        LocalDate localDate = LocalDateTimeUtil.parse(req.getSpecialDate(), DatePattern.NORM_DATE_PATTERN).toLocalDate();
        BaseDataSpecialDate baseDataSpecialDate = new BaseDataSpecialDate();
        baseDataSpecialDate.setId(req.getId());
        baseDataSpecialDate.setSpecialType(req.getSpecialType());
        baseDataSpecialDate.setYear(localDate.getYear());
        baseDataSpecialDate.setMonth(localDate.getMonthValue());
        baseDataSpecialDate.setSpecialDate(localDate);
        baseDataSpecialDateService.saveOrUpdate(baseDataSpecialDate);
        return R.ok();
    }

    @Override
    public R<Void> init(BaseDataSpecialDateInitREQ req) {
        baseDataSpecialDateService.initData(req.getContent(), req.getYear());
        return R.ok();
    }
}
