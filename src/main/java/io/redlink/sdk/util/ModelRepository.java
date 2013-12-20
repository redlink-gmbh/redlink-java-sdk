package io.redlink.sdk.util;

import java.util.Iterator;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Provides a simple transformation from a Model into a Repository,
 * for being able to do regular things there, such as SPARQL queries. 
 * A bit ugly, since a model is simply a collection of statements; 
 * something to discuss with the Sesame people.
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public class ModelRepository extends SailRepository {
	
	public static ModelRepository create(Model model) throws RepositoryException {
		Sail sail = new MemoryStore();
		Repository repo = new SailRepository(sail);
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		conn.begin();
        conn.add(model);
		conn.commit();
		conn.close();
		return new ModelRepository(sail);
	}

	private ModelRepository(Sail sail) {
		super(sail);
	}

}
