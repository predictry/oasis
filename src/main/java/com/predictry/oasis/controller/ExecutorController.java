package com.predictry.oasis.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 
 * Executor class
 * to export tasks
 * outside of the system
 */
@RestController
public class ExecutorController {
  /* 
   * Executor dummy class
   */
  @RequestMapping("/executor/{id}")
  public String executor(@PathVariable String id) {
    return "Hello Bitch Number " + id + " oh la la";
  }
}
