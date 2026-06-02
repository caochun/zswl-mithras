package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.client.ClientVwSyncMapper;
import cn.zswltech.mithras.client.sandrecord.infrastructure.model.ClientSandRecord;
import cn.zswltech.mithras.service.mapper.model.client.ClientVwSync;
import cn.zswltech.mithras.client.externalcustomer.infrastructure.model.ExternalCustomer;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.client.sandrecord.application.ClientSandRecordService;
import cn.zswltech.mithras.service.service.client.ClientVwSyncService;
import cn.zswltech.mithras.client.externalcustomer.application.ExternalCustomerService;
import cn.zswltech.mithras.riskcontrol.clientfile.RiskClientListFileDTO;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shaokang
 * @description 风险管理-监控客户名单文件处理
 * @date 2026-01-28
 */
@Slf4j
@Service
public class RiskControlClientListFileService {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ClientVwSyncService clientVwSyncService;
    @Resource
    private ClientVwSyncMapper clientVwSyncMapper;
    @Resource
    private ClientSandRecordService clientSandRecordService;
    @Resource
    private ExternalCustomerService externalCustomerService;

    @Value("${xinsight.sftp.host:}")
    private String sftpHost;
    @Value("${xinsight.sftp.port:}")
    private int sftpPort;
    @Value("${xinsight.sftp.user:}")
    private String sftpUser;
    @Value("${xinsight.sftp.password:}")
    private String sftpPassword;
    @Value("${xinsight.sftp.maxRow:}")
    private Integer maxRow;// 沙盘名单最大数

    // 表头字段
    private static final String HEADER_1 = "itname";//机构名称
    private static final String HEADER_2 = "creditcode";//统一社会信用代码
    private static final String HEADER_3 = "type";//类型 A-新增;D-删除
    // 沙盘数据类型
    private static final String TYPE_A = "A";// A-新增;D-删除
    private static final String TYPE_D = "D";// A-新增;D-删除
    // 数据来源
    private static final String SOURCE_VW = "VW";//
    private static final String SOURCE_WB = "WB";//

