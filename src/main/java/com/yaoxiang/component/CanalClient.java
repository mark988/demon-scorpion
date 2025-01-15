package com.yaoxiang.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.yaoxiang.entity.PushTable;
import com.yaoxiang.entity.TableColumn;
import com.yaoxiang.utils.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maxiaoguang
 */
@Slf4j
@Component
public class CanalClient {

    //private final static int BATCH_SIZE = 1000;
    @Autowired
    private CanalConfig canalConfig;

    @Autowired
    private DBConfig dbConfig;

    public void run() {

        /**
         * 创建链接
         */
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalConfig.getHost(), canalConfig.getPort()), dbConfig.getName(), dbConfig.getUser(), dbConfig.getPasswd());
        try {

            connector.connect();
            /**
             * 订阅数据库表,全部表
             */
            connector.subscribe(".*\\..*");
            /**
             * 回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
             */
            connector.rollback();
            while (true) {
                /**
                 * 获取指定数量的数据
                  */
                Message message = connector.getWithoutAck(canalConfig.getBatchSize());
                /**
                 * 获取批量ID
                 */
                long batchId = message.getId();
                /**
                 *  获取批量的数量
                 */
                int size = message.getEntries().size();
                //如果没有数据
                if (batchId == -1 || size == 0) {
                    try {
                        //线程休眠2秒
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //如果有数据,处理数据
                    printEntry(canalConfig.getPushUrl(),message.getEntries());
                }
                //进行 batch id 的确认。确认之后，小于等于此 batchId 的 Message 都会被确认。
                // 确认这批变更事件已经被处理
                connector.ack(batchId);
            }
        } catch (Exception e) {
            log.info("发生了异常:{}",e.getMessage());
            e.printStackTrace();
        } finally {
            connector.disconnect();
        }
    }

    /**
     * 打印canal server解析binlog获得的实体类信息
     */
    private static void printEntry(String url, List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                //开启/关闭事务的实体类型，跳过
                continue;
            }
            /**
             * RowChange对象，包含了一行数据变化的所有特征
             * 比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
             */
            CanalEntry.RowChange rowChage;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR # parser of eromanga-event has an error , data:" + entry.toString(), e);
            }

            /**
             * 获取操作类型：insert/update/delete类型
             */
            CanalEntry.EventType eventType = rowChage.getEventType();
            //打印Header信息
            log.info("日志binlog={} ,offset={} ; 数据库={} 表={} ; 事件类型={} ",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType);

            /**
             *  判断是否是DDL语句
             */
            if (rowChage.getIsDdl()) {
                log.info("================》;isDdl: true,sql:" + rowChage.getSql());
            }
            List<Map<String,TableColumn>> list = new ArrayList<>();
            //获取RowChange对象里的每一行数据，打印出来
            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                //如果是删除语句
                //log.info("事件类型 {}",eventType);
                Map<String,TableColumn> row = new HashMap<>();
                if (eventType == CanalEntry.EventType.DELETE) {
                    row= printColumn(rowData.getBeforeColumnsList());
                    //如果是新增语句
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    //log.info("事件类型 {}",eventType);
                    row=printColumn(rowData.getAfterColumnsList());
                    //如果是更新的语句
                } else {
                    //变更前的数据
                    log.info("变更前的数据------->; before");
                     printColumn(rowData.getBeforeColumnsList());
                    //变更后的数据
                    log.info("变更后的数据------->; after");
                    row= printColumn(rowData.getAfterColumnsList());
                }
                list.add(row);
            }
            PushTable pushTable = PushTable.builder() .eventType(eventType.name()).binlog(entry.getHeader().getLogfileName())
                                                .schemaName(entry.getHeader().getSchemaName()).table(entry.getHeader().getTableName())
                                                .list(list) .build();

            OkHttpUtils.post(url, JSON.toJSONString(pushTable));
        }
    }

    private static Map<String, TableColumn> printColumn(List<CanalEntry.Column> columns) {
        Map<String,TableColumn> e = new HashMap<>();
        for (CanalEntry.Column column : columns) {
            e.put(column.getName(),TableColumn.builder().value(column.getValue()).update(column.getUpdated()).build());

            //log.info("列"+column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
        return e;
    }
}