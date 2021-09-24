package com.zmops.zeus.iot.server.transfer.core.state;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStateWrapper implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStateWrapper.class);

    private final Map<Pair<State, State>, StateCallback> callBacks = new HashMap<>();

    private volatile State currentState = State.ACCEPTED;

    public AbstractStateWrapper() {
        addCallbacks();
    }

    /**
     * add callback for state change
     */
    public abstract void addCallbacks();


    public AbstractStateWrapper addCallback(State begin, State end, StateCallback callback) {
        callBacks.put(new ImmutablePair<>(begin, end), callback);
        return this;
    }

    /**
     * change state and execute callback functions
     *
     * @param nextState - next state
     */
    public synchronized void doChangeState(State nextState) {
        LOGGER.debug("state change, current state is {}, next state is {}", currentState, nextState);
        Pair<State, State> statePair = new ImmutablePair<>(currentState, nextState);

        StateCallback callback = callBacks.get(statePair);

        // change state before callback.
        currentState = nextState;
        if (callback != null) {
            callback.call(currentState, nextState);
        }
    }

    /**
     * determine the exception
     *
     * @return
     */
    public boolean isException() {
        State tmpState = currentState;
        return State.KILLED.equals(tmpState) || State.FAILED.equals(tmpState) || State.FATAL.equals(tmpState);
    }

    public boolean isFinished() {
        State tmpState = currentState;
        return State.FATAL.equals(tmpState) || State.SUCCEEDED.equals(tmpState) || State.KILLED.equals(tmpState);
    }

    public boolean isSuccess() {
        return State.SUCCEEDED.equals(currentState);
    }

    public boolean isFailed() {
        return State.FAILED.equals(currentState);
    }

    public boolean isFatal() {
        State tmpState = currentState;
        return State.FATAL.equals(tmpState) || State.KILLED.equals(tmpState);
    }

}
