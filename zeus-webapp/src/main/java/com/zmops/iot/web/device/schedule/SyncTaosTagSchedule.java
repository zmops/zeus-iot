package com.zmops.iot.web.device.schedule;

import com.zmops.zeus.driver.service.ZbxItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * @author yefei
 **/
@EnableScheduling
@Component
@Slf4j
public class SyncTaosTagSchedule {

    @Autowired
    ZbxItem zbxItem;

//    @Scheduled(cron = "0 01 12 1/1 * ? ")
//    public void sync() {
//        List<String> deviceIds = new QDevice().select(QDevice.alias().deviceId).findSingleAttributeList();
//        List<Tag>    tagList   = new QTag().sid.in(deviceIds).findList();
//        if (ToolUtil.isEmpty(tagList)) {
//            return;
//        }
//        Map<String, List<Tag>>    tagMap      = tagList.parallelStream().collect(Collectors.groupingBy(Tag::getSid));
//        List<Map<String, Object>> historyDesc = JdbcConnectionHolder.INSTANCE.getConnection().queryForList("DESCRIBE history");
//        List<String> taosTagNames = historyDesc.parallelStream().filter(o -> "TAG".equals(new String((byte[]) o.get("Note")))).map(o -> new String((byte[]) o.get("Field")))
//                .collect(Collectors.toList());
//
//        if (ToolUtil.isEmpty(taosTagNames)) {
//            return;
//        }
//        List<String> tagNames = tagList.parallelStream().map(Tag::getTag).collect(Collectors.toList());
//        //新增taos没有的标签
//        addTaosTag(tagNames, taosTagNames);
//        //删除taos标签
//        delTaosTag(taosTagNames, tagNames);
//
//        //更新子表 标签值
//        tagMap.forEach((deviceId, tags) -> {
//            //根据deviceId 找到 设备下所有itemId
//            String hostid = new QDevice().select(QDevice.alias().zbxId).deviceId.eq(deviceId).findSingleAttribute();
//            if (ToolUtil.isEmpty(hostid)) {
//                return;
//            }
//            List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemList(null, hostid), ZbxItemInfo.class);
//            List<String>      itemIds   = itemInfos.parallelStream().map(ZbxItemInfo::getItemid).collect(Collectors.toList());
//
//            itemIds.forEach(itemId -> {
//                tags.forEach(tag -> {
//                    JdbcConnectionHolder.INSTANCE.getConnection().execute("ALTER TABLE history_uint SET TAG " + tag.getTag() + "=" + tag.getValue());
//                });
//            });
//
//        });
//    }
//
//    private void delTaosTag(List<String> list1, List<String> list2) {
//        List<String> tagNames     = list2;
//        List<String> taosTagNames = list1;
//        taosTagNames.removeAll(tagNames);
//        if (ToolUtil.isEmpty(taosTagNames)) {
//            return;
//        }
//        taosTagNames.forEach(tagName -> {
//            JdbcConnectionHolder.INSTANCE.getConnection().execute("ALTER STABLE history_uint DROP  TAG " + tagName);
//            JdbcConnectionHolder.INSTANCE.getConnection().execute("ALTER STABLE history DROP  TAG " + tagName);
//        });
//    }
//
//    private void addTaosTag(List<String> list1, List<String> list2) {
//        List<String> tagNames     = list1;
//        List<String> taosTagNames = list2;
//        tagNames.removeAll(taosTagNames);
//        if (ToolUtil.isEmpty(tagNames)) {
//            return;
//        }
//        tagNames.forEach(tagName -> {
//            JdbcConnectionHolder.INSTANCE.getConnection().execute("ALTER STABLE history_uint ADD TAG " + tagName + " NCHAR(16)");
//            JdbcConnectionHolder.INSTANCE.getConnection().execute("ALTER STABLE history ADD TAG " + tagName + " NCHAR(16)");
//        });
//    }

}
