package br.com.vendemais.domain.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Entity
public class Pipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany
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

    public void addStage(Stage stage) {
        stages.add(stage);
    }

    public void removeStage(Stage stage) {
        stages.remove(stage);
    }

    public List<Stage> getStages() {
        return Collections.unmodifiableList(stages);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pipeline pipeline)) return false;
        return Objects.equals(id, pipeline.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
