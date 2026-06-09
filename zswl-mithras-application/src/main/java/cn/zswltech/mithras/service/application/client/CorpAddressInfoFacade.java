package cn.zswltech.mithras.service.application.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.customer.application.client.api.CorpAddressInfoApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.addressinfo.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpAddressInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpAddressInfoService;
import cn.zswltech.mithras.customer.application.lib.client.CorpAddressInfoLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.zswltech.mithras.service.others.Const.ADDRESS_INFO_COUNTRY_CHINA;
import static cn.zswltech.mithras.service.others.Util.errMissingParam;
import org.springframework.stereotype.Service;

/**
 * @author luyi
 */
@Service
public class CorpAddressInfoFacade implements CorpAddressInfoApplicationService {

    @Resource
    private CorpAddressInfoService addressInfoService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private CorpAddressInfoLibService addressInfoLibService;

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> add(CorpAddressInfoAddREQ req) {
        //check
        String country = req.getCountry();
        if (isHomeCountry(country)) {
            errMissingParam(isBlank(req.getProvince()), "省份");
            if (!isSpecialProvince(req.getProvince())) {
                errMissingParam(isBlank(req.getCity()), "城市");
                errMissingParam(isBlank(req.getDistrict()), "区/县");
                errMissingParam(isBlank(req.getDetail()), "详细地址");
                errMissingParam(isBlank(req.getRegionCode()), "行政区域代码");
            }
        }
        addressInfoService.add(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = CorpAddressInfoMapper.class)
    public R<Void> modify(CorpAddressInfoModifyREQ req) {
        String country = req.getCountry();
        if (isHomeCountry(country)) {
            errMissingParam(isBlank(req.getProvince()), "省份");
            if (!isSpecialProvince(req.getProvince())) {
                errMissingParam(isBlank(req.getCity()), "城市");
                errMissingParam(isBlank(req.getDistrict()), "区/县");
                errMissingParam(isBlank(req.getDetail()), "详细地址");
                errMissingParam(isBlank(req.getRegionCode()), "行政区域代码");
            }
        }
        addressInfoService.modify(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<PageR<CorpAddressInfoListRSP>> list(CorpAddressInfoListREQ req) {
//        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
//            return R.ok(PageR.of(Collections.emptyList(), 0));
//        }
        Page<CorpAddressInfo> data = addressInfoService.list(req);
        List<CorpAddressInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), CorpAddressInfoListRSP.class);
        //code to name
        Set<String> codeSet = new HashSet<>(list.size() * 3);
        list.forEach(e -> {
            codeSet.add(e.getCountry());
            codeSet.add(e.getProvince());
            codeSet.add(e.getCity());
            codeSet.add(e.getDistrict());
        });
        if (!codeSet.isEmpty()) {
            Map<String, String> nameMap = addressDictionaryMapper.selectList(Wrappers.<AddressDictionary>lambdaQuery().in(AddressDictionary::getCode, codeSet))
                    .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));
            list.forEach(e -> {
                e.setCountryName(nameMap.get(e.getCountry()));
                e.setProvinceName(nameMap.get(e.getProvince()));
                e.setCityName(nameMap.get(e.getCity()));
                e.setDistrictName(nameMap.get(e.getDistrict()));
            });
        }

        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = CorpAddressInfoMapper.class)
    public R<Void> remove(CorpAddressInfoRemoveREQ req) {
        addressInfoService.remove(req.getId());
        return R.ok();
    }

    private boolean isHomeCountry(String countryCode) {
        return ADDRESS_INFO_COUNTRY_CHINA.equals(countryCode);
    }

    /**
     * 是否为港澳台地区为特殊省份
     *
     * @param provinceCode 省份code
     * @return 是/否
     */
    private boolean isSpecialProvince(String provinceCode) {
        return StrUtil.equalsAny(provinceCode, "810000", "710000", "820000");
    }
}
