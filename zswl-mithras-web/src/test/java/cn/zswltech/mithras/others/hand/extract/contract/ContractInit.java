package cn.zswltech.mithras.others.hand.extract.contract;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.GuaranteeMethodEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractAccountMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractGuarantorMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeasePriceMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentActualMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractTenantryMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentQuestionnaireAnswer;
import cn.zswltech.mithras.service.mapper.payment.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentQuestionnaireAnswerMapper;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentQuestionnaireAnswerService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 合同初始化 旧的
 *
 * @author wangchuanhao
 * @date 2022/8/15 3:00 PM
 */
@Deprecated
public class ContractInit extends ApplicationTest {

    private static final Logger log = LoggerFactory.getLogger(ContractInit.class);

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractAccountMapper contractAccountMapper;
    @Resource
    private ContractTenantryMapper contractTenantryMapper;
    @Resource
    private ContractGuarantorMapper contractGuarantorMapper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private ContractRentActualMapper contractRentActualMapper;
    @Resource
    private ContractLeasePriceMapper contractLeasePriceMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentQuestionnaireAnswerService answerService;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private PaymentQuestionnaireAnswerMapper paymentQuestionnaireAnswerMapper;

    @Test
    public void extractList() {
        ContractExtractor contractExtractor = new ContractExtractor();
        contractExtractor.extractList();
    }

    @Test
    public void test() {
        List<String> contractNumberList = new ArrayList<>();
        ExcelReader excelReader = ExcelUtil.getReader("/Users/wang/Desktop/风控台账-2-融资租赁合同信息登记表20220407.xlsx");
        for (Sheet sheet : excelReader.getSheets()) {
            ExcelReader curReader = new ExcelReader(sheet);
            List<List<Object>> readAll = curReader.read();
            for (List<Object> dataList : readAll) {
                if (dataList.size() >= 3
                        && dataList.get(2) instanceof String
                        && !"合同编号".equals(dataList.get(2))
                        && ((String) dataList.get(2)).contains("号")) {
                    contractNumberList.add((String) dataList.get(2));
                }
            }
        }
        System.out.println(JSON.toJSONString(contractNumberList));
    }

