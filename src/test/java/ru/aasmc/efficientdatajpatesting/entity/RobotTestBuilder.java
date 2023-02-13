package ru.aasmc.efficientdatajpatesting.entity;

import ru.aasmc.efficientdatajpatesting.testutils.TestBuilder;

import java.util.function.Consumer;

import static ru.aasmc.efficientdatajpatesting.entity.Robot.Type.DRIVER;

public class RobotTestBuilder implements TestBuilder<Robot> {
    private String name = "";
    private boolean switched = false;
    private Robot.Type type = DRIVER;

    private RobotTestBuilder() {
    }

    private RobotTestBuilder(RobotTestBuilder builder) {
        this.name = builder.name;
        this.switched = builder.switched;
        this.type = builder.type;
    }

    public static RobotTestBuilder aRobot() {
        return new RobotTestBuilder();
    }

    public RobotTestBuilder name(String name) {
        return copyWith(b -> b.name = name);
    }

    public RobotTestBuilder switched(boolean switched) {
        return copyWith(b -> b.switched = switched);
    }

    public RobotTestBuilder type(Robot.Type type) {
        return copyWith(b -> b.type = type);
    }

    private RobotTestBuilder copyWith(Consumer<RobotTestBuilder> consumer) {
        final var copy = new RobotTestBuilder(this);
        consumer.accept(copy);
        return copy;
    }

    @Override
    public Robot build() {
        final var server = new Robot();
        server.setName(name);
        server.setSwitched(switched);
        server.setType(type);
        return server;
    }
}
