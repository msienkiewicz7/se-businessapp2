package com.businessapp.repositories;

import java.util.List;

import com.businessapp.model.Article;


/**
 * Local in-memory repository implementation using GenericMemRepositoryImpl<E>.
 * @author Sven Graupner
 *
 */
class ArticleRepositoryImpl extends GenericMemRepositoryImpl<Article> implements ArticleRepositoryIntf {


	ArticleRepositoryImpl( List<Article> list ) {
		super( list );
	}


	/**
	 * Create a new entity that is *not* yet managed in the repository.
	 * A new entity is added to the repository using the update() method.
	 *
	 * @return new entity instance that is not yet managed in the repository.
	 */
	@Override
	public Article create() {
		return new Article( "Gerätename...", 0 );
	}

}
