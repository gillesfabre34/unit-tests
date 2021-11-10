package com.airbus.retex.persistence.post;

import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingFunctionalAreaPostRepository extends JpaRepository<RoutingFunctionalAreaPost, Long>, CustomJpaRepository<RoutingFunctionalAreaPost> {
}
