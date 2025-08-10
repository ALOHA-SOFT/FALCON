package com.falcon.shop.service.users;

import org.springframework.stereotype.Service;
import com.falcon.shop.domain.users.UserAuth;
import com.falcon.shop.mapper.users.UserAuthMapper;
import com.falcon.shop.service.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserAuthServiceImpl extends BaseServiceImpl<UserAuth, UserAuthMapper> implements UserAuthService {

}
