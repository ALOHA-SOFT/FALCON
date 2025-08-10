package com.falcon.shop.domain.users;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_auth")    
@Alias("UserAuth")        
public class UserAuth extends Base {

  private Long userNo;
  private String username;
  private String auth; 
  
}
