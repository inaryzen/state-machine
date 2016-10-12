# state-machine

Simple utility class that behaves as a state machine processor except that it does not use states but directly works with transitions. So, it's assumed that the state of the processed machine is defined as the next transition it should perform and, effectively, the machine is just a chain of specified transitions.

###Usage:

A state machine is represented by a regular java class that follows the next rules:
- have a String field annotated by @State with corresponding getter and setter methods. The field holds the next transition name;
- set of methods annotated by @Transition. A transition method should be a public, non-static method that returns String - the name of the next transition.

    public static class Machine() {
        private String state = "begin";
        private int distance = 0;

        public String getState() {
            return state;
        }
        public void setState(String value) {
            state = value;
        }

        public int getDistance() {
            return distance;
        }

        @Transition
        public String move() {
            distance++;
            if (distance % 10 == 0)
                return "turn";
            else if (distance >= 42)
                return "halt";
            else
                return "move";
        }

        @Transition("begin")
        public String start() {
            return "move";
        }

        @Transition
        public String turn() {
            distance += 3;
            return "move";
        }

        @Transition
        public String halt() {
            return "halt";
        }
    }

    public static void main(String[] args) {
        Machine machine = new Machine();
        StateMachine<Machine> processor = new StateMachine(machine) {
            for (;;) {
                processor.transit();
                if ("halt".equals(machine.getStatus()))
                    break;
            }
            System.out.println(machine.getDistance());
        }
    }
