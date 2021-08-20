package com.zmops.iot.async.wrapper;

import com.zmops.iot.async.callback.ICallback;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.strategy.depend.DependOnUpWrapperStrategy;
import com.zmops.iot.async.wrapper.strategy.depend.DependenceStrategy;
import com.zmops.iot.async.wrapper.strategy.skip.SkipStrategy;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 作为优化编排依赖策略后，新增的Builder接口。
 * <p/>
 * 该接口中不再开放很多过时的api。
 *
 * @author create by TcSnZh on 2021/5/4-下午1:26
 */
@SuppressWarnings("unused")
public interface WorkerWrapperBuilder<T, V> {
    /**
     * 设置唯一id。
     * 如果不设置，{@link StableWorkerWrapperBuilder}会使用UUID
     */
    WorkerWrapperBuilder<T, V> id(String id);

    /**
     * 设置{@link IWorker}执行方法。
     *
     * @param worker 传入接口实现类/lambda
     */
    WorkerWrapperBuilder<T, V> worker(IWorker<T, V> worker);

    /**
     * wrapper启动后的传入参数。
     *
     * @param t 参数
     */
    WorkerWrapperBuilder<T, V> param(T t);

    /**
     * 设置{@link ICallback}回调方法。
     */
    WorkerWrapperBuilder<T, V> callback(ICallback<T, V> callback);

    /**
     * 设置跳过策略。通常用于检查下游Wrapper是否已经完成。
     * <p/>
     * 允许不设置。{@link StableWorkerWrapperBuilder}将会默认设置为检查深度为1的下游Wrapper是否执行完成。
     *
     * @param strategy 跳过策略函数。
     */
    WorkerWrapperBuilder<T, V> setSkipStrategy(SkipStrategy strategy);

    /**
     * 设置上游Wrapper依赖关系的选项。
     */
    SetDepend<T, V> setDepend();

    @SuppressWarnings({"UnusedReturnValue"})
    interface SetDepend<T, V> {
        /**
         * 设置在本Wrapper之前的上游Wrapper。
         *
         * @param wrapper 允许传入null。
         */
        SetDepend<T, V> wrapper(WorkerWrapper<?, ?> wrapper);

        default SetDepend<T, V> wrapper(WorkerWrapper... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                wrapper(wrapper);
            }
            return this;
        }

        default SetDepend<T, V> wrapper(Collection<? extends WorkerWrapper> wrappers) {
            if (wrappers == null) {
                return this;
            }
            wrappers.forEach(this::wrapper);
            return this;
        }

        /**
         * 设置必须要执行成功的Wrapper，当所有被该方法设为的上游Wrapper执行成功时，本Wrapper才能执行
         */
        SetDepend<T, V> mustRequireWrapper(WorkerWrapper<?, ?> wrapper);

