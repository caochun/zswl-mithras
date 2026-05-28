package cn.zswltech.mithras.service.service.third;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.zswltech.mithras.service.enums.RelationshipType;
import cn.zswltech.mithras.service.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.service.mapper.model.client.IndustryType;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.third.model.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.text.CharSequenceUtil.split;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.zswltech.mithras.service.enums.ShareholderType.*;
import static cn.zswltech.mithras.service.others.Util.returnBigDecimal;
import static cn.zswltech.mithras.service.others.Util.toMithrasUnit;

/**
 * @author luyi
 */
public class TycConvertor {

    public static MithrasBaseInfo mithrasBaseInfo(TycBaseInfo baseInfo) {
        if (isNull(baseInfo)) {
            return null;
        }
        MithrasBaseInfo info = new MithrasBaseInfo();
        info.setTripleCertInOne(null != baseInfo.getCreditCode() && equal(baseInfo.getCreditCode(), baseInfo.getOrgNumber()));
        info.setZhongZhengCode(null);
        info.setOrgCode(StringUtils.isNotBlank(baseInfo.getOrgNumber()) ? baseInfo.getOrgNumber() : null);
        info.setBizLicenseCode(baseInfo.getRegNumber());
        info.setContinuousStatus(continuousStatus(baseInfo.getRegStatus()));
        if (isNotNull(baseInfo.getEstablishTime())) {
            info.setEstablishDate(LocalDateTimeUtil.of(baseInfo.getEstablishTime()).toLocalDate());
        }
        if (isNotNull(baseInfo.getApprovedTime())) {
            info.setApprovalDate(LocalDateTimeUtil.of(baseInfo.getApprovedTime()).toLocalDate());
        }
        info.setBizLicenceLongTerm(isLongTerm(baseInfo));
        LocalDate maxDate = LocalDateTimeUtil.parseDate("2099-12-31", "yyyy-MM-dd");
        if (isNotNull(baseInfo.getToTime())) {
            info.setBizLicenseEndDate(LocalDateTimeUtil.of(baseInfo.getToTime()).toLocalDate());
            if (info.getBizLicenseEndDate().isAfter(maxDate)) {
                info.setBizLicenseEndDate(maxDate);
            }
        } else if (Boolean.TRUE.equals(info.getBizLicenceLongTerm())) {
            info.setBizLicenseEndDate(maxDate);
        }
        info.setBizScope(baseInfo.getBusinessScope());
        info.setIndustryType(toIndustryCode(baseInfo.getIndustryAll()));
        if (StrUtil.isNotBlank(info.getIndustryType())) {
            // 如果有找到行业分类则找全所有父分类
            info.setIndustryTypeWithParent(findAllParent(info.getIndustryType()));
        }
        info.setEconomyType(null);
        info.setOrgType(null);//
        info.setOrgScale(toOrgScale(baseInfo));//
        info.setRegisterCurrencyType(toCurrencyType(baseInfo.getRegCapitalCurrency()));
        info.setRegisterCapital(toMithrasUnit(returnBigDecimal(baseInfo.getRegCapital()).multiply(new BigDecimal(10000L)).longValue()));//天眼查是万元
        info.setRealCurrencyType(toCurrencyType(baseInfo.getActualCapitalCurrency()));
        info.setRealCapital(toMithrasUnit(returnBigDecimal(baseInfo.getActualCapital()).multiply(new BigDecimal(10000L)).longValue()));
        //
        if ((equal(baseInfo.getActualCapitalCurrency(), baseInfo.getRegCapitalCurrency()) || isNull(baseInfo.getActualCapital()))
                && isNotNull(info.getRealCapital())
                && info.getRegisterCapital() != 0L
        ) {
            info.setRegisterCapitalRate(toMithrasUnit(new BigDecimal(info.getRealCapital()).divide(new BigDecimal(info.getRegisterCapital()), 2, RoundingMode.HALF_UP)));
        }
        info.setCorpRepresent(baseInfo.getLegalPersonName());
        info.setCorpGender(null);
        info.setCorpCertType(null);
        info.setCorpCertCode(null);
        info.setListedCompany(StrUtil.isNotBlank(baseInfo.getBondName()));
        info.setDistrict(baseInfo.getDistrict());
        info.setCity(baseInfo.getCity());
        info.setRegLocation(baseInfo.getRegLocation());
        info.setTycName(baseInfo.getName());
        info.setBase(baseInfo.getBase());
        info.setClientName(baseInfo.getName());
        info.setBase(baseInfo.getBase());
        return info;
    }