    /**
     * 获取客户沙盘数据
     * 并把当天客户记录保存
     *
     * @param today 当天日期
     * @return 需要生成csv文件的数据
     */
    @Transactional(rollbackFor = Throwable.class)
    public List<RiskClientListFileDTO> getClientList(LocalDate today){
        Set<RiskClientListFileDTO> resultLs = new HashSet<>();// 返回需要生成csv文件的数据
        List<ExternalCustomer> externalCusList = new ArrayList<>();// 外部客户数据
        List<ClientSandRecord> newRecordList = new ArrayList<>(); // 需要保存的当天记录数据
        // 1.查询沙盘数据记录表
        ClientSandRecord firstRecord = clientSandRecordService.getOne(Wrappers.<ClientSandRecord>lambdaQuery()
                .eq(ClientSandRecord::getFirstMark, YesOrNoNumberEnum.YES.getCode())
                .last(StringUtil.mysqlLimitOne()));

        // 2. 进行全量或增量判断
        if (ObjectUtil.isNotEmpty(firstRecord)){
            // 不为空,进行增量数据的场景逻辑
            // 2.0 查询vwClientSync比ClientSandRecord多的数据
            List<ClientVwSync> addVwSyncList = clientVwSyncMapper.selectAddListByRecord();
            if (CollectionUtil.isEmpty(addVwSyncList)){
                // 2.1 无新增的数据,直接返回空集合
                return new ArrayList<>(resultLs);
            }else {
                int size = addVwSyncList.size();
                log.info("增加的vw客户数量: {}", size);
                // 2.2 将 addVwSyncList 转换为 RiskClientListFileDTO 并加入 resultLs
                convertVwToDTO(resultLs,addVwSyncList,TYPE_A);
                // 2.3 找到对应数量的外部客户数据 设为为 TYPE_D
                // 2.3.1 查找vwClientSync增加的但是已经在ClientSandRecord存在的外部数据(即变为系统客户且在vw表中的外部客户)
                List<ClientVwSync> vwWBList = clientVwSyncMapper.selectWbClientByRecord();
                Set<String> wbUscCodes = vwWBList.stream().map(ClientVwSync::getCertNumber).collect(Collectors.toSet());
                List<ClientSandRecord> delWbRecordList = clientSandRecordService.list(Wrappers.<ClientSandRecord>lambdaQuery()
                        .eq(ClientSandRecord::getDataSource, SOURCE_WB)
                        .eq(ClientSandRecord::getDataMark, YesOrNoNumberEnum.NO.getCode())
                        .eq(ClientSandRecord::getDataType,TYPE_A)
                        .and(CollectionUtil.isNotEmpty(wbUscCodes), wrapper ->
                                wrapper.notIn(ClientSandRecord::getUscCode, wbUscCodes))// 筛选出不在 wbUscCodes 中的记录
                        .last(StringUtil.mysqlLimit(0, size)));
                // 2.4 将 delWbRecordList 转换为 RiskClientListFileDTO 并加入 resultLs
                convertRecordToDTO(resultLs,delWbRecordList,TYPE_D);
                log.info("删除的外部客户数量: {}", delWbRecordList.size());
                // 2.5 保存当天新增的vw记录数据
                newRecordList = convertVwToRecord(addVwSyncList, TYPE_A, today, SOURCE_VW, YesOrNoNumberEnum.NO.getCode().toString());
                clientSandRecordService.saveBatch(newRecordList);
                // 2.6 更新需删除的外部客户记录
                updateRecordInfo(delWbRecordList,TYPE_D,YesOrNoNumberEnum.NO.getCode().toString());
                // 2.7 查找变为系统客户的外部数据
                if (CollectionUtil.isNotEmpty(wbUscCodes)){
                    List<ClientSandRecord> changeWbRecordList = clientSandRecordService.list(Wrappers.<ClientSandRecord>lambdaQuery()
                            .eq(ClientSandRecord::getDataSource, SOURCE_WB)
                            .eq(ClientSandRecord::getDataMark, YesOrNoNumberEnum.NO.getCode())
                            .eq(ClientSandRecord::getDataType,TYPE_A)
                            .in(ClientSandRecord::getUscCode,wbUscCodes));
                    // 2.8 更新变为系统客户的外部客户记录
                    updateRecordInfo(changeWbRecordList,TYPE_A,YesOrNoNumberEnum.YES.getCode().toString());
                }
            }
        }else {
            // 为空,即为第一版全量数据
            // 3. 查询全量监控名单表数据
            List<ClientVwSync> vwSyncList = clientVwSyncService.list();
            log.info("监控名单表数据条数: {}", vwSyncList.size());
            if (vwSyncList.size() < maxRow){
                //  3.0 查询外部客户表数据
                externalCusList = externalCustomerService.list();
                log.info("外部客户表数据量: {}", externalCusList.size());
                //  计算还需要多少条数据
                int need = maxRow - vwSyncList.size();
                //  3.1 从外部客户表取数据补充，但不能超过其实际数量
                if (CollectionUtil.isNotEmpty(externalCusList)) {
                    int toTake = Math.min(need, externalCusList.size());
                    externalCusList = externalCusList.subList(0, toTake);
                    log.info("外部客户表使用量: {}", externalCusList.size());
                }
            }
            // 3.2 将数据转换
            convertVwToDTO(resultLs,vwSyncList,TYPE_A);
            convertExcToDTO(resultLs,externalCusList,TYPE_A);
            // 3.3 全量数据保存到记录表
            newRecordList = convertVwToRecord(vwSyncList, TYPE_A, today, SOURCE_VW, YesOrNoNumberEnum.YES.getCode().toString());
            log.info("保存到记录表的vw客户数量: {}", newRecordList.size());
            List<ClientSandRecord> recordListByExc = convertExcToRecord(externalCusList, TYPE_A, today, SOURCE_WB, YesOrNoNumberEnum.YES.getCode().toString());
            log.info("保存到记录表的外部客户数量: {}", recordListByExc.size());
            newRecordList.addAll(recordListByExc);
            clientSandRecordService.saveBatch(newRecordList);
        }

        return new ArrayList<>(resultLs);
    }

