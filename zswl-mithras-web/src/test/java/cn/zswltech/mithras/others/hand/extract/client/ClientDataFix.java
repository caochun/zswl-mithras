package cn.zswltech.mithras.others.hand.extract.client;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.customer.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfoLib;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 客户数据修复
 *
 * @author wangchuanhao
 * @date 2022/12/13 4:21 PM
 */
public class ClientDataFix extends ApplicationTest {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;

    @Test
    public void fixCertTypeAndRegionId() {
        Map<String, Long> clientIdMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getClientName, ListUtil.toList("湖州申太建设发展有限公司","广东万嘉通通信科技有限公司","漳州虎鲸冷链物流有限公司","浙江长兴综合物流园区发展有限公司","福州聚福广通供应链有限公司","福建龙麟集团有限公司","江苏振江新能源装备股份有限公司","湖北绿色家园材料技术股份有限公司","湖州南浔城南新农村建设投资有限公司","宁波万众汽车零部件有限公司","扶绥县中苏建设工程有限公司","浙江华眼视觉科技有限公司","兰溪市交通建设投资集团有限公司","菏泽城际公交有限公司","安吉七彩灵峰农业发展有限公司","平阳县新鳌城市建设有限公司","余姚经济开发区建设投资发展有限公司","清徐县美特好农产品配送物流有限公司","宁波市奉化区公共交通有限公司","舟山市普陀港福开发建设有限公司","浙江中通通信有限公司","浙江湖州众创投资建设有限公司","湖州吴兴经开建设投资发展集团有限公司","厦门创德信环保设备有限公司","栋梁铝业有限公司","山西万美医药科技有限公司","长兴宏达水利建设发展有限公司","开玙供应链管理（无锡）有限公司","四川环龙新材料有限公司","余姚市联海实业有限公司","上海裕亿机械设备有限公司","扶绥县昊腾工程建设有限公司","诸暨华海氨纶有限公司","杭州安沃普机械有限公司","宁波象山交通开发建设集团有限公司","杭加（广东）建筑节能新材料有限公司","宁波市奉化区红胜开发建设有限公司","余姚市城西工业开发建设有限公司","浙江逸盛新材料有限公司","舟山市定海城区建设开发有限公司","厦门海福租赁有限公司","天下行租车有限公司","浙江豪邦化工有限公司","德清县城市建设发展总公司","嘉善嘉港燃气有限公司","通冠机械租赁股份有限公司","中建锦程机械设备（上海）有限公司","郑州沃特节能科技股份有限公司","诸暨市城东新城建设有限公司","武汉吉象合力工业车辆有限公司","舟山市定海宝利投资有限公司","诸暨市农村发展投资有限公司","乐山杭加节能新材料有限公司","兰溪市聚业建设开发有限公司","嵊州市交通投资发展集团有限公司","杭州宏迈机械设备有限公司","泰兴市滨江污水处理有限公司","浙江湖州环太湖集团有限公司","浙江鹊山建设有限公司","阳光王子（寿光）特种纸有限公司","南通中盾能源有限公司","华劲集团赣州纸品有限公司","上海鼎衡航运科技有限公司","宁波市奉化区城市投资发展集团有限公司","山东银鹰化纤有限公司","桐庐县国有资产投资经营有限公司","舟山普陀城市投资发展集团有限公司","诸暨市新城投资开发集团有限公司","盐城海瀛控股集团有限公司","浙江临杭物流发展有限公司","德清县下渚湖湿地旅游发展有限公司","江苏省镔鑫钢铁集团有限公司","江苏徐钢钢铁集团有限公司","山西高义钢铁有限公司","江苏德龙镍业有限公司","广西梧州市金海不锈钢有限公司","新疆博海水泥有限公司","宁夏晟晏实业集团能源循环经济有限公司","广西朗知森商贸有限公司","广东禄宇航运有限公司","宜宾海丝特纤维有限责任公司","嵊州市经济开发区东方投资有限公司","绍兴袍江工业区投资开发有限公司","山东仁丰特种材料股份有限公司","湖州新型城市投资发展集团有限公司","山西通才工贸有限公司","湖州市南浔新城投资发展有限公司","山西建邦集团有限公司","昌乐新迈纸业有限公司","山东博汇纸业股份有限公司","江苏博汇纸业有限公司","滨州中裕食品有限公司","河南骏化发展股份有限公司","浙江欣捷建设有限公司","京商第一建设有限公司","东莞市金田纸业有限公司","当涂县清源水务投资有限公司")))
                .stream().collect(Collectors.toMap(Client::getClientName, Client::getId));

        LambdaUpdateWrapper<CorpCommerceInfo> cciUpdateWrapper = new LambdaUpdateWrapper<>();
        cciUpdateWrapper.in(CorpCommerceInfo::getClientId, clientIdMap.values());
        cciUpdateWrapper.set(CorpCommerceInfo::getOrgType, "1");
        commerceInfoMapper.update(null, cciUpdateWrapper);
        LambdaUpdateWrapper<CorpCommerceInfoLib> cciLibUpdateWrapper = new LambdaUpdateWrapper<>();
        cciLibUpdateWrapper.in(CorpCommerceInfoLib::getClientId, clientIdMap.values());
        cciLibUpdateWrapper.set(CorpCommerceInfoLib::getOrgType, "1");
        corpCommerceInfoLibMapper.update(null, cciLibUpdateWrapper);

        Map<String, String> regionMap = new HashMap<>();
        regionMap.put("湖州申太建设发展有限公司","330502");
        regionMap.put("广东万嘉通通信科技有限公司","440101");
        regionMap.put("漳州虎鲸冷链物流有限公司","350600");
        regionMap.put("浙江长兴综合物流园区发展有限公司","330522");
        regionMap.put("福州聚福广通供应链有限公司","350121");
        regionMap.put("福建龙麟集团有限公司","350800");
        regionMap.put("江苏振江新能源装备股份有限公司","320281");
        regionMap.put("湖北绿色家园材料技术股份有限公司","429004");
        regionMap.put("湖州南浔城南新农村建设投资有限公司","330503");
        regionMap.put("宁波万众汽车零部件有限公司","330203");
        regionMap.put("扶绥县中苏建设工程有限公司","451421");
        regionMap.put("浙江华眼视觉科技有限公司","330106");
        regionMap.put("兰溪市交通建设投资集团有限公司","330781");
        regionMap.put("菏泽城际公交有限公司","371701");
        regionMap.put("安吉七彩灵峰农业发展有限公司","330523");
        regionMap.put("平阳县新鳌城市建设有限公司","330326");
        regionMap.put("余姚经济开发区建设投资发展有限公司","330281");
        regionMap.put("清徐县美特好农产品配送物流有限公司","140100");
        regionMap.put("宁波市奉化区公共交通有限公司","330283");
        regionMap.put("舟山市普陀港福开发建设有限公司","330903");
        regionMap.put("浙江中通通信有限公司","330100");
        regionMap.put("浙江湖州众创投资建设有限公司","330503");
        regionMap.put("湖州吴兴经开建设投资发展集团有限公司","330502");
        regionMap.put("厦门创德信环保设备有限公司","350200");
        regionMap.put("栋梁铝业有限公司","330502");
        regionMap.put("山西万美医药科技有限公司","140105");
        regionMap.put("长兴宏达水利建设发展有限公司","330522");
        regionMap.put("开玙供应链管理（无锡）有限公司","320205");
        regionMap.put("四川环龙新材料有限公司","511425");
        regionMap.put("余姚市联海实业有限公司","330281");
        regionMap.put("上海裕亿机械设备有限公司","310120");
        regionMap.put("扶绥县昊腾工程建设有限公司","451421");
        regionMap.put("诸暨华海氨纶有限公司","330681");
        regionMap.put("杭州安沃普机械有限公司","330100");
        regionMap.put("宁波象山交通开发建设集团有限公司","330225");
        regionMap.put("杭加（广东）建筑节能新材料有限公司","441881");
        regionMap.put("宁波市奉化区红胜开发建设有限公司","330283");
        regionMap.put("余姚市城西工业开发建设有限公司","330281");
        regionMap.put("浙江逸盛新材料有限公司","330211");
        regionMap.put("舟山市定海城区建设开发有限公司","330902");
        regionMap.put("厦门海福租赁有限公司","350100");
        regionMap.put("天下行租车有限公司","350200");
        regionMap.put("浙江豪邦化工有限公司","330802");
        regionMap.put("德清县城市建设发展总公司","330521");
        regionMap.put("嘉善嘉港燃气有限公司","330400");
        regionMap.put("通冠机械租赁股份有限公司","410100");
        regionMap.put("中建锦程机械设备（上海）有限公司","310100");
        regionMap.put("郑州沃特节能科技股份有限公司","410102");
        regionMap.put("诸暨市城东新城建设有限公司","330681");
        regionMap.put("武汉吉象合力工业车辆有限公司","420100");
        regionMap.put("舟山市定海宝利投资有限公司","330902");
        regionMap.put("诸暨市农村发展投资有限公司","330681");
        regionMap.put("乐山杭加节能新材料有限公司","511100");
        regionMap.put("兰溪市聚业建设开发有限公司","330781");
        regionMap.put("嵊州市交通投资发展集团有限公司","330683");
        regionMap.put("杭州宏迈机械设备有限公司","330110");
        regionMap.put("泰兴市滨江污水处理有限公司","321283");
        regionMap.put("浙江湖州环太湖集团有限公司","330502");
        regionMap.put("浙江鹊山建设有限公司","330109");
        regionMap.put("阳光王子（寿光）特种纸有限公司","370783");
        regionMap.put("南通中盾能源有限公司","320601");
        regionMap.put("华劲集团赣州纸品有限公司","360702");
        regionMap.put("上海鼎衡航运科技有限公司","310115");
        regionMap.put("宁波市奉化区城市投资发展集团有限公司","330213");
        regionMap.put("山东银鹰化纤有限公司","370785");
        regionMap.put("桐庐县国有资产投资经营有限公司","330100");
        regionMap.put("舟山普陀城市投资发展集团有限公司","330903");
        regionMap.put("诸暨市新城投资开发集团有限公司","330681");
        regionMap.put("盐城海瀛控股集团有限公司","320902");
        regionMap.put("浙江临杭物流发展有限公司","330521");
        regionMap.put("德清县下渚湖湿地旅游发展有限公司","330521");
        regionMap.put("江苏省镔鑫钢铁集团有限公司","320707");
        regionMap.put("江苏徐钢钢铁集团有限公司","320312");
        regionMap.put("山西高义钢铁有限公司","140825");
        regionMap.put("江苏德龙镍业有限公司","320921");
        regionMap.put("广西梧州市金海不锈钢有限公司","450405");
        regionMap.put("新疆博海水泥有限公司","652701");
        regionMap.put("宁夏晟晏实业集团能源循环经济有限公司","640221");
        regionMap.put("广西朗知森商贸有限公司","450105");
        regionMap.put("广东禄宇航运有限公司","440605");
        regionMap.put("宜宾海丝特纤维有限责任公司","511500");
        regionMap.put("嵊州市经济开发区东方投资有限公司","330683");
        regionMap.put("绍兴袍江工业区投资开发有限公司","330602");
        regionMap.put("山东仁丰特种材料股份有限公司","370321");
        regionMap.put("湖州新型城市投资发展集团有限公司","330502");
        regionMap.put("山西通才工贸有限公司","141021");
        regionMap.put("湖州市南浔新城投资发展有限公司","330503");
        regionMap.put("山西建邦集团有限公司","141081");
        regionMap.put("昌乐新迈纸业有限公司","370725");
        regionMap.put("山东博汇纸业股份有限公司","370321");
        regionMap.put("江苏博汇纸业有限公司","320904");
        regionMap.put("滨州中裕食品有限公司","371602");
        regionMap.put("河南骏化发展股份有限公司","411702");
        regionMap.put("浙江欣捷建设有限公司","330205");
        regionMap.put("京商第一建设有限公司","451031");
        regionMap.put("东莞市金田纸业有限公司","441900");
        regionMap.put("当涂县清源水务投资有限公司","340521");

        for (Map.Entry<String, Long> clientIdEntry : clientIdMap.entrySet()) {
            LambdaUpdateWrapper<CorpAddressInfo> caiUpdateWrapper = new LambdaUpdateWrapper<>();
            caiUpdateWrapper.eq(CorpAddressInfo::getClientId, clientIdEntry.getValue());
            caiUpdateWrapper.eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name());
            caiUpdateWrapper.set(CorpAddressInfo::getRegionCode, regionMap.get(clientIdEntry.getKey()));
            corpAddressInfoMapper.update(null, caiUpdateWrapper);
            LambdaUpdateWrapper<CorpAddressInfoLib> caiLibUpdateWrapper = new LambdaUpdateWrapper<>();
            caiLibUpdateWrapper.eq(CorpAddressInfoLib::getClientId, clientIdEntry.getValue());
            caiLibUpdateWrapper.eq(CorpAddressInfoLib::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name());
            caiLibUpdateWrapper.set(CorpAddressInfoLib::getRegionCode, regionMap.get(clientIdEntry.getKey()));
            corpAddressInfoLibMapper.update(null, caiLibUpdateWrapper);
        }
    }

    /**
     * 修复关联方、集团公司
     */
    @Test
    public void fixGroupFlag() throws Exception {
        ExcelReader excelReader = ExcelUtil.getReader("/Users/wang/Desktop/客户模块 数据初始化.xlsx");
        List<List<Object>> allData = excelReader.read();
        for (List<Object> ls : allData) {
            if (ls.size() == 0 || !(ls.get(0) instanceof String)) {
                log.error("格式不正确，跳过");
                continue;
            }
            Client client = clientMapper.selectOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, ls.get(0)).last("LIMIT 1"));
            if (Objects.isNull(client)) {
                log.error("客户不存在，跳过，{}", ls.get(0));
                continue;
            }
            LambdaUpdateWrapper<CorpCommerceInfo> cciUpdateWrapper = new LambdaUpdateWrapper<>();
            LambdaUpdateWrapper<CorpCommerceInfoLib> cciLibUpdateWrapper = new LambdaUpdateWrapper<>();
            cciUpdateWrapper.eq(CorpCommerceInfo::getClientId, client.getId());
            cciLibUpdateWrapper.eq(CorpCommerceInfoLib::getClientId, client.getId());
            cciUpdateWrapper.set(CorpCommerceInfo::getIsRelated, "是".equals(ls.get(4)) ? 1 : 0);
            cciLibUpdateWrapper.set(CorpCommerceInfoLib::getIsRelated, "是".equals(ls.get(4)) ? 1 : 0);
            if ("是".equals(ls.get(5))) {
                cciUpdateWrapper.set(CorpCommerceInfo::getGroupFlag, 1);
                cciLibUpdateWrapper.set(CorpCommerceInfoLib::getGroupFlag, 1);
                cciUpdateWrapper.set(CorpCommerceInfo::getBelongGroupClientId, client.getId());
                cciLibUpdateWrapper.set(CorpCommerceInfoLib::getBelongGroupClientId, client.getId());
            } else {
                cciUpdateWrapper.set(CorpCommerceInfo::getGroupFlag, 0);
                cciLibUpdateWrapper.set(CorpCommerceInfoLib::getGroupFlag, 0);
                if ("无".equals(ls.get(6))) {
                    cciUpdateWrapper.set(CorpCommerceInfo::getBelongGroupClientId, -1L);
                    cciLibUpdateWrapper.set(CorpCommerceInfoLib::getBelongGroupClientId, -1L);
                } else {
                    Client groupCliet = clientMapper.selectOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, ls.get(6)).last("LIMIT 1"));
                    if (Objects.isNull(groupCliet)) {
                        log.error("集团公司未建档，不更新集团数据，{}，{}", ls.get(0), ls.get(6));
                    } else {
                        cciUpdateWrapper.set(CorpCommerceInfo::getBelongGroupClientId, groupCliet.getId());
                        cciLibUpdateWrapper.set(CorpCommerceInfoLib::getBelongGroupClientId, groupCliet.getId());
                    }
                }
            }
            log.info("更新数据,{}", ls.get(0));
            commerceInfoMapper.update(null, cciUpdateWrapper);
            corpCommerceInfoLibMapper.update(null, cciLibUpdateWrapper);
        }
    }

}
