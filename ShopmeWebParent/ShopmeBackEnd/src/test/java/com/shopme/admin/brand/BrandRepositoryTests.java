/*
 * Copyright (c) 2022.
 * Joseph Rumpe (jrumpe@gmail.com)
 * All rights reserved.
 */

package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTests {
    @Autowired
    private BrandRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCreateBrand () {
        String name = "Samsung";
        String logo = "brand-logo.png";
        Brand brand = new Brand();
        brand.setName(name);
        brand.setLogo(logo);
        Brand savedBrand = repo.save(brand);

        assertThat(savedBrand.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateBrandWithOneCategory () {
        Category laptops = entityManager.find(Category.class, 6);
        Brand brand = new Brand("Acer", "brand-logo.png");

        brand.addCategory(laptops);

        Brand savedBrand = repo.save(brand);

        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrandWithTwoCategories () {

        Brand brand = new Brand("Canon", "canon.png");

        Category camPhoto = new Category(2);
        Category digCam = new Category(10);
        Category flashes = new Category(11);
        Category lenses = new Category(12);

        brand.addCategory(camPhoto);
        brand.addCategory(digCam);
        brand.addCategory(flashes);
        brand.addCategory(lenses);

        Brand savedBrand = repo.save(brand);

        assertThat(savedBrand.getId()).isGreaterThan(0);

    }

    @Test
    public void testListAllBrands () {
        Iterable<Brand> listBrands = repo.findAll();
        for (Brand brand : listBrands) {
            System.out.println(brand);
        }
    }

    @Test
    public void testGetBrandById () {
        Brand brand = repo.findById(3).get();
        System.out.println(brand);
        assertThat(brand).isNotNull();

    }

    @Test
    public void testUpdateBrandDetails () {
        String newName = "Samsung Electronics";
        Brand brand = repo.findById(3).get();
        brand.setName(newName);
        repo.save(brand);
    }

    @Test
    public void testDeleteBrand () {
        Integer id = 4;
        repo.deleteById(id);
    }
}