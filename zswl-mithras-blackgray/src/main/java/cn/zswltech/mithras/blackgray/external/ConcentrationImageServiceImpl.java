package cn.zswltech.mithras.blackgray.external;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.blackgray.constant.ConcentrationConstans;
import cn.zswltech.mithras.blackgray.service.RedisService;
import cn.zswltech.mithras.blackgray.external.dto.GroupData;
import cn.zswltech.mithras.blackgray.external.dto.ImageTableQry;
import cn.zswltech.mithras.blackgray.external.dto.OuterGroupResponse;
import cn.zswltech.mithras.blackgray.util.ConcentrationRemoteUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:fengming.dai
 */
@Service
@Slf4j
public class ConcentrationImageServiceImpl {

    @Autowired
    private ConcentrationRemoteUtil remoteUtil;

    @Resource
    RedisService redisService;

    public ImageRelationDTO relation(ImageTableQry qry) {
        //获取所属集团
        ImageRelationDTO relationDTO = getGroup(qry.getName());
        relationDTO.setGroup(relationDTO.getGroup());
        qry.setGroupName(relationDTO.getGroup());
        //List<MemberInfo> list = getMemberInfo(qry);
        //relationDTO.setList(list);
        return relationDTO;
    }


    /**
     * 调用外部接口 获取公司 所属集团
     *
     * @param name
     * @return
     */
    public ImageRelationDTO getGroup(String name) {
        ImageRelationDTO imageRelationDTO = new ImageRelationDTO().setGroup("").setControllerName("");
        if (StringUtils.isEmpty(name)) {
            return imageRelationDTO;
        }
        String groupName = redisService.get(ConcentrationConstans.GROUP_TITLE_BY_COMPANY_NAME + name);
        String groupControllerName = redisService.get(ConcentrationConstans.GROUP_CONTROLLER_BY_COMPANY_NAME + name);
        log.info("集团信息查询缓存: companyName={}, groupTitle={}, groupControllerName={}", name, groupName, groupControllerName);
        if (!StringUtils.isEmpty(groupName) || !StringUtils.isEmpty(groupControllerName)) {
            return imageRelationDTO.setGroup(ObjectUtil.defaultIfNull(groupName, "")).setControllerName(ObjectUtil.defaultIfNull(groupControllerName, ""));
        }

        /**
         * 集团户外部接口 请求
         */
        String groupUrl = "/rdm-cloud-corp/corp_company_group/query?name=" + name;
        OuterGroupResponse outerGroupResponse = remoteUtil.warpHttpGet(groupUrl, OuterGroupResponse.class);
        List<GroupData> groupDataList = outerGroupResponse.getData();
        GroupData groupData = CollectionUtils.isEmpty(groupDataList) ? null : groupDataList.get(0);
        if (null != groupData) {
            groupName = groupData.getTitle();
            //所属集团 报送= 外部
            if (!org.apache.commons.lang3.StringUtils.isEmpty(groupName)) {
                //写入缓存， 缓存一天
                long seconds = 24 * 60 * 60L;
                redisService.set(ConcentrationConstans.GROUP_TITLE_BY_COMPANY_NAME + name, groupName, seconds);
            }
            if (!org.apache.commons.lang3.StringUtils.isEmpty(groupData.getController_name())) {
                //写入缓存， 缓存一天
                long seconds = 24 * 60 * 60L;
                redisService.set(ConcentrationConstans.GROUP_CONTROLLER_BY_COMPANY_NAME + name, groupData.getController_name(), seconds);
            }
            return imageRelationDTO.setGroup(groupData.getTitle()).setControllerName(groupData.getController_name());
        }
        return imageRelationDTO;
    }


}