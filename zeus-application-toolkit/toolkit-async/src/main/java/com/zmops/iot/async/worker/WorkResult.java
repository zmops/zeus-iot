package com.zmops.iot.async.worker;

/**
 * 执行结果
 */
public class WorkResult<V> {
    /**
     * 执行的结果
     */
    private final V result;
    /**
     * 结果状态
     */
    private final ResultState resultState;
    private final Exception ex;

    public WorkResult(V result, ResultState resultState) {
        this(result, resultState, null);
    }

    public WorkResult(V result, ResultState resultState, Exception ex) {
        this.result = result;
        this.resultState = resultState;
        this.ex = ex;
    }

    /**
     * 返回不可修改的DEFAULT单例。
     */
    public static <V> WorkResult<V> defaultResult() {
        //noinspection unchecked
        return (WorkResult<V>) DEFAULT;
    }

    private static final WorkResult<?> DEFAULT = new WorkResult<>(null, ResultState.DEFAULT);

    @Override
    public String toString() {
        return "WorkResult{" +
                "result=" + result +
                ", resultState=" + resultState +
                ", ex=" + ex +
                '}';
    }

    public Exception getEx() {
        return ex;
    }

    public V getResult() {
        return result;
    }

    public ResultState getResultState() {
        return resultState;
    }
}
