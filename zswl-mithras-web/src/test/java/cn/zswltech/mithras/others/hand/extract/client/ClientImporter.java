package cn.zswltech.mithras.others.hand.extract.client;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.service.enums.client.ClientProcessStatus;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.corp.*;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.*;
import cn.zswltech.mithras.service.mapper.model.AddressDictionary;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.GeneralDictionary;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.service.mapper.normal.NormalBankAccountMapper;
import cn.zswltech.mithras.service.mapper.normal.NormalBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.normal.NormalSpouseMapper;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpSubjectItemService;
import cn.zswltech.mithras.service.service.lib.client.impl.ClientVersionServiceImpl;
import cn.zswltech.mithras.service.service.third.model.MithrasBaseInfo;
import cn.zswltech.mithras.service.service.third.model.MithrasRelatedEnterpriseInfo;
import cn.zswltech.mithras.service.service.third.model.MithrasShareholderInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cn.zswltech.mithras.others.hand.extract.ImportCommonHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.CorpAddressType.REGISTRY_ADDRESS;
import static cn.zswltech.mithras.service.others.Const.ENUM_TYC_PROVINCE;

/**
 * 客户数据导入
 *
 * @author wangchuanhao
 * @date 2022/9/25 10:05 AM
 */
@Component
@Slf4j
public class ClientImporter {

    @Resource
    private ImportCommonHelper importCommonHelper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;
    @Resource
    private GeneralDictionaryMapper generalDictionaryMapper;
    @Resource
    private IndustryTypeMapper industryTypeMapper;

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpAddressInfoMapper addressInfoMapper;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Resource
    private CorpRelatedEnterpriseMapper relatedEnterpriseMapper;
    @Resource
    private CorpContactInfoMapper contactInfoMapper;
    @Resource
    private CorpShareholderInfoMapper shareholderInfoMapper;
    @Resource
    private CorpSubjectItemMapper subjectItemMapper;
    @Resource
    private CorpBankAccountMapper bankAccountMapper;
    @Resource
    private CorpBondInfoMapper bondInfoMapper;
    @Resource
    private CorpAddressInfoLibMapper addressInfoLibMapper;
    @Resource
    private CorpCommerceInfoLibMapper commerceInfoLibMapper;
    @Resource
    private CorpRelatedEnterpriseLibMapper relatedEnterpriseLibMapper;
    @Resource
    private CorpContactInfoLibMapper contactInfoLibMapper;
    @Resource
    private CorpShareholderInfoLibMapper shareholderInfoLibMapper;
    @Resource
    private CorpBankAccountLibMapper bankAccountLibMapper;
    @Resource
    private CorpBondInfoLibMapper bondInfoLibMapper;
    @Resource
    private NormalBaseInfoMapper normalBaseInfoMapper;
    @Resource
    private NormalBankAccountMapper normalBankAccountMapper;
    @Resource
    private NormalSpouseMapper normalSpouseMapper;
    @Resource
    private NormalBaseInfoLibMapper normalBaseInfoLibMapper;
    @Resource
    private NormalBankAccountLibMapper normalBankAccountLibMapper;
    @Resource
    private NormalSpouseLibMapper normalSpouseLibMapper;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private ClientVersionServiceImpl clientVersionService;
    @Resource
    private CorpSubjectItemService subjectItemService;
    @Resource
    private ClientService clientService;

