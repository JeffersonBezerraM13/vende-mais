package br.com.vendemais.domain.entity;

import jakarta.persistence.*;

import java.util.*;


/**
 * Represents a sales pipeline that groups and orders the stages used to advance
 * opportunities.
 */
@Entity
public class Pipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position asc")
    private List<Stage> stages = new ArrayList<>();

    public Pipeline(String title) {
        this.title = title;
    }

    protected Pipeline(){}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Attaches a stage to this pipeline so it becomes part of the ordered funnel
     * definition.
     *
     * @param stage stage to be managed by this pipeline
     */
    public void addStage(Stage stage) {
        if (stage == null) {
            return;
        }

        stage.setPipeline(this);
        stages.add(stage);
    }

    /**
     * Removes a stage from this pipeline when the funnel definition is being
     * reconfigured.
     *
     * @param stage stage to be detached from this pipeline
     */
    public void removeStage(Stage stage) {
        if (stage == null) {
            return;
        }

        stages.remove(stage);
        stage.setPipeline(null);
    }

    /**
     * Returns the earliest stage in the pipeline so new opportunities can start
     * in the correct position when no explicit stage is provided.
     *
     * @return the first configured stage, or {@code null} when the pipeline has no stages
     */
    public Stage getFirstStage() {
        if (stages == null || stages.isEmpty()) {
            return null;
        }

        return stages.stream()
                .min(Comparator.comparing(Stage::getPosition))
                .orElse(null);
    }

    public Stage getStage(int index){
        return stages.get(index);
    }

    public List<Stage> getStages() {
        return Collections.unmodifiableList(stages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Pipeline pipeline)) {
            return false;
        }

        if (id == null || pipeline.id == null) {
            return false;
        }

        return Objects.equals(id, pipeline.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