    @Test
    public void test1() {
        String tzExcelData = "[\"浙商租【2022】转字第（Z-0001）号\",\"浙商租【2022】转字第（Z-0002）号\",\"浙商租【2022】转字第（Z-0003）号\",\"浙商租【2022】转字第（Z-0004）号\",\"浙商租【2022】 保理字第（B-0001）号\",\"浙商租【2022】 保理字第（B-0002）号\",\"浙商租【2022】 保理字第（B-0003）号\",\"浙商租【2022】 保理字第（B-0004）号\",\"浙商租【2022】 保理字第（B-0005）号\",\"浙商租【2022】 保理字第（B-0006）号\",\"浙商租【2022】 保理字第（B-0007）号\",\"浙商租【2022】 保理字第（B-0008）号\",\"浙商租【2022】 保理字第（B-0009）号\",\"浙商租【2022】 保理字第（B-0010）号\",\"浙商租【2022】租字第（A-0001）号\",\"浙商租【2022】租字第（A-0002）号\",\"浙商租【2022】租字第（A-0003）号\",\"浙商租【2022】租字第（A-0004）号\",\"浙商租【2022】租字第（A-0005）号\",\"浙商租【2022】租字第（A-0006）号\",\"浙商租【2022】租字第（A-0007）号\",\"浙商租【2022】租字第（A-0008）号\",\"浙商租【2022】租字第（A-0009）号\",\"浙商租【2022】租字第（A-0010）号\",\"浙商租【2022】租字第（A-0011）号\",\"浙商租【2022】租字第（A-0012）号\",\"浙商租【2022】租字第（A-0013）号\",\"浙商租【2022】租字第（A-0014）号\",\"浙商租【2022】租字第（A-0015）号\",\"浙商租【2022】租字第（A-0016）号\",\"浙商租【2022】租字第（A-0017）号\",\"浙商租【2022】租字第（A-0018）号\",\"浙商租【2022】租字第（A-0019）号\",\"浙商租【2022】租字第（A-0020）号\",\"浙商租【2022】租字第（A-0021）号\",\"浙商租【2022】租字第（A-0022）号\",\"浙商租【2022】租字第（A-0023）号\",\"浙商租【2022】租字第（A-0024）号\",\"浙商租【2022】租字第（A-0025）号\",\"浙商租【2022】租字第（A-0026）号\",\"浙商租【2022】租字第（A-0027）号\",\"浙商租【2022】租字第（A-0028）号\",\"浙商租【2022】租字第（A-0029）号\",\"浙商租【2022】租字第（A-0030）号\",\"浙商租【2022】租字第（A-0031）号\",\"浙商租【2022】租字第（A-0032）号\",\"浙商租【2022】租字第（A-0033）号\",\"浙商租【2022】租字第（A-0034）号\",\"浙商租【2022】租字第（A-0035）号\",\"浙商租【2022】租字第（A-0036）号\",\"浙商租【2022】租字第（A-0037）号\",\"浙商租【2022】租字第（A-0038）号\",\"浙商租【2022】租字第（A-0039）号\",\"浙商租【2022】租字第（A-0040）号\",\"浙商租【2022】租字第（A-0041）号\",\"浙商租【2022】租字第（A-0042）号\",\"浙商租【2022】租字第（A-0043）号\",\"浙商租【2022】租字第（A-0044）号\",\"浙商租【2022】租字第（A-0045）号\",\"浙商租【2022】租字第（A-0046）号\",\"浙商租【2022】租字第（A-0047）号\",\"浙商租【2022】租字第（A-0048）号\",\"浙商租【2022】租字第（A-0049）号\",\"浙商租【2022】租字第（A-0050）号\",\"浙商租【2022】租字第（A-0051）号\",\"浙商租【2022】租字第（A-0052）号\",\"浙商租【2022】租字第（A-0053）号\",\"浙商租【2022】租字第（A-0053）号\",\"浙商租【2022】租字第（A-0054）号\",\"浙商租【2022】租字第（A-0055）号\",\"浙商租【2022】租字第（A-0056）号\",\"浙商租【2022】租字第（A-0057）号\",\"浙商租【2022】租字第（A-0058）号\",\"浙商租【2022】租字第（A-0059）号\",\"浙商租【2022】租字第（A-0060）号\",\"浙商租【2022】租字第（A-0061）号\",\"浙商租【2022】租字第（A-0062）号\",\"浙商租【2022】租字第（A-0063）号\",\"浙商租【2022】租字第（A-0064）号\",\"浙商租【2022】租字第（A-0065）号\",\"浙商租【2022】租字第（A-0066）号\",\"浙商租【2021】租字第（GCJX-A-0001）号\",\"浙商租【2021】租字第（GCJX-A-0002）号\",\"浙商租【2021】租字第（GCJX-A-0003）号\",\"浙商租【2021】租字第（GCJX-A-0004）号\",\"浙商租【2021】租字第（GCJX-A-0005）号\",\"浙商租【2021】租字第（GCJX-A-0006）号\",\"浙商租【2021】租字第（GCJX-C-0001）号\",\"浙商租【2022】租字第（C-0002）号\",\"浙商租【2022】租字第（C-GCJX-0003）号\",\"浙商租【2022】租字第（C-0004）号\",\"浙商租【2022】租字第（C-GCJX-0004）号\",\"浙商租【2022】租字第（C-GCJX-0005）号\",\"浙商租【2021】 管理合作字第（JH-0001）号\",\"浙商租【2021】租字第（A-0001）号\",\"浙商租【2021】租字第（A-0002）号\",\"浙商租【2021】租字第（A-0003）号\",\"浙商租【2021】租字第（A-0004）号\",\"浙商租【2021】租字第（A-0005）号\",\"浙商租【2021】租字第（A-0006）号\",\"浙商租【2021】租字第（A-0007）号\",\"浙商租【2021】租字第（A-0008）号\",\"浙商租【2021】租字第（A-0009）号\",\"浙商租【2021】租字第（A-0010）号\",\"浙商租【2021】租字第（A-0010）号\",\"浙商租【2021】租字第（A-0011）号\",\"浙商租【2021】租字第（A-0012）号\",\"浙商租【2021】租字第（A-0013）号\",\"浙商租【2021】租字第（A-0014）号\",\"浙商租【2021】租字第（A-0015）号\",\"浙商租【2021】租字第（A-0016）号\",\"浙商租【2021】租字第（A-0017）号\",\"浙商租【2021】租字第（A-0018）号\",\"浙商租【2021】租字第（A-0019）号\",\"浙商租【2021】租字第（A-0020）号\",\"浙商租【2021】租字第（A-0021）号\",\"浙商租【2021】租字第（A-0022）号\",\"浙商租【2021】租字第（A-0023）号\",\"浙商租【2021】租字第（A-0024）号\",\"浙商租【2021】租字第（A-0025）号\",\"浙商租【2021】租字第（A-0026）号\",\"浙商租【2021】租字第（A-0027）号\",\"浙商租【2021】租字第（A-0028）号\",\"浙商租【2021】租字第（A-0029）号\",\"浙商租【2021】租字第（A-0030）号\",\"浙商租【2021】租字第（A-0031）号\",\"浙商租【2021】租字第（A-0032）号\",\"浙商租【2021】租字第（A-0033）号\",\"浙商租【2021】租字第（A-0034）号\",\"浙商租【2021】租字第（A-0035）号\",\"浙商租【2021】租字第（A-0036）号\",\"浙商租【2021】租字第（A-0037）号\",\"浙商租【2021】租字第（A-0038）号\",\"浙商租【2021】租字第（A-0039）号\",\"浙商租【2021】租字第（A-0040）号\",\"浙商租【2021】租字第（A-0041）号\",\"浙商租【2021】租字第（A-0042）号\",\"浙商租【2021】租字第（A-0043）号\",\"浙商租【2021】租字第（A-0044）号\",\"浙商租【2021】租字第（A-0045）号\",\"浙商租【2021】租字第（A-0045）号\",\"浙商租【2021】租字第（A-0046）号\",\"浙商租【2021】租字第（A-0047）号\",\"浙商租【2021】租字第（A-0048）号\",\"浙商租【2021】租字第（A-0049）号\",\"浙商租【2021】租字第（A-0050）号\",\"浙商租【2021】租字第（A-0051）号\",\"浙商租【2021】租字第（A-0052）号\",\"浙商租【2021】租字第（A-0053）号\",\"浙商租【2021】租字第（A-0054）号\",\"浙商租【2021】租字第（A-0055）号\",\"浙商租【2021】租字第（A-0056）号\",\"浙商租【2021】租字第（A-0057）号\",\"浙商租【2021】租字第（A-0058）号\",\"浙商租【2021】租字第（A-0059）号\",\"浙商租【2021】租字第（A-0060）号\",\"浙商租【2021】租字第（A-0061）号\",\"浙商租【2021】租字第（A-0062）号\",\"浙商租【2021】租字第（A-0063）号\",\"浙商租【2021】租字第（A-0064）号\",\"浙商租【2021】租字第（A-0065）号\",\"浙商租【2021】租字第（A-0066）号\",\"浙商租【2021】租字第（A-0067）号\",\"浙商租【2021】租字第（A-0068）号\",\"浙商租【2021】租字第（A-0069）号\",\"浙商租【2021】租字第（A-0070）号\",\"浙商租【2021】租字第（A-0071）号\",\"浙商租【2021】租字第（A-0072）号\",\"浙商租【2021】租字第（A-0073）号\",\"浙商租【2021】租字第（A-0074）号\",\"浙商租【2021】租字第（A-0075）号\",\"浙商租【2021】租字第（A-0076）号\",\"浙商租【2021】租字第（A-0077）号\",\"浙商租【2021】租字第（JLG-0001）号\",\"浙商租【2021】租字第（JLG-0002）号\",\"浙商租【2021】租字第（JLG-0003）号\",\"浙商租【2021】租字第（JLG-0004）号\",\"浙商租【2021】租字第（JLG-0005）号\",\"浙商租【2021】租字第（JLG-0006）号\",\"浙商租【2021】租字第（JLG-0007）号\",\"浙商租【2021】租字第（JLG-0008）号\",\"浙商租【2021】租字第（JLG-0009）号\",\"浙商租【2021】租字第（JLG-0010）号\",\"浙商租【2021】租字第（JN-0001）号\",\"浙商租【2021】租字第（XB-0001）号\",\"浙商租【2021】租字第（XB-0002）号\",\"浙商租【2021】租字第（XB-0003）号\",\"浙商租【2021】租字第（CF-0001）号\",\"浙商租【2021】租字第（CF-0002）号\",\"浙商租【2021】租字第（CF-0003）号\",\"浙商租【2021】租字第（CF-0005）号\",\"浙商租【2021】租字第（HF-0001）号\",\"浙商租【2021】租字第（SZ-0001）号\",\"浙商租【2021】 保理字第（B-0001）号\",\"浙商租【2021】 保理字第（B-0002）号\",\"浙商租【2021】 保理字第（B-0003）号\",\"浙商租【2021】 保理字第（B-0004-03）号\",\"浙商租【2021】 保理字第（B-0005）号\",\"浙商租【2021】 保理字第（B-0006）号\",\"浙商租【2021】 保理字第（B-0004-04）号\",\"浙商租【2021】 保理字第（B-0007）号\",\"浙商租【2021】 保理字第（B-0008）号\",\"浙商租【2021】 保理字第（B-0009）号\",\"浙商租【2021】 保理字第（B-0010）号\",\"中拓租 【2020】租字第（A-0001）号\",\"中拓租 【2020】租字第（A-0002）号\",\"中拓租 【2020】租字第（A-0003）号\",\"中拓租 【2020】租字第（A-0004）号\",\"中拓租 【2020】租字第（A-0005）号\",\"中拓租 【2020】租字第（A-0006）号\",\"中拓租 【2020】租字第（A-0007）号\",\"中拓租【2020】租字第（A-0008）号\",\"中拓租【2020】租字第（A-0009）号\",\"中拓租【2020】租字第（A-0010）号\",\"中拓租【2020】租字第（A-0011）号\",\"中拓租【2020】租字第（A-0012）号\",\"中拓租【2020】租字第（A-0013）号\",\"中拓租【2020】租字第（A-0013）号\",\"中拓租【2020】 租字第（A-0014）号\",\"中拓租【2020】 租字第（A-0015）号\",\"中拓租【2020】 租字第（A-0016）号\",\"中拓租【2020】 租字第（A-0017）号\",\"中拓租【2020】 租字第（A-0018）号\",\"中拓租【2020】 租字第（A-0019）号\",\"中拓租【2020】 租字第（A-0020）号\",\"中拓租【2020】 租字第（A-0021）号\",\"中拓租【2020】 租字第（A-0022）号\",\"中拓租【2020】 租字第（A-0023）号\",\"中拓租【2020】 租字第（A-0024）号\",\"中拓租【2020】 租字第（A-0025）号\",\"中拓租【2020】 租字第（A-0026）号\",\"中拓租【2020】 租字第（A-0027）号\",\"中拓租【2020】 租字第（A-0028）号\",\"中拓租【2020】 租字第（A-0029）号\",\"中拓租【2020】 租字第（A-0030）号\",\"中拓租【2020】 租字第（A-0031）号\",\"中拓租【2020】 租字第（A-0034）号\",\"中拓保【2020】保字第（B-0001）号\",\"中拓保【2020】保字第（B-0002）号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0003)号\",\"中拓租【2020】保理字第（B-0004）号\",\"中拓租【2020】保理字第（B-0004）号\",\"中拓租【2020】保理字第（B-0004）号\",\"中拓租【2020】保理字第（B-0005）号\",\"中拓租【2020】保理字第（B-0006）号\",\"中拓租【2020】保理字第（B-0007）号\",\"中拓租【2020】保理字第（B-0008）号\",\"中拓租【2019】租字第（WCWC-0001）号\",\"中拓租【2019】租字第（WCWC-0002）号\",\"中拓租【2019】租字第（WCWC-0003）号\",\"中拓租【2019】租字第（WCWC-0004）号\",\"中拓租【2019】租字第（WCWC-0005）号\",\"中拓租【2019】租字第（WCWC-0006）号\",\"中拓租【2019】租字第（WCWC-0007）号\",\"中拓租【2019】租字第（WCWC-0008）号\",\"中拓租【2019】租字第（WCWC-0009）号\",\"中拓租【2019】租字第（WCWC-0010）号\",\"中拓租【2019】租字第（WCWC-0011）号\",\"中拓租【2019】租字第（WCWC-0012）号\",\"中拓租【2019】租字第（WCWC-0013）号\",\"中拓租【2019】租字第（WCWC-0014）号\",\"中拓租【2019】租字第（WCWC-0016）号\",\"中拓租【2019】租字第（WCWC-0018）号\",\"中拓租【2019】租字第（WCWC-0019）号\",\"中拓租【2019】租字第（WCWC-0020）号\",\"中拓租【2019】租字第（WCWC-0021）号\",\"中拓租【2019】租字第（WCWC-0022）号\",\"中拓租【2019】租字第（WCWC-0023）号\",\"中拓租【2019】租字第（WCWC-0024）号\",\"中拓租【2019】租字第（WCWC-0025）号\",\"中拓租【2019】租字第（WCWC-0026）号\",\"中拓租[2016]租字第（A-0001）号\",\"中拓租【2016】租字第（A-0002）号\",\"中拓租【2016】租字第（A-0003）号\",\"中拓租【2017】租字第（A-0001）号\",\"中拓租【2017】租字第（A-0002）号\",\"中拓租【2017】租字第（A-0004）号\",\"中拓租【2017】租字第（A-0005）号、\\n中拓租【2017】租字第（A-0006）号\",\"中拓租【2017】租字第（A-0007）号\",\"中拓租【2017】租字第（A-0008）号\",\"中拓租【2017】租字第（A-0009）号\",\"中拓租【2017】租字第（A-0010）号\",\"中拓租【2017】租字第（A-0011）号\",\"中拓租【2018】租字第（A-0001）号\",\"中拓租【2018】租字第（A-0002）号\",\"中拓租【2018】租字第（A-0003）号\",\"中拓租【2018】租字第（A-0004）号\",\"中拓租【2018】租字第（A-0005）号\",\"中拓租【2018】租字第（A-0006）号\",\"中拓租【2018】租字第（A-0007）号\",\"中拓租【2018】租字第（A-0008）号\",\"中拓租【2018】租字第（A-0009）号\",\"中拓租【2018】租字第（A-0010）号\",\"中拓租【2018】租字第（A-0011）号\",\"中拓租【2018】租字第（A-0012）号\",\"中拓租【2019】租字第（A-0001）号\",\"中拓租【2019】租字第（A-0002）号\",\"中拓租【2019】租字第（A-0003）号\",\"中拓租【2019】租字第（A-0004）号\",\"中拓租【2019】租字第（A-0005）号\",\"中拓租【2019】租字第（A-0006）号\",\"中拓租【2019】租字第（A-0008）号\",\"中拓租【2019】租字第（A-0009）号\",\"中拓租【2019】租字第（A-0010）号\",\"中拓租【2019】租字第（A-0011）号\",\"中拓租【2019】租字第（A-0012）号\",\"中拓租【2019】租字第（A-0013）号\",\"中拓租【2019】租字第（A-0014）号\",\"中拓租【2019】租字第（A-0015）号\",\"中拓租【2019】租字第（A-0016）号\",\"中拓保【2016】保字第（B-0001）号\",\"中拓保【2017】保理字第（B-0001)号\",\"中拓保【2017】保理字第（B-0002)号\",\"中拓保【2017】保理字第（B-0003)号\",\"中拓保【2018】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0001)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0002)号\",\"中拓租【2019】保理字第（B-0003)号\",\"中拓租【2019】保理字第（B-0004)号\",\"中拓租【2019】保理字第（B-0005)号\",\"中拓租【2019】保理字第（B-0006)号\",\"中拓租【2019】保理字第（B-0007)号\",\"中拓租【2019】保理字第（B-0008)号\",\"中拓租【2019】保理字第（B-0009)号\",\"中拓租【2019】保理字第（B-0009)号\",\"中拓租【2019】保理字第（B-0009)号\"]";
        List<String> tzExcelDataList = JSONArray.parseArray(tzExcelData, String.class);
        ExcelReader excelReader = ExcelUtil.getReader("/Users/wang/Desktop/合同模块数据整理_线上.xlsx");
        List<List<Object>> readAll = excelReader.read();
        Set<String> rzyDataSet = new HashSet<>();
        for (List<Object> dataList : readAll) {
            if (dataList.size() >= 3
                    && dataList.get(2) instanceof String
                    && !"合同编号".equals(dataList.get(0))
                    && ((String) dataList.get(0)).contains("号")) {
                rzyDataSet.add((String) dataList.get(0));
            }
        }
        tzExcelDataList.removeIf(rzyDataSet::contains);
        System.out.println("融租易未包含数据:" + JSON.toJSONString(tzExcelDataList));
    }

