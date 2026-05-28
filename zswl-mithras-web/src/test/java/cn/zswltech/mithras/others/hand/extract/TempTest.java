package cn.zswltech.mithras.others.hand.extract;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.style.StyleUtil;
import cn.zswltech.mithras.others.hand.extract.client.ClientExtractor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用来满足产品的一些临时需求
 *
 * @author wangchuanhao
 * @date 2022/8/18 2:09 PM
 */
public class TempTest {


    public static void main(String[] args) throws Exception {
        //contractExcelMarkResignUser();
        exportContractClientExcel();
    }

    /**
     * 分析离职人员
     */
    public static void analyseResignUser() {
        String oldUserArrayJson = "[\"张俊青\",\"赵军伟\",\"何晓\",\"张端清\",\"宋魏秦\",\"洪梦恺\",\"罗彪\",\"米辉\",\"汪曼苇\",\"王璐璐\",\"王山山\",\"崔坤玉\",\"陈艳娟\",\"郑其印\",\"李艳\",\"王诗岚\",\"何佳薇\",\"鲁桂欣\",\"曹佳\",\"伍杰\",\"肖永杰\",\"鲁素萍\",\"蔡荣\",\"卢小琴\",\"陈凯鹏\",\"吴巧玲\",\"邹晓曦\",\"谷晓丰\",\"郭志刚\",\"杨剑雄\",\"陈虞奇\",\"毛潇逸\",\"汪晶晶\",\"骆晨\",\"刘怡杉\",\"冯帅\",\"刘延彪\",\"李玉龙\",\"马樱之\",\"张赟\",\"祝文娟\",\"金丽妮\",\"沈毅\",\"潘钱燊\",\"蒋照辉\",\"何栋\",\"洪晓成\",\"陈哲\",\"张晖\",\"徐凯\",\"朱金娟\",\"鲍家友\",\"张闻天\",\"赵佳萍\",\"宋轶锋\",\"李雅婷\",\"王道羿\",\"钱伟\",\"倪辉\",\"任三强\",\"王飞\",\"陆雅芳\",\"黄颖君\",\"王丽慧\",\"王吉琛\",\"骆思洁\",\"陈红升\",\"邱彤亮\",\"葛兴旺\",\"林沙石\",\"刘小幸\",\"周斐\",\"黄颖\",\"毛敏康\",\"孙志平\",\"曹俊\",\"章镒军\",\"孙亭亭\",\"陈钢\",\"张士亮\",\"秦海琨\",\"王博\",\"冯莎\",\"崔启法\",\"张艾嘉\"]";
        String newUserArrayJson = "[\"蒋照辉\",\"伍杰\",\"李艳\",\"潘钱燊\",\"鲁素萍\",\"杨剑雄\",\"张端清\",\"何晓\",\"张俊青\",\"黄颖\",\"洪梦恺\",\"鲍家友\",\"宋魏秦\",\"林沙石\",\"米辉\",\"何栋\",\"郑其印\",\"鲁桂欣\",\"王山山\",\"祝文娟\",\"李玉龙\",\"刘延彪\",\"蔡荣\",\"陈凯鹏\",\"邹晓曦\",\"金丽妮\",\"孙亭亭\",\"王博\",\"卢小琴\",\"冯帅\",\"马樱之\",\"赵佳萍\",\"肖永杰\",\"陆雅芳\",\"毛敏康\",\"曹俊\",\"陈钢\",\"汪晶晶\",\"陈红升\",\"宋轶锋\",\"邱彤亮\",\"葛兴旺\",\"孙志平\",\"张士亮\",\"崔启法\",\"张艾嘉\",\"秦海琨\",\"倪辉\",\"王璐璐\",\"陈艳娟\",\"张赟\",\"钱伟\",\"章镒军\",\"骆晨\",\"冯莎\",\"王吉琛\",\"骆思洁\",\"周斐\",\"何佳薇\",\"王诗岚\",\"黄颖君\",\"杜顺顺\"]";
        Set<String> oldUserSet = new HashSet<>(JSONArray.parseArray(oldUserArrayJson, String.class));
        Set<String> newUserSet = new HashSet<>(JSONArray.parseArray(newUserArrayJson, String.class));
        System.out.println("差异人员:" + JSON.toJSONString(Sets.difference(oldUserSet, newUserSet)));
    }

