package cn.zswltech.mithras.application.orchestration.facade.document.migrate;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.zswltech.mithras.document.application.MigrateFileApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.document.enums.MaterialsType;
import cn.zswltech.mithras.document.enums.NormalMaterialsType;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.zswltech.mithras.foundation.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * 调用接口后 迁移文件
 *
 * @author wangchuanhao
 * @date 2022/11/26 1:47 PM
 */
@Service
@Slf4j
public class MigrateFileFacade implements MigrateFileApplicationService {

    /**
     * 文件根路径
     */
//    private static final String DATA_ROOT = "/Users/wang/Desktop/上传测试";
    private static final String DATA_ROOT = "/data/mithras/file_migrate";
    private static final String CONFIG_DIR = DATA_ROOT + "/config";
    private static final String CLIENT_CONFIG_FILE = CONFIG_DIR + "/client.config";
    private static final String CONTRACT_CONFIG_FILE = CONFIG_DIR + "/contract.config";
    private static final String CLIENT_FILE_DIR = DATA_ROOT + "/client";
    private static final String CONTRACT_FILE_DIR = DATA_ROOT + "/contract";

    /**
     * 锁
     */
    private static final String lockKeyPre = "mithras:migrate_file_lock:";

    /**
     * 合同编号映射
     */
    private static Map<String, String> contractCodeFixMap = new HashMap<>();

    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    static {
        contractCodeFixMap.put("浙商租【2021】租字第(A-0043)号", "浙商租【2021】租字第(A-0061)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0009)号", "中拓租【2021】租字第(A-0010)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0062)号", "浙商租【2021】租字第(A-0067)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0020)号", "浙商租【2021】租字第(A-0033)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0088)号", "浙商租【2022】租字第(A-0042)号");
        contractCodeFixMap.put("浙商租【2022】租字第(C-0027)号", "浙商租【2022】租字第(C-0002)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0047)号", "浙商租【2021】租字第(A-0059)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0005)号", "浙商租【2021】租字第(JLG-0002)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0091)号", "浙商租【2022】租字第(A-0044)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0050)号", "浙商租【2021】租字第(A-0063)号");
        contractCodeFixMap.put("浙商租【2022】租字第(C-0030)号", "浙商租【2022】租字第(C-GCJX-0004)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0059)号", "浙商租【2021】租字第(A-0068)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0017)号", "浙商租【2021】租字第(HF-0001)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0051)号", "浙商租【2021】租字第(A-0064)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0099)号", "浙商租【2022】租字第(A-0047)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0076)号", "浙商租【2022】租字第(A-0018)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0032)号", "浙商租【2021】租字第(A-0045)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0017)号", "浙商租【2021】租字第(A-0006)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0013)号", "浙商租【2021】租字第(JLG-0005)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0006)号", "浙商租【2021】租字第(JLG-0003)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0027)号", "浙商租【2021】租字第(A-0041)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0087)号", "浙商租【2022】租字第(A-0037)号");
        contractCodeFixMap.put("浙商租【2022】租字第(C-0024)号", "浙商租【2022】租字第(GCJX-C-0001)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0042)号", "浙商租【2021】租字第(A-0056)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0046)号", "浙商租【2021】租字第(A-0058)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0090)号", "浙商租【2022】租字第(A-0043)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0071)号", "浙商租【2022】租字第(A-0015)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0094)号", "浙商租【2022】租字第(A-0038)号");
        contractCodeFixMap.put("浙商租【2022】租字第(C-0031)号", "浙商租【2022】租字第(C-GCJX-0005)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0039)号", "浙商租【2021】租字第(A-0052)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0018)号", "浙商租【2021】租字第(JLG-0006)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0075)号", "浙商租【2022】租字第(A-0017)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0054)号", "浙商租【2021】租字第(A-0062)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0098)号", "浙商租【2021】租字第(A-0039)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0079)号", "浙商租【2022】租字第(A-0023)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0035)号", "浙商租【2021】租字第(A-0049)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0061)号", "浙商租【2021】租字第(A-0075)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0082)号", "浙商租【2022】租字第(A-0020)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0112)号", "浙商租【2022】租字第(A-0061)号");
        contractCodeFixMap.put("浙商租【2022】租字第(C-0029)号", "浙商租【2022】租字第(C-0004)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0007)号", "浙商租【2021】租字第(JLG-0004)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0049)号", "浙商租【2021】租字第(A-0054)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0003)号", "浙商租【2021】租字第(A-0024)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0026)号", "浙商租【2021】租字第(A-0025)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0022)号", "浙商租【2021】租字第(JLG-0008)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0041)号", "浙商租【2021】租字第(A-0028)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0086)号", "浙商租【2022】租字第(A-0036)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0022)号", "浙商租【2021】租字第(A-0027)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0007)号", "浙商租【2021】租字第(A-0026)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0045)号", "浙商租【2021】租字第(A-0057)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0070)号", "浙商租【2022】租字第(A-0009)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0093)号", "浙商租【2022】租字第(A-0046)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0100)号", "浙商租【2022】租字第(A-0048)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0104)号", "浙商租【2022】租字第(A-0053)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0108)号", "浙商租【2022】租字第(A-0062)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0038)号", "浙商租【2021】租字第(A-0051)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0097)号", "浙商租【2022】租字第(A-0051)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0074)号", "浙商租【2022】租字第(A-0014)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0011)号", "浙商租【2021】租字第(XB-0002)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0019)号", "浙商租【2021】租字第(A-0034)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0053)号", "浙商租【2021】租字第(A-0060)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0078)号", "浙商租【2022】租字第(A-0016)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0015)号", "浙商租【2021】租字第(XB-0003)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0034)号", "浙商租【2021】租字第(A-0048)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0057)号", "浙商租【2021】租字第(A-0071)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0081)号", "浙商租【2022】租字第(A-0025)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0060)号", "浙商租【2021】租字第(A-0074)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0111)号", "浙商租【2022】租字第(A-0066)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0048)号", "浙商租【2021】租字第(A-0053)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0002)号", "浙商租【2021】租字第(A-0011)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0008)号", "浙商租【2021】租字第(XB-0001)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0040)号", "浙商租【2021】租字第(A-0029)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0021)号", "浙商租【2021】租字第(A-0035)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0063)号", "浙商租【2021】租字第(A-0077)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0089)号", "浙商租【2022】租字第(A-0039)号");
        contractCodeFixMap.put("浙商租【2022】租字第(C-0026)号", "浙商租【2022】租字第(C-GCJX-0003)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0006)号", "浙商租【2021】租字第(A-0012)号");
        contractCodeFixMap.put("浙商租【2021】租字第(C-0004)号", "浙商租【2021】租字第(JN-0001)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0025)号", "浙商租【2021】租字第(A-0038)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0103)号", "浙商租【2022】租字第(A-0052)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0092)号", "浙商租【2022】租字第(A-0045)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0014)号", "浙商租【2021】租字第(A-0008)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0037)号", "浙商租【2021】租字第(A-0050)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0096)号", "浙商租【2022】租字第(A-0050)号");
        contractCodeFixMap.put("浙商租【2021】租字第(A-0056)号", "浙商租【2021】租字第(A-0070)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0110)号", "浙商租【2022】租字第(A-0065)号");
        contractCodeFixMap.put("浙商租【2022】租字第(A-0080)号", "浙商租【2022】租字第(A-0024)号");
    }


