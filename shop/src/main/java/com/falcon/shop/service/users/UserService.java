package com.falcon.shop.service.users;

import com.github.pagehelper.PageInfo;

import java.util.List;

import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.users.Users;
import com.falcon.shop.service.BaseService;

public interface UserService extends BaseService<Users> {

    // 회원등록
    boolean join(Users entity);

    // 회원조회 - username
    Users selectByUsername(String username);
    
    // 페이징 조회
    PageInfo<Users> page(QueryParams queryParams);

    // 회원정보 수정
    boolean update(Users entity);                                     // 수정
    boolean updateById(Users entity);

    // 비밀번호 확인
    boolean checkPassword(Users existingUser, String password);  

    // 아이디 찾기 - 이름과 이메일로 사용자 조회
    Users findByEmail(String email);
    List<Users> findByEmailList(String email);

    // 비밀번호 찾기 - 아이디와 이메일로 사용자 조회 후 임시 비밀번호 생성
    boolean resetPassword(String username, String email);  

  
}