    /**
     * 合同整理excel 标记出主办和协办是离职人员的
     */
    public static void contractExcelMarkResignUser() {
        String resignUserJson = "[\"赵军伟\",\"汪曼苇\",\"曹佳\",\"吴巧玲\",\"刘怡杉\",\"王丽慧\",\"谷晓丰\",\"沈毅\",\"陈虞奇\",\"李雅婷\",\"陈哲\",\"张晖\",\"王飞\",\"任三强\",\"朱金娟\",\"毛潇逸\",\"崔坤玉\",\"徐凯\",\"王道羿\",\"张闻天\",\"刘小幸\",\"洪晓成\",\"郭志刚\",\"罗彪\"]";
        Set<String> resignUserSet = new HashSet<>(JSONArray.parseArray(resignUserJson, String.class));
        ExcelReader excelReader = ExcelUtil.getReader("/Users/wang/Desktop/合同模块数据整理_线上_20220818_1352.xlsx");
        List<List<Object>> dataList = excelReader.read();
        int i = 1;
        for (List<Object> data : dataList) {
            Set<String> contractUserSet = new HashSet<>();
            contractUserSet.add((String) data.get(11));
            if (StringUtils.isNotBlank((String)data.get(12))) {
                contractUserSet.addAll(Stream.of(((String)data.get(12)).split(",")).collect(Collectors.toList()));
            }
            if (Sets.intersection(resignUserSet, contractUserSet).size() > 0) {
                // 有交集
                System.out.println(String.format("第%s行存在离职人员", i));
            }
            i++;
        }
    }

