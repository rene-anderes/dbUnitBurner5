package org.anderes.edu.dbunitburner.sample.data;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface RecipeRepository extends PagingAndSortingRepository<Recipe, String> {

    Collection<Recipe> findByTitleLike(String string);
    
    List<String> findAllTag();

}
