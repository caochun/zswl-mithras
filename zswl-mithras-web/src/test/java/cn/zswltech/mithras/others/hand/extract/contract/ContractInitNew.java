package cn.zswltech.mithras.others.hand.extract.contract;

import cn.zswltech.mithras.others.service.ApplicationTest;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
//import org.testng.IClass;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 合同初始化 新的
 *
 * @author wangchuanhao
 * @date 2022/9/28 11:12 AM
 */
public class ContractInitNew extends ApplicationTest {

    @Resource
    private ContractPaymentImporter contractPaymentImporter;
    @Resource
    private ContractPaymentAppendImporter contractPaymentAppendImporter;

    @Test
    public void init() {
        contractPaymentImporter.clearData();
        contractPaymentImporter.initBaseData();
        contractPaymentImporter.genVersion();
    }

    @Test
    public void initAppend() {
        contractPaymentAppendImporter.initBaseData();
        contractPaymentAppendImporter.genVersion();
    }

    public static void main(String[] args) throws Exception {
        List<String> ls = Arrays.asList("2022A0061-01","2022A0066-01","2022A0065-01","2022A0062-01","2022A0053-SX-01","2022A0052-01","2022A0048-01","2022A0047-01","2021A0039-01","2022A0051-01","2022A0050-01","2022A0038-01","2022A0046-01","2022CGCJX0005-01","2022A0045-01","2022A0044-01","2022A0043-01","2022A0039-01","2022A0042-01","2022A0037-01","2022A0036-01","2022CGCJX0004-01","2022A0020-01","2022A0025-01","2022A0024-01","2022A0023-01","2022A0031-01","2022A0016-01","2022A0018-01","2022A0017-01","2022A0014-01","2022A0015-01","2022C0002-01","2022CGCJX0003-01","2022A0009-01","2022CGCJX0001-01","2021A0077-01","2021A0067-01","2021A0075-01","2021A0074-01","2021A0068-01","2021A0071-01","2021A0070-01","2021JLG0008-01","2021A0062-01","2021A0060-01","2021JLG0006-01","2021HF0001-01","2021XB0003-01","2021A0064-01","2021A0063-01","2021A0054-01","2021A0053-01","2021A0059-01","2021A0058-01","2021A0057-01","2021JLG0005-01","2021A0061-01","2021A0045-01","2021A0044-01","2021A0043-01","2021A0040-01","2021A0023-01","2021A0019-01","2021A0018-01","2021A0013-01","2021A0005-01","2021A0004-01","2021A0003-01","2020A0002-01","2020A0001-01","2021A0056-01","2021A0028-01","2021A0029-01","2021XB0002-01","2021A0052-01","2021A0051-01","2021XB0001-01","2021A0050-01","2021A0049-01","2021A0048-01","2021A0046-01","2021JLG0004-01","2021JLG0003-01","2021A0041-01","2021JLG0002-01","2021A0025-01","2021A0038-01","2021JN0001-01","2021A0027-01","2021A0035-01","2021A0033-01","2021A0034-01","2021A0006-01","2021A0008-01","2021A0010-01","2021A0026-01","2021A0012-01","2021A0024-01","2021A0011-01","2020A0034-01","2020A0004-01","2020A0009-01","2020A0014-01","2020A0015-01","2020A0018-01","2020A0019-01","2019A0010-01","2019A0011-01","2020A0030-01","2020A0010-01","2020A0007-01","2019A0009-01","2020A0016-01","2020A0020-01","2020A0021-01","2020A0001-01","2019A0016-01","2019A0015-01","2020A0013-01","2020A0011-01","2020A0025-01","2020A0026-01","2020A0012-01","2020A0022-01","2020A0031-01","2019A0002-01","2021A0016-01","2021A0032-01","2021A0022-01","2019A0013-01","2019A0014-01");
        Set<String> set = new HashSet<>(), repeatSet = new HashSet<>();
        for (String s : ls) {
            if (set.contains(s)) {
                repeatSet.add(s);
            }
            set.add(s);
        }
        System.out.println(JSON.toJSONString(repeatSet));
    }

}