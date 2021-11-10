package com.airbus.retex.model.step;

import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepActivation extends AbstractVersionableChildModel<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", referencedColumnName = "id")
    private Step step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_fa_id")
    private OperationFunctionalArea operationFunctionalArea;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "stepActivation")
    private List<RoutingFunctionalAreaPost> routingFunctionalAreaPosts = new ArrayList<>();

    @Column
    private Boolean activated;

    @Column(name = "creation_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    public void addRoutingFunctionalAreaPost(RoutingFunctionalAreaPost routingFunctionalAreaPost) {
        routingFunctionalAreaPost.setStepActivation(this);
        this.routingFunctionalAreaPosts.add(routingFunctionalAreaPost);
    }

    public Float getThresholdForPost(Post post) {
        if (this.routingFunctionalAreaPosts.isEmpty()) {
            return null;
        }

        Float threshold = null;
        for (RoutingFunctionalAreaPost rfap: this.routingFunctionalAreaPosts) {
            if (rfap.getPost().getNaturalId().equals(post.getNaturalId())) {
                threshold = rfap.getThreshold();
            }
        }

        return threshold;
    }
}
