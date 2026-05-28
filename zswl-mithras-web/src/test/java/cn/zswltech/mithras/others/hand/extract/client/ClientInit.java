package cn.zswltech.mithras.others.hand.extract.client;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.corp.*;
import cn.zswltech.mithras.service.mapper.lib.client.*;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.service.mapper.normal.NormalBankAccountMapper;
import cn.zswltech.mithras.service.mapper.normal.NormalBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.normal.NormalSpouseMapper;
import cn.zswltech.mithras.service.service.SysUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 旧的 客户导入
 *
 * @author wangchuanhao
 * @date 2022/8/18 12:57 PM
 */
@Deprecated
public class ClientInit extends ApplicationTest {

    private static final Logger log = LoggerFactory.getLogger(ClientInit.class);

    @Resource
    private ClientMapper clientMapper;
    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private SysUserService sysUserService;
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
    private TransactionTemplate transactionTemplate;

    @Test
    public void extractList() {
        ClientExtractor clientExtractor = new ClientExtractor();
        clientExtractor.extractList();
    }

    @Test
    public void initData() throws Exception {
        List<String> needImportClientNameList = new ArrayList<>();
        needImportClientNameList.addAll(JSONUtil.toList("[\"许沂炎\"]"
                , String.class));

//        needImportClientNameList.addAll(Arrays.asList("123", "王仕锦", "宁波市奉化区惠江基础设施建设有限公司", "浙江鹊山建设有限公司", "长沙人健商贸有限公司", "宁波象山交通开发建设集团有限公司", "华劲集团赣州纸品有限公司", "万邦德集团有限公司", "泰兴市成兴国有资产经营投资有限公司", "宋丽华", "杨振兴", "韩茂", "合肥众兴机械设备有限公司", "宋佃学", "山西顺天立大健康产业集团有限公司", "浙江豪邦化工有限公司", "武汉吉象合力工业车辆有限公司", "刘富强", "湖南人健企业集团有限公司", "杨立新", "广东禄宇航运有限公司", "李永强", "响水德力新材料科技有限公司", "余姚市城西工业开发建设有限公司", "盐城市亭湖城市资产投资实业有限公司", "曾小山", "储德群", "开玙供应链管理（无锡）有限公司", "江苏博汇纸业有限公司", "绍兴袍江工业区投资开发有限公司", "李多珠", "湖州市南浔新城投资发展有限公司", "宁俊", "长兴宏达水利建设发展有限公司", "卢衍铭", "曾鸿飞", "桂永杰", "湖南和立东升实业集团有限公司", "兰溪市城市投资集团有限公司", "响水巨合金属制品有限公司", "邹祖良", "响水康阳贸易有限公司", "湖南人健创业投资有限公司", "万冰", "王丹莉", "张惠玲", "沈根莲", "杭州银泰购物中心有限公司", "江苏嘉亭实业投资有限公司", "许阿强", "严昊", "山西通才工贸有限公司", "浙江恒逸集团有限公司", "万城万充（杭州）新能源汽车服务有限公司", "丰磊", "浙江交科供应链管理有限公司", "湖州吴兴产业投资发展集团有限公司", "李娟", "朱四军", "王爱钦", "莫水娣", "湖南鑫达晨创业投资有限公司", "卢燕娴", "舟山市定海区城乡建设集团有限公司", "上海鼎衡投资控股集团有限公司", "湖州经开投资发展集团有限公司", "洛阳骏化生物科技有限公司", "宋佃忠", "科威瑞（广东）矿机装备有限公司", "侯懿珉", "响水天龙不锈钢铸造有限公司", "江苏徐钢钢铁集团有限公司", "宋景涛", "四川金田纸业有限公司", "新疆天泰纤维有限公司", "乐山杭加节能新材料有限公司", "余姚经济开发区建设投资发展有限公司", "湖南淇达工程机械有限公司", "山西碧锦纳川医药有限公司", "湖州新型城市投资发展集团有限公司", "石咏梅", "广西柳钢中金不锈钢有限公司", "赣州华劲纸业有限公司", "徐丽蓉", "潘清兰", "肖清美", "浙江华海合力科技股份有限公司", "欧阳忠", "河南骏化发展股份有限公司", "苏州亿充新能源汽车租赁有限公司", "徐州海西置业有限公司", "西林浩辉建设有限公司", "赖文龙", "水城县鑫新炭素有限责任公司", "宁波交运建设集团有限公司", "刘旭燕", "李秀荣", "林锦堂", "陈君华", "新绛县恒裕运输有限公司", "杨华", "浙江大盈建设有限公司", "兰溪市聚业建设开发有限公司", "福建豪邦化工有限公司", "浙江长兴经开建设开发有限公司", "乌拉特后旗力源新材料有限公司", "李铭森", "欣捷投资控股集团有限公司", "山西立恒钢铁集团股份有限公司", "吴晓年", "昊华骏化集团有限公司", "李亚", "林金良", "林依芳", "蒋伟平", "嵊州市城市建设投资发展集团有限公司", "徐州科建环保科技股份有限公司", "王泽光", "嵊州市交通投资发展集团有限公司", "山西建邦集团有限公司", "山东仁丰特种材料股份有限公司", "卢宝祥", "浙江湖州南浔经济建设开发有限公司", "陈婷婷", "广西梧州市金海不锈钢有限公司", "舟山市定海宝利投资有限公司", "郑州锦和冷链仓储有限公司", "王静", "响水昌隆贸易有限公司", "朱丹", "张秀英", "张体东", "上海富盛浙工建材有限公司", "德清县下渚湖湿地旅游发展有限公司", "宁波万城万充电动汽车服务有限公司", "嵊州市经济开发区东方投资有限公司", "杭州新天地集团有限公司", "李勋", "李勇", "泰兴市虹桥园工业开发有限公司", "浙商中拓集团股份有限公司", "许阿珍", "广西梧州市丰盈不锈钢有限公司", "浙江中拓供应链管理有限公司", "郑州锦和建设工程有限公司", "宁波市奉化区投资集团有限公司", "宁夏金晶科技有限公司", "绍兴袍江新农村建设投资有限公司", "张巍元", "上海申浙数智轨道科技有限公司", "李柏任", "佛山市百年卓越钢业有限公司", "扶绥县昊腾工程建设有限公司 ", "顾柔坚", "虢利平", "清徐县美特好农产品配送物流有限公司", "环龙工业集团有限公司", "福州聚福广通供应链有限公司", "当涂县清源水务投资有限公司", "肖德虹", "东莞市金田纸业有限公司", "佛山市辉煌不锈钢有限公司", "浙商中拓集团电力科技有限公司", "桐庐县国有资产投资经营有限公司", "余惠君", "四川环龙新材料有限公司", "阎秋", "浙江华展新材料有限公司", "当涂县城乡建设投资有限责任公司", "夏艳雪", "吴朝顺", "林东方", "山东博汇纸业股份有限公司", "朱长龙", "山东银鹰股份有限公司", "菏泽交通集团有限公司", "杨从议", "福瑞祥（青岛）物联网技术服务有限公司", "张日欢", "内蒙古华恒能源科技有限公司", "林国文", "德清县建设发展集团有限公司", "温州万城万充新能源汽车服务有限公司", "湖州中植融云投资有限公司", "余慧君", "浙江国金融资租赁股份有限公司", "盐城海瀛控股集团有限公司", "赵守明", "田美链", "巩春红", "王珍", "邹红卫", "广西双首能源科技有限公司", "千里马（湖北）数字化租赁有限公司", "浙江逸盛新材料有限公司", "嘉善嘉港燃气有限公司", "蔡华峻", "泰兴市滨江污水处理有限公司", "大连华德天宇集团有限公司", "厦门顺磊建材有限公司", "淄博弘瑞再生资源有限公司", "广西梧州市中成达新材料有限公司", "厦门创德信环保设备有限公司", "湖南人健干粉砂浆有限公司", "陈翠霞", "诸暨市新城投资开发集团有限公司", "湖南省邵东市新仁铝业有限责任公司", "山东世纪阳光纸业集团有限公司", "四川杭加汉驭建筑节能新材料有限公司", "余姚市牟山湖开发有限公司", "胡瑾瑜", "王丽丽", "上海裕亿机械设备有限公司", "张云昊", "张天福", "大连港毅都冷链有限公司", "连云港赣榆众诚投资有限公司", "朱明冬", "廖爱华", "文罗生", "杭州桑尼能源科技股份有限公司", "千里马机械供应链股份有限公司", "响水欧力金属制品有限公司", "江苏德龙镍业有限公司", "朱凯军", "戴国芳", "淳安县交通发展投资集团有限公司", "长沙万城万充新能源汽车租赁有限公司", "张锐", "浙江富强置业有限公司", "湖南宝能地产集团有限公司", "陈华禄", "淄博智瑞纸业有限公司", "李亚丈夫", "江苏德顺镍业有限公司", "舟山市普陀区国有资产投资经营有限公司", "山西聚鑫智云运输有限公司", "苏英", "王文辉", "王东兴", "长兴城市建设投资集团有限公司", "湖南人健混凝土有限公司", "吴俊", "长沙顺捷机电有限公司", "徐州康宝房地产开发有限公司", "诸暨市城乡投资集团有限公司", "华劲集团股份有限公司", "顾清波", "唐山中海船舶燃料有限公司", "杨延良", "浙江荣盛控股集团有限公司", "曹晓维", "郑州广源伟业实业有限公司", "厦门海福租赁有限公司", "韦秀珍", "太平洋建设集团有限公司", "万城万充（福州）电动汽车运营有限责任公司", "宁波市奉化区城市投资发展集团有限公司", "贺亭亭", "广西朗知森商贸有限公司", "杨立志", "霍尔果斯博海水泥有限公司", "德清联创科技新城建设有限公司", "浙江欣捷建设有限公司", "刘亚芹", "宁波市海金食品有限公司", "河南通冠重工实业有限公司", "胡震", "杭州宏迈机械设备有限公司", "中国阳光纸业控股有限公司", "山东天源热电有限公司", "湖州申太建设发展有限公司", "天津市宏信船舶运输有限公司", "溧阳宝润钢铁有限公司", "桂安平", "绍兴亿充新能源汽车服务有限公司", "龚曦光", "戴娇", "余姚市联海实业有限公司", "徐州德龙金属科技有限公司", "山西高义钢铁有限公司", "杨淑丽", "常州吉百机械设备有限公司", "诸暨市农村发展投资有限公司", "湖南太平洋建设有限公司", "佛山市金海辉煌不锈钢有限公司", "长兴港通建设开发有限公司", "广东万城万充电动车运营股份有限公司", "菏泽城际公交有限公司", "富春控股集团有限公司", "万城万充(广州)新能源汽车租赁有限公司", "苍南县五岳建设工程有限公司", "宁夏晟晏实业集团能源循环经济有限公司", "王磊", "吕萍", "蔡函烨", "江苏省镔鑫钢铁集团有限公司", "江苏振江新能源装备股份有限公司", "诸暨市城东新城建设有限公司", "宋佃凤", "浙江省德清县交通投资集团有限公司", "宜宾丝丽雅股份有限公司", "湖南新仁置业有限公司", "王凤枝", "段志军", "杨娟", "黄伟成", "山东海天生物化工有限公司", "福建龙麟集团有限公司", "李鑫", "毛静", "张哲华", "孔列岚", "宁波万众汽车零部件有限公司", "庄惠", "吕红霞", "浙江湖州环太湖集团有限公司", "李玉明", "巢璐", "湖南天福房地产开发有限公司", "淳安千岛湖旅游集团有限公司", "刘爱群", "万城万充(泉州)电动汽车运营有限责任公司", "董开", "周南方", "万城万充（武汉）新能源汽车服务有限公司", "四川环龙生活用品有限公司", "山东银鹰化纤有限公司", "福建龙麟环境工程有限公司", "饶伟导", "余泽民", "浙江中通通信有限公司", "滨州中裕食品有限公司", "京商第一建设有限公司", "响水德丰金属材料有限公司", "肖波", "黄荷琴", "湖州织里童装产业投资发展有限公司", "宜宾海丝特纤维有限责任公司", "余伟汉", "浙江临杭物流发展有限公司", "邓娥英", "浙江杭加泽通建筑节能新材料有限公司", "上海鼎衡航运科技有限公司", "宁波市奉化区交通投资发展集团有限公司", "德清县城市建设发展总公司", "中建锦程机械设备（上海）有限公司", "杨斌", "浙江华眼视觉科技有限公司", "杭加（广东）建筑节能新材料有限公司", "周骏", "倪国富", "山西美特好连锁超市股份有限公司", "滕州金晶玻璃有限公司", "山东滨城国家粮食储备库", "北京国俊投资有限公司", "侯学情", "浙江屯德科技有限公司", "宋民松", "刘鹏", "郑州沃特节能科技股份有限公司", "杭州安沃普机械有限公司", "天津开发区之海船舶油料供应有限公司", "宁波市奉化区红胜开发建设有限公司", "淳安千岛湖建设集团有限公司", "许沂炎", "杭州康柏斯科技有限公司", "舟山市定海城区建设开发有限公司", "盐城润瀛实业投资有限公司", "宁波海创科技园开发有限公司", "上海浩诺供应链管理有限公司", "邱燕", "李铭鸿", "卜春华", "戴笠", "舟山普陀城市投资发展集团有限公司", "栋梁铝业有限公司", "北京亚冷控股有限公司", "厦门创德机械有限公司", "刘智", "通冠机械租赁股份有限公司", "梁瑞南", "浙江长兴综合物流园区发展有限公司", "袁毅", "诸暨华海氨纶有限公司", "淄博惠润热力有限公司", "江苏正威新材料股份有限公司", "庄树广", "万城万充（珠海）电动汽车运营有限公司", "响水恒生不锈钢铸造有限公司", "新疆博海水泥有限公司", "余姚市舜财投资控股有限公司", "南通中盾能源有限公司", "广西顶锋不锈钢有限公司", "徐利桦", "山西万美医药科技有限公司", "诸暨市华海新材有限公司", "长兴交通投资集团有限公司", "山东金晶科技股份有限公司", "李鑫夫人", "董海元", "菏泽财金投资集团有限公司", "宁霄", "桐庐县国有资本投资运营控股集团有限公司", "南丹县南方有色金属有限责任公司", "金帝联合能源集团股份有限公司", "江苏九鼎集团有限公司", "广西南国铜业有限责任公司", "江西星光煤业有限公司", "佛山市万城万充新能源汽车租赁有限公司", "唐丽君", "江西诺瑞环境资源科技有限公司", "湖南人健宏城投资有限公司", "金帝联合控股集团有限公司", "宁夏晟晏实业集团有限公司", "陈立芳", "卢斯侃", "宜宾丝丽雅集团有限公司", "林依岁", "湖北绿色家园材料技术股份有限公司", "刘爱云", "福建顺磊集团有限公司", "赖红梅", "天下行租车有限公司", "嵊州市投资控股有限公司", "王刚", "阳光王子（寿光）特种纸有限公司", "漳州虎鲸冷链物流有限公司", "宁波市奉化区公共交通有限公司", "连云港振江轨道交通设备有限公司", "杨利娟", "李薇", "孙银玲", "邹红妹"));

        String allClientJson = IoUtil.read(new ClassPathResource("init/客户模块数据整理json_线上_20220818.json").getInputStream(), Charset.defaultCharset());
        Map<String, ClientExtractor.InitDataModel> clientMap = JSONArray.parseArray(allClientJson, ClientExtractor.InitDataModel.class)
                .stream().collect(Collectors.toMap(ClientExtractor.InitDataModel::getClientName, m -> m, (k1, k2) -> k1));
        // 融租易里不存在的客户列表
        List<String> hdNotExistClientNameList = needImportClientNameList.stream().filter(n -> !clientMap.containsKey(n)).collect(Collectors.toList());
        needImportClientNameList.removeAll(hdNotExistClientNameList);

        // 加前缀
        needImportClientNameList = needImportClientNameList.stream().map(s -> "（汉得导入勿用）" + s).collect(Collectors.toList());

        // 已存在的客户列表
        List<String> sysExistClientNameList = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                        .in(Client::getClientName, needImportClientNameList))
                .stream().map(Client::getClientName).collect(Collectors.toList());
        needImportClientNameList.removeAll(sysExistClientNameList);

