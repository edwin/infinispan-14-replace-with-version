package com.edw.controller;

import com.edw.helper.GenerateCacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 *     com.edw.controller.IndexController
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 29 Jan 2024 15:55
 */
@RestController
public class IndexController {

    @Autowired
    private GenerateCacheHelper generateCacheHelper;

    @GetMapping(path = "/start")
    public String addAll() {
        return "good";
    }
}