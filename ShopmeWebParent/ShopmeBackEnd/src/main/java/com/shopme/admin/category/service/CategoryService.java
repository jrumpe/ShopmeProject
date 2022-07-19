package com.shopme.admin.category.service;

import com.shopme.admin.category.CategoryNotFoundException;
import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.admin.category.repository.CategoryRepository;
import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;
    @Autowired
    private CategoryRepository repo;

    public List<Category> listbyPage (CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
//        TODO Function code to sort categories when toggled in ascending or descending order

        Sort sort = Sort.by("name");
        if (sortDir == null || sortDir.isEmpty()) {
            sort = sort.ascending();
        } else if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        Page<Category> pageCategories = null;

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        if (keyword != null && !keyword.isEmpty()) {
            pageCategories = repo.search(keyword, pageable);
        } else {
            pageCategories = repo.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();

            for (Category category : searchResult) {
                category.setHasChildren(category.getChildren().size() > 0);
            }
            return searchResult;
        } else {
            return listHierarchicalCategories(rootCategories, sortDir);
        }
    }


    private List<Category> listHierarchicalCategories (List<Category> rootCategories, String sortDir) {

        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortedSubCategories(rootCategory.getChildren(), sortDir);
//            Set<Category> children = sortedSubCategories(rootCategory.getChildren());

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));
                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }
        return hierarchicalCategories;
    }


    private void listSubHierarchicalCategories (List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {

        Set<Category> children = sortedSubCategories(parent.getChildren(), sortDir);

        int newSubLevel = subLevel + 1;

        for (Category subCategory : children) {
            String name = "";

            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);

        }

    }


    public List<Category> listCategoriesUsedInForm () {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = sortedSubCategories(category.getChildren());

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
                }

            }
        }


        return categoriesUsedInForm;
    }

    public Category save (Category category) {
        return repo.save(category);
    }

    public void listSubCategoriesUsedInForm (List<Category> categoriesUsedInForm, Category parent, int subLevel) {

        int newSubLevel = subLevel + 1;
        Set<Category> children = sortedSubCategories(parent.getChildren());

        for (Category subCategory : children) {
            String name = "";

            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }

            name += subCategory.getName();

            System.out.println(subCategory.getName());
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));


            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    public Category get (Integer id) throws CategoryNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any Category with ID: " + id);
        }
    }

    public String checkUnique (Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = repo.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "DuplicateName";
            } else {
                Category categoryByAlias = repo.findByAlias(alias);
                if (categoryByAlias != null) {
                    return "DuplicateAlias";
                }
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "DuplicateName";
            }

            Category categoryByAlias = repo.findByAlias(alias);

            if (categoryByAlias != null && categoryByAlias.getId() != id) {
                return "DuplicateAlias";
            }
        }
        return "OK";
    }

    private SortedSet<Category> sortedSubCategories (Set<Category> children) {
        return sortedSubCategories(children, "asc");
    }

    private SortedSet<Category> sortedSubCategories (Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare (Category cat1, Category cat2) {
                if (sortDir == null || sortDir.isEmpty()) {
                    return cat1.getName().compareTo(cat2.getName());
                } else {
                    if (sortDir.equals("asc")) {
                        return cat1.getName().compareTo(cat2.getName());
                    } else {
                        return cat2.getName().compareTo(cat1.getName());
                    }
                }
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }

    public void updateCategoryEnabledStatus (Integer id, boolean enabled) {

        repo.updateEnabledStatus(id, enabled);
    }

    public void delete (Integer id) throws CategoryNotFoundException {
        Long countById = repo.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
        }
        repo.deleteById(id);
    }

}
