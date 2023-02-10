package ru.aasmc.efficientdatajpatesting.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "robot")
public class Robot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private boolean switched;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;

    public enum Type {
        DRIVER,
        LOADER,
        VACUUM
    }
}