    @Override
    @SneakyThrows
    public R<Void> migrateFileClient(String pw) {
        String lockKey = lockKeyPre + "client";
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException("文件迁移进行中，请勿重复操作");
        }
        try {
            log.info("客户文件迁移处理开始, time:{}", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
            File clientFileDir = new File(CLIENT_FILE_DIR);
            Set<String> existHandleClientNameSet = new HashSet<>();
            if (FileUtil.exist(CLIENT_CONFIG_FILE)) {
                String configString = IoUtil.read(new FileInputStream(CLIENT_CONFIG_FILE), Charset.defaultCharset());
                if (StringUtils.isNotBlank(configString)) {
                    existHandleClientNameSet.addAll(JSONArray.parseArray(configString, String.class));
                }
            }

            List<String> clientNameList = Stream.of(clientFileDir.list()).collect(Collectors.toList());
            for (String clientName : clientNameList) {
                String fixClientName = clientName.trim().replaceAll("\t", "");
                if (existHandleClientNameSet.contains(fixClientName)) {
                    log.info("此客户已处理过:{}", fixClientName);
                    continue;
                }
                Client client = clientMapper.selectOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, fixClientName).last("LIMIT 1"));
                if (Objects.isNull(client)) {
                    log.info("此客户不存在:{}", fixClientName);
                    continue;
                }
                File targetClientDir = new File(clientFileDir.getAbsolutePath() + "/" + clientName);
                transactionTemplate.execute(status -> {
                    try {
                        // 进到客户文件夹了
                        List<File> classFileDirs = Stream.of(targetClientDir.listFiles()).filter(f -> f.isDirectory()).collect(Collectors.toList());
                        if (ClientType.CORPORATION.name().equals(client.getClientType())) {
                            for (File classFileDir : classFileDirs) {
                                if ("法人代表".equals(classFileDir.getName())) {
                                    classFileDir = new File(classFileDir.getAbsolutePath() + "/" + "实际控制人身份证");
                                }
                                MaterialsType materialsType = convertCorp(classFileDir.getName());
                                List<File> actualFiles = Stream.of(classFileDir.listFiles()).collect(Collectors.toList());
                                for (File f : actualFiles) {
                                    materialsListService.add(new FileInputStream(f), f.getName(), client.getId(), materialsType.name(), BusinessModuleEnum.CLIENT.name());
                                }
                            }
                        } else if (ClientType.NORMAL.name().equals(client.getClientType())) {
                            for (File classFileDir : classFileDirs) {
                                NormalMaterialsType normalMaterialsType = convertNormal(classFileDir.getName());
                                List<File> actualFiles = Stream.of(classFileDir.listFiles()).collect(Collectors.toList());
                                for (File f : actualFiles) {
                                    materialsListService.add(new FileInputStream(f), f.getName(), client.getId(), normalMaterialsType.name(), BusinessModuleEnum.CLIENT.name());
                                }
                            }
                        }
                        existHandleClientNameSet.add(fixClientName);
                        IoUtil.write(new FileOutputStream(CLIENT_CONFIG_FILE), true, JSON.toJSONString(existHandleClientNameSet).getBytes(StandardCharsets.UTF_8));
                        log.info("此客户文件迁移完成:{}", fixClientName);
                        return true;
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        log.error("事务执行出错", e);
                        throw new RuntimeException(e);
                    }
                });
            }

