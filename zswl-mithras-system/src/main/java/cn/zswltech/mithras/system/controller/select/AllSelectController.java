package cn.zswltech.mithras.system.controller.select;

import cn.zswltech.mithras.api.AllSelectApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.LeafSelectRSP;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.TreeSelectRSP;
import cn.zswltech.mithras.system.application.select.AllSelectApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class AllSelectController implements AllSelectApi {

    @Resource
    private AllSelectApplicationService allSelectApplicationService;

    @Override
    public R<Map<String, List<SelectRSP>>> allSelect() {
        return allSelectApplicationService.allSelect();
    }

    @Override
    public R<List<SelectRSP>> countryList() {
        return allSelectApplicationService.countryList();
    }

    @Override
    public R<List<LeafSelectRSP>> childRegionList(String code) {
        return allSelectApplicationService.childRegionList(code);
    }

    @Override
    public R<List<LeafSelectRSP>> rootIndustry() {
        return allSelectApplicationService.rootIndustry();
    }

    @Override
    public R<List<TreeSelectRSP>> allIndustry() {
        return allSelectApplicationService.allIndustry();
    }

    @Override
    public R<List<LeafSelectRSP>> childIndustry(String code) {
        return allSelectApplicationService.childIndustry(code);
    }

    @Override
    public R<List<SelectRSP>> founderList(String name, String job, Boolean sameDept, int pageSize) {
        return allSelectApplicationService.founderList(name, job, sameDept, pageSize);
    }

    @Override
    public R<List<SelectRSP>> orgList(String name, Integer type) {
        return allSelectApplicationService.orgList(name, type);
    }

    @Override
    public R<List<SelectRSP>> newOrgList(String name, Integer type) {
        return allSelectApplicationService.newOrgList(name, type);
    }

    @Override
    public R<List<SelectRSP>> orgListByUserId(Long userId) {
        return allSelectApplicationService.orgListByUserId(userId);
    }

    @Override
    public R<List<SelectRSP>> businessheaderListByDeptId(Long deptId) {
        return allSelectApplicationService.businessheaderListByDeptId(deptId);
    }

    @Override
    public R<List<SelectRSP>> levelOrgList(String name, Integer type, Integer level) {
        return allSelectApplicationService.levelOrgList(name, type, level);
    }
}
