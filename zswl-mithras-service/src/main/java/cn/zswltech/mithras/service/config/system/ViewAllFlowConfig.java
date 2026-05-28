package cn.zswltech.mithras.service.config.system;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.mapper.SystemConfigMapper;
import cn.zswltech.mithras.service.mapper.model.SystemConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yibin
 */
@Component
public class ViewAllFlowConfig {

    private List<String> userAccountList = new ArrayList<>();

    private List<String> deptCodeList = new ArrayList<>();

    private List<String> jobCodeList = new ArrayList<>();


    @Resource
    private SystemConfigMapper systemConfigMapper;

    public static final String CONFIG_KEY = "view_all_process";

    @PostConstruct
    public void init() {
        SystemConfig one = systemConfigMapper.selectOne(Wrappers.<SystemConfig>lambdaQuery()
                .eq(SystemConfig::getStatus, 1)
                .eq(SystemConfig::getConfigKey, CONFIG_KEY)
        );

        if (null != one) {
            String value = one.getConfigValue();
            if (StrUtil.isNotBlank(value)) {
                JSONObject jo = JSONUtil.parseObj(value);
                JSONArray userAccountArray = jo.getJSONArray("userAccountList");
                JSONArray deptCodeArray = jo.getJSONArray("deptCodeList");
                JSONArray jobCodeArray = jo.getJSONArray("jobCodeList");
                //
                this.userAccountList = userAccountArray.toList(String.class);
                this.jobCodeList = jobCodeArray.toList(String.class);
                this.deptCodeList = deptCodeArray.toList(String.class);
            }
        }
    }

}
