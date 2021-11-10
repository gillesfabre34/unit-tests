package com.airbus.retex.helper;

import com.airbus.retex.business.dto.request.RequestDto;
import com.airbus.retex.model.request.Request;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Iterator;

@Service
@Transactional(rollbackFor = Exception.class)
public class DatabaseVerificationService {
    @Autowired
    private EntityManager em;

    /**
     * Retrieve one Request according to the dto.id field
     * fetch dynamically nested relationships
     * @param dto
     * @param relationships
     * @return
     * @throws JSONException
     */
    public Request retrieveOne(RequestDto dto, JSONObject relationships) throws JSONException {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Request> queryBuilder = builder.createQuery(Request.class);
        Root<Request> root = queryBuilder.from(Request.class);
        RootAdapter adapter = new RootAdapter();
        adapter.setObject(root);
        dependencies(adapter, relationships);

        Predicate predicate = builder.and(
                builder.equal(root.get("id"), dto.getId())
        );
        queryBuilder.where(predicate);

        return em.createQuery(queryBuilder).getSingleResult();
    }


    // inner logic

    /**
     * Build fetched dependencies dynamically
     * @param current
     * @param relationships
     * @throws JSONException
     */
    private void dependencies(DependencyAdapter current, JSONObject relationships) throws JSONException {
        Iterator keys = relationships.keys();
        while (keys.hasNext()){
            Object key = keys.next();
            Fetch<Object, Object> dep = current.fetch((String) key);
            FetchAdapter adapter = new FetchAdapter();
            adapter.setObject(dep);
            Object rels =relationships.get((String) key);
            if (rels instanceof JSONObject){
                dependencies(adapter, (JSONObject)rels);
            }
        }
    }

    // helper classes to wrap JPA Fetch and Root objects

    interface DependencyAdapter {
        Fetch<Object, Object> fetch(String key);
    }

    static class FetchAdapter implements DependencyAdapter{
        @Setter
        private Fetch object;

        public Fetch<Object, Object> fetch(String key){
            return object.fetch(key);
        }
    }

    static class RootAdapter implements DependencyAdapter{
        @Setter
        private Root object;

        public Fetch<Object, Object> fetch(String key){
            return object.fetch(key);
        }
    }
}