        default SetDepend<T, V> mustRequireWrapper(WorkerWrapper... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                mustRequireWrapper(wrapper);
            }
            return this;
        }

        /**
         * 一个用于动态判断是否must的方法，与旧的{@code .depend(WorkerWrapper,boolean)}效果相同。
         *
         * @param must 如果为true，则等同于{@link #mustRequireWrapper(WorkerWrapper)}，否则等同于{@link #wrapper(WorkerWrapper)}
         */
        default SetDepend<T, V> requireWrapper(WorkerWrapper<?, ?> wrapper, boolean must) {
            return must ? mustRequireWrapper(wrapper) : wrapper(wrapper);
        }

        /**
         * 对单个Wrapper设置特殊策略。
         *
         * @param wrapper  需要设置特殊策略的Wrapper。
         * @param strategy 特殊策略。
         */
        SetDepend<T, V> specialDependWrapper(DependOnUpWrapperStrategy strategy, WorkerWrapper<?, ?> wrapper);

        default SetDepend<T, V> specialDependWrapper(DependOnUpWrapperStrategy strategy, WorkerWrapper... wrappers) {
            if (strategy == null || wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> workerWrapper : wrappers) {
                specialDependWrapper(strategy, workerWrapper);
            }
            return this;
        }

        /**
         * 设置基本策略并返回。
         * <p>
         * 如果从未调用该方法，则在{@link #build()}时使用{@link #defaultStrategy()}作为默认策略。
         * </p>
         *
         * @param dependenceStrategy 根据上游Wrapper判断本Wrapper是否启动的最终策略。
         */
        SetDepend<T, V> strategy(DependenceStrategy dependenceStrategy);

        /**
         * 默认策略为{@link DependenceStrategy#ALL_DEPENDENCIES_ALL_SUCCESS}
         */
        default SetDepend<T, V> defaultStrategy() {
            return strategy(DependenceStrategy.ALL_DEPENDENCIES_ALL_SUCCESS);
        }

        /**
         * 结束依赖关系设置。返回到所属的{@link WorkerWrapperBuilder}
         */
        WorkerWrapperBuilder<T, V> end();
    }

    /**
     * 便捷式设置依赖的上游Wrapper。
     *
     * @param wrappers 上游Wrapper
     */
    default WorkerWrapperBuilder<T, V> depends(WorkerWrapper... wrappers) {
        return setDepend().wrapper(wrappers).end();
    }

    default WorkerWrapperBuilder<T, V> depends(Collection<WorkerWrapper> wrappers) {
        return setDepend().wrapper(wrappers).end();
    }

    default WorkerWrapperBuilder<T, V> depends(DependenceStrategy strategy, WorkerWrapper... wrappers) {
        return setDepend().wrapper(wrappers).strategy(strategy).end();
    }

    default WorkerWrapperBuilder<T, V> depends(DependenceStrategy strategy, Collection<WorkerWrapper> wrappers) {
        return setDepend().wrapper(wrappers).strategy(strategy).end();
    }

    /**
     * 设置下游Wrapper依赖关系的选项。
     */
    SetNext<T, V> setNext();

    interface SetNext<T, V> {
        /**
         * 设置在本Wrapper之后的下游Wrapper。
         */
        SetNext<T, V> wrapper(WorkerWrapper<?, ?> wrapper);

        default SetNext<T, V> wrapper(WorkerWrapper... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                wrapper(wrapper);
            }
            return this;
        }

        default SetNext<T, V> wrapper(Collection<? extends WorkerWrapper> wrappers) {
            if (wrappers == null) {
                return this;
            }
            wrappers.forEach(this::wrapper);
            return this;
        }

        /**
         * 调用该方法将会让传入的此下游workerWrappers对本Wrapper强依赖(must)
         *
         * @param wrapper 下游Wrapper
         */
        SetNext<T, V> mustToNextWrapper(WorkerWrapper<?, ?> wrapper);

        default SetNext<T, V> requireToNextWrapper(WorkerWrapper<?, ?> wrapper, boolean must) {
            return must ? mustToNextWrapper(wrapper) : wrapper(wrapper);
        }

        /**
         * 调用该方法将会让传入的此下游workerWrappers对本Wrapper进行特殊策略判断，
         *
         * @param strategy 对本Wrapper的特殊策略。
         * @param wrapper  依赖本Wrapper的下游Wrapper。
         * @return 返回Builder自身。
         */
        SetNext<T, V> specialToNextWrapper(DependOnUpWrapperStrategy strategy, WorkerWrapper<?, ?> wrapper);

        WorkerWrapperBuilder<T, V> end();
    }

    /**
     * 便捷式设置本Wrapper被依赖的下游Wrapper。
     *
     * @param wrappers 下游Wrapper
     */
    default WorkerWrapperBuilder<T, V> nextOf(WorkerWrapper... wrappers) {
        return setNext().wrapper(wrappers).end();
    }

    default WorkerWrapperBuilder<T, V> nextOf(Collection<WorkerWrapper> wrappers) {
        return setNext().wrapper(wrappers).end();
    }

    /**
     * 设置超时时间的具体属性
     */
    SetTimeOut<T, V> setTimeOut();

    interface SetTimeOut<T, V> {
        /**
         * 是否启动超时判断。
         * <p>
         * 默认为true
         *
         * @param enableElseDisable 是则true
         */
        SetTimeOut<T, V> enableTimeOut(boolean enableElseDisable);

        /**
         * 设置单个WorkerWrapper的超时时间。若不设置则不进行超时判断
         *
         * @param time 时间数值
         * @param unit 时间单位
         */
        SetTimeOut<T, V> setTime(long time, TimeUnit unit);

        WorkerWrapperBuilder<T, V> end();
    }

    /**
     * 便携式设置单个WorkerWrapper的超时时间。若不设置则不进行超时判断
     *
     * @param time 时间数值
     * @param unit 时间单位
     */
    default WorkerWrapperBuilder<T, V> timeout(long time, TimeUnit unit) {
        return timeout(true, time, unit);
    }

    default WorkerWrapperBuilder<T, V> timeout(boolean enableTimeOut, long time, TimeUnit unit) {
        return setTimeOut().enableTimeOut(enableTimeOut).setTime(time, unit).end();
    }

    /**
     * 是否允许被试图中断线程
     *
     * @param allow 是则true
     */
    WorkerWrapperBuilder<T, V> allowInterrupt(boolean allow);

    /**
     * 构建Wrapper。
     *
     * @return 返回WorkerWrapper
     */
    WorkerWrapper<T, V> build();
}
