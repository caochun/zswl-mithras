package cn.zswltech.mithras.third.datashare.service.job;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.basic.Constant;
import cn.zswltech.mithras.third.datashare.mapper.model.DataShareFk;
import cn.zswltech.mithras.third.datashare.mapper.model.DataShareManager;
import cn.zswltech.mithras.third.datashare.service.DataShareFkService;
import cn.zswltech.mithras.third.datashare.service.DataShareManagerService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ldhu
 * @date 2022/11/21
 * @description 1、定时调用 上次调用时点到当前时点区间内 增量查询 报销单单号查询接口，查看已完成的报销单
 * 2、报销单号记录到
 */
@Slf4j
@Component
public class DataShareFkJob {
    @Resource
    private DataShareManagerService dataShareManagerService;

    @Resource
    private DataShareFkService dataShareFkService;

    @XxlJob("archivesAndAttachment")
    public void archivesAndAttachment() {
        try {
            // 极端情况允许xxl传入开始时间,结束时间     {"lastModifyStartDate":"yyyy-MM-dd HH:mm:ss","lastModifyEndDate":"yyyy-MM-dd HH:mm:ss","businessNos":"A,B"}
            String param = XxlJobHelper.getJobParam();

            // 1、获取上次执行时间
            LambdaQueryWrapper<DataShareManager> query = Wrappers.lambdaQuery();
            query.eq(DataShareManager::getModelName, Constant.DATA_SHARE_FK);
            query.orderByDesc(DataShareManager::getEndTime);  // 按照最后执行时间降序
            query.last("limit 1");
            DataShareManager dataShareManager = dataShareManagerService.getOne(query);

            // 2、获取已完成报销单清单
            LocalDateTime now = LocalDateTime.now();
            String lastModifyStartDate = null;
            String lastModifyEndDate = timeString(now, Constant.DATE_TIME_PATTERN);
            if (ObjectUtil.isEmpty(dataShareManager)) { // 如果是初次执行，开始时间今天 00:00:00
                lastModifyStartDate = timeString(LocalDate.now().atStartOfDay(), Constant.DATE_TIME_PATTERN);
            } else {
                lastModifyStartDate = timeString(dataShareManager.getEndTime(), Constant.DATE_TIME_PATTERN);
            }
            if (ObjectUtil.isNotEmpty(param)) { // 如果xxljob指定了参数 最后修改开始时间,最后修改结束时间
                JSONObject paramJson = JSONObject.parseObject(param);
                if (StringUtils.isNotEmpty(paramJson.getString("businessNos"))) { // 指定报销单  需要将对应的报销单信息逻辑删除，并重发
                    List<String> businessNos = Arrays.asList(paramJson.getString("businessNos").split(","));
                    delShareByBysinessList(businessNos);
                    dataShareFkService.batchGetDetails(businessNos);
                    sendCQ2();
                    return;
                }
                if (StringUtils.isNotEmpty(paramJson.getString("lastModifyStartDate")) && StringUtils.isNotEmpty(paramJson.getString("lastModifyEndDate"))) {
                    lastModifyStartDate = paramJson.getString("lastModifyStartDate");
                    lastModifyEndDate = paramJson.getString("lastModifyEndDate");
                }
            }
            Set<String> archives = dataShareFkService.sendToArchives(lastModifyStartDate, lastModifyEndDate);

            // 变量列表，如果报销单已经在数据库则过滤掉
            if (ObjectUtil.isEmpty(archives)) {
                saveShareLog(lastModifyStartDate, lastModifyEndDate, archives);
                sendCQ2();
                return;
            }
            List<List<String>> batchList = splitList(new ArrayList<>(archives), 500);
            Set<String> savedSet = new HashSet<>();
            for (List<String> batch : batchList) {
                // 从数据库中查询已经存在报销单 记录到savedList
                getSavedSet(batch, savedSet);
            }

            // archives中剥离出savedList  剩下的去查询接口二
            List<String> businessNoList = archives.stream().filter(a -> !savedSet.contains(a)).collect(Collectors.toList());
            dataShareFkService.batchGetDetails(businessNoList);

            saveShareLog(lastModifyStartDate, lastModifyEndDate, archives);
            sendCQ2();
        } catch (Exception e) {
            log.error("数据共享接口定时任务异常", e);
            throw new RuntimeException(e);
        }
    }

    private void delShareByBysinessList(List<String> businessNos) {
        LambdaQueryWrapper<DataShareFk> query = Wrappers.lambdaQuery();
        query.in(DataShareFk::getBusinessNo, businessNos);
        query.eq(DataShareFk::getStatus, Constant.CQSENDSTATUS_SENDED);
        dataShareFkService.remove(query);
    }

    /**
     * 记录接口调用日志
     */
    private void saveShareLog(String lastModifyStartDate, String lastModifyEndDate, Set<String> archives) {
        // 记录执行历史
        DataShareManager dataShareManagerNew = new DataShareManager();
        dataShareManagerNew.setModelName(Constant.DATA_SHARE_FK);
        dataShareManagerNew.setStartTime(stringTime(lastModifyStartDate, Constant.DATE_TIME_PATTERN));
        dataShareManagerNew.setEndTime(stringTime(lastModifyEndDate, Constant.DATE_TIME_PATTERN));
        dataShareManagerNew.setPageSize(1000);
        dataShareManagerNew.setPageNum(archives.size() / 1000);
        dataShareManagerNew.setDataTotal(archives.size());
        dataShareManagerService.save(dataShareManagerNew);
    }


    /*发送苍穹  失败后下一次会自动重发，最多三次*/
    private void sendCQ2() {
        /*查询数据库中 未发送苍穹或者发送苍穹失败的次数<3*/
        LambdaQueryWrapper<DataShareFk> wrapper = Wrappers.lambdaQuery();
        wrapper.in(DataShareFk::getStatus, Constant.CQSENDSTATUS_UNSEND, Constant.CQSENDSTATUS_ERROR);
        wrapper.le(DataShareFk::getRetryNum, 3);  // 按照最后执行时间降序
        List<DataShareFk> dataList = dataShareFkService.list(wrapper);
        for (DataShareFk dataShareFk : dataList) {
            // 发送苍穹，并将发送结果记录数据库，如果成功变更状态为1，失败变更状态为-1，重试次数+1
            dataShareFkService.sendCQ2AttachmentSave(dataShareFk);
        }
    }

    /*数据库中分批次查询是否有已经存在的单据*/
    private void getSavedSet(List<String> batch, Set<String> savedList) {
        LambdaQueryWrapper<DataShareFk> query = Wrappers.lambdaQuery();
        query.in(DataShareFk::getBusinessNo, batch);
        List<DataShareFk> dataShareManagerList = dataShareFkService.list(query);
        if (ObjectUtil.isNotEmpty(dataShareManagerList)) {
            for (DataShareFk dataShareFk : dataShareManagerList) {
                savedList.add(dataShareFk.getBusinessNo());
            }
        }
    }


    /*集合按照一定大小分割，防止一次性in太多失败*/
    private static List<List<String>> splitList(List<String> source, int batchSize) {
        List<List<String>> result = new ArrayList<>();

        for (int i = 0; i < source.size(); i += batchSize) {
            // 计算当前批次的结束位置（不包含）
            int end = Math.min(i + batchSize, source.size());
            // 截取子列表
            result.add(new ArrayList<>(source.subList(i, end)));
        }

        return result;
    }

    /*字符串转化成时间*/
    private static LocalDateTime stringTime(String time, String pattern) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(time, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /*时间转化成字符串*/
    private static String timeString(LocalDateTime time, String pattern) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }
}
