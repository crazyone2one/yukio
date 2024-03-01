package cn.master.yukio.util;

import java.util.List;
import java.util.function.Consumer;

/**
 * 批量处理类
 *
 * @author Created by 11's papa on 03/01/2024
 **/
public class BatchProcessUtils {
    private final static int BATCH_SIZE = 200;

    public static void consumerByString(List<String> deleteIds, Consumer<List<String>> consumer) {
        if (deleteIds.size() > BATCH_SIZE) {
            int unProcessingCount = deleteIds.size();
            while (deleteIds.size() > BATCH_SIZE) {
                List<String> processingList = deleteIds.subList(0, BATCH_SIZE);
                consumer.accept(processingList);
                deleteIds.removeAll(processingList);
                if (deleteIds.size() == unProcessingCount) {
                    break;
                } else {
                    unProcessingCount = deleteIds.size();
                }
            }
            if (!deleteIds.isEmpty()) {
                consumer.accept(deleteIds);
            }
        } else {
            consumer.accept(deleteIds);
        }
    }
}
