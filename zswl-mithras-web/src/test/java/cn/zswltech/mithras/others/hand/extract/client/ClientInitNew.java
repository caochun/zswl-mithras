package cn.zswltech.mithras.others.hand.extract.client;

import cn.hutool.core.io.IoUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 新的 客户导入
 *
 * @author wangchuanhao
 * @date 2022/9/25 12:34 PM
 */
public class ClientInitNew extends ApplicationTest {

    @Resource
    private ClientImporter clientImporter;
    @Resource
    private ClientTycExporter clientTycExporter;

    @Test
    public void init() {
        clientImporter.initBaseData();
        //clientImporter.initSubjectData();
        clientImporter.genVersion();
//        clientImporter.clearData();
    }

    @Test
    public void queryTycBaseInfo() throws Exception {
        //String handClientDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得客户模块导出_20220926.json").getInputStream(), Charset.defaultCharset());
        //JSONArray handClientDataArray = JSONArray.parseArray(handClientDataString);
        //List<String> uscCodeList = handClientDataArray.stream().map(d -> ((JSONObject)d).getString("social_credit_code")).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<String> uscCodeList = Arrays.asList("91330105MA2KER1H4N","913303275586060950","91330521MA2B4DBR98","91330902MA2A2B3P4W","91320412MA1NMRK6X6","91330327717675363Y","913101123207673826","913707256705418695","913301006091684447","91330521059589286F","911311021097952062");
        log.info("社会统一信用代码:{}", JSON.toJSONString(uscCodeList));
        String data = clientTycExporter.tycInfo(uscCodeList);
        IoUtil.write(new FileOutputStream("/Users/wang/Desktop/汉得客户模块天眼查数据_20220928_simple.json"), true, data.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) throws Exception {
        JSONObject data0927 = JSONObject.parseObject(IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得客户模块天眼查数据_20220928_simple.json").getInputStream(), Charset.defaultCharset()));
        JSONObject data0926 = JSONObject.parseObject(IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得客户模块天眼查数据_20220927_merge.json").getInputStream(), Charset.defaultCharset()));
        data0926.putAll(data0927);
        IoUtil.write(new FileOutputStream("/Users/wang/Desktop/汉得客户模块天眼查数据_20220928_merge.json"), true, data0926.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

//    public static void main(String[] args) {
//        Set<String> projErrClientNameSet = new HashSet<>(Arrays.asList("浙江舟山筑盟建设发展有限责任公司","浙江欣捷建设有限公司","昌乐新迈纸业有限公司","宁波溪口雪窦山风景名胜区自来水有限公司","李鑫","浙江长龙海运有限公司","兰溪市交通建设投资集团有限公司","浙江交科供应链管理有限公司","上海鼎衡船务有限责任公司","开玙供应链（无锡）有限公司","泰兴市成兴国有资产经营投资有限公司","嵊州市投资控股有限公司","湖州申太建设发展有限公司","许沂炎","河南凤宝特钢有限公司","邹昌盛","江苏九鼎新材料股份有限公司","诸暨市城东新城建设有限公司","绍兴柯桥经济开发区开发投资有限公司","中建锦程机械设备（上海）有限公司","金鼎重工有限公司","绍兴高新技术产业开发区迪荡新城投资发展有限公司","嵊州市交通发展有限公司","浙江逸盛新材料有限公司","杭州千岛湖高铁新区发展有限公司","兰溪市城市投资集团有限公司","吴少杰","浙江豪邦化工有限公司","福建鑫宏兴航运有限公司","佛山市金海辉煌不锈钢有限公司","宁波市奉化区旅游集散中心有限公司","杭州暗箱科技有限公司","浙商中拓集团股份有限公司","德清乾龙建设发展有限公司","宁波市雪窦开发投资集团有限公司"));
//        projErrClientNameSet.removeAll(Arrays.asList("德清联创科技新城建设有限公司","泰兴市成兴国有资产经营投资有限公司","陈华禄","大连华德天宇集团有限公司","湖州申太建设发展有限公司","福建鑫宏兴航运有限公司","德清乾龙建设发展有限公司","肖清美","许沂炎","兰溪市城市投资集团有限公司","兰溪市交通建设投资集团有限公司","嵊州市经济开发区投资有限公司","中讯网络科技有限公司","浙商中拓集团股份有限公司","嵊州市投资控股有限公司","余惠君","杨利娟","张体东","绍兴柯桥经济开发区开发投资有限公司","绍兴市柯桥区滨海城市建设开发投资有限公司","吴少杰","杭州千岛湖高铁新区发展有限公司","邹昌盛之妻","邹昌盛","雷丽","邹运长","浙江交科供应链管理有限公司","宁波溪口雪窦山风景名胜区自来水有限公司","浙江逸盛新材料有限公司","李鑫","广邦（厦门）塑胶科技有限公司","浙江豪邦化工有限公司","宁波市雪窦开发投资集团有限公司","宁波市奉化区旅游集散中心有限公司","柳州钢铁股份有限公司","中建锦程机械设备（上海）有限公司","地上铁租车（深圳）有限公司","诸暨市城东新城建设有限公司","绍兴市科技创业投资有限公司","绍兴高新技术产业开发区迪荡新城投资发展有限公司","杭州中诚装备服务股份有限公司","绍兴添溢工程机械租赁有限公司","金鼎重工有限公司","陈佩珍","浙江卓航物流发展股份有限公司","浙江长龙海运有限公司","林州凤宝管业有限公司","河南凤宝特钢有限公司","上海鼎衡航运科技有限公司","象山县滨海投资有限公司","宁波市沥平航运有限公司","宁波市海洲船务有限公司","象山县通用燃气有限公司","盐城市亭湖城市资产投资实业有限公司","盐城海瀛控股集团有限公司","太平洋建设集团有限公司","佛山市金海辉煌不锈钢有限公司","张锐","浙江欣捷建设有限公司"));
//        System.out.println(JSON.toJSONString(projErrClientNameSet));
//    }

//    public static void main(String[] args) throws Exception {
//        JSONObject tycData0927 = JSONObject.parseObject(IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得客户模块天眼查数据_20220927_merge.json").getInputStream(), Charset.defaultCharset()));
//        String handClientDataString = IoUtil.read(new FileSystemResource("/Users/wang/Desktop/汉得客户模块导出_20220928.json").getInputStream(), Charset.defaultCharset());
//        JSONArray handClientDataArray = JSONArray.parseArray(handClientDataString);
//        Set<String> newCreditCodeSet = handClientDataArray.stream().map(d -> ((JSONObject)d).getString("social_credit_code")).filter(Objects::nonNull).collect(Collectors.toSet());
//        newCreditCodeSet.removeAll(tycData0927.keySet());
//        System.out.println(JSON.toJSONString(newCreditCodeSet));
//    }

}
