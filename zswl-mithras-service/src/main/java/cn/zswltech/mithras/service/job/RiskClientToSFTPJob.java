package cn.zswltech.mithras.service.job;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlClientListFileService;
import cn.zswltech.mithras.service.service.riskcontrol.dto.RiskClientListFileDTO;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * XXL-Job定时任务：生成客户沙盘数据上传至慧眼SFTP服务器
 */
@Slf4j
@Component
public class RiskClientToSFTPJob {

    @Value("${spring.profiles.active}")
    private String active; // 获取当前环境配置项
    @Value("${xinsight.sftp.fileNamePrefix:}")
    private String FILE_NAME_PREFIX; // 文件名称前缀
    @Value("${xinsight.sftp.pathPrefix:}")
    private String PATH_PREFIX; // SFTP目录前缀

    @Resource
    private RiskControlClientListFileService riskControlClientListFileService;

    private static final String PROD = "prod";
    private static final String TEST = "test";

    @XxlJob("syncRiskClientToSFTPJob") // 0 0 8 * * ?   每天8：00
    public void syncRiskClientToSFTPJob() {
        log.info("------开始执行客户沙盘数据上传至慧眼SFTP服务器任务------");

        try {
            long startTime = System.currentTimeMillis();

            String param = XxlJobHelper.getJobParam();
            log.info("XxlJob传参param: {}", param);
            // 获取客户列表并生成csv文件上传至SFTP
            riskClientToSFTP(param);

            log.info("------SFTP传输完成，耗时 {} ms------", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("------SFTP传输任务执行失败------", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void riskClientToSFTP(String param) throws Exception {
        LocalDate today = LocalDate.now();
        if(ObjectUtil.isNotEmpty(param)) {
            today = LocalDateTimeUtil.parse(param, "yyyy-MM-dd").toLocalDate();
        }
        // 1. 获取客户沙盘数据
        List<RiskClientListFileDTO> clientList = riskControlClientListFileService.getClientList(today);
        if (CollectionUtil.isEmpty(clientList)) {
            log.info("客户沙盘数据为空，任务终止。");
            return;
        }
        log.info("客户沙盘数据条数: {}", clientList.size());

        // 2. 执行SFTP传输
        String dateStr = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        String fileName = FILE_NAME_PREFIX + dateStr + GlobalConstants.OFFICE_CSV_SUFFIX;
        log.info("客户沙盘文件名称: {}", fileName);
        String pathSub = "";
        if (StrUtil.equalsAny(active, PROD)){
            pathSub = PROD;// 生产环境目录拼接
        }else {
            pathSub = TEST;// 测试环境目录拼接
        }
        String path = "/" + PATH_PREFIX + "/" + pathSub + "/" + dateStr;
        log.info("SFTP服务器目录路径: {}", path);
        riskControlClientListFileService.createAndTransferCsv(fileName, clientList, path);
    }
}
