/*
 * Copyright (c) 2022.
 * Joseph Rumpe (jrumpe@gmail.com)
 * All rights reserved.
 */

package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository repo;

    public List<Brand> listAll () {
        return (List<Brand>) repo.findAll();
    }
}