            log.info("客户文件迁移处理结束, time:{}", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    @SneakyThrows
    public R<Void> migrateFileContract(String pw) {
        String lockKey = lockKeyPre + "contract";
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException("合同文件迁移进行中，请勿重复操作");
        }
        try {
            log.info("合同文件迁移处理开始, time:{}", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
            File contractFileDir = new File(CONTRACT_FILE_DIR);
            Set<String> existHandleContractCodeSet = new HashSet<>();
            if (FileUtil.exist(CONTRACT_CONFIG_FILE)) {
                String configString = IoUtil.read(new FileInputStream(CONTRACT_CONFIG_FILE), Charset.defaultCharset());
                if (StringUtils.isNotBlank(configString)) {
                    existHandleContractCodeSet.addAll(JSONArray.parseArray(configString, String.class));
                }
            }
            List<String> contractCodeSet = Stream.of(contractFileDir.list()).collect(Collectors.toList());
            for (String contractCode : contractCodeSet) {
                String fixContractCode = contractCode.trim().replaceAll("（", "(").replaceAll("）", ")");
                if (!contractCodeFixMap.containsKey(fixContractCode)) {
                    log.info("此合同编号没找到映射:{}", fixContractCode);
                    continue;
                }
                String mapContractCode = contractCodeFixMap.get(fixContractCode);
                if (existHandleContractCodeSet.contains(fixContractCode)) {
                    log.info("此合同已处理过:{}", fixContractCode);
                    continue;
                }
                ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectOne(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, mapContractCode).last("LIMIT 1"));
                if (Objects.isNull(contractBaseInfo)) {
                    log.info("此合同不存在, 原code：{},映射code：{}", fixContractCode, mapContractCode);
                    continue;
                }
                File targetContractDir = new File(contractFileDir.getAbsolutePath() + "/" + contractCode);
                transactionTemplate.execute(status -> {
                    try {
                        // 进到合同文件夹了
                        List<File> classFileDirs = Stream.of(targetContractDir.listFiles()).filter(f -> f.isDirectory()).collect(Collectors.toList());
                        ContractTypeEnum fileType = ContractTypeEnum.OTHER_CONTRACT;
                        for (File classFileDir : classFileDirs) {
                            List<File> actualFiles = Stream.of(classFileDir.listFiles()).collect(Collectors.toList());
                            for (File f : actualFiles) {
                                materialsListService.add(new FileInputStream(f), f.getName(), contractBaseInfo.getId(), fileType.name(), BusinessModuleEnum.CONTRACT.name());
                            }
                        }
                        existHandleContractCodeSet.add(fixContractCode);
                        IoUtil.write(new FileOutputStream(CONTRACT_CONFIG_FILE), true, JSON.toJSONString(existHandleContractCodeSet).getBytes(StandardCharsets.UTF_8));
                        log.info("此合同文件迁移完成, 原code：{},映射code：{}", fixContractCode, mapContractCode);
                        return true;
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        log.error("事务执行出错", e);
                        throw new RuntimeException(e);
                    }
                });
            }

            log.info("合同文件迁移处理结束, time:{}", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    public static void main(String[] args) throws Exception {
//        File clientDir = new File("/Users/wang/Desktop/汉得文件/客户模块");
//        Set<String> fileClientSet = Stream.of(clientDir.list()).map(s -> s.trim().replaceAll("\t","")).collect(Collectors.toSet());
//        Set<String> existClientSet = new HashSet<>(Arrays.asList("测试001","德清同创建设发展有限公司","常州优纳新材料科技有限公司","上海宥纳新材料科技有限公司","苍南县国有资产投资集团有限公司","河北衡水运输集团有限公司","德清国际会议中心有限公司","张家界市经济发展投资集团有限公司","苍南县公共事业投资集团有限公司","德清联创科技新城建设有限公司","泸州市高新投资集团有限公司","西南云海大数据产业投资有限公司","张家界市公共交通发展有限公司","聚信国际融资租赁股份有限公司","上海聚信海聚新能源科技有限公司","邯郸市金泰包装材料有限公司","宋晓刚","浙江电联集团有限公司","兰溪市聚源建设开发有限公司","兰溪市鸿业建设有限公司","马鞍山经济技术开发区建设投资有限公司","马鞍山绿茵文化发展有限公司","泰兴市中鑫投资集团有限公司","杭州临安艾康生物技术有限公司","江苏创网通信科技有限公司","山东桓台鲁泰道路工程有限公司","海创投资发展集团有限公司","艾康生物技术（杭州）有限公司","山东飞源新材料有限公司","淄博飞源化工有限公司","青田县国有资产控股集团有限公司","泰州金姜水务有限公司","泰州市金东城市建设投资集团有限公司","河北海乾威钢管有限公司","金华市婺城区城乡建设投资集团有限公司","北京伍强智能科技有限公司","常山联亿供应链管理合伙企业（有限合伙）","常山众卡物流产业园投资有限公司","常山福金汽车租赁有限公司","福建省明洲环保发展有限公司","舟山开源供水有限责任公司","舟山市水务集团有限公司","山西天恒氢能科技有限公司","衡水衡运公共交通有限公司","王骅新","宁波经开科技发展有限公司","常州市玉燕制冷科技有限公司","中国联合水泥集团有限公司","莒县中联水泥有限公司","台州市路桥污水处理有限公司","台州市路桥区城市建设集团有限公司","衢州市衢江区建设投资集团有限公司","衢州市衢江区城乡建设发展有限公司","河南国联铁集物流有限公司","泰兴市智光环保科技有限公司","燕舞集团有限公司","杭州万兴科技股份有限公司","盐城市城市资产投资集团有限公司","浙江龙票通信科技有限公司","同信通信股份有限公司","泰兴市成兴国有资产经营投资有限公司","马中民","仪征市水达供水有限公司","杭州暗箱科技有限公司","滨州城建投资集团有限公司","山东滨港管理运营有限公司","福建省晋江城市建设投资开发集团有限责任公司","福建省晋江新佳园控股有限公司","淄博生态产业新城投资发展集团有限公司","淄博市城市资产运营有限公司","青田县交通发展投资有限公司","嵊州市城南建设投资有限公司","宗永华","宣城市公共交通有限公司","陈华禄","浙江方正电机股份有限公司","大连华德天宇集团有限公司","吉利科技集团有限公司","小灵狗出行科技有限公司","广州虎鲸数字化商业运营有限公司","赣州市章贡区建设投资集团有限公司","赣州市章贡区文化旅游发展集团有限公司","方陆芳","上饶市上武高速公路经营管理有限责任公司","上饶市城市建设投资开发集团有限公司","江华平","浙江波普环境服务有限公司","都市股份有限公司","丽水市莲都区国有资产投资经营有限公司","宁陵县牧原肉食品有限公司","牧原食品股份有限公司","新疆兴泰纤维科技有限公司","仪征市城市发展投资控股集团有限公司","新疆中泰化学股份有限公司","仪征市水交投资有限公司","泰兴市城市投资发展集团有限公司","泰兴市襟江投资有限公司","山东汇丰石化集团有限公司","淄博海益精细化工有限公司","遂昌县经济投资发展集团有限公司","孔颖","李丹琪","严振声","薛威","浙江新聚欣实业集团有限公司","浙江聚欣科技有限公司","杭州之江城市建设投资集团有限公司","昆明市交通投资有限责任公司","昆明现代物流发展集团有限公司","杭州煦达新能源科技有限公司","常州市金坛区城市污水处理有限公司","常州市金坛区交通产业集团有限公司","江苏金坛建设发展有限公司","常州市金坛区公共交通有限公司","广西盛隆冶金有限公司","徐振","杭州朗迅科技有限公司","芯云半导体（诸暨）有限公司","杭州芯云半导体技术有限公司","浙江禾众汽车企业管理集团有限公司","海宁市新国鸿汽车销售服务有限公司","平湖禾众誉德汽车销售服务有限公司","丽水市大花园旅游建设发展有限公司","丽水市文化旅游投资发展有限公司","湖州吴兴产业投资发展集团有限公司","湖州申太建设发展有限公司","攀枝花市公共交通有限责任公司","福建鑫宏兴航运有限公司","郑州锦和建设工程有限公司","陈武军","郑州广源伟业实业有限公司","广东万嘉通通信科技有限公司","漳州虎鲸冷链物流有限公司","四川创网通信科技有限公司","刘旭燕","许阿强","许阿珍","肖德虹","山西沃能化工科技有限公司","浙江长兴综合物流园区发展有限公司","卜春华","曹祥彬","胡震","福建新华威化纤染织有限公司","华祥(中国)高纤有限公司","福州聚福广通供应链有限公司","王泽光","陈君华","包头市威丰稀土电磁材料股份有限公司","包头威丰新材料有限公司","中国广电河北网络股份有限公司","泉速通（济南）供应链管理有限公司","江苏海兴化工有限公司","刘国红","周业忠","保定市公共交通有限公司","霍尔果斯博海水泥有限公司","湖南福泰物流有限公司","山东海力化工股份有限公司","常德福泰运输股份有限公司","韩松","德清县恒达建设发展有限公司","郭川","湖州莫干山高新集团有限公司","德清乾龙建设发展有限公司","浙江孔辉汽车科技有限公司","许沂炎","肖清美","福建龙麟环境工程有限公司","福建龙麟集团有限公司","河南六建租赁有限公司","山东乾运高科新材料有限公司","河南亚冷供应链管理有限公司","上海浩诺供应链管理有限公司","广东亚冷国际供应链管理有限公司","海兴化工","衢州绿色发展集团有限公司","一道新能源科技（衢州）有限公司","浙江华铁应急设备科技股份有限公司","诸暨市锦华新能源科技有限公司","浙江大黄蜂建筑机械设备有限公司","浙江鑫升新能源科技有限公司","江苏振江新能源装备股份有限公司","连云港振江轨道交通设备有限公司","山西闻祥国际贸易有限公司","陈立芳","杨华","湖北绿色家园化工贸易有限公司","广安交通投资建设开发集团有限责任公司","湖北绿色家园材料技术股份有限公司","广安市广泰公共交通有限责任公司","江苏龙尚重工有限公司","铜川市国有资本投资运营有限公司","湖州南浔城南新农村建设投资有限公司","铜川市公共交通有限责任公司","湖州启城市政工程有限公司","张佳","芜湖造船厂有限公司","黄海涛","赛路丰汽车集团有限公司","南通赛路丰吉智汽车销售服务有限公司","岳阳帝豪汽车销售服务有限公司","邵阳赛路丰汽车销售服务有限公司","永州赛路丰汽车销售服务有限公司","湖南赛路丰汽车有限公司","王凤枝","董海元","郑州锦和冷链仓储有限公司","天邦食品股份有限公司","杭州萧山江南养殖有限公司","北京晟农亚农业科技发展有限公司","宁夏铭岛铝业有限公司","浙江铭岛新材料股份有限公司","浙江铭岛实业有限公司","浙江诸长建筑设备安装有限公司","浙江铭岛铝业有限公司","沈一泽","王磊","杨从议","宁波万众汽车零部件有限公司","广东中舒建设集团有限公司","扶绥县中苏建设工程有限公司","浙江华眼视觉科技有限公司","肖妙如","台州市路桥区新城开发投资有限公司","方敬群","浙江富强置业有限公司","甘肃人为峰药业股份有限公司","北京国俊投资有限公司","苍南县五岳建设工程有限公司","北京亚冷控股有限公司","徐丽蓉","宋景涛","江西星光煤业有限公司","湖南人健宏城投资有限公司","广西双首能源科技有限公司","广西顶锋不锈钢有限公司","河南中联交通产业发展有限公司","湖南天福房地产开发有限公司","江苏国强镀锌实业有限公司","王珍","段志军","淳安千岛湖建设集团有限公司","淳安千岛湖旅游集团有限公司","湖南人健混凝土有限公司","湘潭金山投资有限公司","水城县鑫新炭素有限责任公司","文罗生","湖南宝能地产集团有限公司","杨娟","邱燕","杭州银泰购物中心有限公司","虢利平","杨立志","杨立新","淳安县交通发展投资集团有限公司","湖南鑫达晨创业投资有限公司","湖南人健创业投资有限公司","长沙顺捷机电有限公司","凤凰县德夯文化旅游开发有限责任公司","长沙人健商贸有限公司","潘清兰","饶伟导","湖南人健企业集团有限公司","莫水娣","李永强","龚曦光","李铭鸿","韦秀珍","曾鸿飞","赖文龙","吴朝顺","佛山市辉煌不锈钢有限公司","佛山市百年卓越钢业有限公司","广西梧州市丰盈不锈钢有限公司","广西柳钢中金不锈钢有限公司","孔列岚","卢斯侃","卢衍铭","张迈方","杨学华","深圳市穗深冷气设备有限公司","李新建","平阳县国资发展有限公司","河北冰峰供应链管理有限公司","兰溪市城市投资集团有限公司","石家庄运鼎驭捷汽车运输有限公司","兰溪市交通建设投资集团有限公司","浙江金鑫建材家居有限公司","湖州南浔城市投资发展集团有限公司","湖州南浔城市新区建设投资有限公司","菏泽财金投资集团有限公司","菏泽交通集团有限公司","菏泽城际公交有限公司","浙江安吉两山国有控股集团有限公司","安吉七彩灵峰农业发展有限公司","南通亿鸿建筑机械租赁有限公司","济南泽畅工程机械有限公司","宫海霞","窦勇","山东胜通钢帘线有限公司","山东大业股份有限公司","苏英","平阳县新鳌城市建设有限公司","朱长龙","毛静","田美链","刘智","刘金霞","湖州市飞英融资租赁有限公司","詹国海","河北鑫旺石油化工有限公司","河北鑫海化工集团有限公司","嵊州市经济开发区投资有限公司","中讯网络科技有限公司","浙江天启新能源集团有限公司","浙商中拓集团股份有限公司","青岛邦拓新材料科技有限公司","佰润新材（寿光）有限公司","山东大地盐化集团有限公司","东经人造皮草（宁波）有限公司","湖州南浔交通水利投资建设集团有限公司","山东昊邦化学有限公司","余姚经济开发区建设投资发展有限公司","储德群","清徐县美特好农产品配送物流有限公司","溧阳宝润钢铁有限公司","山西美特好连锁超市股份有限公司","浙江长兴经开建设开发有限公司","宁波市奉化区公共交通有限公司","宁波东经新型面料有限公司","舟山市普陀港福开发建设有限公司","云南鑫鹏物流有限公司","舟山群岛新区蓬莱国有资产投资集团有限公司","岱山县安澜城市建设投资集团有限公司","中国旭阳集团有限公司","湖州南浔旅游投资发展集团有限公司","湖州南浔凤凰文化旅游发展集团有限公司","嵊州市投资控股有限公司","浙江兴合融资租赁有限公司","嵊州市高新技术发展有限公司","浙江中通通信有限公司","长兴城市建设投资集团有限公司","长兴城南新城开发建设有限公司","宁波奉化元康公路建设有限公司","宁波市奉化区交通投资发展集团有限公司","山东铁鹰建设工程有限公司","江苏泰鸿纺织科技有限公司","安吉县城西北开发有限公司","浙江天子湖实业投资有限公司","湖州市南浔区国有资产投资控股有限责任公司","顾柔坚","浙江湖州众创投资建设有限公司","刘亚芹","顾清波","黄凤鸣","熊传辉","云南交通运输有限责任公司","安宁公交有限公司","云南昆明交通运输集团有限公司","安吉紫梅实业有限公司","宁波市奉化区溪口城市建设投资有限公司","庄惠","河北文丰实业集团有限公司","唐山文丰特钢有限公司","赵守明","余惠君","刘高芳","高兴超","湖州吴兴国有资本投资发展有限公司","福瑞祥（青岛）物联网技术服务有限公司","湖州吴兴美妆小镇建设投资开发有限公司","湖州吴兴经开建设投资发展集团有限公司","叶华彪","张文瑾","上海屹丰汽车模具制造有限公司","屹丰汽车科技集团有限公司","嘉兴屹丰汽车部件有限公司","刘美华","董开","鲁冰（山东）冷链科技有限公司","浙江浙商装备工程服务有限公司","山东万泽冷链股份有限公司","山东万泽优鲜供应链有限公司","厦门创德信环保设备有限公司","厦门创德机械有限公司","蒋秀兰","何福东","武汉元星冷链物流有限公司","大元冷链物流（德清）有限公司","元星冷链物流（昆山）有限公司","上海大元冷链物流有限公司","武安市裕华钢铁有限公司","山东史泰丰肥业有限公司","赤峰富龙公用（集团）有限责任公司","山东晋控日月新材料有限公司","赤峰富龙热力有限责任公司","侯学情","浙江源宏医药科技有限公司","蔡函烨","蔡华峻","内蒙古华恒能源科技有限公司","唐山港陆钢铁有限公司","宁波市海金食品有限公司","宁波康明环保科技有限公司","宁波广昌达新材料有限公司","镇海石化工业贸易有限责任公司","呼和浩特旭阳中燃能源有限公司","河北旭阳能源有限公司","杭州创意投资发展有限公司","旭阳集团有限公司","杭州利坤投资发展有限公司","万邦德集团有限公司","栋梁铝业有限公司","周骏","沈根莲","杨忠达","上海富品冷链物流有限公司","湖南淇达工程机械有限公司","合肥众兴机械设备有限公司","杭州胜冠机械设备有限公司","山东京博控股集团有限公司","山东京博石油化工有限公司","北京颖泰嘉和生物科技股份有限公司","北京建龙重工集团有限公司","江苏常隆农化有限公司","绍兴柯桥恒鸣化纤有限公司","山西建龙实业有限公司","诸暨市华海新材有限公司","巢璐","杨利娟","张体东","王静","贺亭亭","夏艳雪","肖波","绍兴柯桥经济开发区开发投资有限公司","绍兴市柯桥区滨海城市建设开发投资有限公司","张秀英","刘富强","厦门海福实业有限公司","石咏梅","胡瑾瑜","山西碧锦纳川医药有限公司","山西顺天立大健康产业集团有限公司","山西万美医药科技有限公司","长兴南太湖投资开发有限公司","大连港毅都冷链有限公司","长兴宏达水利建设发展有限公司","郑州航空港毅都冷链有限公司","韩茂","曹晓维","余璐岚","湖北江汉建筑工程机械有限公司","厦门顺磊建材有限公司","河北超威电源有限公司","张巍元","徐利桦","环龙工业集团有限公司","开玙供应链管理（无锡）有限公司","四川环龙生活用品有限公司","王丽丽","黄伟成","四川环龙新材料有限公司","余姚市联海实业有限公司","林东方","上海申浙数智轨道科技有限公司","上海裕亿机械设备有限公司","天祥建设集团股份有限公司","天祥建筑科技（兰溪市）有限公司","扶绥县昊腾工程建设有限公司","成都大宏立机器股份有限公司","刘爱群","余姚市牟山湖开发有限公司","陕西黑猫焦化股份有限公司","邹怡臻","内蒙古黑猫煤化工有限公司","杭州铁集货运股份有限公司","浙江华展新材料有限公司","浙江华海合力科技股份有限公司","诸暨华海氨纶有限公司","淳安县国有资产投资有限公司","吴少杰","福建省长盛发海运有限公司","杭州空港投资开发有限公司","杭州空港新城保税物流中心有限公司","杭州千岛湖高铁新区发展有限公司","合肥拾八岗货运有限公司","海盐县顺程紧固件有限公司","杭州安沃普机械有限公司","常州吉百机械设备有限公司","湖南星邦智能装备股份有限公司","邹昌盛之妻","邹昌盛","雷丽","邹运长","孙银玲","大连运昌船务有限公司","李柏任","李勋","侯懿珉","浙江绿色慧联有限公司","台州市路桥公共资产投资管理集团有限公司","浙江嘉澳绿色新能源有限公司","台州市路桥区交通建设集团有限公司","浙江绿农生态环境有限公司","宁波交运建设集团有限公司","宁波象山交通开发建设集团有限公司","海南金海浆纸业有限公司","湖北顺乐钢铁有限公司","浙江嘉澳环保科技股份有限公司","浙江东江能源科技有限公司","杭加（广东）建筑节能新材料有限公司","湖北金盛兰冶金科技有限公司","宁波市奉化区投资集团有限公司","捷尔杰（天津）设备有限公司","宁波市奉化区红胜开发建设有限公司","深圳市铁木投资（集团）有限公司","深圳市铁木科技发展有限公司","广东东阳光科技控股股份有限公司","乳源东阳光电化厂","王仕锦","宁波象山海洋产业投资集团有限公司","宁波市镇海区海江投资发展有限公司","宁波市镇海蛟川投资发展有限公司","安徽众鼎工程机械租赁有限公司","浙江交科供应链管理有限公司","宁波溪口雪窦山风景名胜区自来水有限公司","浙江恒逸集团有限公司","浙江荣盛控股集团有限公司","永宁尔集团股份有限公司","余姚市舜财投资控股有限公司","安吉永宁尔纺织有限公司","余姚市城西工业开发建设有限公司","宁波市奉化区溪口旅游集团有限公司","泸天化（集团）有限责任公司","宁波市奉化区农商发展集团有限公司","杭州砺剑隧道工程有限公司","福建豪邦化工有限公司","浙江逸盛新材料有限公司","李鑫","唐山中海船舶燃料有限公司","天津开发区之海船舶油料供应有限公司","天津市宏信船舶运输有限公司","浙江超威动力能源有限公司","舟山市定海区城乡建设集团有限公司","舟山市定海城区建设开发有限公司","杨淑丽","杭州锦江建材集团有限公司","河南锦荣水泥有限公司","桂安平","朱四军","桂永杰","泸州产业发展投资集团有限公司","四川中蓝国塑新材料科技有限公司","厦门海福建机有限公司","王丹莉","靖江国林木业有限公司","中国林产品集团有限公司","周南方","南丹县南方有色金属有限责任公司","广西南国铜业有限责任公司","林锦堂","厦门海福租赁有限公司","天下行租车有限公司","福建顺磊集团有限公司","福建晋工机械有限公司","广邦（厦门）塑胶科技有限公司","浙江豪邦化工有限公司","陈婷婷","德清县建设发展集团有限公司","德清县城市建设发展总公司","众能联合数字技术有限公司","战隽","韩治东","日照大恒船务有限公司","科威瑞（广东）矿机装备有限公司","日照东宏达海运有限公司","浙江成峰实业有限公司","山东渠风食品科技有限公司","山东柠檬生化有限公司","兰考昆仑燃气有限公司","朱凯军","慈溪市皓远机械设备有限公司","嘉善嘉港燃气有限公司","宁波象山大目湾经济开发有限公司","象山县大目湾新城投资开发有限公司","河南通冠重工实业有限公司","通冠机械租赁股份有限公司","浙江超威贝特瑞科技有限公司","超威电源集团有限公司","宁波市雪窦开发投资集团有限公司","宁波市奉化区旅游集散中心有限公司","柳州钢铁股份有限公司","中建锦程机械设备（上海）有限公司","江西诺瑞环境资源科技有限公司","郑州沃特节能科技股份有限公司","诸暨市城东新城建设有限公司","地上铁租车（深圳）有限公司","李勇","绍兴市科技创业投资有限公司","绍兴高新技术产业开发区迪荡新城投资发展有限公司","千里马（湖北）数字化租赁有限公司","宁霄","童国昌","张权","杭州张胜实业有限公司","杭州中诚装备服务股份有限公司","浙商中拓集团（海南）有限公司","武汉吉象合力工业车辆有限公司","千里马机械供应链股份有限公司","舟山市定海区国有资产经营有限公司","舟山市定海宝利投资有限公司","安吉县产业投资发展集团有限公司","安吉县旅游发展有限公司","诸暨市城乡投资集团有限公司","诸暨市农村发展投资有限公司","绍兴添溢工程机械租赁有限公司","朱丹","张云昊","长兴港通建设开发有限公司","长兴交通投资集团有限公司","江苏九鼎集团有限公司","富春控股集团有限公司","浙江杭加泽通建筑节能新材料有限公司","上海富盛浙工建材有限公司","乐山杭加节能新材料有限公司","山东九鼎新材料有限公司","兰溪市聚业建设开发有限公司","四川杭加汉驭建筑节能新材料有限公司","江苏正威新材料股份有限公司","嵊州市城市建设投资发展集团有限公司","嵊州市交通投资发展集团有限公司","广西通盛融资租赁有限公司","杭州宏迈机械设备有限公司","泰兴市虹桥园工业开发有限公司","万冰","泰兴市滨江污水处理有限公司","上海鼎衡投资控股集团有限公司","湖州经开投资发展集团有限公司","浙江湖州环太湖集团有限公司","浙江鹊山建设有限公司","网营物联股份有限公司","海宁网营物联供应链股份有限公司","金光纸业（中国）投资有限公司","杭州网营物联控股集团有限公司","广西金桂浆纸业有限公司","嘉兴卓航海运有限公司","浙江大盈建设有限公司","金鼎钢铁集团有限公司","安徽首矿大昌金属材料有限公司","金鼎重工有限公司","浙江舟山筑盟建设发展有限责任公司","广西美斯达工程机械设备有限公司","陈佩珍","黄静杰","阳光王子（寿光）特种纸有限公司","项坚波","沈惠珍","金帝联合控股集团有限公司","南通中盾能源有限公司","金帝联合能源集团股份有限公司","项兴成","浙江卓航物流发展股份有限公司","浙江长龙海运有限公司","宁俊","林州凤宝管业有限公司","河南凤宝特钢有限公司","华劲集团赣州纸品有限公司","赣州华劲纸业有限公司","兰溪市兰创投资集团有限公司","兰溪市兴港港航开发建设有限公司","华劲集团股份有限公司","李多珠","诸暨市越都投资发展有限公司","上海鼎衡航运科技有限公司","象山县滨海投资有限公司","殷祖安","宁波市沥平航运有限公司","象山县通用燃气有限公司","宁波市海洲船务有限公司","宁波市海洲物流有限公司","宁波市奉化区城市投资发展集团有限公司","靖江市城投基础设施发展有限公司","靖江市润新建设有限公司","浙江省交通投资集团有限公司","山西立恒焦化有限公司","重庆正源国杰工程机械有限公司","中国林业集团有限公司","扬中市八桥污水处理厂有限公司","绍兴市柯桥区城建投资开发集团有限公司","绍兴市柯桥区杭衢高速公路连接线有限公司","杨小花","国林汇泰（上海）企业发展有限公司","杨清平","平潭综合实验区向洋船务有限公司","山东银鹰股份有限公司","山东银鹰化纤有限公司","新疆天泰纤维有限公司","常州市金坛区建设资产经营有限公司","山西晋南钢铁集团有限公司","桐庐县国有资本投资运营控股集团有限公司","桐庐县国有资产投资经营有限公司","舟山市普陀区国有资产投资经营有限公司","舟山普陀城市投资发展集团有限公司","扬中绿洲新城实业集团有限公司","扬中市交通投资发展有限公司","江苏金坛国发国际投资发展有限公司","江苏尧兴园林绿化有限公司","诸暨市交通投资集团有限公司","诸暨市新城投资开发集团有限公司","郑珊珊","福建东南造船有限公司","洪安前","盐城市亭湖城市资产投资实业有限公司","江苏嘉亭实业投资有限公司","盐城海瀛控股集团有限公司","浙江临杭物流发展有限公司","浙江省德清县交通投资集团有限公司","湖州织里童装产业投资发展有限公司","盐城润瀛实业投资有限公司","浙江湖州南浔经济建设开发有限公司","德清县下渚湖湿地旅游发展有限公司","长沙万城万充新能源汽车租赁有限公司","万城万充（武汉）新能源汽车服务有限公司","万城万充（福州）电动汽车运营有限责任公司","万城万充(泉州)电动汽车运营有限责任公司","江苏省镔鑫钢铁集团有限公司","万城万充(广州)新能源汽车租赁有限公司","连云港赣榆众诚投资有限公司","太平洋建设集团有限公司","江苏徐钢钢铁集团有限公司","徐州科建环保科技股份有限公司","徐州海西置业有限公司","徐州康宝房地产开发有限公司","西林浩辉建设有限公司","山西聚鑫智云运输有限公司","新绛县恒裕运输有限公司","山西高义钢铁有限公司","山西建邦集团铸造有限公司","山东世纪阳光纸业集团有限公司","浙江中拓供应链管理有限公司","浙江屯德科技有限公司","杭州康柏斯科技有限公司","乌拉特后旗力源新材料有限公司","湖南新仁置业有限公司","江苏德龙镍业有限公司","响水恒生不锈钢铸造有限公司","响水德丰金属材料有限公司","响水巨合金属制品有限公司","响水昌隆贸易有限公司","响水康阳贸易有限公司","响水德力新材料科技有限公司","响水欧力金属制品有限公司","响水天龙不锈钢铸造有限公司","徐州德龙金属科技有限公司","江苏德顺镍业有限公司","武汉乾冶众联科技有限公司","武汉乾冶工程技术有限公司","湖南省邵东市新仁铝业有限责任公司","广西梧州市金海不锈钢有限公司","新疆博海水泥有限公司","佛山市金海辉煌不锈钢有限公司","广西梧州市中成达新材料有限公司","广东万城万充电动车运营股份有限公司","湖州中植融云投资有限公司","万城万充（杭州）新能源汽车服务有限公司","宁夏晟晏实业集团能源循环经济有限公司","浙商中拓集团电力科技有限公司","宁夏晟晏实业集团有限公司","中国电建集团江西省电力建设有限公司","广西朗知森商贸有限公司","金华融盛投资发展集团有限公司","湖南太平洋建设有限公司","金华科技园创业服务中心有限公司","浙江国金融资租赁股份有限公司","广东禄宇航运有限公司","宁波万城万充电动汽车服务有限公司","绍兴亿充新能源汽车服务有限公司","佛山市万城万充新能源汽车租赁有限公司","万城万充（珠海）电动汽车运营有限公司","温州万城万充新能源汽车服务有限公司","苏州亿充新能源汽车租赁有限公司","山东金晶科技股份有限公司","山东海天生物化工有限公司","宁夏金晶科技有限公司","滕州金晶玻璃有限公司","山东天源热电有限公司","宜宾丝丽雅股份有限公司","宜宾海丝特纤维有限责任公司","宜宾丝丽雅集团有限公司","嵊州市经济开发区东方投资有限公司","绍兴袍江新农村建设投资有限公司","绍兴袍江工业区投资开发有限公司","宋佃凤","吕萍","宋佃学","巩春红","宋佃忠","丰磊","林金良","林依芳","严昊","王爱钦","林依岁","张天福","张锐","吴俊","吴晓年","吕红霞","闫伟","张高溢","张哲华","王东兴","戴娇","戴笠","戴国芳","邓娥英","曾小山","廖爱华","邹红妹","邹祖良","刘爱云","林国文","邹红卫","黄荷琴","朱明冬","王文辉","李娟","李铭森","李薇","余泽民","陈翠霞","余伟汉","卢燕娴","杨斌","阎秋","王刚","杨振兴","刘鹏","杨延良","李秀荣","宋民松","宋丽华","oJLFJgQJggyFQAtoUvQQiw==","泉州和泰通海运有限公司","厦门鹭盛船运有限公司","杭州新天地集团有限公司","海南成功网联科技股份有限公司","山东仁丰特种材料股份有限公司","淄博弘瑞再生资源有限公司","淄博惠润热力有限公司","淄博智瑞纸业有限公司","湖州新型城市投资发展集团有限公司","山西通才工贸有限公司","湖州市南浔新城投资发展有限公司","山西立恒钢铁集团股份有限公司","山西建邦集团有限公司","昌乐新迈纸业有限公司","山东博汇纸业股份有限公司","江苏博汇纸业有限公司","滨州中裕食品有限公司","山东滨城国家粮食储备库","河南骏化发展股份有限公司","昊华骏化集团有限公司","洛阳骏化生物科技有限公司","浙江欣捷建设有限公司","欣捷投资控股集团有限公司","宁波海创科技园开发有限公司","京商第一建设有限公司","蒋伟平","倪国富","东莞市金田纸业有限公司","宁波市奉化区惠江基础设施建设有限公司","四川金田纸业有限公司","当涂县清源水务投资有限公司","当涂县城乡建设投资有限责任公司","欧阳忠","唐丽君","李玉明","张惠玲","庄树广","张日欢","卢宝祥","赖红梅","袁毅","梁瑞南","2132323","山西建邦集团有限公司通才铁路专用线分公司","胡高山","华铅良","陆冰花","周昊瀚","江苏慧智新材料科技有限公司"));
//        System.out.println(JSON.toJSONString(Sets.difference(fileClientSet, existClientSet)));

        File clientDir = new File("/Users/wang/Desktop/汉得文件/contract");
        Set<String> fileContractSet = Stream.of(clientDir.list()).map(s -> s.trim().replaceAll("（","(").replaceAll("）",")")).collect(Collectors.toSet());

        System.out.println(JSON.toJSONString(fileContractSet));
//        List<String> contractCodeList = Arrays.asList("中拓租【2019】租字第（A-0009）号","中拓租【2020】租字第（A-0016）号","浙商租【2021】租字第（A-0009）号","浙商租【2021】租字第（A-0039）号","浙商租【2022】租字第（A-0104）号","浙商租【2022】租字第（A-0103）号","中拓租【2019】租字第（A-0011）号","中拓租【2019】租字第（A-0010）号","中拓租【2020】租字第（A-0030）号","中拓租【2019】租字第（A-0015）号","中拓租【2019】租字第（A-0016）号","浙商租【2022】租字第（A-0091）号","浙商租【2021】租字第（A-0037）号","中拓租【2019】租字第（A-0013）号","中拓租【2019】租字第（A-0014）号","中拓租【2019】租字第（A-0005）号","中拓租【2019】租字第（A-0008）号","浙商租【2021】租字第（A-0047）号","中拓租【2020】租字第（A-0004）号","中拓租【2020】租字第（A-0007）号","中拓租【2020】租字第（A-0009）号","中拓租【2020】租字第（A-0014）号","中拓租【2020】租字第（A-0015）号","中拓租【2020】租字第（A-0010）号","浙商租【2021】租字第（A-1045）号","中拓租【2020】租字第（A-0012）号","浙商租【2021】租字第（A-0001）号","中拓租【2020】租字第（A-0001）号","浙商租【2021】租字第（A-0019）号","浙商租【2021】租字第（A-0006）号","中拓租【2019】租字第（A-0002）号","中拓租【2019】租字第（A-0002）号","中拓租【2019】租字第（A-0002）号","中拓租【2020】租字第（A-0013）号","浙商租【2022】租字第（A-0108）号","中拓租【2020】租字第（A-0018）号","中拓租【2020】租字第（A-0019）号","中拓租【2020】租字第（A-0021）号","中拓租【2020】租字第（A-0020）号","中拓租【2020】租字第（A-0022）号","中拓租【2020】租字第（A-0031）号","中拓租【2020】租字第（A-0034）号","浙商租【2021】租字第（A-0020）号","浙商租【2021】租字第（A-0109）号","浙商租【2021】租字第（A-1001）号","浙商租【2021】租字第（A-1002）号","中拓租【2020】租字第（A-0025）号","中拓租【2020】租字第（A-0026）号","浙商租【2021】租字第（A-1003）号","浙商租【2021】租字第（A-1004）号","浙商租【2021】租字第（A-0014）号","浙商租【2021】租字第（A-0017）号","中拓租【2020】租字第（A-0011）号","浙商租【2021】租字第（BA-0032）号","中拓租【2018】租字第（C-0006）号","浙商租【2021】租字第（A-1013）号","浙商租【2021】租字第（A-0002）号","浙商租【2021】租字第（A-1018）号","浙商租【2021】租字第（A-1019）号","浙商租【2021】租字第（BA-0022）号","浙商租【2021】租字第（A-1005）号","浙商租【2021】租字第（A-0003）号","浙商租【2021】租字第（A-0007）号","浙商租【2021】租字第（A-0022）号","浙商租【2021】租字第（A-0042）号","浙商租【2021】租字第（A-0021）号","浙商租【2021】租字第（A-1040）号","浙商租【2021】租字第（A-1023）号","浙商租【2021】租字第（A-0026）号","浙商租【2021】租字第（A-0027）号","浙商租【2021】租字第（C-0001）号","浙商租【2021】租字第（C-0004）号","浙商租【2021】租字第（A-1043）号","浙商租【2021】租字第（A-1044）号","浙商租【2021】租字第（C-0005）号","浙商租【2021】租字第（C-0007）号","浙商租【2021】租字第（A-0034）号","浙商租【2021】租字第（A-0032）号","浙商租【2021】租字第（A-0032）号","浙商租【2021】租字第（A-0035）号","浙商租【2021】租字第（A-0038）号","浙商租【2021】租字第（C-0008）号","浙商租【2021】租字第（A-0043）号","浙商租【2021】租字第（A-0054）号","浙商租【2021】租字第（A-0041）号","浙商租【2021】租字第（A-0040）号","浙商租【2021】租字第（C-0011）号","浙商租【2021】租字第（C-0006）号","浙商租【2021】租字第（C-0015）号","浙商租【2021】租字第（C-0013）号","浙商租【2021】租字第（C-0018）号","浙商租【2021】租字第（C-0017）号","浙商租【2022】租字第（C-0031）号","浙商租【2021】租字第（A-0050）号","浙商租【2021】租字第（A-0051）号","浙商租【2021】租字第（A-0048）号","浙商租【2021】租字第（A-0049）号","浙商租【2021】租字第（A-0053）号","浙商租【2021】租字第（A-0045）号","浙商租【2021】租字第（A-0046）号","浙商租【2021】租字第（A-0059）号","浙商租【2021】租字第（A-0060）号","浙商租【2021】租字第（A-0061）号","浙商租【2021】租字第（A-0056）号","浙商租【2021】租字第（A-0057）号","浙商租【2022】租字第（A-0005）号","浙商租【2022】租字第（A-0006）号","浙商租【2021】租字第（C-0022）号","浙商租【2021】租字第（A-0063）号","浙商租【2022】租字第（A-0070）号","浙商租【2022】租字第（A-0071）号","浙商租【2022】租字第（C-0027）号","浙商租【2022】租字第（C-0024）号","浙商租【2022】租字第（A-0075）号","浙商租【2022】租字第（A-0076）号","浙商租【2022】租字第（A-0074）号","浙商租【2021】租字第（A-0025）号","浙商租【2022】租字第（A-0098）号","浙商租【2022】租字第（A-0078）号","浙商租【2022】租字第（C-0026）号","浙商租【2022】租字第（C-0030）号","浙商租【2022】租字第（C-0029）号","浙商租【2022】租字第（A-0082）号","浙商租【2022】租字第（A-0094）号","浙商租【2022】租字第（A-0079）号","浙商租【2022】租字第（A-0080）号","浙商租【2022】租字第（A-0081）号","浙商租【2021】租字第（A-0062）号","浙商租【2021】租字第（A-0062）号","浙商租【2022】租字第（A-0086）号","浙商租【2022】租字第（A-0087）号","浙商租【2022】租字第（A-0088）号","浙商租【2022】租字第（A-0090）号","浙商租【2022】租字第（A-0089）号","浙商租【2022】租字第（A-0092）号","浙商租【2022】租字第（A-0093）号","浙商租【2022】租字第（A-0099）号","浙商租【2022】租字第（A-0096）号","浙商租【2022】租字第（A-0097）号","浙商租【2022】租字第（A-0110）号","浙商租【2022】租字第（A-0111）号","浙商租【2022】租字第（A-0100）号","浙商租【2022】租字第（A-0112）号");
//        Set<String> existSet = new HashSet<>(), errorSet = new HashSet<>();
//        for (String contractCode : contractCodeList) {
//            if (existSet.contains(contractCode)) {
//                errorSet.add(contractCode);
//            }
//            existSet.add(contractCode);
//        }
//        System.out.println(JSON.toJSONString(errorSet));

    }

    public NormalMaterialsType convertNormal(String originCode) {
        switch (originCode) {
            case "保证人身份证、户口本（如有）":
                return NormalMaterialsType.HOUSEHOLD;
            case "保证人个人名下资产清单及资产权属证明":
                return NormalMaterialsType.NAMED_ASSETS;
            case "保证人个人信用报告":
                return NormalMaterialsType.PERSONAL_CREDIT_REPORT;
            default:
                return NormalMaterialsType.OTHERS;
        }
    }

    public MaterialsType convertCorp(String originCode) {
        switch (originCode) {
            case "营业执照副本":
            case "法人代表":
            case "实际控制人身份证":
            case "公司章程及章程修正案":
            case "企业简介、主要股东和管理层简历":
                return MaterialsType.BASIC_INFORMATION;
            case "近三年财务报表或近三年审计报告（含附注说明）复印件及最近一期的资产负债表及利润表":
            case "最近一个会计年度及最近一期财务报表科目余额表":
            case "成立不足三年的,提供自成立至授信申请日的年度财务报表或审计报告（含附注说明）和最近一期的资产负债表及利润表":
                return MaterialsType.FINANCIAL_INFORMATION;
            default:
                return MaterialsType.OTHERS;
        }
    }


}