    /**
     * 合同相关客户数据
     */
    public static void exportContractClientExcel() throws Exception {
        String resignUserJson = "[\"赵军伟\",\"汪曼苇\",\"曹佳\",\"吴巧玲\",\"刘怡杉\",\"王丽慧\",\"谷晓丰\",\"沈毅\",\"陈虞奇\",\"李雅婷\",\"陈哲\",\"张晖\",\"王飞\",\"任三强\",\"朱金娟\",\"毛潇逸\",\"崔坤玉\",\"徐凯\",\"王道羿\",\"张闻天\",\"刘小幸\",\"洪晓成\",\"郭志刚\",\"罗彪\"]";
        Set<String> resignUserSet = new HashSet<>(JSONArray.parseArray(resignUserJson, String.class));
        Map<String, List<String>> clientRelatedContractMap = new HashMap<>();

        // 汉得全量客户
        String allClientJson = IoUtil.read(new ClassPathResource("init/客户模块数据整理json_线上_20220818.json").getInputStream(), Charset.defaultCharset());
        Map<String, ClientExtractor.InitDataModel> clientMap = JSONArray.parseArray(allClientJson, ClientExtractor.InitDataModel.class)
                .stream().collect(Collectors.toMap(ClientExtractor.InitDataModel::getClientName, m -> m, (k1,k2) -> k1));

        JSONArray contractDataArray = JSONArray.parseArray(IoUtil.read(new ClassPathResource("init/合同模块数据整理json_线上_20220818_1633.json").getInputStream(), Charset.defaultCharset()));
        System.out.println("合同数量:" + contractDataArray.size());
        Set<String> allContractClientNameSet = new HashSet<>();
        for (int i = 0; i < contractDataArray.size(); i++) {
            JSONObject dataObj = contractDataArray.getJSONObject(i);
            Set<String> curClientSet = new HashSet<>();
            curClientSet.add(dataObj.getString("bp_id_tenant_n"));
            JSONArray bpArray = dataObj.getJSONArray("bpArray");
            if (bpArray.size() > 0) {
                curClientSet.addAll(bpArray.stream().map(j -> ((JSONObject)j).getString("bp_id_n")).collect(Collectors.toSet()));
            }
            JSONArray guaranteeArray = dataObj.getJSONArray("guaranteeArray");
            if (guaranteeArray.size() > 0) {
                curClientSet.addAll(guaranteeArray.stream().map(j -> ((JSONObject)j).getString("bp_id_n")).collect(Collectors.toSet()));
            }
            JSONArray pawnInfoArray = dataObj.getJSONArray("pawnInfoArray");
            if (pawnInfoArray.size() > 0) {
                curClientSet.addAll(pawnInfoArray.stream().map(j -> ((JSONObject)j).getString("bp_id_mortgagor_n")).collect(Collectors.toSet()));
            }
            JSONArray pledgeInfoArray = dataObj.getJSONArray("pledgeInfoArray");
            if (pawnInfoArray.size() > 0) {
                curClientSet.addAll(pledgeInfoArray.stream().map(j -> ((JSONObject)j).getString("bp_id_mortgagor_n")).collect(Collectors.toSet()));
            }

            for (String clientName : curClientSet) {
                clientRelatedContractMap.computeIfAbsent(clientName, k -> new ArrayList<>());
                clientRelatedContractMap.get(clientName).add(dataObj.getString("contract_number"));
            }
            allContractClientNameSet.addAll(curClientSet);
        }
        List<String> hdNotExistClientNameList = allContractClientNameSet.stream().filter(n -> !clientMap.containsKey(n)).collect(Collectors.toList());
        System.out.println("客户模块不存在的数据：" + JSON.toJSONString(hdNotExistClientNameList));
        allContractClientNameSet.removeAll(hdNotExistClientNameList);

        List<List<String>> outputDataList = new ArrayList<>();
        int i = 1;
        List<Integer> needMarkRowList = new ArrayList<>();
        for (String clientName : allContractClientNameSet) {
            ClientExtractor.InitDataModel ciData = clientMap.get(clientName);
            outputDataList.add(Arrays.asList(
                    ciData.getClientName(),
                    "ORG".equals(ciData.getBpClass()) ? "法人" : "自然人",
                    ciData.getSocialCreditCode(),
                    ciData.getIdCardNo(),
                    ciData.getCreateUserName(),
                    clientRelatedContractMap.get(clientName).stream().collect(Collectors.joining(","))
            ));
            // 有离职人员 标红该行
            if (resignUserSet.contains(ciData.getCreateUserName())) {
                needMarkRowList.add(i);
            }
            i++;
        }
        ExcelWriter excelWriter = new ExcelWriter("/Users/wang/Desktop/合同模块_关联客户数据_线上_20220818_1735.xlsx");
        excelWriter.writeHeadRow(Arrays.asList("客户名称", "客户类型", "统一社会信用代码", "身份证", "创建人", "关联合同编号"));

        excelWriter.write(outputDataList, false);

        for (Integer needMarkRowNumber : needMarkRowList) {
            CellStyle markRowStyle = excelWriter.getOrCreateRowStyle(needMarkRowNumber);
            Font font = excelWriter.createFont();
            font.setFontName("宋体");
            font.setBold(true);
            font.setColor(Font.COLOR_RED);
            markRowStyle.setFont(font);
            for (int j = 0; j < 6; j++) {
                CellStyle cellStyle = excelWriter.createCellStyle(j, needMarkRowNumber);
                cellStyle.setFont(font);
//                cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
//                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                StyleUtil.setAlign(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
//                cellStyle.setWrapText(true);
                StyleUtil.setBorder(cellStyle, BorderStyle.THIN, IndexedColors.BLACK);
                excelWriter.setStyle(cellStyle, j, needMarkRowNumber);
            }
            //excelWriter.getCellStyle().setFont(font);
        }

        System.out.println(JSON.toJSONString(needMarkRowList));
        excelWriter.flush();
        excelWriter.close();
    }

}
