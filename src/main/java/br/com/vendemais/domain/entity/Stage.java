package br.com.vendemais.domain.entity;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Represents a single ordered checkpoint inside a sales pipeline.
 */
@Entity
@Table(name = "stage", uniqueConstraints = {
        //Bloqueia posições repetidas no mesmo funil
        @UniqueConstraint(columnNames = {"pipeline_id", "position"})
})
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String code;
    @Column
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;

    public Stage(String name, String code, Integer position, Pipeline pipeline) {
        this.name = name;
        this.code = code;
        this.position = position;
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
