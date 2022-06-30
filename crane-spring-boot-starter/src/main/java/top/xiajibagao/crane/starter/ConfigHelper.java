package top.xiajibagao.crane.starter;

import org.springframework.beans.factory.ListableBeanFactory;
import top.xiajibagao.crane.core.operator.interfaces.*;

/**
 * 配置工具类
 *
 * @author huangchengxing
 * @date 2022/06/27 17:39
 * @see 0.6.0
 */
public class ConfigHelper {

    private ConfigHelper() {
    }
    
    /**
     * 向{@link OperateProcessor}注册{@link SourceReader}、{@link SourceReadInterceptor}, {@link TargetWriter}与{@link TargetWriteInterceptor}
     *
     * @param operateProcessor 操作处理器
     * @param beanFactory beanFactory
     * @author huangchengxing
     * @date 2022/6/27 18:03
     */
    public static void registerForOperateProcessor(
        OperateProcessor operateProcessor, ListableBeanFactory beanFactory) {
        operateProcessor.registerSourceReaders(beanFactory.getBeansOfType(SourceReader.class).values().toArray(new SourceReader[0]));
        operateProcessor.registerSourceReadInterceptors(beanFactory.getBeansOfType(SourceReadInterceptor.class).values().toArray(new SourceReadInterceptor[0]));
        operateProcessor.registerTargetWriters(beanFactory.getBeansOfType(TargetWriter.class).values().toArray(new TargetWriter[0]));
        operateProcessor.registerTargetWriteInterceptors(beanFactory.getBeansOfType(TargetWriteInterceptor.class).values().toArray(new TargetWriteInterceptor[0]));
    }

}
