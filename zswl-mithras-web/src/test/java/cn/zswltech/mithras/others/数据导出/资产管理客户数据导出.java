package cn.zswltech.mithras.others.数据导出;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/12/19
 * @description
 */
@Slf4j
public class 资产管理客户数据导出 extends ApplicationTest {
    private static final List<String> CLIENT_NAME_LIST = Arrays.asList("山西通才工贸有限公司","江苏省镔鑫钢铁集团有限公司","浙商中拓集团股份有限公司","浙江交科供应链管理有限公司","浙商中拓集团（云南）有限公司","广西梧州市金海不锈钢有限公司","浙江临杭物流发展有限公司","湖州新型城市投资发展集团有限公司","湖州市南浔新城投资发展有限公司","江苏徐钢钢铁集团有限公司","河南骏化发展股份有限公司","昌乐新迈纸业有限公司","新疆博海水泥有限公司","德清县下渚湖湿地旅游发展有限公司","山西高义钢铁有限公司","绍兴袍江经济技术开发区投资发展集团有限公司","嵊州市经济开发区东方投资有限公司","山东仁丰特种材料股份有限公司","兰溪市聚业建设开发有限公司","广东禄宇航运有限公司","浙江湖州环太湖集团有限公司","泰兴市滨江污水处理有限公司","嵊州市交通投资发展集团有限公司","舟山普陀城市投资发展集团有限公司","盐城海瀛控股集团有限公司","诸暨市新城投资开发集团有限公司","当涂县清源水务投资有限公司","浙江鹊山建设有限公司","南通中盾能源有限公司","乐山杭加节能新材料有限公司","杭加（广东）建筑节能新材料有限公司","东莞市金田纸业有限公司","山东银鹰化纤有限公司","杭州宏迈机械设备有限公司","宁波市奉化区城市投资发展集团有限公司","武汉吉象合力工业车辆有限公司","浙江豪邦化工有限公司","诸暨市城东新城建设有限公司","德清县城市建设发展总公司","通冠机械租赁股份有限公司","浙江逸盛新材料有限公司","诸暨市农村发展投资有限公司","中建锦程机械设备（上海）有限公司","杭州安沃普机械有限公司","上海裕亿机械设备有限公司","厦门海福租赁有限公司","余姚市城西工业开发建设有限公司","舟山市定海宝利投资有限公司","阳光王子（寿光）特种纸有限公司","宁波市奉化区红胜开发建设有限公司","余姚市联海实业有限公司","诸暨华海氨纶有限公司","山西万美医药科技有限公司","宁波象山交通开发建设集团有限公司","舟山市定海城区建设开发有限公司","四川环龙新材料有限公司","栋梁铝业有限公司","宁波市奉化区公共交通有限公司","浙江中通通信有限公司","长兴宏达水利建设发展有限公司","上海鼎衡航运科技有限公司","嘉善嘉港燃气有限公司","厦门创德信环保设备有限公司","安吉七彩灵峰农业发展有限公司","江苏德龙镍业有限公司","清徐县美特好农产品配送物流有限公司","湖州吴兴经开建设投资发展集团有限公司","余姚经济开发区建设投资发展有限公司","舟山市普陀港福开发建设有限公司","平阳县新鳌城市建设有限公司","扶绥县中苏建设工程有限公司","浙江湖州众创投资建设有限公司","天下行租车有限公司","宁波万众汽车零部件有限公司","湖北绿色家园材料技术股份有限公司","兰溪市交通建设投资集团有限公司","福建龙麟集团有限公司","江苏振江新能源装备股份有限公司","福州聚福广通供应链有限公司","漳州虎鲸冷链物流有限公司","浙江华眼视觉科技有限公司","浙江长兴综合物流园区发展有限公司","湖州南浔城南新农村建设投资有限公司","湖州申太建设发展有限公司","菏泽城际公交有限公司","广东万嘉通通信科技有限公司","杭州萧山江南养殖有限公司","上海浩诺供应链管理有限公司","湖州南浔凤凰文化旅游发展集团有限公司","攀枝花市公共交通有限责任公司","山西晋南钢铁集团有限公司","泰兴市智光环保科技有限公司","四川创网通信科技有限公司","仪征市水交投资有限公司","福建鑫宏兴航运有限公司","兰溪市鸿业建设有限公司","湖北金盛兰冶金科技有限公司","泉速通（济南）供应链管理有限公司"," 泉速通（上海）供应链管理有限公司 ","德清同创建设发展有限公司","湖州南浔旅游投资发展集团有限公司","都市股份有限公司","苍南县公共事业投资集团有限公司","上海穗深冷气设备有限公司","泰州市姜城水务有限责任公司","湖州织里童装园区经营管理有限公司","兰溪市聚源建设开发有限公司","泉州晋江滨江商务区开发建设有限公司","当涂县华泓水务科技有限公司","宁波经开科技发展有限公司","淮安新城投资控股有限公司","泰州金姜水务有限公司","晋江市高铁新城开发建设有限责任公司","江苏创网通信科技有限公司","淄博飞源化工有限公司","山西天恒氢能科技有限公司","贵州通源投资集团有限公司","溧阳德龙金属科技有限公司","江苏慧智新材料科技有限公司","诸暨市锦晨新能源科技有限公司","浙江铭岛铝业有限公司","湖州南浔善琏美丽村镇建设有限公司","海创投资发展集团有限公司","扶绥县昊腾工程建设有限公司","天津铁厂有限公司","华嘉铝业有限公司","舟山普陀城投开发建设有限公司","丹阳练湖水城投资建设有限公司","泰兴市港口集团有限公司","淮安市淮创商务有限公司","丹阳市丹昇新农村建设发展有限公司","丽水兴昌新材料科技股份有限公司","安徽天逸运输有限责任公司","湖州经开投资发展集团有限公司","泰州鑫宝三维科技产业园发展有限公司","莆田市荔城区金宝投资开发有限公司","连云港市智慧城市轨道交通工程有限公司","浙江安吉修竹绿化工程有限公司","诸暨浣江国际商贸城开发有限公司","德清联创科技新城建设有限公司","安吉县旅游发展有限公司","淮安市兴淮农村经济发展有限公司","诸暨市越都投资发展有限公司","肇庆市高要建投投资开发集团有限公司","欧龙汽车贸易集团有限公司","广州南菱汽车股份有限公司","九江诺贝尔陶瓷有限公司","安吉紫梅实业有限公司","金华市金东城市资产经营有限公司","舟山市定海区金塘北部开发投资有限公司","浙江金义田园智城开发建设有限公司","湖州吴兴南太湖建设投资集团有限公司","湖州吴兴产业投资发展集团有限公司","卓智网络科技有限公司","安吉县城西北开发有限公司","平阳县国阳铁路投资有限公司","虎鲸(杭州)冷链科技有限公司","成都市新益州城市建设发展有限公司","成都空港产业兴城投资发展有限公司","宣城市宣州区乡村振兴投资集团有限公司","浙江宝利德股份有限公司","成都香城产业发展集团有限公司","桂林鑫广达世奥汽车销售服务有限公司","兰溪市鸿图实业有限公司","徐州太一光能科技有限公司","平潭综合实验区城市发展集团有限公司","舟山市定海区绿艺园林绿化有限公司","益阳力天宝崐汽车销售服务有限公司","浙江安吉国控建设发展集团有限公司","东台市东方建设投资发展有限公司","泰州市惠泰建设发展有限公司","肇庆市端州区端鸿城市建设投资开发有限公司","杭州高晨源晟科技有限公司","杭州欣跃新能源有限公司","舟山永盛海运有限公司","上海申浙数智轨道科技有限公司","湖南永蓝高速公路有限公司","台州市国际会议中心有限公司","安徽昊方机电股份有限公司","合肥中驰声屏障技术有限公司","仪征市扬子文旅控股集团有限公司","杭州蓝月光电有限公司","成都空港产融投资发展有限公司","长沙虎鲸冷链物流有限公司","亳州常钰冷链物流有限公司","威顿水泥集团有限责任公司","肇庆市高要区国有资产经营有限公司","中讯网络科技有限公司");

