package cn.zswltech.mithras.others;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.MergeCellRule;
import com.deepoove.poi.data.Paragraphs;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.Tables;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.BorderStyle;
import org.springframework.core.io.ClassPathResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * word文档生成
 *
 * @author wangchuanhao
 * @date 2022/7/4 1:44 PM
 */
public class WordGenerateTest {

    public static void main(String[] args) throws IOException {
        String outputFileName = "/Users/wang/Desktop/生成文档.docx";
        ClassPathResource fileSystemResource = new ClassPathResource("word/文件模版.docx");
        XWPFTemplate template = XWPFTemplate.compile(fileSystemResource.getFile()).render(
                new HashMap<String, Object>(){{
                    /**
                     * leaseType
                     * companyName
                     * originPrice
                     * totalPrice
                     */
                    put("testTitle", Paragraphs.of());
                    put("leaseType", "售后回租");
                    put("companyName", "舟山市定海城区建设开发有限公司");
                    put("originPrice", "20,000.00");
                    put("totalPrice", "22,508.12");

                    // 租金支付表
                    List<RowRenderData> leasePayCalTableList = new ArrayList<>();
                    leasePayCalTableList.add(Rows.of("租金支付概算表（单位：元）", null, null, null).center().create());
                    leasePayCalTableList.addAll(generateLeasePayCalTableData());
                    MergeCellRule mergeRule = MergeCellRule.builder()
                            // 单元格合并规则，可以map多次包含多个合并规则，i是行，j是列，此处合并从 第1行第0列 到 第1行第2列 的单元格
                            // 复杂表格看看这个https://blog.csdn.net/weixin_59158648/article/details/118415467
                            .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(0, 3))
                            .build();
                    RowRenderData[] leasePayCalTableArray = new RowRenderData[leasePayCalTableList.size()];
                    leasePayCalTableList.toArray(leasePayCalTableArray);
                    put("leasePayCalTable", Tables.of(leasePayCalTableArray)
                            .mergeRule(mergeRule)
                            .create());

                    // 经济情况表
                    MergeCellRule financialTableMergeRule = MergeCellRule.builder()
                            .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(1, 0))

                            .map(MergeCellRule.Grid.of(0, 1), MergeCellRule.Grid.of(0, 2))
                            .map(MergeCellRule.Grid.of(0, 3), MergeCellRule.Grid.of(0, 4))
                            .map(MergeCellRule.Grid.of(0, 5), MergeCellRule.Grid.of(0, 6))
                            .map(MergeCellRule.Grid.of(0, 7), MergeCellRule.Grid.of(0, 8))

                            .map(MergeCellRule.Grid.of(12, 1), MergeCellRule.Grid.of(12, 2))
                            .map(MergeCellRule.Grid.of(12, 3), MergeCellRule.Grid.of(12, 4))
                            .map(MergeCellRule.Grid.of(12, 5), MergeCellRule.Grid.of(12, 6))
                            .map(MergeCellRule.Grid.of(12, 7), MergeCellRule.Grid.of(12, 8))

                            .map(MergeCellRule.Grid.of(13, 1), MergeCellRule.Grid.of(13, 2))
                            .map(MergeCellRule.Grid.of(13, 3), MergeCellRule.Grid.of(13, 4))
                            .map(MergeCellRule.Grid.of(13, 5), MergeCellRule.Grid.of(13, 6))
                            .map(MergeCellRule.Grid.of(13, 7), MergeCellRule.Grid.of(13, 8))

                            .build();
                    List<RowRenderData> financialTableList = generateFinancialTable();
                    RowRenderData[] financialTableArray = new RowRenderData[financialTableList.size()];
                    financialTableList.toArray(financialTableArray);
                    put("financialTable", Tables.of(financialTableArray)
                            .mergeRule(financialTableMergeRule)
                            .create());
                }});
        template.writeAndClose(new FileOutputStream(outputFileName));
    }

    private static List<RowRenderData> generateLeasePayCalTableData() {
        return Arrays.asList(
                /**
                 * 1	14,067,574.40	2,850,000.00	11,217,574.40
                 * 2	14,067,574.40	2,690,149.56	11,377,424.84
                 * 3	14,067,574.40	2,528,021.26	11,539,553.14
                 * 4	14,067,574.40	2,363,582.63	11,703,991.78
                 * 5	14,067,574.40	2,196,800.75	11,870,773.66
                 * 6	14,067,574.40	2,027,642.22	12,039,932.18
                 * 7	14,067,574.40	1,856,073.19	12,211,501.22
                 * 8	14,067,574.40	1,682,059.30	12,385,515.11
                 * 9	14,067,574.40	1,505,565.70	12,562,008.70
                 * 10	14,067,574.40	1,326,557.08	12,741,017.32
                 * 11	14,067,574.40	1,144,997.58	12,922,576.82
                 * 12	14,067,574.40	960,850.86	13,106,723.54
                 * 13	14,067,574.40	774,080.05	13,293,494.35
                 * 14	14,067,574.40	584,647.76	13,482,926.65
                 * 15	14,067,574.40	392,516.05	13,675,058.35
                 * 16	14,067,574.40	197,646.47	13,869,927.93
                 * 合计	225,081,190.48	25,081,190.48	200,000,000.00
                 */
                Rows.of("期数", "当期租金", "利息", "当期本金").center().create(),
                Rows.of("1", "14,067,574.40", "2,850,000.00", "11,217,574.40").center().create(),
                Rows.of("2", "14,067,574.40", "2,690,149.56", "11,377,424.84").center().create(),
                Rows.of("3", "14,067,574.40", "2,528,021.26", "11,539,553.14").center().create(),
                Rows.of("4", "14,067,574.40", "2,363,582.63", "11,703,991.78").center().create(),
                Rows.of("5", "14,067,574.40", "2,196,800.75", "11,870,773.66").center().create(),
                Rows.of("6", "14,067,574.40", "2,027,642.22", "12,039,932.18").center().create(),
                Rows.of("7", "14,067,574.40", "1,856,073.19", "12,211,501.22").center().create(),
                Rows.of("8", "14,067,574.40", "1,682,059.30", "12,385,515.11").center().create(),
                Rows.of("9", "14,067,574.40", "1,505,565.70", "12,562,008.70").center().create(),
                Rows.of("10", "14,067,574.40", "1,326,557.08", "12,741,017.32").center().create(),
                Rows.of("11", "14,067,574.40", "1,144,997.58", "12,922,576.82").center().create(),
                Rows.of("12", "14,067,574.40", "960,850.86", "13,106,723.54").center().create(),
                Rows.of("13", "14,067,574.40", "774,080.05", "13,293,494.35").center().create(),
                Rows.of("14", "14,067,574.40", "584,647.76", "13,482,926.65").center().create(),
                Rows.of("15", "14,067,574.40", "392,516.05", "13,675,058.35").center().create(),
                Rows.of("16", "14,067,574.40", "197,646.47", "13,869,927.93").center().create(),
                Rows.of("合计", "225,081,190.48", "25,081,190.48", "200,000,000.00").center().create()

        );
    }

    private static List<RowRenderData> generateFinancialTable() {
        return Arrays.asList(
                Rows.of("项目", "2020年", null, "2019年", null, "2018年", null, "2017年", null).center().create(),
                Rows.of(null, "金额", "同比增长", "金额", "同比增长", "金额", "同比增长", "金额", "同比增长").center().create(),
                Rows.of("地区生产总值", "1,512.1", "12.00%", "1,371.6", "9.2%", "1,316.7", "6.7%", "1,219.0", "8.8%").center().create(),
                Rows.of("第一产业增加值", "152.9", "2.20%", "146.4", "1.6%", "142.6", "5.8%", "143.0", "5.1%").center().create(),
                Rows.of("第二产业增加值", "590.2", "29.60%", "475.5", "17.6%", "428.4", "6.0%", "444.0", "9.7%").center().create(),
                Rows.of("第三产业增加值", "769.0", "1.70%", "749.7", "5.7%", "745.7", "7.2%", "632.0", "8.7%").center().create(),
                Rows.of("规模以上工业增加值", "-", "60.20%", "293.7", "43.2%", "-", "6.4%", "-", "11.0%").center().create(),
                Rows.of("固定资产投资", "-", "1.5%", "-", "8.6%", "-", "7.5%", "1,450.3", "15.0%").center().create(),
                Rows.of("社会消费品零售总额", "-", "-7.50%", "580.6", "8.2%", "536.9", "8.7%", "505.7", "10.6%").center().create(),
                Rows.of("进出口总额", "1,667.3", "21.60%", "1,371.1", "20.7%", "1,135.5", "44.9%", "783.0", "12.4%").center().create(),
                Rows.of("存款余额", "2,540.0", "11.30%", "2,282.1", "12.2%", "2,034.1", "1.3%", "2,008.5", "4.8%").center().create(),
                Rows.of("贷款余额", "2,773.8", "13.90%", "2,435.7", "20.1%", "2,028.9", "17.9%", "1,721.6", "13.1%").center().create(),
                Rows.of("人均GDP（元）", "-", null, "11.7", null, "11.2", null, "10.5", null).center().create(),
                Rows.of("人均GDP/全国人均GDP", "-", null, "1.65", null, "1.74", null, "1.76", null).center().create()
        );
    }

}
