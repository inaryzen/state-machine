package com.inaryzen.statemachine;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple utility class that behaves a as state machine processor except that it does not use states but directly works
 * with transitions. So, it's assumed that the state of the processed machine is defined as the next transition
 * it should perform and, effectively, the machine is just a chain of specified transitions.
 *
 * Created by inaryzen on 10/11/2016.
 *
 */
public class StateMachine<T> {
    private final T target;
    private final Map<String, Method> transitions;
    private final StateProperty state;

    public StateMachine(T target) {
        this.target = Objects.requireNonNull(target);
        this.transitions = resolveStates(target);
        this.state = resolveState(target);
    }

    private static Map<String, Method> resolveStates(Object target) {
        Map<String, Method> mapping = new HashMap<>();
        Class<?> type = target.getClass();
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            Transition transition = method.getAnnotation(Transition.class);
            if (transition != null) {
                String stateName = transition.stateName();
                if (stateName.isEmpty()) {
                    stateName = method.getName();
                }
                stateName = stateName.toLowerCase();
                mapping.put(stateName, method);
            }
        }
        if (mapping.isEmpty())
            throw new IllegalArgumentException("Failed to find transitions in class: " + type);
        return mapping;
    }

    private static StateProperty resolveState(Object target) {
        Class<?> type = target.getClass();
        for (Field field : type.getDeclaredFields()) {
            State state = field.getAnnotation(State.class);
            if (state == null) continue;

            String fieldName = field.getName();
            char[] chars = fieldName.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            fieldName = new String(chars);
            Method getter;
            Method setter;
            try {
                getter = type.getMethod("get" + fieldName);
                setter = type.getMethod("set" + fieldName, String.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Failed to find getter/setter for the state", e);
            }
            if (String.class != getter.getReturnType())
                throw new IllegalArgumentException("Getter method for the state must return String");

            return new StateProperty(getter, setter);
        }
        throw new IllegalArgumentException("Field annotated with @State not found");
    }

    public void transit() {
        String transitionName = state.get(target);
        Method transition = transitions.get(transitionName);
        String nextTransition;
        try {
            nextTransition = (String)transition.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Cannot perform transition: " + transitionName, e);
        }
        state.set(target, nextTransition);
    }

    public T getTarget() {
        return target;
    }

    private static final class StateProperty {
        private final Method getter;
        private final Method setter;

        public StateProperty(Method getter, Method setter) {
            this.getter = getter;
            this.setter = setter;
        }

        public String get(Object instance) {
            String result;
            try {
                result = (String)getter.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Cannot retrieve state property", e);
            }
            return result;
        }

        public void set(Object instance, String value) {
            try {
                setter.invoke(instance, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Cannot set new value to the state property", e);
            }
        }
    }
}
