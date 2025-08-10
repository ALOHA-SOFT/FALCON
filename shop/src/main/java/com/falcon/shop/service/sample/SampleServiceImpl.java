package com.falcon.shop.service.sample;

import org.springframework.stereotype.Service;

import com.falcon.shop.domain.Sample;
import com.falcon.shop.mapper.SampleMapper;
import com.falcon.shop.service.BaseServiceImpl;

import groovy.util.logging.Slf4j;

@Slf4j
@Service
public class SampleServiceImpl extends BaseServiceImpl<Sample, SampleMapper> implements SampleService {

  
}
