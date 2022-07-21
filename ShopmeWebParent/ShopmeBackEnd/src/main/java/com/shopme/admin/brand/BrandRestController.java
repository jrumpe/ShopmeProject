/*
 * Copyright (c) 2022.
 * Joseph Rumpe (jrumpe@gmail.com)
 * All rights reserved.
 */

package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {

    @Autowired
    BrandService service;

    @PostMapping("/brands/check_unique")
    public String checkUnique (@Param("id") Integer id, @Param("name") String name) {

        return service.checkUnique(id, name);
    }
}
