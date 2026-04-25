package br.com.vendemais.domain.entity;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Represents a single ordered checkpoint inside a sales pipeline.
 */
@Entity
@Table(
        name = "stage",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stage_pipeline_position",
                        columnNames = {"pipeline_id", "position"}
                ),
                @UniqueConstraint(
                        name = "uk_stage_pipeline_code",
                        columnNames = {"pipeline_id", "code"}
                )
        }
)
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pipeline_id", nullable = false)
    private Pipeline pipeline;

    public Stage(String name, String code, Integer position, Pipeline pipeline) {
        this.name = name;
        this.code = code;
        this.position = position;
        this.pipeline = pipeline;
    }

    protected Stage() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the immutable technical code used by the backend to identify the
     * stage inside a pipeline.
     *
     * @return technical stage code
     */
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
        if (this == o) {
            return true;
        }

        if (!(o instanceof Stage stage)) {
            return false;
        }

        if (id == null || stage.id == null) {
            return false;
        }

        return Objects.equals(id, stage.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}