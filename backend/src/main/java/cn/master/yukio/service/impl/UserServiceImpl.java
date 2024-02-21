package cn.master.yukio.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.yukio.entity.User;
import cn.master.yukio.mapper.UserMapper;
import cn.master.yukio.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