    public static void main(String[] args) throws Exception {
//        ContractExtractor contractExtractor = new ContractExtractor();
//        contractExtractor.extractAll();
//        ContractExtractor.extractFileName();
        downloadFile();
    }

    /**
     * 数据初始化
     * @throws Exception
     */
    @Test
    public void initData() throws Exception {
        // 找出当前所有用户 拿到id
        UserQuery userQuery = new UserQuery();
        userQuery.setPageSize(Integer.MAX_VALUE);
        Map<String, Long> userMap = userServiceAPI.queryUserSys(userQuery).getContents().stream().collect(Collectors.toMap(UserVO::getUserName, UserVO::getId));
        Map<Long, Long> userOrgMap = userMap.values().stream().collect(Collectors.toMap(id -> id, id -> sysUserService.getSpecificUserDeptList(id).get(0).getId()));
        Map<String, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                        .like(Client::getClientName, "%（汉得导入勿用）%"))
                .stream().map(c -> {
                    c.setClientName(c.getClientName().replace("（汉得导入勿用）", ""));
                    return c;
                }).collect(Collectors.toMap(Client::getClientName, c -> c));
        Map<Long, Client> clientIdMap = clientMap.values().stream().collect(Collectors.toMap(Client::getId, c -> c));

        JSONArray handDataArray = JSONArray.parseArray(IoUtil.read(new ClassPathResource("init/合同模块数据整理json_线上_20220818_1633.json").getInputStream(), Charset.defaultCharset()));
        // 数据
        List<ContractBaseInfo> contractBaseInfoList = new ArrayList<>();
        Map<String, List<ContractAccount>> contractAccountListMap = new HashMap<>();
        Map<String, List<ContractTenantry>> contractTenantryListMap = new HashMap<>();
        Map<String, List<ContractGuarantor>> contractGuarantorListMap = new HashMap<>();