    private static String toOrgScale(TycBaseInfo baseInfo) {
        int socialStaffNum = Optional.ofNullable(baseInfo.getSocialStaffNum()).orElse(0);
        if (socialStaffNum < 100) {
            return "TINY";
        } else if (socialStaffNum < 300) {
            return "SMALL";
        } else if (socialStaffNum < 2000) {
            return "MIDDLE";
        } else {
            return "BIG";
        }
    }

    private static String toCurrencyType(String currencyName) {
        if (isBlank(currencyName)) {
            return null;
        }
        switch (currencyName) {
            case "人民币":
                return "CNY";
            case "欧元":
                return "EUR";
            case "英镑":
                return "GBP";
            case "日元":
                return "JPY";
            case "美元":
                return "USD";
            case "港币":
                return "HKD";
        }
        return null;
    }

    private static String toIndustryCode(TycBaseInfo.IndustryAll industryAll) {
        if(industryAll == null){
            return null;
        }
        String code = null;
        IndustryTypeMapper industryTypeMapper = SpringContextHolder.getBean(IndustryTypeMapper.class);
        String category = industryAll.getCategory();
        IndustryType industryType1 = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getDisplay, category).eq(IndustryType::getLevel, 1));
        if (null != industryType1) {
            code = industryType1.getCode();
            IndustryType industryType2 = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getDisplay, industryAll.getCategoryBig()).eq(IndustryType::getParentId, industryType1.getId()));
            if (null != industryType2) {
                code = industryType2.getCode();
                IndustryType industryType3 = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getDisplay, industryAll.getCategoryMiddle()).eq(IndustryType::getParentId, industryType2.getId()));
                if (null != industryType3) {
                    code = industryType3.getCode();
                    IndustryType industryType4 = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getDisplay, industryAll.getCategorySmall()).eq(IndustryType::getParentId, industryType3.getId()));
                    if (null != industryType4) {
                        code = industryType4.getCode();
                    }
                }
            }
        }
        return code;
    }

    private static List<String> findAllParent(String industryType) {
        if (StrUtil.isBlank(industryType)) {
            return Collections.emptyList();
        }
        IndustryTypeMapper industryTypeMapper = SpringUtil.getBean(IndustryTypeMapper.class);
        List<IndustryType> all = industryTypeMapper.selectList(Wrappers.lambdaQuery());
        if (CollectionUtil.isEmpty(all)) {
            return Collections.emptyList();
        }
        Deque<String> deque = new LinkedList<>();
        IndustryType dbModel = null;
        Map<Long, IndustryType> industryTypeMap = new HashMap<>(all.size() + all.size() / 2);
        for (IndustryType it : all) {
            if (Objects.equals(it.getCode(), industryType)) {
                dbModel = it;
            }
            industryTypeMap.put(it.getId(), it);
        }
        if (Objects.isNull(dbModel)) {
            return Collections.emptyList();
        }
        deque.offer(industryType);
        // 寻找父节点
        do {
            dbModel = industryTypeMap.get(dbModel.getParentId());
            if (Objects.nonNull(dbModel)) {
                deque.offer(dbModel.getCode());
            }
        } while (Objects.nonNull(dbModel));
        List<String> result = new LinkedList<>();
        while (!deque.isEmpty()) {
            result.add(deque.pollLast());
        }
        return result;
    }


    private static String continuousStatus(String regStatus) {
        if (split("在业,存续,迁出", ",").contains(regStatus)) {
            return "1";
        } else if (split("注销,已注销", ",").contains(regStatus)) {
            return "2";
        } else if (split("吊销，未注销,吊销,清算,停业,撤销,其他", ",").contains(regStatus)) {
            return "9";
        } else {
            return "X";
        }
    }

    private static boolean isLongTerm(TycBaseInfo baseInfo) {
        Long toTime = baseInfo.getToTime();
        return null == toTime || LocalDateTimeUtil.of(toTime).getYear() >= 2099;
    }

    public static List<MithrasRelatedEnterpriseInfo> relatedEnterpriseInfo(JSONObject result) {
        IndustryTypeMapper industryTypeMapper = SpringContextHolder.getBean(IndustryTypeMapper.class);
        List<MithrasRelatedEnterpriseInfo> list = new ArrayList<>();
        if (isNotNull(result)) {
            JSONArray items = result.getJSONArray("items");
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                MithrasRelatedEnterpriseInfo info = new MithrasRelatedEnterpriseInfo();
                info.setEnterpriseName(item.getStr("name"));
                info.setRelationship(RelationshipType.INVEST.name());

                //天眼查的单位是万元，所以扩大一万倍
                BigDecimal amount = item.getBigDecimal("amount");
                if (null != amount) {
                    amount = amount.multiply(new BigDecimal("10000"));
                }
                info.setInvestAmount(toMithrasUnit(amount));
                //天眼查的单位是万元，所以扩大一万倍
                info.setRegisterCapital(toMithrasUnit(returnBigDecimal(item.getStr("regCapital"), new BigDecimal("10000"))));


                info.setShareholdingRatio(toMithrasUnit(returnBigDecimal(item.getStr("percent"))));
                info.setContinuousStatus(continuousStatus(item.getStr("regStatus")));
                info.setEstablishDate(Optional.ofNullable(item.getLong("estiblishTime")).map(LocalDateTimeUtil::of).map(LocalDateTime::toLocalDate).orElse(null));
                List<IndustryType> category = industryTypeMapper.selectList(
                        Wrappers.<IndustryType>lambdaQuery()
                                .eq(IndustryType::getDisplay, item.getStr("category"))
                );
                info.setIndustryTypeName(item.getStr("category"));
                if (isNotNull(category)) {
                    info.setIndustryType(category.get(0).getCode());
                }
                list.add(info);
            }
        }
        return list;
    }

    public static List<MithrasShareholderInfo> shareholderInfo(JSONObject result) {
        List<MithrasShareholderInfo> list = new ArrayList<>();
        if (isNotNull(result)) {
            JSONArray items = result.getJSONArray("items");
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                MithrasShareholderInfo info = new MithrasShareholderInfo();
                //
                info.setShareholderName(item.getStr("name"));
                //
                Integer type = item.getInt("type");
                String shareholderType = equal(type, 1) ? LEGAL_PERSON.name() : equal(type, 2) ? NORMAL_PERSON.name() : OTHER.name();
                info.setShareholderType(shareholderType);
                //实缴金额
                JSONArray capitalActlJsonArray = item.getJSONArray("capitalActl");
                //认缴
                JSONArray capitalJsonArray = item.getJSONArray("capital");
                //优先获取认缴的出资占比
                if (!CollUtil.isEmpty(capitalJsonArray)) {
                    String percent = capitalJsonArray.getJSONObject(0).getStr("percent");
                    if (!StringUtils.isBlank(percent)) {
                        info.setCapitalPercent(toMithrasUnit(returnBigDecimal(percent)));
                        info.setCapitalWay(capitalJsonArray.getJSONObject(0).getStr("paymet"));
                    }
                } else if (!CollUtil.isEmpty(capitalActlJsonArray)) { //认缴为空取实缴
                    String percent = capitalActlJsonArray.getJSONObject(0).getStr("percent");
                    //如果实缴的出资占比不为空，更新占比
                    if (!StringUtils.isBlank(percent)) {
                        info.setCapitalPercent(toMithrasUnit(returnBigDecimal(percent)));
                        info.setCapitalWay(capitalActlJsonArray.getJSONObject(0).getStr("paymet"));
                    }
                }
                //统计实缴金额
                if (!capitalActlJsonArray.isEmpty()) {
                    BigDecimal amomon = BigDecimal.ZERO;
                    for (int c = 0; c < capitalActlJsonArray.size(); c++) {
                        amomon = amomon.add(returnBigDecimal(capitalActlJsonArray.getJSONObject(c).getStr("amomon")));
                    }
                    //默认认为单位是"万人民币"
                    info.setActualPaidTotal(toMithrasUnit(amomon.multiply(new BigDecimal("10000"))));
                }
                if (!capitalJsonArray.isEmpty()) {
                    BigDecimal amomon = BigDecimal.ZERO;
                    for (int c = 0; c < capitalJsonArray.size(); c++) {
                        amomon = amomon.add(returnBigDecimal(capitalJsonArray.getJSONObject(c).getStr("amomon")));
                    }
                    //默认认为单位是"万人民币"
                    info.setPaidTotal(toMithrasUnit(amomon.multiply(new BigDecimal("10000"))));
                }

                //todo confirm
                info.setRealController(null);
                list.add(info);
            }
        }
        return list;
    }

    public static List<MithrasCompanyInfo> companyInfoConvertor(TycDataList req) {
        if(CollectionUtil.isEmpty(req.getItems())){
            return new ArrayList<>();
        }
        return req.getItems().stream().map(result -> {
            MithrasCompanyInfo rsp = new MithrasCompanyInfo();
            rsp.setCreditCode(result.getCreditCode());
            rsp.setCompanyName(result.getName());
            return rsp;
        }).collect(Collectors.toList());
    }
}

