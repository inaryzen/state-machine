package com.inaryzen.statemachine;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by inaryzen on 10/12/2016.
 */
public class StateMachineTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void transitTest() throws Exception {
        StateMachine<Machine> machine = new StateMachine<>(new Machine());
        assertEquals(machine.getTarget().getState(), "sleep");
        machine.transit();
        assertEquals(machine.getTarget().getState(), "awake");
        machine.transit();
        assertEquals(machine.getTarget().getState(), "sleep");
    }

    static class Machine {
        @State
        private String state = "sleep";

        @Transition
        public String awake() {
            return "sleep";
        }

        @Transition
        public String sleep() {
            return "awake";
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    @Test
    public void noTransitionsExceptionTest() throws Exception {
        thrown.expectMessage("Failed to find transitions in class");
        StateMachine<MachineWithoutTransitions> machine = new StateMachine<>(new MachineWithoutTransitions());
        machine.transit();
    }

    static class MachineWithoutTransitions {
        @State
        private String state = "sleep";

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    @Test
    public void noStateExceptionTest() throws Exception {
        thrown.expectMessage("Field annotated with @State not found");
        StateMachine<MachineWithoutState> machine = new StateMachine<>(new MachineWithoutState());
        machine.transit();
    }

    static class MachineWithoutState {
        @Transition
        public String someTransition() {
            return "";
        }
    }

    @Test
    public void noStateGetterExceptionTest() throws Exception {
        thrown.expectMessage("Failed to find getter/setter for the state");
        StateMachine<MachineWithoutStateGetter> machine = new StateMachine<>(new MachineWithoutStateGetter());
        machine.transit();
    }

    static class MachineWithoutStateGetter {
        @State
        private String state;

        @Transition
        public String someTransition() {
            return "";
        }
    }

    @Test
    public void incorrectTypeOfStateException() throws Exception {
        thrown.expectMessage("Getter method for the state must return String");
        StateMachine<MachineWithoutIncorrectState> machine = new StateMachine<>(new MachineWithoutIncorrectState());
        machine.transit();
    }

    static class MachineWithoutIncorrectState {
        @State
        private Integer state;

        @Transition
        public String someTransition() {
            return "";
        }

        public Integer getState() {
            return state;
        }

        public void setState(String value) {
            state = Integer.parseInt(value);
        }
    }
}