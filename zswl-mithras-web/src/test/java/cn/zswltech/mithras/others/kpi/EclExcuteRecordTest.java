package cn.zswltech.mithras.others.kpi;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.mapper.model.kpi.EclExecuteRecord;
import cn.zswltech.mithras.service.service.kpi.EclExecuteRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
public class EclExcuteRecordTest extends ApplicationTest {
    @Resource
    private EclExecuteRecordService eclExecuteRecordService;

    @Test
    public void test(){
        List<EclExecuteRecord> list = eclExecuteRecordService.list(Wrappers.<EclExecuteRecord>lambdaQuery());
        exportToCSV(list, "/Users/vico/Documents/指标导出/资产减值调用记录.xlsx");
    }

    // 表头映射（字段名 -> 注释）
    private static final ArrayList<String> HEADERS = ListUtil.toList(
            "id", "调用记录编号", "拨备计提详情id", "客户id", "客户名称", "合同id", "合同编号",
            "内评评级", "ecl违约概率", "外评级别", "ecl外评违约概率", "所属分组", "五级分类",
            "逾期天数", "租赁物类型", "剩余本金", "应计利息", "保证金", "下一期租金",
            "风险敞口", "ead", "合同到期日", "债项阶段", "期限调整系数t",
            "ecl基准/乐观/悲观调整因子z", "ecl基准/乐观/悲观情景权重", "违约损失率(lgd)",
            "基准pdforward", "乐观pdforward", "悲观pdforward", "基准pdifrs9",
            "乐观pdifrs9", "悲观pdifrs9", "基准ecl", "乐观ecl", "悲观ecl", "ecl",
            "备注", "下迁等级"
    );

    public void exportToCSV(List<EclExecuteRecord> data, String filePath) {
        // 创建CSV写入器（指定分隔符和字符编码）
        try (ExcelWriter writer = new ExcelWriter(filePath)) {
            // 写入表头
            writer.writeHeadRow(HEADERS);

            // 写入数据行
            for (EclExecuteRecord record : data) {
                List<Object> rowData = new ArrayList<>();

                // 按顺序添加字段值
                rowData.add(record.getId());
                rowData.add(record.getModelRecordKey());
                rowData.add(record.getKpiProvisionDetailId());
                rowData.add(record.getClientId());
                rowData.add(record.getClientName());
                rowData.add(record.getContractId());
                rowData.add(record.getContractCode());
                rowData.add(record.getInnerMdLevel());
                rowData.add(record.getEclPd());
                rowData.add(record.getOuterLevel());
                rowData.add(record.getEclOuterPd());
                rowData.add(record.getGroup());
                rowData.add(record.getClassify());
                rowData.add(record.getLateDay());
                rowData.add(record.getLeaseType());
                rowData.add(record.getRemainPrincipal());
                rowData.add(record.getAccruedInterest());
                rowData.add(record.getDeposit());
                rowData.add(record.getNextRent());
                rowData.add(record.getRiskExposure());
                rowData.add(record.getEad());
                rowData.add(record.getContractExpirationDate());
                rowData.add(record.getEclStep());
                rowData.add(record.getEclFactorT());
                rowData.add(record.getEclParamZ());
                rowData.add(record.getEclParamWeight());
                rowData.add(record.getLgd());
                rowData.add(record.getBasePdForward());
                rowData.add(record.getOptPdForward());
                rowData.add(record.getGloPdForward());
                rowData.add(record.getEclBaseIfrs9());
                rowData.add(record.getEclOptIfrs9());
                rowData.add(record.getEclGloIfrs9());
                rowData.add(record.getBaseEcl());
                rowData.add(record.getOptEcl());
                rowData.add(record.getGloEcl());
                rowData.add(record.getEcl());
                rowData.add(record.getRemark());
                rowData.add(record.getRzyEclDownLevel());

                // 写入当前行
                writer.writeRow(rowData);
            }

            System.out.println("导出成功，保存路径：" + FileUtil.getAbsolutePath(filePath));
        }
    }


}
