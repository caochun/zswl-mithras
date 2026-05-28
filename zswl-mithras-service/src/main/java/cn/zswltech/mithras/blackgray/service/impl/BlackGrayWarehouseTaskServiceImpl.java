package cn.zswltech.mithras.blackgray.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.DictionaryService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.gruul.dao.dal.entity.DictionaryDO;
import cn.zswltech.gruul.dao.dal.query.DictionaryQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseTaskAddREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseTaskListREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseTaskModifyREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseTaskRemoveREQ;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayBusinessTaskTypeEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayOrgEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseRecordMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayWarehouseTaskMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayWarehouseTask;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseTaskService;
import cn.zswltech.mithras.blackgray.service.GruulAuthService;
import cn.zswltech.mithras.blackgray.service.RedisService;
import cn.zswltech.mithras.blackgray.utils.StringUtils;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 黑灰名单任务表
* @author 
* @date 2024-01-16
*/
@Service
@Slf4j
public class BlackGrayWarehouseTaskServiceImpl implements BlackGrayWarehouseTaskService {

    @Resource
    private BlackGrayWarehouseTaskMapper blackGrayWarehouseTaskMapper;
    @Resource
    private RedisService redisService;
    /*@Resource
    private BlackGrayOutboundAuditService blackGrayOutboundAuditService;*/
    @Resource
    private BlackGrayWarehouseRecordMapper blackGrayWarehouseRecordMapper;
    @Resource
    DictionaryService dictionaryService;
   /* @Resource
    NoticeUtil noticeUtil;*/
    @Resource
    GruulAuthService gruulAuthService;
    @Resource
    private SysUserService sysUserService;


