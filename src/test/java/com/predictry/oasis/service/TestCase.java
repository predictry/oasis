package com.predictry.oasis.service;

import javax.transaction.Transactional;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.predictry.oasis.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class) @ActiveProfiles("test")
@ContextConfiguration(classes=TestConfig.class, loader=AnnotationConfigContextLoader.class)
@Transactional
public abstract class TestCase {

}
