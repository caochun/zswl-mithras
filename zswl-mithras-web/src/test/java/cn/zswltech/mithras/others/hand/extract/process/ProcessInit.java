package cn.zswltech.mithras.others.hand.extract.process;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.HandProcessOperateRecordMapper;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.HandProcessOperateRecord;
import cn.zswltech.mithras.service.service.SysUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程提取
 *
 * @author wangchuanhao
 * @date 2022/8/18 10:03 AM
 */
public class ProcessInit extends ApplicationTest {

    @Resource
    private HandProcessOperateRecordMapper handProcessOperateRecordMapper;
    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private SysUserService sysUserService;

    @Test
    public void extractList() {
        ProcessExtractor processExtractor = new ProcessExtractor();
        processExtractor.extractList();
    }

    @Test
    public void initData() throws Exception {
        // 找出当前所有用户 拿到id
        UserQuery userQuery = new UserQuery();
        userQuery.setPageSize(Integer.MAX_VALUE);
        Map<String, Long> userMap = userServiceAPI.queryUserSys(userQuery).getContents().stream().collect(Collectors.toMap(UserVO::getUserName, UserVO::getId));
        Map<Long, Long> userOrgMap = userMap.values().stream().collect(Collectors.toMap(id -> id, id -> sysUserService.getSpecificUserDeptList(id).get(0).getId()));

        List<ProcessExtractor.InitDataModel> dataModelList = JSONArray.parseArray(IoUtil.read(new ClassPathResource("init/流程模块数据整理json_线上_20220818.json").getInputStream(), Charset.defaultCharset()), ProcessExtractor.InitDataModel.class);
        List<HandProcessOperateRecord> recordList = dataModelList.stream().map(d -> {
            HandProcessOperateRecord record = new HandProcessOperateRecord();
            record.setModelName(d.getModelName());
            record.setProcessInstanceId(d.getProcessInstanceId());
            record.setProcessInstanceName(d.getProcessInstanceName());
            record.setStartTime(Optional.ofNullable(d.getStartTime()).map(s -> LocalDateTimeUtil.parse(s, "yyyy-MM-dd HH:mm:ss")).orElse(null));
            record.setEndTime(Optional.ofNullable(d.getEndTime()).map(s -> LocalDateTimeUtil.parse(s, "yyyy-MM-dd HH:mm:ss")).orElse(null));
            record.setOperateInfo(JSON.toJSONString(d.getOperateModelList()));
            record.setStartUserName(d.getStartUserName());
            record.setStartUserId(userMap.get(d.getStartUserName()));
            record.setStartUserDeptId(userOrgMap.get(record.getStartUserId()));

            record.setCreateBy(3L);
            record.setUpdateBy(3L);
            return record;
        }).collect(Collectors.toList());
        System.out.println(String.format("导入流程数据:%s条", recordList.size()));

        for (HandProcessOperateRecord handProcessRecord : recordList) {
            handProcessOperateRecordMapper.insert(handProcessRecord);
        }
    }

}