    private static final String BLACK_GRAY_TASK_NUM_LOCK = "BLACK_GRAY_TASK_NUM_LOCK:";

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long add(BlackGrayWarehouseTaskAddREQ req) {
        String taskNum = null;
        BlackGrayWarehouseTask info = BeanUtil.copyProperties(req, BlackGrayWarehouseTask.class);
        try {
            Long accountId = AccountUtil.getLoginInfo().getId();
            if (ObjectUtil.isEmpty(req.getOrgCode())) {
                info.setOrgCode(BlackGrayOrgEnum.ZSZL.name());
            }
            taskNum = getTaskNum(info, 3);
            info.setTaskNum(taskNum);
            if(ObjectUtil.isEmpty(info.getTimePoint())){
                LocalDate now = LocalDate.now();
                info.setTimePoint(now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM")));
            }
            if (CollectionUtil.isNotEmpty(req.getUploadFileList())) {
                info.setUploadFile(JSONUtil.toJsonStr(req.getUploadFileList()));
            }
            info.setAuditStatus((int) AuditStatusEnum.WAIT.getCode());
            info.setCurrentOperator(String.valueOf(accountId));
            info.setCreatedBy(String.valueOf(accountId));
            info.setUpdatedBy(String.valueOf(accountId));
            blackGrayWarehouseTaskMapper.insert(info);
        }finally {
            redisService.del(BLACK_GRAY_TASK_NUM_LOCK + taskNum);
        }

        /*
            对接待办，给生成出来任务的接收岗发待办
            只有生成的定期任务需要发待办，页面用户手动创建的非定期任务不用待办
         */
        if (BlackGrayBusinessTaskTypeEnum.TIMED.name().equals(req.getTaskType())) {
            DictionaryQuery query = new DictionaryQuery();
            query.setDictKey("blackGrayWarehouseTaskAssignRoles");
            Response<List<DictionaryDO>> response = dictionaryService.getDictionaryListNoPage(query);
            if (!response.getSuccess() || CollectionUtils.isEmpty(response.getData())) {
                log.warn("黑灰名单任务派发待办字典未配置, dictKey={}", query.getDictKey());
            } else {
                List<DictionaryDO> orgCfg = response.getData().stream().filter(d -> req.getOrgCode().equals(d.getCode())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(orgCfg) || org.apache.commons.lang3.StringUtils.isBlank(orgCfg.get(0).getDisplay())) {
                    log.warn("黑灰名单任务派发字典中机构{}未配置接受岗位角色", req.getOrgCode());
                } else {
                    String dealRoleCode = orgCfg.get(0).getDisplay();
                    // 根据字典角色code配置，查出用户，发待办
                    Map<String, List<UserVO>> dealUserDOMap = gruulAuthService.getUserByRoleCodes(Collections.singletonList(dealRoleCode));
                    Set<String> dealUsers = new HashSet<>();
                    for (List<UserVO> users : dealUserDOMap.values()) {
                        dealUsers.addAll(users.stream().map(UserVO::getAccount).collect(Collectors.toSet()));
                    }
                    /*// 发送待办
                    AuditNoticeModelAndUrlEnum noticeModelAndUrlEnum = AuditNoticeModelAndUrlEnum.BLACK_GRAY_WAREHOUSE_TASK_ADD;
                    String content = String.format(
                            AuditNoticeContentTemplateConstant.TODO_CONTENT,
                            noticeModelAndUrlEnum.getModelName(),
                            info.getTaskNum(),
                            "待处理");
                    String url = String.format(noticeModelAndUrlEnum.getDetailUrl(), info.getId() + "?type=edit");
                    // 涉及待办中ticket的问题，只能一个个发
                    for (String dealUser : dealUsers) {
                        noticeUtil.buildAndSendOneTodoNoticeMsg(Collections.singletonList(req.getOrgCode()),Collections.singletonList(dealUser), content,
                                url, info.getId(),
                                FlowModelKeyConstant.BLACK_GRAY_WAREHOUSE_TASK, "system",
                                AuditStatusEnum.WAIT.getCode());
                    }*/
                }
            }
        }

        return info.getId();
    }

    //任务编号，唯一索引，保存时生成，机构缩写+8位年月日+至少3位自增数,如GDRF20230329001，理论位数应该是15位，考虑到后三位自增数极端情况下不一定够用，所以预留32位
    private String getTaskNum(BlackGrayWarehouseTask info, int tryNum) {
        String orgName = BlackGrayOrgEnum.ZSZL.name();
        BlackGrayWarehouseTask blackGrayWarehouseTask = blackGrayWarehouseTaskMapper.selectOne(Wrappers.<BlackGrayWarehouseTask>lambdaQuery()
                .between(BlackGrayWarehouseTask::getGmtCreate, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay())
                .orderByDesc(BlackGrayWarehouseTask::getTaskNumSequence)
                .last(StringUtils.mysqlLimitOne()));
        String oldTaskNum = blackGrayWarehouseTask == null ? null : blackGrayWarehouseTask.getTaskNumSequence();
        String taskNum;
        String taskNumSequence;
        if (oldTaskNum != null) {
            String[] split = oldTaskNum.split("-");
            if (split.length != 2) {
                throw new MithrasException("任务编号规则异常");
            }
            taskNumSequence = String.format("%s-%03d", split[0], Integer.parseInt(split[1]) + 1);
        } else {
            taskNumSequence = String.format("%s-%03d", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), 1);
        }
        taskNum = String.format("%s-%s",orgName.toUpperCase(Locale.ROOT), taskNumSequence);
        info.setTaskNum(taskNum);
        info.setTaskNumSequence(taskNumSequence);
        String value = redisService.get(BLACK_GRAY_TASK_NUM_LOCK + taskNum);
        if (StringUtil.isBlank(value)) {
            // 当前线程占用该taskNum，在redis中上锁，最长时间60秒，任务完成后解锁
            redisService.set(BLACK_GRAY_TASK_NUM_LOCK + taskNum, taskNum, 60);
            return taskNum;
        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("taskNum获取锁睡眠任务被打断", e);
                Thread.currentThread().interrupt();
            }
            getTaskNum(info, --tryNum);
        }
        throw new MithrasException("任务生成繁忙，请稍后重试");
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(BlackGrayWarehouseTaskModifyREQ req) {
        BlackGrayWarehouseTask originalInfo = blackGrayWarehouseTaskMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException("记录不存在");
        }
        BlackGrayWarehouseTask info = BeanUtil.copyProperties(req, BlackGrayWarehouseTask.class);
        if(CollectionUtil.isNotEmpty(req.getUploadFileList())){
            info.setUploadFile(JSONUtil.toJsonStr(req.getUploadFileList()));
        }
        blackGrayWarehouseTaskMapper.updateById(info);
    }
    @Override
    public BlackGrayWarehouseTask detail(Long id) {
        return blackGrayWarehouseTaskMapper.selectById(id);
    }

    @Override
    public PageR<BlackGrayWarehouseTask> list(BlackGrayWarehouseTaskListREQ req) {
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayWarehouseTask> records = blackGrayWarehouseTaskMapper.selectList(Wrappers.<BlackGrayWarehouseTask>lambdaQuery()
                .eq(BlackGrayWarehouseTask::getBusinessSource, req.getBusinessSource())
                .like(ObjectUtil.isNotEmpty(req.getTaskNum()), BlackGrayWarehouseTask::getTaskNum, req.getTaskNum())
                .eq(ObjectUtil.isNotEmpty(req.getTimePoint()), BlackGrayWarehouseTask::getTimePoint, req.getTimePoint())
                .in(CollectionUtil.isNotEmpty(req.getAuditStatus()), BlackGrayWarehouseTask::getAuditStatus, req.getAuditStatus())
                .eq(ObjectUtil.isNotEmpty(req.getOrgCode()), BlackGrayWarehouseTask::getOrgCode, req.getOrgCode())
                .eq(ObjectUtil.isNotEmpty(req.getOvertimeFlag()), BlackGrayWarehouseTask::getOvertimeFlag, req.getOvertimeFlag())
                .eq(BlackGrayWarehouseTask::getOrgCode, BlackGrayOrgEnum.ZSZL.name())
                .orderByDesc(BlackGrayWarehouseTask::getTaskNumSequence));
        PageInfo<BlackGrayWarehouseTask> pageInfo = new PageInfo<>(records);
        /*if (ObjectUtil.isNotEmpty(records)) {
            Map<Long, AuditTask> currentOperator = getCurrentOperator(records.stream().map(BlackGrayWarehouseTask::getId).collect(Collectors.toList()));
            records.forEach(record -> {
                AuditTask auditTask = currentOperator.get(record.getId());
                if (ObjectUtil.isNotEmpty(auditTask)) {
                    record.setCurrentOperator(auditTask.getCurrentOperator() == null ? record.getCreatedBy() : auditTask.getCurrentOperator());
                    record.setAuditTaskId(auditTask.getId());
                    record.setPreOperator(auditTask.getPreOperator());
                    record.setLatestMsg(auditTask.getLatestMsg());
                }
            });
        }*/
        return PageR.of(records, pageInfo.getTotal());
    }

    /*private Map<Long, AuditTask> getCurrentOperator(List<Long> bizIds){
        List<AuditTask> auditTasksByBizIds = blackGrayOutboundAuditService.getAuditTasksByBizIds(AuditBizTypeEnum.BLACK_GRAY_WAREHOUSE_TASK.getType(), bizIds);
        if(CollectionUtil.isEmpty(auditTasksByBizIds)){
            return MapUtil.empty();
        }
        return auditTasksByBizIds.stream().collect(Collectors.toMap(AuditTask::getBizId, e->e, (a, b) -> b));
    }*/

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(BlackGrayWarehouseTaskRemoveREQ req) {
        blackGrayWarehouseTaskMapper.deleteBatchIds(req.getIds());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateStatue(Long id, Integer status, Long taskId){
        BlackGrayWarehouseTask blackGrayWarehouseTask = blackGrayWarehouseTaskMapper.selectById(id);
        if(ObjectUtil.isEmpty(blackGrayWarehouseTask)){
            throw new MithrasException("记录不存在");
        }
        BlackGrayWarehouseTask record = new BlackGrayWarehouseTask();
        record.setId(id);
        record.setAuditTaskId(taskId);
        record.setAuditStatus(status);
        blackGrayWarehouseTaskMapper.updateById(record);
        blackGrayWarehouseRecordMapper.updateAuditStatus(blackGrayWarehouseTask.getTaskNum(), status);
    }


    /**
     * 更新任务逾期状态
     **/
    @XxlJob("blackGrayCheckTaskTimeoutJob")
    @Transactional(rollbackFor = Throwable.class)
    public void blackGrayCheckTaskTimeoutJob() {
       try {
           blackGrayWarehouseTaskMapper.blackGrayCheckTaskTimeoutJob();
       } catch (Exception e) {
           log.error("blackGrayAutoOutboundJob error", e);
       }
    }

}