    /**
     * 导入基本信息页
     */
    @SneakyThrows
    public void initBaseData() {
        Map<String, Long> userIdMap = importCommonHelper.queryUserIdMap();
        Map<Long, Long> userOrgMap = importCommonHelper.queryUserOrgMap(userIdMap.values());
        Map<String, Long> orgIdMap = importCommonHelper.queryOrgMap();

        String handClientDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得客户模块导出_20220928.json").getInputStream(), Charset.defaultCharset());
        JSONArray handClientDataArray = JSONArray.parseArray(handClientDataString);
        String tycClientDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得客户模块天眼查数据_20220928_merge.json").getInputStream(), Charset.defaultCharset());
        JSONObject tycClientDataObj = JSONArray.parseObject(tycClientDataString);

        // 错误数据暂不导入
        Set<String> errData = new HashSet<>(Arrays.asList("杭州中诚装备服务股份有限公司", "杭州中诚装备服务有限公司", "湖南人健干粉砂浆有限公司", "湖南和立东升实业集团有限公司", "杭州桑尼能源科技股份有限公司", "兰溪市国有资产投资发展有限公司", "舟山普陀城市投资发展集团有限公司（担保人）", "浙江大学医学院附属口腔医院（浙江省口腔医院）", "绍兴市柯桥区滨海城市建设开发投资有限公司", "上海裕亿", "杭州千岛湖高铁新区发展有限公司", "浙江交科供应链管理有限公司", "宁波溪口雪窦山风景名胜区自来水有限公司", "浙江逸盛新材料有限公司", "我", "广邦（厦门）塑胶科技有限公司", "浙江豪邦化工有限公司", "柳州钢铁股份有限公司", "中建锦程机械设备（上海）有限公司", "地上铁租车（深圳）有限公司", "绍兴市科技创业投资有限公司", "绍兴高新技术产业开发区迪荡新城投资发展有限公司", "绍兴添溢工程机械租赁有限公司", "泰兴市成兴国有资产经营投资有限公司", "杭州网营物联控股集团有限公司-废弃", "中国阳光纸业控股有限公司", "金鼎重工有限公司", "德清联创科技新城建设有限公司", "错", "兰溪市交通建设投资集团有限公司", "浙江卓航物流发展股份有限公司", "浙江长龙海运有限公司", "林州凤宝管业有限公司", "河南凤宝特钢有限公司", "上海鼎衡航运科技有限公司", "象山县滨海投资有限公司", "宁波市沥平航运有限公司", "宁波市海洲船务有限公司", "象山县通用燃气有限公司", "222", "盐城市亭湖城市资产投资实业有限公司", "盐城市亭湖城市资产投资实业", "盐城海瀛控股集团有限公司", "太平洋建设集团有限公司", "绍兴市柯桥区滨海城市建设开发投资有限公司", "杭州中诚装备服务股份有限公司", "陈华禄", "诸暨市城东新城建设有限公司", "绍兴柯桥经济开发区开发投资有限公司", "德清联创科技新城建设有限公司", "大连华德天宇集团有限公司", "嵊州市经济开发区投资有限公司", "兰溪市城市投资集团有限公司", "陈佩珍", "福建鑫宏兴航运有限公司", "佛山市金海辉煌不锈钢有限公司", "宁波市奉化区旅游集散中心有限公司", "兰溪市交通建设投资集团有限公司", "浙商中拓集团股份有限公司", "德清乾龙建设发展有限公司", "张锐", "宁波市雪窦开发投资集团有限公司", "泰兴市成兴国有资产经营投资有限公司", "湖州申太建设发展有限公司", "嵊州市投资控股有限公司", "余惠君", "余慧君", "李亚丈夫", "李亚", "李鑫夫人", "李鑫", "杨利娟", "张体东", "肖清美", "许沂炎", "邹昌盛之妻", "邹昌盛", "雷丽", "邹运长", "林剑华之妻", "林剑华", "吴少杰之妻", "吴少杰", "象山县滨海投资有限公司", "象山县通用燃气有限公司", "中讯网络科技有限公司", "浙江欣捷建设有限公司"));
        Set<String> needImportClientCode = new HashSet<>(Arrays.asList("BP2021060400004", "BP2021112300037", "BP2021110100004", "BP2021101100004", "BP2021083100032", "BP2021083000031", "BP2021081100019", "BP2021072200019", "BP2021072200018", "BP2021062300020", "BP2021062200019", "BP2021061900016", "BP2021061600011", "BP2021061600010", "BP2021051200016", "BP2022081200029", "BP2021040100003", "BP2022092600032", "BP2022033000045", "BP2021031500025", "BP2021031400024", "BP2021031200023", "BP2021031200022", "BP2021031100014", "BP2021030500010", "BP2021030500008", "BP2021030500007", "BP2021030500006", "BP2021011500009", "BP2021011500007", "BP202101150021", "BP2021112300037", "BP2021060400004", "BP2022080500014", "BP2021061700015", "BP2021112300038", "BP2022092600032", "BP2022080500013", "BP2022031700019", "BP2022033000048", "BP2021032400037", "BP2022070800007", "BP202101150058", "BP2021062300021", "BP2022033000045", "BP2022031300016", "BP2022052400025", "BP202101150109", "BP2021062300022", "BP2022081200029", "BP2022071100010", "BP2022021800018", "BP2022011800028", "BP2021080900015", "BP2021112300044", "BP2021112300043", "BP2022052000023", "BP2022052000022", "BP2021092700032", "BP2021092700031", "BP2021092700030", "BP2021092700029", "BP2021101300008", "BP2021030500010", "BP2021030500006", "BP2022031600018", "BP2021050900001"));
        Map<String, String> idCardMap = new HashMap<String, String>() {{
            put("BP2022011800028", "510128197108032180");
            put("BP2021080900015", "13020219711124701X");
            put("BP2021112300044", "32092319820218216X");
            put("BP2021112300043", "32038219840109483X");
            put("BP2022052000023", "352601195010122524");
            put("BP2022052000022", "352601194907052511");
            put("BP2021092700032", "230903198703060323");
            put("BP2021092700031", "210211198701023519");
            put("BP2021092700030", "210211195509263527");
            put("BP2021092700029", "21021119531216703X");
            put("BP2021101300008", "350321199210038816");
        }};
        Map<String, String> socialCreditCodeMap = new HashMap<String, String>() {{
            put("BP2021060400004", "91330106566051501P");
            put("BP2021112300037", "913306217125879893");
            put("BP2021110100004", "91310120554313543T");
            put("BP2021101100004", "91330127328182821N");
            put("BP2021083100032", "91330108757213059M");
            put("BP2021083000031", "91330283093358385U");
            put("BP2021081100019", "91330211MA2AFXPF3N");
            put("BP2021072200019", "913502005628182349");
            put("BP2021072200018", "91330800680746534A");
            put("BP2021062300020", "91450200715187622B");
            put("BP2021062200019", "91310120074843000G");
            put("BP2021061900016", "914403003349641927");
            put("BP2021061600011", "913306007266150396");
            put("BP2021061600010", "913306007719222220");
            put("BP2021051200016", "91330602MA29BUJP6F");
            put("BP2022081200029", "91321283748721326M");
            put("BP2021040100003", "91130400559092268L");
            put("BP2022092600032", "91330521560970726A");
            put("BP2022033000045", "91330781MA2DFTBDX9");
            put("BP2021031500025", "91330109060981090D");
            put("BP2021031400024", "91330903738406517X");
            put("BP2021031200023", "91410581660948585B");
            put("BP2021031200022", "91410581739067174E");
            put("BP2021031100014", "91310000759869086C");
            put("BP2021030500010", "91330225799531063A");
            put("BP2021030500008", "91330205728135880F");
            put("BP2021030500007", "913302057133453913");
            put("BP2021030500006", "91330225739451950K");
            put("BP2021011500009", "91320902791066451C");
            put("BP2021011500007", "91320900598565179K");
            put("BP202101150021", "91320000608946953Q");
        }};
        Set<String> zhongZhengCodeClearSet = new HashSet<>(Arrays.asList("BP2021030500010", "BP2021030500006"));


        transactionTemplate.execute(status -> {
            try {
                for (int i = 0; i < handClientDataArray.size(); i++) {
                    JSONObject handClientDataObj = handClientDataArray.getJSONObject(i);
                    handClientDataObj.put("bp_name", handClientDataObj.getString("bp_name").trim().replaceAll("\\t", ""));
                    if (errData.contains(handClientDataObj.getString("bp_name"))) {
                        if (!needImportClientCode.contains(handClientDataObj.getString("bp_code"))) {
                            // 客户编号限制
                            continue;
                        }
                    }
                    if (idCardMap.containsKey(handClientDataObj.getString("bp_code"))) {
                        // 自然人身份证更正
                        handClientDataObj.put("id_card_no", idCardMap.get(handClientDataObj.getString("bp_code")));
                    }
                    if (socialCreditCodeMap.containsKey(handClientDataObj.getString("bp_code"))) {
                        // 法人社会统一信用代码更正
                        handClientDataObj.put("social_credit_code", socialCreditCodeMap.get(handClientDataObj.getString("bp_code")));
                    }
                    if (zhongZhengCodeClearSet.contains(handClientDataObj.getString("bp_code"))) {
                        // 清空中征码
                        handClientDataObj.put("identity_code", null);
                    }
                    // 此次只处理错误客户
                    log.info("处理客户{}:{}", i, handClientDataObj.getString("bp_name"));

                    // 法人 天眼查数据
                    JSONObject tycDataObj = Optional.ofNullable(tycClientDataObj.getJSONObject(handClientDataObj.getString("social_credit_code"))).orElse(new JSONObject());
                    MithrasBaseInfo tycBaseInfo = Optional.ofNullable(JSONObject.parseObject(tycDataObj.getString("mithrasBaseInfo"), MithrasBaseInfo.class)).orElse(new MithrasBaseInfo());
                    List<MithrasShareholderInfo> shareholderInfoArray = Optional.ofNullable(JSONArray.parseArray(tycDataObj.getString("shareholderInfoArray"), MithrasShareholderInfo.class)).orElse(new ArrayList<>());
                    List<MithrasRelatedEnterpriseInfo> relatedEnterpriseInfoArray = Optional.ofNullable(JSONArray.parseArray(tycDataObj.getString("relatedEnterpriseInfoArray"), MithrasRelatedEnterpriseInfo.class)).orElse(new ArrayList<>());

                    // 创建人 根据名字拿，拿不到说明已离职 就先用管理员
                    Long createByUserId = userIdMap.getOrDefault(handClientDataObj.getString("create_user_name"), 3L);
                    // 创建部门 管理员创建的需找到汉得实际创建部门 非管理员创建的直接拿系统中该用户的部门
                    Long createByDeptId = Objects.equals(3L, createByUserId) ? orgIdMap.get(handClientDataObj.getString("unit_id_n")) : userOrgMap.get(createByUserId);

                    // 主表信息
                    Client client = new Client();
                    client.setHandImportFlag(2);
                    client.setClientName(handClientDataObj.getString("bp_name"));
                    client.setClientType("ORG".equals(handClientDataObj.getString("bp_class")) ? ClientType.CORPORATION.name() : ClientType.NORMAL.name());
                    client.setUscCode(handClientDataObj.getString("social_credit_code"));
                    client.setClientStatus(ClientStatus.NEW.name());
                    client.setProcessStatus(ClientProcessStatus.EFFECT_BLANK.name());
                    client.setCertType("NP".equals(handClientDataObj.getString("bp_class")) ? "10" : null);
                    client.setCertNumber("NP".equals(handClientDataObj.getString("bp_class")) ? handClientDataObj.getString("id_card_no") : null);
                    client.setTycName(tycBaseInfo.getTycName());
                    client.setCreateByDept(createByDeptId);
                    client.setCreateBy(createByUserId);
                    client.setUpdateBy(createByUserId);
                    try {
                        clientMapper.insert(client);
                    } catch (DuplicateKeyException de) {
                        log.error("重复key不处理:{}", client.getUscCode());
                        continue;
                    }

                    if (ClientType.CORPORATION.name().equals(client.getClientType())) {
                        // 法人

                        // 工商信息
                        CorpCommerceInfo corpCommerceInfo = new CorpCommerceInfo();
                        corpCommerceInfo.setZhongZhengCode("中征码(原贷款卡编码)".equals(handClientDataObj.getString("identity_type_n")) ? handClientDataObj.getString("identity_code") : null);
                        corpCommerceInfo.setTripleCertInOne(tycBaseInfo.getTripleCertInOne());
                        corpCommerceInfo.setOrgCode(StringUtils.isNotBlank(tycBaseInfo.getOrgCode()) ? tycBaseInfo.getOrgCode() : null);
                        corpCommerceInfo.setBizLicenseCode(tycBaseInfo.getBizLicenseCode());
                        corpCommerceInfo.setContinuousStatus(tycBaseInfo.getContinuousStatus());
                        corpCommerceInfo.setEstablishDate(tycBaseInfo.getEstablishDate());
                        corpCommerceInfo.setApprovalDate(tycBaseInfo.getApprovalDate());
                        corpCommerceInfo.setBizLicenceLongTerm(tycBaseInfo.getBizLicenceLongTerm());
                        corpCommerceInfo.setBizLicenseEndDate(tycBaseInfo.getBizLicenseEndDate());
                        corpCommerceInfo.setBizScope(tycBaseInfo.getBizScope());
                        corpCommerceInfo.setIndustryType(tycBaseInfo.getIndustryType());
                        corpCommerceInfo.setOrgType(Optional.ofNullable(handClientDataObj.getString("organization_type_n"))
                                .map(n -> generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery().eq(GeneralDictionary::getDictKey, "orgType").eq(GeneralDictionary::getDisplay, handClientDataObj.getString("organization_type_n")).last("LIMIT 1")))
                                .map(GeneralDictionary::getCode).orElse(null));
                        corpCommerceInfo.setEconomyType(Optional.ofNullable(handClientDataObj.getString("economic_type_n"))
                                .map(n -> generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery().eq(GeneralDictionary::getDictKey, "economyType").eq(GeneralDictionary::getDisplay, handClientDataObj.getString("economic_type_n")).last("LIMIT 1")))
                                .map(GeneralDictionary::getCode).orElse(null));
                        corpCommerceInfo.setOrgType(tycBaseInfo.getOrgType());
                        corpCommerceInfo.setOrgScale(tycBaseInfo.getOrgScale());
                        corpCommerceInfo.setRegisterCurrencyType(tycBaseInfo.getRegisterCurrencyType());
                        corpCommerceInfo.setRegisterCapital(tycBaseInfo.getRegisterCapital());
                        corpCommerceInfo.setRealCurrencyType(tycBaseInfo.getRealCurrencyType());
                        corpCommerceInfo.setRealCapital(tycBaseInfo.getRealCapital());
                        corpCommerceInfo.setRegisterCapitalRate(tycBaseInfo.getRegisterCapitalRate());
                        corpCommerceInfo.setCorpRepresent(handClientDataObj.getString("legal_person"));
                        corpCommerceInfo.setCorpGender(Optional.ofNullable(handClientDataObj.getString("gender_n")).map(n -> "男".equals(n) ? GenderType.MALE.name() : GenderType.FEMALE.name()).orElse(null));
                        corpCommerceInfo.setCorpCertType("10");
                        corpCommerceInfo.setCorpCertCode(handClientDataObj.getString("id_card_no"));
                        corpCommerceInfo.setListedCompany(Optional.ofNullable(handClientDataObj.getString("listed_company_flag_n")).map(n -> "是".equals(n)).orElse(null));
                        corpCommerceInfo.setClientId(client.getId());
                        corpCommerceInfo.setCreateBy(createByUserId);
                        corpCommerceInfo.setUpdateBy(createByUserId);

                        List<CorpAddressInfo> corpAddressInfoList = new ArrayList<>();
                        // 地址信息 - 办公地址
                        JSONArray corpAddressHandArray = handClientDataObj.getJSONArray("corpAddressArray");
                        corpAddressHandArray.forEach(d -> {
                            JSONObject handCorpAddressObj = (JSONObject) d;
                            if (!"OFFICE_ADDRESS".equals(handCorpAddressObj.getString("address_type"))) {
                                return;
                            }
                            CorpAddressInfo workAddress = convertHandAddress(handCorpAddressObj);
                            workAddress.setClientId(client.getId());
                            workAddress.setCreateBy(createByUserId);
                            workAddress.setUpdateBy(createByUserId);
                            corpAddressInfoList.add(workAddress);
                        });

                        // 地址信息 - 注册地址
                        CorpAddressInfo registerAddress = convertTycAddress(tycBaseInfo);
                        registerAddress.setClientId(client.getId());
                        registerAddress.setCreateBy(createByUserId);
                        registerAddress.setUpdateBy(createByUserId);
                        corpAddressInfoList.add(registerAddress);

                        // 银行账户
                        List<CorpBankAccount> corpBankAccountList = new ArrayList<>();
                        JSONArray corpBankHandArray = handClientDataObj.getJSONArray("corpBankAccountArray");
                        corpBankHandArray.forEach(d -> {
                            JSONObject handCorpBankObj = (JSONObject) d;
                            CorpBankAccount corpBankAccount = new CorpBankAccount();
                            corpBankAccount.setAccountName(handCorpBankObj.getString("bank_account_name"));
                            corpBankAccount.setAccountNumber(handCorpBankObj.getString("bank_account_num"));
                            corpBankAccount.setAccountBank(Optional.ofNullable(handCorpBankObj.getString("bank_full_name")).orElse("") + Optional.ofNullable(handCorpBankObj.getString("bank_branch_name")).orElse(""));
                            corpBankAccount.setMainAccount("Y".equals(handCorpBankObj.getString("primary_flag")));
                            corpBankAccount.setClientId(client.getId());
                            corpBankAccount.setCreateBy(createByUserId);
                            corpBankAccount.setUpdateBy(createByUserId);
                            corpBankAccountList.add(corpBankAccount);
                        });

                        // 股东信息
                        List<CorpShareholderInfo> shareholderInfoList = shareholderInfoArray.stream().map(d -> {
                            CorpShareholderInfo corpShareholderInfo = new CorpShareholderInfo();
                            corpShareholderInfo.setShareholderType(d.getShareholderType());
                            corpShareholderInfo.setShareholderName(d.getShareholderName());
                            corpShareholderInfo.setPaidTotal(d.getPaidTotal());
                            corpShareholderInfo.setActualPaidTotal(d.getActualPaidTotal());
                            corpShareholderInfo.setCapitalWay(d.getCapitalWay());
                            corpShareholderInfo.setCapitalPercent(d.getCapitalPercent());
                            corpShareholderInfo.setRealController(d.getRealController());
                            corpShareholderInfo.setClientId(client.getId());
                            corpShareholderInfo.setCreateBy(createByUserId);
                            corpShareholderInfo.setUpdateBy(createByUserId);
                            return corpShareholderInfo;
                        }).collect(Collectors.toList());

                        // 关联企业
                        List<CorpRelatedEnterprise> relatedEnterpriseList = relatedEnterpriseInfoArray.stream().map(d -> {
                            CorpRelatedEnterprise corpRelatedEnterprise = new CorpRelatedEnterprise();
                            corpRelatedEnterprise.setEnterpriseName(d.getEnterpriseName());
                            corpRelatedEnterprise.setRelationship(d.getRelationship());
                            corpRelatedEnterprise.setRegisterCapital(d.getRegisterCapital());
                            corpRelatedEnterprise.setShareholdingRatio(d.getShareholdingRatio());
                            corpRelatedEnterprise.setInvestAmount(d.getInvestAmount());
                            corpRelatedEnterprise.setContinuousStatus(d.getContinuousStatus());
                            corpRelatedEnterprise.setEstablishDate(d.getEstablishDate());
                            corpRelatedEnterprise.setIndustryType(d.getIndustryType());
                            corpRelatedEnterprise.setClientId(client.getId());
                            corpRelatedEnterprise.setCreateBy(createByUserId);
                            corpRelatedEnterprise.setUpdateBy(createByUserId);
                            return corpRelatedEnterprise;
                        }).collect(Collectors.toList());

                        commerceInfoMapper.insert(corpCommerceInfo);
                        corpAddressInfoList.forEach(addressInfoMapper::insert);
                        corpBankAccountList.forEach(bankAccountMapper::insert);
                        shareholderInfoList.forEach(shareholderInfoMapper::insert);
                        relatedEnterpriseList.forEach(relatedEnterpriseMapper::insert);

                    } else {
                        // 自然人
                        // 基本信息
                        NormalBaseInfo normalBaseInfo = new NormalBaseInfo();
                        normalBaseInfo.setCertType(client.getCertType());
                        normalBaseInfo.setCertNumber(client.getCertNumber());
                        normalBaseInfo.setGender(Optional.ofNullable(handClientDataObj.getString("gender_n")).map(n -> "男".equals(n) ? GenderType.MALE.name() : GenderType.FEMALE.name()).orElse(null));
                        normalBaseInfo.setMarriageType(ClientImporterHelper.extractMarriageType(handClientDataObj.getString("marital_status")));
                        // 先都填中国 然后看看有没有非中国的
                        normalBaseInfo.setCountry("156");
                        normalBaseInfo.setAge(Objects.nonNull(client.getCertNumber()) && client.getCertNumber().length() == 18 ? IdcardUtil.getAgeByIdCard(client.getCertNumber(), new Date()) : null);
                        normalBaseInfo.setMobileNumber(ClientImporterHelper.extractNormalPhone(handClientDataObj));
                        normalBaseInfo.setHomeAddress(ClientImporterHelper.extractNormalAddress(handClientDataObj));
                        normalBaseInfo.setMail(ClientImporterHelper.extractNormalMail(handClientDataObj));
                        normalBaseInfo.setClientId(client.getId());
                        normalBaseInfo.setCreateBy(createByUserId);
                        normalBaseInfo.setUpdateBy(createByUserId);

                        // 配偶信息
                        NormalSpouse normalSpouse = null;
                        if (StringUtils.isNotBlank(handClientDataObj.getString("bp_name_sp"))) {
                            normalSpouse = new NormalSpouse();
                            normalSpouse.setSpouseName(handClientDataObj.getString("bp_name_sp"));
                            normalSpouse.setCertType("10");
                            normalSpouse.setCertNumber(handClientDataObj.getString("id_card_no_sp"));
                            normalSpouse.setClientId(client.getId());
                            normalSpouse.setCreateBy(createByUserId);
                            normalSpouse.setUpdateBy(createByUserId);
                        }

                        // 银行账户
                        List<NormalBankAccount> normalBankAccountList = new ArrayList<>();
                        JSONArray normalBankHandArray = handClientDataObj.getJSONArray("normalBankAccountArray");
                        normalBankHandArray.forEach(d -> {
                            JSONObject handNormalBankObj = (JSONObject) d;
                            NormalBankAccount normalBankAccount = new NormalBankAccount();
                            normalBankAccount.setAccountName(handNormalBankObj.getString("bank_account_name"));
                            normalBankAccount.setAccountNumber(handNormalBankObj.getString("bank_account_num"));
                            normalBankAccount.setAccountBank(Optional.ofNullable(handNormalBankObj.getString("bank_full_name")).orElse("") + Optional.ofNullable(handNormalBankObj.getString("bank_branch_name")).orElse(""));
                            normalBankAccount.setMainAccount("Y".equals(handNormalBankObj.getString("primary_flag")));
                            normalBankAccount.setClientId(client.getId());
                            normalBankAccount.setCreateBy(createByUserId);
                            normalBankAccount.setUpdateBy(createByUserId);
                            normalBankAccountList.add(normalBankAccount);
                        });

                        normalBaseInfoMapper.insert(normalBaseInfo);
                        if (Objects.nonNull(normalSpouse)) {
                            normalSpouseMapper.insert(normalSpouse);
                        }
                        normalBankAccountList.forEach(normalBankAccountMapper::insert);
                    }
                }

                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });

    }

    /**
     * 导入公开信息页
     */
    @SneakyThrows
    public void initTycData() {
        // 这个可以暂时不处理 要做时直接天眼查/external/sync下，怕花太多次数
    }

    /**
     * 导入财报
     */
    @SneakyThrows
    public void initSubjectData() {
        Map<String, Long> clientIdMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery().eq(Client::getHandImportFlag, 1))
                .stream().collect(Collectors.toMap(Client::getClientName, Client::getId));
        String handClientDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/数据导入/汉得客户模块导出_20220923.json").getInputStream(), Charset.defaultCharset());
        JSONArray handClientDataArray = JSONArray.parseArray(handClientDataString);

        transactionTemplate.execute(status -> {
            try {
                for (int i = 0; i < handClientDataArray.size(); i++) {
                    JSONObject handClientDataObj = handClientDataArray.getJSONObject(i);
                    handClientDataObj.put("bp_name", handClientDataObj.getString("bp_name").trim().replaceAll("\\t", ""));

                    JSONArray handSubjectArray = handClientDataObj.getJSONArray("subjectArray");
                    if (Objects.isNull(handSubjectArray) || handSubjectArray.size() == 0) {
                        continue;
                    }
                    log.info("有报表数据的公司:{}", handClientDataObj.getString("bp_name"));
                    List<CorpSubjectItemService.SubjectSheetData> capitalBalanceSheetData = new ArrayList<>(), profitBalanceSheetData = new ArrayList<>(), cashFlowSheetData = new ArrayList<>();
                    for (int j = 0; j < handSubjectArray.size(); j++) {
                        JSONObject handSubjectObj = handSubjectArray.getJSONObject(j);
                        log.info("financial_type:{}", handSubjectObj.getString("financial_type"));
                        CorpSubjectItemService.SubjectSheetData cbData = new CorpSubjectItemService.SubjectSheetData();
                        cbData.setSheetName(SubjectItemType.CAPITAL_BALANCE.sheetName);
                        cbData.setReportType("本部报表".equals(handSubjectObj.getString("report_type")) ? SubjectReportType.LOCAL.name() : SubjectReportType.MERGED.name());
                        cbData.setQuarter(handSubjectObj.getInteger("fiscal_month"));
                        cbData.setYear(handSubjectObj.getInteger("fiscal_year"));
                        //cbData.setItemMap(handSubjectObj.getJSONArray("balanceSheetArray").stream().map(d -> (JSONObject)d).collect(Collectors.toMap(d -> d.getString(""), d -> new CorpSubjectItemService.Item(d.getString(""), new BigDecimal(d.getString("")).setScale(2, RoundingMode.HALF_UP)))));

                        CorpSubjectItemService.SubjectSheetData pbData = new CorpSubjectItemService.SubjectSheetData();
                        pbData.setReportType("本部报表".equals(handSubjectObj.getString("report_type")) ? SubjectReportType.LOCAL.name() : SubjectReportType.MERGED.name());
                        pbData.setQuarter(handSubjectObj.getInteger("fiscal_month"));
                        pbData.setYear(handSubjectObj.getInteger("fiscal_year"));

                        CorpSubjectItemService.SubjectSheetData cfData = new CorpSubjectItemService.SubjectSheetData();
                        cfData.setReportType("本部报表".equals(handSubjectObj.getString("report_type")) ? SubjectReportType.LOCAL.name() : SubjectReportType.MERGED.name());
                        cfData.setQuarter(handSubjectObj.getInteger("fiscal_month"));
                        cfData.setYear(handSubjectObj.getInteger("fiscal_year"));


                        capitalBalanceSheetData.add(cbData);
                        profitBalanceSheetData.add(pbData);
                        cashFlowSheetData.add(cfData);
                    }
                    // subjectItemService.handleOrgSubjectImport(clientIdMap.get(handClientDataObj.getString("bp_name")), capitalBalanceSheetData, profitBalanceSheetData, cashFlowSheetData);
                }
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });

    }

    /**
     * 生成版本
     */
    public void genVersion() {
        List<Long> clientIdList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().eq(Client::getHandImportFlag, 2))
                .stream().map(Client::getId).collect(Collectors.toList());
        transactionTemplate.execute(status -> {
            try {
                clientIdList.forEach(clientId -> {
                    clientVersionService.recordVersion(clientId, VersionTypeEnum.EFFECT, 3L, null, VersionTypeConstants.NORMAL);
                    clientService.recordClientStatus(clientId, ClientStatus.TAKE_EFFECT, ClientProcessStatus.EFFECT_BLANK);
                });
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });
    }

    /**
     * 清空导入的数据
     */
    public void clearData() {
        List<Long> clientIdList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getHandImportFlag, Arrays.asList(1, 2)))
                .stream().map(Client::getId).collect(Collectors.toList());
        transactionTemplate.execute(status -> {
            try {
                clientMapper.delete(Wrappers.<Client>lambdaQuery().in(Client::getId, clientIdList));
                subjectItemMapper.delete(Wrappers.<CorpSubjectItem>lambdaQuery().in(CorpSubjectItem::getClientId, clientIdList));
                bondInfoMapper.delete(Wrappers.<CorpBondInfo>lambdaQuery().in(CorpBondInfo::getClientId, clientIdList));
                bondInfoLibMapper.delete(Wrappers.<CorpBondInfoLib>lambdaQuery().in(CorpBondInfoLib::getClientId, clientIdList));
                addressInfoMapper.delete(Wrappers.<CorpAddressInfo>lambdaQuery().in(CorpAddressInfo::getClientId, clientIdList));
                addressInfoLibMapper.delete(Wrappers.<CorpAddressInfoLib>lambdaQuery().in(CorpAddressInfoLib::getClientId, clientIdList));
                relatedEnterpriseMapper.delete(Wrappers.<CorpRelatedEnterprise>lambdaQuery().in(CorpRelatedEnterprise::getClientId, clientIdList));
                relatedEnterpriseLibMapper.delete(Wrappers.<CorpRelatedEnterpriseLib>lambdaQuery().in(CorpRelatedEnterpriseLib::getClientId, clientIdList));
                contactInfoMapper.delete(Wrappers.<CorpContactInfo>lambdaQuery().in(CorpContactInfo::getClientId, clientIdList));
                contactInfoLibMapper.delete(Wrappers.<CorpContactInfoLib>lambdaQuery().in(CorpContactInfoLib::getClientId, clientIdList));
                shareholderInfoMapper.delete(Wrappers.<CorpShareholderInfo>lambdaQuery().in(CorpShareholderInfo::getClientId, clientIdList));
                shareholderInfoLibMapper.delete(Wrappers.<CorpShareholderInfoLib>lambdaQuery().in(CorpShareholderInfoLib::getClientId, clientIdList));
                bankAccountMapper.delete(Wrappers.<CorpBankAccount>lambdaQuery().in(CorpBankAccount::getClientId, clientIdList));
                bankAccountLibMapper.delete(Wrappers.<CorpBankAccountLib>lambdaQuery().in(CorpBankAccountLib::getClientId, clientIdList));
                commerceInfoMapper.delete(Wrappers.<CorpCommerceInfo>lambdaQuery().in(CorpCommerceInfo::getClientId, clientIdList));
                commerceInfoLibMapper.delete(Wrappers.<CorpCommerceInfoLib>lambdaQuery().in(CorpCommerceInfoLib::getClientId, clientIdList));
                normalBaseInfoMapper.delete(Wrappers.<NormalBaseInfo>lambdaQuery().in(NormalBaseInfo::getClientId, clientIdList));
                normalBaseInfoLibMapper.delete(Wrappers.<NormalBaseInfoLib>lambdaQuery().in(NormalBaseInfoLib::getClientId, clientIdList));
                normalBankAccountMapper.delete(Wrappers.<NormalBankAccount>lambdaQuery().in(NormalBankAccount::getClientId, clientIdList));
                normalBankAccountLibMapper.delete(Wrappers.<NormalBankAccountLib>lambdaQuery().in(NormalBankAccountLib::getClientId, clientIdList));
                normalSpouseMapper.delete(Wrappers.<NormalSpouse>lambdaQuery().in(NormalSpouse::getClientId, clientIdList));
                normalSpouseLibMapper.delete(Wrappers.<NormalSpouseLib>lambdaQuery().in(NormalSpouseLib::getClientId, clientIdList));
                commonVersionMapper.delete(Wrappers.<CommonVersion>lambdaQuery().eq(CommonVersion::getModule, BusinessModuleEnum.CLIENT.name()).in(CommonVersion::getMainId, clientIdList));
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });
        log.info("删除客户id:{}", JSON.toJSONString(clientIdList));
    }

    private CorpAddressInfo convertTycAddress(MithrasBaseInfo mithrasBaseInfo) {
        CorpAddressInfo info = new CorpAddressInfo();
        info.setCountry("156");//默认中国
        info.setAddressType(REGISTRY_ADDRESS.name());
        GeneralDictionary tycProvince = generalDictionaryMapper.selectOne(Wrappers.<GeneralDictionary>lambdaQuery()
                .eq(GeneralDictionary::getDictKey, ENUM_TYC_PROVINCE)
                .eq(GeneralDictionary::getCode, mithrasBaseInfo.getBase())
        );
        if (null != tycProvince) {
            AddressDictionary province = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getDisplay, tycProvince.getDisplay()));
            if (null != province) {
                info.setProvince(province.getCode());
                AddressDictionary city = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getParentId, province.getId())
                        .eq(AddressDictionary::getDisplay, mithrasBaseInfo.getCity()));
                if (null != city) {
                    info.setCity(city.getCode());
                    AddressDictionary district = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getParentId, city.getId())
                            .eq(AddressDictionary::getDisplay, mithrasBaseInfo.getDistrict()));
                    if (null != district) {
                        info.setDistrict(district.getCode());
                        info.setRegionCode(district.getCode());
                    }
                }
            }
        }
        info.setDetail(mithrasBaseInfo.getRegLocation());
        return info;
    }

    private CorpAddressInfo convertHandAddress(JSONObject handAddressObj) {
        CorpAddressInfo corpAddressInfo = new CorpAddressInfo();
        corpAddressInfo.setAddressType(CorpAddressType.WORK_ADDRESS.name());
        corpAddressInfo.setCountry("156");
        AddressDictionary province = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getDisplay, handAddressObj.getString("province_id_n")));
        if (null != province) {
            corpAddressInfo.setProvince(province.getCode());
            AddressDictionary city = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getParentId, province.getId())
                    .eq(AddressDictionary::getDisplay, handAddressObj.getString("city_id_n")));
            if (null != city) {
                corpAddressInfo.setCity(city.getCode());
                AddressDictionary district = addressDictionaryMapper.selectOne(Wrappers.<AddressDictionary>lambdaQuery().eq(AddressDictionary::getParentId, city.getId())
                        .eq(AddressDictionary::getDisplay, handAddressObj.getString("district_id_n")));
                if (null != district) {
                    corpAddressInfo.setDistrict(district.getCode());
                }
            }
        }
        corpAddressInfo.setDetail(handAddressObj.getString("address"));
        corpAddressInfo.setRegionCode(handAddressObj.getString("regionalism_code"));
        return corpAddressInfo;
    }

}
