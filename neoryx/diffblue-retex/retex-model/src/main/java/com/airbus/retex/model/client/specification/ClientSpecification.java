package com.airbus.retex.model.client.specification;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.client.Client;

public class ClientSpecification {
	/**
	 * private contructeur
	 */
	private ClientSpecification() {

	}

    /**
     *
     * @param name
     * @return
     */
    public static Specification<Client> filterByName(String name) {
        return (root, query, cb) -> cb.like(root.get("name"), name + "%");
    }

}
