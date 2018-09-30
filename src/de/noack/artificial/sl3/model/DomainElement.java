package de.noack.artificial.sl3.model;

import de.noack.artificial.sl3.util.IdUtil;

import java.io.Serializable;
import java.util.HashMap;

public abstract class DomainElement implements Serializable {

	protected static HashMap<Integer, DomainElement> MODEL_REGISTRY = new HashMap <>();

	private int id;

	public DomainElement() {
		this.id = IdUtil.createNewId();
		MODEL_REGISTRY.put(id, this);
	}

}
