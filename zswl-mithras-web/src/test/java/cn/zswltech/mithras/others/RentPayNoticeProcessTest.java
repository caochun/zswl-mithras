package cn.zswltech.mithras.others;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.fund.RentPayNoticeProcessDTO;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/13/14:30
 * @description
 */
@Slf4j
@ActiveProfiles(value = "dev")
@RunWith(JUnit4.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentPayNoticeProcessTest {

    @Test
    public void generateSql() {
        RentPayNoticeProcessDTO rentPayNoticeProcessDTO = new RentPayNoticeProcessDTO();
        List<RentPayNoticeProcessDTO> data = new ArrayList<>();

//        data.add(getDto("JCSSYWB", Collections.singletonList(107L)));
//        data.add(getDto("GGSY", Collections.singletonList(126L)));
//        data.add(getDto("XNYYWB", Collections.singletonList(126L)));
//        data.add(getDto("JTYSYWB", Collections.singletonList(184L)));
//        data.add(getDto("HYYWB", Collections.singletonList(184L)));
//        data.add(getDto("SYCYWB", Collections.singletonList(108L)));
//        data.add(getDto("JSSYB", Collections.singletonList(108L)));
//        data.add(getDto("XJZZHXJJTD", Collections.singletonList(108L)));

        data.add(getDto("JCSSYWB", Collections.singletonList(107L)));
        data.add(getDto("GGSY", Collections.singletonList(109L)));
        data.add(getDto("XNYYWB", Collections.singletonList(109L)));
        data.add(getDto("JTYSYWB", Collections.singletonList(106L)));
        data.add(getDto("HYYWB", Collections.singletonList(106L)));
        data.add(getDto("SYCYWB", Collections.singletonList(108L)));
        data.add(getDto("JSSYB", Collections.singletonList(108L)));
        data.add(getDto("XJZZHXJJTD", Collections.singletonList(108L)));

        System.out.println(JSONUtil.parse(data));
    }

    private RentPayNoticeProcessDTO getDto(String deptCode, List<Long> userId) {
        RentPayNoticeProcessDTO dto = new RentPayNoticeProcessDTO();
        dto.setDeptCode(deptCode);
        dto.setUserIds(userId);
        return dto;
    }
}
