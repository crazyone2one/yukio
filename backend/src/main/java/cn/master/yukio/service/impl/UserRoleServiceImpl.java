package cn.master.yukio.service.impl;

import cn.master.yukio.entity.UserRole;
import cn.master.yukio.exception.MSException;
import cn.master.yukio.mapper.UserRoleMapper;
import cn.master.yukio.service.IUserRoleService;
import cn.master.yukio.util.Translator;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.yukio.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Override
    public void checkRoleIsGlobalAndHaveMember(List<String> roleIdList, boolean isSystem) {
        List<UserRole> globalRoleList = this.queryChain().select(USER_ROLE.ID).from(USER_ROLE)
                .where(USER_ROLE.ID.in(roleIdList)
                        .and(USER_ROLE.TYPE.eq("SYSTEM"))
                        .and(USER_ROLE.SCOPE_ID.eq("GLOBAL"))).list();
        if (globalRoleList.size() != roleIdList.size()) {
            throw new MSException(Translator.get("role.not.global"));
        }
    }
}
