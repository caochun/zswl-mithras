package cn.zswltech.mithras.service.application.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseholdPropertyApplicationService;
import cn.zswltech.mithras.dto.SingleFileREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseholdPropertyRSP;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseholdPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yangxiong
 * @since 2023-08-14
 */
@Service
@Slf4j
public class LeaseholdPropertyFacade implements LeaseholdPropertyApplicationService {
    @Resource
    private LeaseholdPropertyService leaseholdPropertyService;

    @Override
    public R<PageR<LeaseholdPropertyRSP>> list(LeaseholdPropertyREQ param) {
        return R.ok(leaseholdPropertyService.pageList(param));
    }

    @Override
    public R<Void> excelImport(SingleFileREQ singleFile) {
        try {
            leaseholdPropertyService.excelImport(singleFile.getFile().getInputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入租赁物类型数据发生未知异常", e);
            return R.fail("导入租赁物类型数据发生未知异常");
        }
        return R.ok();
    }
}
