package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.StageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String code;
    @Column(unique = true)
    private Integer position;

    //tirar essa informação
    @Enumerated(EnumType.STRING)
    private StageType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;

    public Stage(String name, String code, Integer position, StageType type, Pipeline pipeline) {
        this.name = name;
        this.code = code;
        this.position = position;
        this.type = type;
        this.pipeline = pipeline;
    }

    protected Stage() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public StageType getType() {
        return type;
    }

    public void setType(StageType type) {
        this.type = type;
    }

    public boolean isOpen() {
        return this.type == StageType.OPEN;
    }

    public boolean isWon() {
        return this.type == StageType.WON;
    }

    public boolean isLost() {
        return this.type == StageType.LOST;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Stage stage)) return false;
        return Objects.equals(id, stage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