    /**
     * 创建CSV文件并通过SFTP传输到远程服务器
     * 采用临时文件机制确保传输的原子性（要么完全成功，要么完全失败）
     *
     * @param <T> 数据类型泛型
     * @param fileName CSV文件名
     * @param dataList 要导出为CSV的数据列表
     * @param path SFTP远程目录路径
     * @throws Exception 文件生成或SFTP传输异常
     */
    public <T> void createAndTransferCsv(String fileName, List<T> dataList, String path) throws Exception {
        // 1. 生成CSV文件
        File csvFile = generateCsvFile(fileName, dataList);
        JSch jsch = new JSch();
        Session session = jsch.getSession(sftpUser, sftpHost, sftpPort);
        ChannelSftp sftpChannel = null;
        // 2. SFTP传输
        try {
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");// 不严格检查主机密钥
            session.setTimeout(30000);// 30秒超时
            session.connect();// 建立会话连接
            sftpChannel = (ChannelSftp) session.openChannel("sftp");// 创建SFTP通道并连接
            sftpChannel.connect();
            // 创建远程目录（如果不存在）
            mkdirs(sftpChannel, path);
            // 文件名处理：使用临时文件避免传输过程中被读取不完整文件
            String reportFileName = csvFile.getName();// 最终文件名（如：XXX_20260101.csv）
            log.info("上传SFTP的最终文件名: {}", reportFileName);
            String tempFileName = csvFile.getName().replace(".csv", ".tmp");// 临时文件名（如：XXX_20260101.tmp）
            log.info("上传SFTP的临时文件名: {}", tempFileName);

            // 传输文件：使用临时文件名上传
            try (FileInputStream fis = new FileInputStream(csvFile)) {
                sftpChannel.put(fis, path + "/" + tempFileName);
            } catch (Exception e) {
                log.error("上传远程文件异常", e);
                throw new MithrasException("上传远程文件异常");
            }
            // 原子性操作：临时文件重命名为正式文件（确保传输完整性）
            sftpChannel.rename(path + "/" + tempFileName, path + "/" + reportFileName);
        } catch (Exception e) {
            log.error("上传远程文件异常", e);
            throw new MithrasException("上传远程文件异常");
        } finally {
            // 3. 删除本地临时文件
            if (sftpChannel != null) {
                sftpChannel.disconnect(); // 关闭SFTP通道
            }
            session.disconnect(); // 关闭会话
            csvFile.delete(); // 删除本地临时文件
        }
    }

    /**
     * 生成CSV格式的文件，使用"|*|"作为分隔符，UTF-8编码
     * 包含固定表头和数据行
     *
     * @param <T> 数据类型泛型
     * @param fileName 输出文件名
     * @param dataList 数据列表
     * @return 生成的CSV文件对象
     * @throws IOException 文件写入异常
     */
    public <T> File generateCsvFile(String fileName, List<T> dataList) throws IOException {
        if (dataList == null || dataList.isEmpty()) {
            throw new MithrasException("数据不能为空");
        }

        File file = new File(fileName);
        Class<?> clazz = dataList.get(0).getClass();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // 写入固定表头
            String[] fixedHeaders = {HEADER_1, HEADER_2, HEADER_3};
            writer.write(String.join("|*|", fixedHeaders));
            writer.newLine();

            // 写入数据
            for (T item : dataList) {
                // 这里需要根据数据结构获取三个字段的值
                String itname = getFieldValue(item, HEADER_1);
                String creditcode = getFieldValue(item, HEADER_2);
                String type = getFieldValue(item, HEADER_3);

                writer.write(String.join("|*|", itname, creditcode, type));
                writer.newLine();
            }
        }

