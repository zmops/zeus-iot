package com.zmops.iot.async.wrapper;

import com.zmops.iot.async.callback.ICallback;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.exception.SkippedException;
import com.zmops.iot.async.worker.WorkResult;
import com.zmops.iot.async.wrapper.strategy.depend.DependMustStrategyMapper;
import com.zmops.iot.async.wrapper.strategy.depend.DependOnUpWrapperStrategy;
import com.zmops.iot.async.wrapper.strategy.depend.DependOnUpWrapperStrategyMapper;
import com.zmops.iot.async.wrapper.strategy.depend.DependenceStrategy;
import com.zmops.iot.async.wrapper.strategy.skip.SkipStrategy;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 一个稳定的Builder，兼容1.4版本之前的代码。
 * <p/>
 * 效果等同于v1.4及之前的{@link WorkerWrapper.Builder}。
 * <p/>
 * 考虑到由于废弃了must方式编排、needCheckNextWrapperResult判断跳过，转用策略器方式，导致本类为了向上兼容保留了一些低效的功能。
 * <p/>
 * 权限修饰符为default，表示暂不对外开放。
 *
 * @author create by TcSnZh on 2021/5/3-上午12:36
 */
class StableWorkerWrapperBuilder<T, V, BUILDER_SUB_CLASS extends StableWorkerWrapperBuilder<T, V, BUILDER_SUB_CLASS>>
        implements WorkerWrapperBuilder<T, V> {
    /**
     * 该wrapper的唯一标识。
     * 如果不设置则使用{@code UUID.randomUUID().toString()}
     */
    private String id;
    /**
     * worker将来要处理的param
     */
    private T param;
    private IWorker<T, V> worker;
    private ICallback<T, V> callback;
    /**
     * 自己后面的所有
     */
    private Set<WorkerWrapper<?, ?>> nextWrappers;
    /**
     * 自己依赖的所有
     */
    private Set<WorkerWrapper<?, ?>> dependWrappers;
    /**
     * 旧版本的检查是否跳过的开关
     */
    private Boolean needCheckNextWrapperResult = null;
    /**
     * 新版本的检查是否跳过的策略。
     */
    private SkipStrategy skipStrategy;
    /**
     * 基本依赖策略。
     * <p/>
     * 如果在{@link #build()}调用时，{@code dependenceStrategy==null}，
     * 则会给WorkerWrapper设置默认策略{@link DependenceStrategy#ALL_DEPENDENCIES_ALL_SUCCESS}。
     */
    private DependenceStrategy dependenceStrategy;
    /**
     * 存储自己需要特殊对待的dependWrapper集合
     */
    private Map<WorkerWrapper<?, ?>, DependOnUpWrapperStrategy> dependWrapperActionStrategyMap;
    /**
     * 存储需要特殊对待自己的nextWrapper集合。
     */
    private Map<WorkerWrapper<?, ?>, DependOnUpWrapperStrategy> selfIsSpecialMap;
    /**
     * 一个保存以must=true方式传入的WorkerWrapper的集合。
     * <p/>
     * 该Set将会加入到{@link WorkerWrapper.StableWrapperStrategy#getDependMustStrategyMapper().mustDependSet}之中
     */
    private Set<WorkerWrapper<?, ?>> mustDependSet;
    /**
     * 存储强依赖于自己的wrapper集合
     */
    private Set<WorkerWrapper<?, ?>> selfIsMustSet;
    /**
     * 是否使用了旧的编排模式（Must开关）
     * <p/>
     * 之所以需要以下两个属性，是为了隔离旧api与新api的策略不兼容的情况。<b>建议早日替换旧方法</b>
     * 例如旧代码里调用{@link WorkerWrapper.Builder#depend(WorkerWrapper, boolean)}，参数传入了false。
     */
    private boolean useV15DeprecatedMustDependApi = false;
    /**
     * 是否使用了新的编排模式。
     * <p/>
     * {@link #useV15DeprecatedMustDependApi}
     */
    private boolean useV15NewDependApi = false;
    /**
     * 单个Wrapper超时相关属性
     */
    private boolean enableTimeOut = false;
    private long time = -1;
    private TimeUnit unit = null;
    /**
     * 是否允许被打断
     */
    private boolean allowInterrupt = false;

    /**
     * 标记自己正在building
     */
    private boolean isBuilding = false;

    @Override
    public BUILDER_SUB_CLASS worker(IWorker<T, V> worker) {
        this.worker = worker;
        return returnThisBuilder();
    }

    @Override
    public BUILDER_SUB_CLASS param(T t) {
        this.param = t;
        return returnThisBuilder();
    }

    @Override
    public BUILDER_SUB_CLASS id(String id) {
        if (id != null) {
            this.id = id;
        }
        return returnThisBuilder();
    }

    @Override
    public BUILDER_SUB_CLASS setSkipStrategy(SkipStrategy strategy) {
        this.skipStrategy = strategy;
        return returnThisBuilder();
    }

    @Override
    public BUILDER_SUB_CLASS callback(ICallback<T, V> callback) {
        this.callback = callback;
        return returnThisBuilder();
    }

    @Override
    public SetDependImpl setDepend() {
        useV15NewDependApi = true;
        checkCanNotCompatibleDeprecateMustDependApi(false);
        return new SetDependImpl();
    }

    public class SetDependImpl implements SetDepend<T, V> {
        @Override
        public SetDependImpl wrapper(WorkerWrapper<?, ?> wrapper) {
            if (wrapper == null) {
                return this;
            }
            if (dependWrappers == null) {
                dependWrappers = new LinkedHashSet<>();
            }
            dependWrappers.add(wrapper);
            return this;
        }

        @Override
        public SetDependImpl mustRequireWrapper(WorkerWrapper<?, ?> wrapper) {
            if (wrapper == null) {
                return this;
            }
            wrapper(wrapper);
            if (mustDependSet == null) {
                mustDependSet = new LinkedHashSet<>();
            }
            mustDependSet.add(wrapper);
            return this;
        }

        @Override
        public SetDependImpl specialDependWrapper(DependOnUpWrapperStrategy strategy, WorkerWrapper<?, ?> wrapper) {
            if (strategy == null || wrapper == null) {
                return this;
            }
            if (dependWrapperActionStrategyMap == null) {
                dependWrapperActionStrategyMap = new LinkedHashMap<>();
            }
            dependWrapperActionStrategyMap.put(wrapper, strategy);
            return this;
        }

        @Override
        public SetDependImpl strategy(DependenceStrategy dependenceStrategy) {
            if (dependenceStrategy == null) {
                return this;
            }
            StableWorkerWrapperBuilder.this.dependenceStrategy = dependenceStrategy;
            return this;
        }

        @Override
        public BUILDER_SUB_CLASS end() {
            return returnThisBuilder();
        }
    }

    @Override
    public SetNextImpl setNext() {
        useV15NewDependApi = true;
        checkCanNotCompatibleDeprecateMustDependApi(false);
        return new SetNextImpl();
    }

    public class SetNextImpl implements SetNext<T, V> {
        @Override
        public SetNextImpl wrapper(WorkerWrapper<?, ?> wrapper) {
            if (wrapper == null) {
                return this;
            }
            if (nextWrappers == null) {
                nextWrappers = new LinkedHashSet<>();
            }
            nextWrappers.add(wrapper);
            return this;
        }

        @Override
        public SetNextImpl mustToNextWrapper(WorkerWrapper<?, ?> wrapper) {
            if (wrapper == null) {
                return this;
            }
            wrapper(wrapper);
            if (selfIsMustSet == null) {
                selfIsMustSet = new LinkedHashSet<>();
            }
            selfIsMustSet.add(wrapper);
            return this;
        }

        @Override
        public SetNextImpl specialToNextWrapper(DependOnUpWrapperStrategy strategy, WorkerWrapper<?, ?> wrapper) {
            if (strategy == null || wrapper == null) {
                return this;
            }
            wrapper(wrapper);
            if (selfIsSpecialMap == null) {
                selfIsSpecialMap = new LinkedHashMap<>();
            }
            selfIsSpecialMap.put(wrapper, strategy);
            return this;
        }

        @Override
        public BUILDER_SUB_CLASS end() {
            return returnThisBuilder();
        }
    }

    @Override
    public SetTimeOut<T, V> setTimeOut() {
        return new SetTimeOutImpl();
    }

    public class SetTimeOutImpl implements SetTimeOut<T, V> {
        @Override
        public SetTimeOutImpl enableTimeOut(boolean enableElseDisable) {
            StableWorkerWrapperBuilder.this.enableTimeOut = enableElseDisable;
            return this;
        }

        @Override
        public SetTimeOutImpl setTime(long time, TimeUnit unit) {
            if (time <= 0 || unit == null) {
                throw new IllegalStateException("Illegal argument : time=" + time + " must > 0, unit=" + unit + " require not null");
            }
            StableWorkerWrapperBuilder.this.time = time;
            StableWorkerWrapperBuilder.this.unit = unit;
            return this;
        }

        @Override
        public BUILDER_SUB_CLASS end() {
            return returnThisBuilder();
        }
    }

    @Override
    public WorkerWrapperBuilder<T, V> allowInterrupt(boolean allow) {
        allowInterrupt = allow;
        return returnThisBuilder();
    }

    @Override
    public WorkerWrapper<T, V> build() {
        isBuilding = true;
        // ========== 设置单wrapper超时检查 ==========
        {
            if (enableTimeOut) {
                if (time <= 0) {
                    throw new IllegalArgumentException("timeout time " + time + " must > 0");
                }
                if (unit == null) {
                    throw new IllegalArgumentException(new NullPointerException("timeout unit require not null"));
                }
            }
        }
        // ========== 构造wrapper ==========
        WorkerWrapper<T, V> wrapper = new StableWorkerWrapper<>(
                id == null ? UUID.randomUUID().toString() : id,
                worker,
                callback,
                allowInterrupt,
                enableTimeOut,
                time,
                unit
        );
        wrapper.setDependWrappers(new LinkedHashSet<>());
        wrapper.setNextWrappers(new LinkedHashSet<>());
        // ========== 设置依赖关系/策略 ==========
        {
            if (dependWrappers != null && dependWrappers.size() > 0) {
                dependWrappers.forEach(dependWrapper -> {
                    wrapper.getDependWrappers().add(dependWrapper);
                    dependWrapper.getNextWrappers().add(wrapper);
                });
            }
            if (nextWrappers != null && nextWrappers.size() > 0) {
                nextWrappers.forEach(next -> {
                    wrapper.getNextWrappers().add(next);
                    next.getDependWrappers().add(wrapper);
                });
            }
            if (useV15DeprecatedMustDependApi) {
                // 适配旧api的must开关
                if (mustDependSet != null && mustDependSet.size() > 0) {
                    wrapper.getWrapperStrategy().setDependMustStrategyMapper(new DependMustStrategyMapper()
                            .addDependMust(mustDependSet));
                }
                //noinspection deprecation
                wrapper.getWrapperStrategy().setDependenceStrategy(DependenceStrategy.IF_MUST_SET_NOT_EMPTY_ALL_SUCCESS_ELSE_ANY);
            } else {
                if (mustDependSet != null && mustDependSet.size() > 0) {
                    wrapper.getWrapperStrategy().setDependMustStrategyMapper(new DependMustStrategyMapper().addDependMust(mustDependSet));
                }
                if (dependenceStrategy == null) {
                    setDepend().defaultStrategy();
                }
                wrapper.getWrapperStrategy().setDependenceStrategy(dependenceStrategy);
            }
            if (dependWrapperActionStrategyMap != null && dependWrapperActionStrategyMap.size() > 0) {
                DependOnUpWrapperStrategyMapper mapper = new DependOnUpWrapperStrategyMapper();
                dependWrapperActionStrategyMap.forEach(mapper::putMapping);
                wrapper.getWrapperStrategy().setDependWrapperStrategyMapper(mapper);
            }
            if (selfIsMustSet != null && selfIsMustSet.size() > 0) {
                //noinspection deprecation
                selfIsMustSet.forEach(next -> Optional.ofNullable(next.getWrapperStrategy().getDependMustStrategyMapper())
                        .ifPresent(mustMapper -> mustMapper.addDependMust(wrapper)));
            }
            if (selfIsSpecialMap != null && selfIsSpecialMap.size() > 0) {
                selfIsSpecialMap.forEach((next, strategy) -> {
                    DependOnUpWrapperStrategyMapper dependOnUpWrapperStrategyMapper = next.getWrapperStrategy().getDependWrapperStrategyMapper();
                    if (dependOnUpWrapperStrategyMapper == null) {
                        next.getWrapperStrategy().setDependWrapperStrategyMapper(
                                dependOnUpWrapperStrategyMapper = new DependOnUpWrapperStrategyMapper());
                    }
                    dependOnUpWrapperStrategyMapper.putMapping(wrapper, strategy);
                });
            }
        }
        // ========== 设置检查是否跳过策略 ==========
        {
            if (skipStrategy == null) {
                wrapper.getWrapperStrategy().setSkipStrategy(needCheckNextWrapperResult != null && !needCheckNextWrapperResult ?
                        SkipStrategy.NOT_SKIP
                        : SkipStrategy.CHECK_ONE_LEVEL
                );
            } else {
                wrapper.getWrapperStrategy().setSkipStrategy(skipStrategy);
            }
        }
        // ========== end ==========
        wrapper.state.set(WorkerWrapper.State.INIT.id);
        wrapper.setParam(param);
        return wrapper;
    }

    // ========== deprecated methods ==========

    /**
     * @deprecated 建议使用 {@link WorkerWrapperBuilder#depends(WorkerWrapper[])}
     * 或{@link WorkerWrapperBuilder#setDepend()}设置更多选项，例如{@link SetDepend#wrapper(WorkerWrapper[])}
     * 如果是想要“必须依赖”的功能，则使用{@link SetDepend#mustRequireWrapper(WorkerWrapper[])}
     */
    @Deprecated
    public BUILDER_SUB_CLASS depend(WorkerWrapper<?, ?>... wrappers) {
        if (wrappers == null) {
            return returnThisBuilder();
        }
        for (WorkerWrapper<?, ?> wrapper : wrappers) {
            depend(wrapper);
        }
        return returnThisBuilder();
    }

    /**
     * @deprecated 建议使用 {@link WorkerWrapperBuilder#depends(WorkerWrapper[])}。
     * 或{@link WorkerWrapperBuilder#setDepend()}设置更多选项，例如{@link SetDepend#wrapper(WorkerWrapper)}
     * 如果是想要“必须依赖”的功能，则使用{@link SetDepend#mustRequireWrapper(WorkerWrapper[])}
     */
    @Deprecated
    public BUILDER_SUB_CLASS depend(WorkerWrapper<?, ?> wrapper) {
        return depend(wrapper, true);
    }

    /**
     * @deprecated 建议使用 {@link SetDepend#requireWrapper(WorkerWrapper, boolean)}}
     */
    @Deprecated
    public BUILDER_SUB_CLASS depend(WorkerWrapper<?, ?> wrapper, boolean isMust) {
        if (wrapper == null) {
            return returnThisBuilder();
        }
        useV15DeprecatedMustDependApi = true;
        checkCanNotCompatibleDeprecateMustDependApi(true);
        if (dependWrappers == null) {
            dependWrappers = new LinkedHashSet<>();
        }
        dependWrappers.add(wrapper);
        if (isMust) {
            if (mustDependSet == null) {
                mustDependSet = new LinkedHashSet<>();
            }
            mustDependSet.add(wrapper);
        }
        return returnThisBuilder();
    }

    @Deprecated
    public BUILDER_SUB_CLASS next(WorkerWrapper<?, ?>... wrappers) {
        if (wrappers == null) {
            return returnThisBuilder();
        }
        for (WorkerWrapper<?, ?> wrapper : wrappers) {
            next(wrapper);
        }
        return returnThisBuilder();
    }

    @Deprecated
    public BUILDER_SUB_CLASS next(WorkerWrapper<?, ?> wrapper) {
        return next(wrapper, true);
    }

    /**
     * 会将wrapper增加到{@link #nextWrappers}。
     * 如果selfIsMust为true，还会将wrapper额外增加到{@link #selfIsMustSet}。
     *
     * @param wrapper    WorkerWrapper instance
     * @param selfIsMust 是否强依赖自己（“强依赖”是旧版本的叫法。即是否必须在自己执行后才能执行。）
     * @return 返回Builder。
     * @deprecated 不推荐使用Must开关去设置之后的Wrapper。
     */
    @Deprecated
    public BUILDER_SUB_CLASS next(WorkerWrapper<?, ?> wrapper, boolean selfIsMust) {
        useV15DeprecatedMustDependApi = true;
        checkCanNotCompatibleDeprecateMustDependApi(true);
        if (nextWrappers == null) {
            nextWrappers = new LinkedHashSet<>();
        }
        nextWrappers.add(wrapper);
        //强依赖自己
        if (selfIsMust) {
            if (selfIsMustSet == null) {
                selfIsMustSet = new LinkedHashSet<>();
            }
            selfIsMustSet.add(wrapper);
        }
        return returnThisBuilder();
    }

    /**
     * 设置是否要检查之后的Wrapper是否已经执行完毕。
     * <p/>
     * 默认为true。
     *
     * @param needCheckNextWrapperResult 设为true后，如果之后的Wrapper已经执行完毕。
     *                                   则跳过本Wrapper并设置{@link WorkResult#getEx()}为{@link SkippedException}。
     * @deprecated v1.5中已经废弃。请使用
     */
    @Deprecated
    public BUILDER_SUB_CLASS needCheckNextWrapperResult(boolean needCheckNextWrapperResult) {
        this.needCheckNextWrapperResult = needCheckNextWrapperResult;
        return returnThisBuilder();
    }

    // util method

    private BUILDER_SUB_CLASS returnThisBuilder() {
        //noinspection unchecked
        return (BUILDER_SUB_CLASS) this;
    }

    private void checkCanNotCompatibleDeprecateMustDependApi(boolean isOld) {
        if (!isBuilding && (!isOld && useV15DeprecatedMustDependApi || isOld && useV15NewDependApi)) {
            throw new UnsupportedOperationException("新旧api之间不可兼容，请将v1.5之前废弃的方法升级为注释中建议的方法后再调用v1.5之后的新api");
        }
    }
}