    @Test
    public void testExport() {
        List<DataExcelModel> dataExcelModelList = new LinkedList<>();
        for (String clientName : CLIENT_NAME_LIST) {
            Client client = SpringUtil.getBean(ClientService.class).getOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, clientName));
            if (Objects.isNull(client)) {
                log.info("没有找到<{}>的客户信息", clientName);
                continue;
            }
            dataExcelModelList.add(this.build(client));
        }
        ExcelUtil.getWriter(true).write(dataExcelModelList).flush(FileUtil.getOutputStream("/Users/mockorz/result.xlsx"), true);
    }

    private DataExcelModel build(Client client) {
        DataExcelModel dataExcelModel = new DataExcelModel();
        dataExcelModel.setClientName(client.getClientName());
        // 所属集团
        CorpCommerceInfo corpCommerceInfo = SpringUtil.getBean(CorpCommerceInfoService.class).detail(client.getId(), null);
        if (Objects.isNull(corpCommerceInfo)) {
            dataExcelModel.setBelongGroupName("无");
        } else {
            if (Objects.equals(corpCommerceInfo.getBelongGroupClientId(), -1L)) {
                dataExcelModel.setBelongGroupName("无");
            } else {
                Client belongClient = SpringUtil.getBean(ClientService.class).getById(corpCommerceInfo.getBelongGroupClientId());
                dataExcelModel.setBelongGroupName(Optional.ofNullable(belongClient).map(Client::getClientName).orElse(""));
            }
        }
        // 合同相关
        LambdaQueryWrapper<ContractBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(ContractBaseInfo::getClientId, client.getId());
        query.in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()));
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).list(query);
        if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
            List<String> interestRateList = new LinkedList<>();
            List<String> consultingFeeList = new LinkedList<>();
            List<String> actualIrrList = new LinkedList<>();
            for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
                interestRateList.add(this.interestRate(contractBaseInfo));
                consultingFeeList.add(this.consultingFee(contractBaseInfo));
                actualIrrList.add(this.actualIrr(contractBaseInfo));
            }
            dataExcelModel.setContractInterestRate(CharSequenceUtil.join( " | ", interestRateList));
            dataExcelModel.setConsultingFee(CharSequenceUtil.join(" | ", consultingFeeList));
            dataExcelModel.setActualIRR(CharSequenceUtil.join(" | ", actualIrrList));
        }
        return dataExcelModel;
    }

    private String interestRate(ContractBaseInfo contractBaseInfo) {
        ContractPriceDetailREQ req = new ContractPriceDetailREQ();
        req.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP rsp = SpringUtil.getBean(ContractPriceService.class).detail(req);
        Integer lpr = rsp.getLprPercent();
        Integer lprAdd = rsp.getLprAddPercent();
        int interestRate = Optional.ofNullable(lpr).orElse(0) + Optional.ofNullable(lprAdd).orElse(0);
        String interestRateStr = BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString();
        return contractBaseInfo.getContractCode() + "  " + interestRateStr + "%";
    }

    private String consultingFee(ContractBaseInfo contractBaseInfo) {
        ContractPriceDetailREQ req = new ContractPriceDetailREQ();
        req.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP rsp = SpringUtil.getBean(ContractPriceService.class).detail(req);
        if (Objects.isNull(rsp.getConsultingFee())) {
            return contractBaseInfo.getContractCode();
        }
        String consultingFeeStr = BigDecimal.valueOf(rsp.getConsultingFee()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString();
        return contractBaseInfo.getContractCode() + "  " + consultingFeeStr + "元";
    }

    private String actualIrr(ContractBaseInfo contractBaseInfo) {
        List<ContractReceipt> contractReceiptList = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            return contractBaseInfo.getContractCode();
        }
        List<String> list = new LinkedList<>();
        for (ContractReceipt contractReceipt : contractReceiptList) {
            if (Objects.isNull(contractReceipt.getActualIrr())) {
                list.add(contractBaseInfo.getContractCode());
            } else {
                list.add(contractBaseInfo.getContractCode() + "  " + contractReceipt.getReceiptCode() + "  " + BigDecimal.valueOf(contractReceipt.getActualIrr()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString() + "%");
            }
        }
        return CharSequenceUtil.join(" | ", list);
    }

    @Data
    public static class DataExcelModel {
        private String clientName;
        private String belongGroupName;
        private String contractInterestRate;
        private String consultingFee;
        private String actualIRR;
    }
}