        return file;
    }

    // 获取字段值
    private <T> String getFieldValue(T item, String fieldName) {
        if (item == null) {
            return "";
        }

        // 如果item是Map类型
        if (item instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) item;
            Object value = map.get(fieldName);
            return value != null ? value.toString() : "";
        }

        // 如果item是Java对象，通过反射获取字段值
        try {
            Field field = item.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(item);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            // 如果反射失败，尝试通过getter方法获取
            try {
                String getterName = "get" +
                        fieldName.substring(0, 1).toUpperCase() +
                        fieldName.substring(1);
                Method getter = item.getClass().getMethod(getterName);
                Object value = getter.invoke(item);
                return value != null ? value.toString() : "";
            } catch (Exception ex) {
                return "";
            }
        }
    }

    // vw数据转换DTO
    private void convertVwToDTO(Set<RiskClientListFileDTO> results,List<ClientVwSync> vwSyncListSub,String type){
        if (CollectionUtil.isNotEmpty(vwSyncListSub)){
            for (ClientVwSync clientVwSync : vwSyncListSub) {
                RiskClientListFileDTO dto = new RiskClientListFileDTO();
                dto.setItname(clientVwSync.getClientName());
                dto.setCreditcode(clientVwSync.getCertNumber());
                dto.setType(type);
                results.add(dto);
            }
        }
    }

    // 外部数据转换DTO
    private void convertExcToDTO(Set<RiskClientListFileDTO> results,List<ExternalCustomer> subExtCusList,String type){
        if (CollectionUtil.isNotEmpty(subExtCusList)){
            for (ExternalCustomer customer : subExtCusList) {
                RiskClientListFileDTO dto = new RiskClientListFileDTO();
                dto.setItname(customer.getClientName());
                dto.setCreditcode(customer.getUscCode());
                dto.setType(type);
                results.add(dto);
            }
        }
    }

    // 记录表数据转换DTO
    private void convertRecordToDTO(Set<RiskClientListFileDTO> results,List<ClientSandRecord> delRecordList,String type){
        if (CollectionUtil.isNotEmpty(delRecordList)){
            for (ClientSandRecord record : delRecordList) {
                RiskClientListFileDTO dto = new RiskClientListFileDTO();
                dto.setItname(record.getClientName());
                dto.setCreditcode(record.getUscCode());
                dto.setType(type);
                results.add(dto);
            }
        }
    }

    // vw转换记录表
    private List<ClientSandRecord> convertVwToRecord(List<ClientVwSync> vwSyncList,
                                                     String type, LocalDate today, String dataSource, String firstFlag){
        List<ClientSandRecord> sandRecordList = new ArrayList<>();
        if (CollectionUtil.isEmpty(vwSyncList)){
            return sandRecordList;
        }
        for (ClientVwSync vwSync : vwSyncList) {
            ClientSandRecord record = new ClientSandRecord();
            record.setClientId(Long.valueOf(vwSync.getClientId()));
            record.setClientName(vwSync.getClientName());
            record.setUscCode(vwSync.getCertNumber());
            record.setRecordDate(today);
            record.setDataSource(dataSource);
            record.setDataType(type);
            record.setFirstMark(firstFlag);
            sandRecordList.add(record);
        }
        return sandRecordList;
    }

    // 外部数据转换记录表
    private List<ClientSandRecord> convertExcToRecord(List<ExternalCustomer> extCusList,
                                                     String type, LocalDate today, String dataSource, String firstFlag){
        List<ClientSandRecord> sandRecordList = new ArrayList<>();
        if (CollectionUtil.isEmpty(extCusList)){
            return sandRecordList;
        }
        for (ExternalCustomer customer : extCusList) {
            ClientSandRecord record = new ClientSandRecord();
            record.setClientName(customer.getClientName());
            record.setUscCode(customer.getUscCode());
            record.setRecordDate(today);
            record.setDataSource(dataSource);
            record.setDataType(type);
            record.setFirstMark(firstFlag);
            sandRecordList.add(record);
        }
        return sandRecordList;
    }

    private void updateRecordInfo(List<ClientSandRecord> wbList,String type,String dataMark){
        if (CollectionUtil.isEmpty(wbList)){
            return;
        }
        for (ClientSandRecord record : wbList) {
            record.setDataType(type);
            record.setDataMark(dataMark);
        }
        clientSandRecordService.updateBatchById(wbList);
    }

    /**
     * 递归创建远程SFTP目录
     * 逐级检查并创建不存在的目录
     *
     * @param sftp SFTP通道对象
     * @param path 要创建的目录路径（如：/home/user/reports）
     * @throws SftpException SFTP操作异常
     */
    private void mkdirs(ChannelSftp sftp, String path) throws SftpException {
        String[] folders = path.split("/");
        String currentPath = "";
        // 逐级创建目录
        for (String folder : folders) {
            if (folder.isEmpty()) continue;// 跳过空字符串（如开头的"/"产生的空元素）
            currentPath += "/" + folder;// 构建当前层级路径
            try {
                sftp.stat(currentPath); // 检查目录是否存在
            } catch (SftpException e) {
                sftp.mkdir(currentPath);// 不存在则创建
            }
        }
    }

}
