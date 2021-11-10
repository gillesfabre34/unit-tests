package com.airbus.retex.persistence.post;

import com.airbus.retex.model.post.Post;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, CustomJpaRepository<Post> {
    /**
     * find post by post id
     * @param postId
     * @return
     */
    Post findByNaturalId(long postId);
}
