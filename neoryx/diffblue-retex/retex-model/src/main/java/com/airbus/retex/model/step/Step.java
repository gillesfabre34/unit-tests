package com.airbus.retex.model.step;

import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.todoList.TodoList;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
public class Step extends AbstractVersionableChildModel<Long> implements TranslatableModel<StepTranslation, StepFieldsEnum>, Comparable<StepType> {
    public static final String FIELD_INFORMATION = "information";
    public static final String FIELD_NAME = "name";

    @Column(name = "number")
    private Integer stepNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private StepType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routing_component_id", referencedColumnName = "id", nullable = true)
    private RoutingComponent routingComponent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id", referencedColumnName = "id", nullable = true)
    private TodoList todoList;

    // Step can have many Posts
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "step")
    private Set<Post> posts = new HashSet<>();

    // Step can have many StepActivation
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "step")
    private Set<StepActivation> stepActivations = new HashSet<>();

    // Step can have many files
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "step_media",
            joinColumns = {@JoinColumn(name = "step_id")},
            inverseJoinColumns = {@JoinColumn(name = "media_id")}
    )
    private Set<Media> files = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "entity")
    private Set<StepTranslation> translations = new HashSet<>();

    @Column(name = "is_deletable")
    public Boolean isDeletable;

    public void setDeletable(Boolean deletable) {
        if(null == isDeletable || Boolean.TRUE.equals(isDeletable)){
            isDeletable = deletable;
        }
    }

    public void addTranslation(StepTranslation translation) {
        translation.setEntity(this);
        translations.add(translation);
    }

    public void addFiles(Set<Media> files ) {
        this.files.addAll(files);
    }

    public void addFile(Media media){
        this.files.add(media);
    }

    public void setPosts(Set<Post> posts){
        this.posts = posts;
        posts.forEach(post -> post.setStep(this));
    }

    public void addPost(Post post){
        post.setStep(this);
        this.posts.add(post);
    }

    public void addStepActivation(StepActivation stepActivation){
        stepActivation.setStep(this);
        this.stepActivations.add(stepActivation);
    }

    @Override
    public int compareTo(StepType o) {
        if (this.getType().equals(o)) {
            return 0;
        } else if (this.getType().equals(StepType.AUTO)) {
            return -1;
        } else {
            return 1;
        }
    }
}