        List<String> errContractNumberList = new ArrayList<>();
        for (int i = 0; i < handDataArray.size(); i++) {
            JSONObject handData = handDataArray.getJSONObject(i);
            ContractBaseInfo contractBaseInfo = new ContractBaseInfo();
            contractBaseInfo.setContractCode(handData.getString("contract_number"));
            contractBaseInfo.setProjName(handData.getString("project_name"));
            contractBaseInfo.setLeaseType("售后回租".equals(handData.getString("business_type_n")) ? LeaseType.hui_zu.name() : LeaseType.zhi_zu.name());
            contractBaseInfo.setProjCode(handData.getString("project_number"));

            // 获取人 主办
            Long projSponsorUserId = userMap.getOrDefault(handData.getString("employee_id_n"), 3L);
            contractBaseInfo.setProjSponsorUserId(projSponsorUserId);
            contractBaseInfo.setBizDeptId(userOrgMap.get(projSponsorUserId));
            contractBaseInfo.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(contractBaseInfo.getBizDeptId(), JobEnum.businesshead.name()));
            contractBaseInfo.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(contractBaseInfo.getBizDeptId(), JobEnum.leaderincharge.name()));
            // 协办 assist_employee_id_n、assist_employee_id_a_n
            List<Long> cosUserList = new ArrayList<>();
            Long cosUser1 = Optional.ofNullable(handData.getString("assist_employee_id_n")).map(userMap::get).orElse(null);
            Long cosUser2 = Optional.ofNullable(handData.getString("assist_employee_id_a_n")).map(userMap::get).orElse(null);
            if (cosUser1 != null) {
                cosUserList.add(cosUser1);
            }
            if (cosUser2 != null) {
                cosUserList.add(cosUser2);
            }
            contractBaseInfo.setProjCosponsorUserIds(JSON.toJSONString(cosUserList));

            // 主承租人
            Client mainClient = clientMap.get(handData.getString("bp_id_tenant_n").replace("\t", "").trim());
            if (mainClient == null) {
                errContractNumberList.add(contractBaseInfo.getContractCode());
                continue;
            }
            contractBaseInfo.setClientId(mainClient.getId());
            contractBaseInfo.setContractStatus(ContractStatus.NEW.name());

            // 处理子表逻辑
            // 收款账号
            JSONArray handAccountArray = handData.getJSONArray("bankArray");
            contractAccountListMap.put(contractBaseInfo.getContractCode(), handAccountArray.stream()
                    .map(j -> (JSONObject)j)
                    .map(j -> {
                        ContractAccount contractAccount = new ContractAccount();
                        contractAccount.setAccountAddress(j.getString("bank_full_name"));
                        contractAccount.setAccountNum(j.getString("bank_account_num"));
                        contractAccount.setAccountName(j.getString("bank_account_name"));
                        return contractAccount;
                    }).collect(Collectors.toList())
            );

            // 承租人 = 主承租人 + 客户数组（bpArray）（主承租人 + 联合承租人）
            JSONArray handBpArray = handData.getJSONArray("bpArray");
            contractTenantryListMap.put(contractBaseInfo.getContractCode(), handBpArray.stream()
                    .map(j -> (JSONObject)j)
                    .map(j -> {
                        if (!"承租人".equals(j.getString("bp_category_n")) && !"联合承租人".equals(j.getString("bp_category_n"))) {
                            // 非承租人 返回空 下个阶段剔除
                            return null;
                        }
                        Client client = clientMap.get(j.getString("bp_name_n"));
                        // 如果客户不存在 先跳过
                        if (client == null) {
                            return null;
                        }
                        ContractTenantry contractTenantry = new ContractTenantry();
                        contractTenantry.setLesseeId(client.getId());
                        contractTenantry.setLesseeType("承租人".equals(j.getString("bp_category_n")) ? LesseeTypeEnum.MAIN_LESSSEE.name() : LesseeTypeEnum.JOINT_LESSEE.name());
                        contractTenantry.setLesseeName("（汉得导入勿用）" + client.getClientName());
                        contractTenantry.setIsReport("是".equals(j.getString("credit_reporting_flag_n")) ? 1 : 0);
                        return contractTenantry;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            if (contractTenantryListMap.get(contractBaseInfo.getContractCode()).stream()
                    .filter(b -> contractBaseInfo.getClientId().equals(b.getLesseeId()))
                    .count() == 0) {
                // 主承租人没维护 维护下
                ContractTenantry contractTenantry = new ContractTenantry();
                contractTenantry.setLesseeId(contractBaseInfo.getClientId());
                contractTenantry.setLesseeType(LesseeTypeEnum.MAIN_LESSSEE.name());
                contractTenantry.setLesseeName("（汉得导入勿用）" + clientIdMap.get(contractBaseInfo.getClientId()).getClientName());
                contractTenantry.setIsReport(0);
                contractTenantryListMap.get(contractBaseInfo.getContractCode()).add(0, contractTenantry);
            }

            // 担保措施 = 客户数组（bpArray）担保人 + 担保措施（guaranteeArray）
            List<ContractGuarantor> contractGuarantorList = new ArrayList<>();
            contractGuarantorListMap.put(contractBaseInfo.getContractCode(), contractGuarantorList);
            JSONArray handGuaranteeArray = handData.getJSONArray("guaranteeArray");

            Set<String> guaranteeNameExistSet = new HashSet<>();
            contractGuarantorList.addAll(handGuaranteeArray.stream()
                    .map(j -> (JSONObject)j)
                    .map(j -> {
                        ContractGuarantor contractGuarantor = new ContractGuarantor();
                        Client client = clientMap.get(j.getString("bp_id_n"));
                        // 如果客户不存在 先跳过
                        if (client == null) {
                            return null;
                        }
                        guaranteeNameExistSet.add(client.getClientName());
                        contractGuarantor.setGuarantorType(client.getClientType());
                        contractGuarantor.setGuarantorIds(JSON.toJSONString(Arrays.asList(client.getId())));
                        contractGuarantor.setGuaranteeMethod("连带责任担保".equals(j.getString("guarantee_type_n")) ? GuaranteeMethodEnum.JOINT_RESPONSIBILITY.name() : null);
                        contractGuarantor.setIsReport("是".equals(j.getString("credit_reporting_flag_n")) ? 1 : 0);
                        return contractGuarantor;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            contractGuarantorList.addAll(handBpArray.stream()
                    .map(j -> (JSONObject)j)
                    .map(j -> {
                        if (!"担保人".equals(j.getString("bp_category_n"))) {
                            // 非担保人 返回空 下个阶段剔除
                            return null;
                        }
                        if (guaranteeNameExistSet.contains(j.getString("bp_name_n"))) {
                            // 担保措施模块维护过了 不再维护
                            return null;
                        }
                        Client client = clientMap.get(j.getString("bp_id_n"));
                        // 如果客户不存在 先跳过
                        if (client == null) {
                            return null;
                        }
                        ContractGuarantor contractGuarantor = new ContractGuarantor();
                        contractGuarantor.setGuarantorType(client.getClientType());
                        contractGuarantor.setGuarantorIds(JSON.toJSONString(Arrays.asList(client.getId())));
                        return contractGuarantor;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            contractBaseInfoList.add(contractBaseInfo);
        }

        // 事务
        transactionTemplate.execute(status -> {
            try {
                for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
                    contractBaseInfo.setContractCode("（汉得导入勿用）" + contractBaseInfo.getContractCode());
                    log.info("导入合同主表：{}", JSON.toJSONString(contractBaseInfo));
                    contractBaseInfoMapper.insert(contractBaseInfo);
                }
                Map<String, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoList.stream().collect(Collectors.toMap(c -> c.getContractCode().replace("（汉得导入勿用）", ""), c -> c));

                contractAccountListMap.entrySet().forEach(e -> {
                    Long contractId = contractBaseInfoMap.get(e.getKey()).getId();
                    e.getValue().forEach(a -> {
                        a.setContractId(contractId);
                        log.info("导入合同收款账号：{}", JSON.toJSONString(a));
                        contractAccountMapper.insert(a);
                    });
                });

                contractTenantryListMap.entrySet().forEach(e -> {
                    Long contractId = contractBaseInfoMap.get(e.getKey()).getId();
                    e.getValue().forEach(a -> {
                        a.setContractId(contractId);
                        log.info("导入合同承租人：{}", JSON.toJSONString(a));
                        contractTenantryMapper.insert(a);
                    });
                });

                contractGuarantorListMap.entrySet().forEach(e -> {
                    Long contractId = contractBaseInfoMap.get(e.getKey()).getId();
                    e.getValue().forEach(a -> {
                        a.setContractId(contractId);
                        log.info("导入合同担保措施：{}", JSON.toJSONString(a));
                        contractGuarantorMapper.insert(a);
                    });
                });


                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }
        });


        System.out.println(String.format("导入合同数量:%s", contractBaseInfoList.size()));
        System.out.println("导入合同错误：" + JSON.toJSONString(errContractNumberList));
    }

    @Test
    public void clearData() {
        List<ContractBaseInfo> importContractList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .like(ContractBaseInfo::getContractCode, "%（汉得导入勿用）%"));
        // 事务
        transactionTemplate.execute(status -> {
            try {
                List<Long> contractIdList = importContractList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                contractBaseInfoMapper.delete(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getId, contractIdList));
                contractAccountMapper.delete(Wrappers.<ContractAccount>lambdaQuery()
                        .in(ContractAccount::getContractId, contractIdList));
                contractTenantryMapper.delete(Wrappers.<ContractTenantry>lambdaQuery()
                        .in(ContractTenantry::getContractId, contractIdList));
                contractGuarantorMapper.delete(Wrappers.<ContractGuarantor>lambdaQuery()
                        .in(ContractGuarantor::getContractId, contractIdList));
                contractLeasePriceMapper.delete(Wrappers.<ContractLeasePrice>lambdaQuery()
                        .in(ContractLeasePrice::getContractId, contractIdList));
                contractRentActualMapper.delete(Wrappers.<ContractRentActual>lambdaQuery()
                        .in(ContractRentActual::getContractId, contractIdList));
                contractReceiptMapper.delete(Wrappers.<ContractReceipt>lambdaQuery()
                        .in(ContractReceipt::getContractId, contractIdList));
                List<Long> paymentIdList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .in(PaymentBaseInfo::getContractId, contractIdList)).stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
                paymentBaseInfoMapper.delete(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getId, paymentIdList));
                paymentQuestionnaireAnswerMapper.delete(Wrappers.<PaymentQuestionnaireAnswer>lambdaQuery()
                        .in(PaymentQuestionnaireAnswer::getPaymentId, paymentIdList));
                paymentActualDetailMapper.delete(Wrappers.<PaymentActualDetail>lambdaQuery().in(PaymentActualDetail::getPaymentId, paymentIdList));
                collectionBaseInfoMapper.delete(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getContractId, contractIdList));


                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }});
    }

    /**
     * 从excel 中 导入合同 现金流 报价方案等信息
     */
    @Test
    public void initDataFromExcel() {
        String fileName = "/Users/wang/Desktop/财务台账-冒烟数据-合同数据初始化模板.xlsx";
        File file = new File(fileName);
        ExcelReader excelReader = ExcelUtil.getReader(file);
        Set<String> sheetNameSheet = new HashSet<>(excelReader.getSheetNames());
        excelReader.setSheet("业务合同情况汇总表");
        List<List<Object>> contractExcelList = excelReader.read();
        // 事务
        transactionTemplate.execute(status -> {
            try {
                for (int i = 2; i < contractExcelList.size(); i++) {
                    List<Object> dataList = contractExcelList.get(i);
                    String handContractNumber = (String) dataList.get(5);
                    if (handContractNumber == null || !handContractNumber.contains("号")) {
                        continue;
                    }
                    ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery()
                            .eq(ContractBaseInfo::getContractCode, "（汉得导入勿用）" + handContractNumber)
                            .last("LIMIT 1")
                    );
                    if (Objects.isNull(contractBaseInfo)) {
                        log.error("合同基表数据不存在:{}", handContractNumber);
                        continue;
                    }
                    String actualContractNumber = (String) dataList.get(8);
                    String detailSheetName = (String) dataList.get(9);
                    if (!sheetNameSheet.contains(detailSheetName)) {
                        log.error("sheet不存在:{}", detailSheetName);
                        continue;
                    }
                    excelReader.setSheet(detailSheetName);
                    List<List<Object>> detailSheetDataList = excelReader.read();

                    // 报价方案
                    ContractLeasePrice contractLeasePrice = new ContractLeasePrice();
                    contractLeasePrice.setContractId(contractBaseInfo.getId());
                    contractLeasePrice.setApplyCreditAmount(Optional.ofNullable(detailSheetDataList.get(0).get(12)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setLeaseMonthCount(Optional.ofNullable(detailSheetDataList.get(0).get(14)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(12L)).intValue()).orElse(null));
                    contractLeasePrice.setConsultingFee(Optional.ofNullable(detailSheetDataList.get(0).get(16)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setDownPayment(Optional.ofNullable(detailSheetDataList.get(2).get(12)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setLeaseRatePercent(Optional.ofNullable(detailSheetDataList.get(1).get(14)).map(d -> extractNumber(d, k -> k.replace("%", ""), k -> k * 100)).map(b -> b.multiply(new BigDecimal(10000L)).intValue()).orElse(null));
                    contractLeasePrice.setEarnestMoney(Optional.ofNullable(detailSheetDataList.get(1).get(16)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setNominalPrice(Optional.ofNullable(detailSheetDataList.get(2).get(16)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    contractLeasePrice.setIrrPercent(Optional.ofNullable(detailSheetDataList.get(2).get(18)).map(d -> extractNumber(d, k -> k.replace("%", ""), k -> k * 100)).map(b -> b.multiply(new BigDecimal(10000L)).intValue()).orElse(null));
                    contractLeasePrice.setDefaultInterestRate(Optional.ofNullable(detailSheetDataList.get(3).get(16)).map(d -> extractNumber(d, k -> k.replace("%", ""), k -> k * 100)).map(b -> b.multiply(new BigDecimal(10000L)).intValue()).orElse(null));

                    log.info("新增报价方案:{}", JSON.toJSONString(contractLeasePrice));
                    contractLeasePriceMapper.insert(contractLeasePrice);

                    // 付款主表
                    PaymentBaseInfo paymentBaseInfo = new PaymentBaseInfo();
                    paymentBaseInfo.setContractId(contractBaseInfo.getId());
                    paymentBaseInfo.setClientId(contractBaseInfo.getClientId());
                    paymentBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                    paymentBaseInfo.setConProjectType(contractBaseInfo.getProjectType());
                    paymentBaseInfo.setConBizDeptId(contractBaseInfo.getBizDeptId());
                    paymentBaseInfo.setApplyPaymentDate(Optional.ofNullable(detailSheetDataList.get(5).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd")).orElse(null));
                    paymentBaseInfo.setEarnestMoney(Optional.ofNullable(detailSheetDataList.get(1).get(16)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setConsultingFee(Optional.ofNullable(detailSheetDataList.get(0).get(16)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setDownPayment(Optional.ofNullable(detailSheetDataList.get(2).get(12)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    paymentBaseInfo.setNominalPrice(Optional.ofNullable(detailSheetDataList.get(2).get(16)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));

                    Integer paymentCount = paymentBaseInfoMapper.selectCount(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getContractId, paymentBaseInfo.getContractId()));
                    if (paymentCount == null) {
                        paymentCount = 0;
                    }
                    paymentBaseInfo.setPaymentCode(PaymentBaseInfoService.generatePaymentCode(paymentBaseInfo.getContractCode()
                            .replace("（汉得导入勿用）", "")
                            .replace("（", "(")
                            .replace("）", ")"), paymentCount + 1));
                    log.info("新增付款主表数据:{}", JSON.toJSONString(paymentBaseInfo));
                    paymentBaseInfoMapper.insert(paymentBaseInfo);
                    answerService.create(paymentBaseInfo.getId());

                    // 付款记录
                    PaymentActualDetail paymentActualDetail = new PaymentActualDetail();
                    paymentActualDetail.setContractId(contractBaseInfo.getId());
                    paymentActualDetail.setPaymentId(paymentBaseInfo.getId());
                    Integer actualDetailCount = paymentActualDetailMapper.selectCount(Wrappers.<PaymentActualDetail>lambdaQuery()
                            .eq(PaymentActualDetail::getPaymentId, paymentBaseInfo.getId()));
                    if (actualDetailCount == null) {
                        actualDetailCount = 0;
                    }
                    paymentActualDetail.setSeqCode(String.format("%02d", actualDetailCount + 1));
                    paymentActualDetail.setPaidInAmount(Optional.ofNullable(detailSheetDataList.get(0).get(12)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                    log.info("新增付款记录:{}", JSON.toJSONString(paymentActualDetail));
                    paymentActualDetailMapper.insert(paymentActualDetail);

                    // 收款主表
                    List<CollectionBaseInfo> collectionBaseInfoList = new ArrayList<>();
                    for (int rentRow = 5;; rentRow++) {
                        if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                            break;
                        }
                        // TODO 第0期的特殊处理、第一期和最后一期的特殊处理
                        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
                        collectionBaseInfo.setContractId(contractBaseInfo.getId());
                        collectionBaseInfo.setContractCode(contractBaseInfo.getContractCode());
                        collectionBaseInfo.setClientId(contractBaseInfo.getClientId());
                        // TODO code待生成
                        collectionBaseInfo.setCode(null);
                        collectionBaseInfo.setPaymentId(paymentBaseInfo.getId());
                        collectionBaseInfo.setPaymentCode(paymentBaseInfo.getPaymentCode());

                        collectionBaseInfo.setPhase(Optional.ofNullable(detailSheetDataList.get(rentRow).get(11)).map(d -> extractNumber(d, null, null)).map(b -> b.intValue()).orElse(null));
                        collectionBaseInfo.setPlanCollectionDate(Optional.ofNullable(detailSheetDataList.get(rentRow).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        collectionBaseInfo.setCashFlowAmount(Optional.ofNullable(detailSheetDataList.get(rentRow).get(13)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(14)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(15)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setCashFlowItem(CashFlowItemEnum.RENT.name());
                        // TODO 待确认 现金流金额和租金的区别
                        //collectionBaseInfo.setCashFlowAmount(Optional.ofNullable(detailSheetDataList.get(rentRow).get(17)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        collectionBaseInfo.setPenaltyInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(31)).map(d -> "-".equals(d) ? null : d).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));


                        collectionBaseInfoList.add(collectionBaseInfo);
                    }
                    for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                        log.info("新增收款主表数据:{}", JSON.toJSONString(collectionBaseInfo));
                        collectionBaseInfoMapper.insert(collectionBaseInfo);
                    }

                    // 收款记录

                    // 合同-借据
                    ContractReceipt contractReceipt = new ContractReceipt();
                    contractReceipt.setContractId(contractBaseInfo.getId());
                    contractReceipt.setPaymentApplyCode(paymentBaseInfo.getPaymentCode());
                    log.info("新增合同借据:{}", JSON.toJSONString(contractReceipt));
                    contractReceiptMapper.insert(contractReceipt);

                    // 实际租金
                    List<ContractRentActual> rentActualList = new ArrayList<>();
                    for (int rentRow = 5;; rentRow++) {
                        if ("合计".equals(detailSheetDataList.get(rentRow).get(11))) {
                            break;
                        }
                        ContractRentActual rentActual = new ContractRentActual();
                        // 借据id
                        rentActual.setReceiptId(contractReceipt.getId());

                        rentActual.setContractId(contractBaseInfo.getId());
                        rentActual.setCashFlowPhase(Optional.ofNullable(detailSheetDataList.get(rentRow).get(11)).map(d -> extractNumber(d, null, null)).map(b -> b.intValue()).orElse(null));
                        rentActual.setCashFlowDate(Optional.ofNullable(detailSheetDataList.get(rentRow).get(12)).map(d -> ((DateTime)d).toString("yyyy-MM-dd")).map(d -> LocalDateTimeUtil.parseDate(d, "yyyy-MM-dd")).orElse(null));
                        rentActual.setRent(Optional.ofNullable(detailSheetDataList.get(rentRow).get(13)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setInterest(Optional.ofNullable(detailSheetDataList.get(rentRow).get(14)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(15)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));
                        rentActual.setRemainingPrincipal(Optional.ofNullable(detailSheetDataList.get(rentRow).get(16)).map(d -> extractNumber(d, null, null)).map(b -> b.multiply(new BigDecimal(10000L)).longValue()).orElse(null));

                        rentActualList.add(rentActual);
                    }

                    for (ContractRentActual rentActual : rentActualList) {
                        log.info("新增实际租金表记录:{}", JSON.toJSONString(rentActual));
                        contractRentActualMapper.insert(rentActual);
                    }

                    log.info("此次数据，报价方案:{}，实际租金表:{}", JSON.toJSONString(contractLeasePrice), JSON.toJSONString(rentActualList));
                    // 只测试一个
                    // return true;
                }
//                if (1 == 1) {
//                    throw new RuntimeException();
//                }
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("事务执行出错", e);
                throw e;
            }});
    }

    public static void downloadFile() throws Exception {
        String rootDir = "/Users/wang/Desktop/汉得文件/合同模块";
        String fileDataJson = IoUtil.read(new FileInputStream("/Users/wang/Desktop/合同文件整理_线上_20221124.json"), Charset.defaultCharset());
        JSONObject fileDataObj = JSONObject.parseObject(fileDataJson);
        JSONArray errorArray = new JSONArray();
        for (String contractNumer : fileDataObj.keySet()) {
            JSONObject fileObj = fileDataObj.getJSONObject(contractNumer);
            for (String fileType : fileObj.keySet()) {
                JSONArray fileArray = fileObj.getJSONArray(fileType);
                for (int i = 0; i < fileArray.size(); i++) {
                    JSONObject acObj = fileArray.getJSONObject(i);
                    try {
                        HttpUtil.downloadFile("http://10.100.222.10/core/fnd/attachment/download?attachment_id=" + acObj.getString("attachment_id") + "&access_id=undefined&table_name=undefined&table_pk_value=undefined&table_pk_value=undefined&document_id=undefined&document_category=undefined", rootDir + "/" + contractNumer + "/" + fileType + "/" + acObj.getString("file_name"));
                        System.out.println("下载成功:" + acObj.getString("file_name"));
                    } catch (Exception e) {
                        log.error("下载失败:{}", acObj.getString("file_name"), e);
                        errorArray.add(acObj);
                        acObj.put("contract_number", contractNumer);
                        acObj.put("fileType", fileType);
                    }
                }
            }
        }
        System.out.println("下载失败数据:" + errorArray.toJSONString());
    }

    /**
     * 数字提取
     * @return
     */
    public static BigDecimal extractNumber(Object data, Function<String, String> stringHandler, Function<Double, Double> doubleHandler) {
        if (Objects.isNull(data)) {
            return null;
        }
        if (data instanceof String) {
            if (StringUtils.isBlank((String)data)) {
               return null;
            }
            if (Objects.isNull(stringHandler)) {
                return new BigDecimal(((String)data));
            } else {
                return new BigDecimal((stringHandler.apply((String)data)));
            }
        } else if (data instanceof Double) {
            if (Objects.isNull(doubleHandler)) {
                return new BigDecimal(((Double)data));
            } else {
                return new BigDecimal((doubleHandler.apply((Double) data)));
            }
        } else if (data instanceof Float) {
            return new BigDecimal((Float)data);
        } else if (data instanceof Integer) {
            return new BigDecimal((Integer)data);
        } else if (data instanceof Long) {
            return new BigDecimal((Long)data);
        }
        return null;
    }


}