        // 去重
        needImportClientNameList = needImportClientNameList.stream().distinct().collect(Collectors.toList());

        // 找出当前所有用户 拿到id
        UserQuery userQuery = new UserQuery();
        userQuery.setPageSize(Integer.MAX_VALUE);
        Map<String, Long> userMap = userServiceAPI.queryUserSys(userQuery).getContents().stream().collect(Collectors.toMap(UserVO::getUserName, UserVO::getId));
        Map<Long, Long> userOrgMap = userMap.values().stream().collect(Collectors.toMap(id -> id, id -> sysUserService.getSpecificUserDeptList(id).get(0).getId()));

        // 新增
        List<Client> importClientList = needImportClientNameList.stream().map(n -> {
            ClientExtractor.InitDataModel handData = clientMap.get(n.replace("（汉得导入勿用）", ""));
            Client client = new Client();
            client.setClientName(n);
            client.setClientType("ORG".equals(handData.getBpClass()) ? ClientType.CORPORATION.name() : ClientType.NORMAL.name());
            client.setClientStatus(ClientStatus.NEW.name());
            client.setCertType("NP".equals(handData.getBpClass()) ? "10" : null);
            client.setCertNumber(handData.getIdCardNo());
            client.setUscCode(handData.getSocialCreditCode());
            client.setHandImportFlag(1);

            // 创建人 根据名字拿，拿不到说明已离职 就先用管理员
            Long createByUserId = userMap.getOrDefault(handData.getCreateUserName(), 3L);
            client.setCreateBy(createByUserId);
            client.setUpdateBy(createByUserId);
            client.setCreateByDept(userOrgMap.get(createByUserId));

            return client;
        }).collect(Collectors.toList());

        // 懒得写批量方法了
        for (Client c : importClientList) {
            try {
                // 先加个前缀 之后再统一删掉重新导一次
                clientMapper.insert(c);
            } catch (DuplicateKeyException duplicateKeyException) {
                log.error("唯一键重复:{}", JSON.toJSONString(c));
            }
        }

        System.out.println(String.format("客户数据导入成功数量:%s，这些数据因汉得系统不存在所以不处理:%s，这些数据因已存在我方系统所以不处理",
                needImportClientNameList.size(), JSON.toJSONString(hdNotExistClientNameList), JSON.toJSONString(sysExistClientNameList)));
    }

    @Test
    public void clearData() {
        List<Long> clientIdList = clientMapper.selectList(Wrappers.<Client>lambdaQuery().eq(Client::getHandImportFlag, 1))
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
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });
        log.info("删除客户id:{}", JSON.toJSONString(clientIdList));
    }

}
