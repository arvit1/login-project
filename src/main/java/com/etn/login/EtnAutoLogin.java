package com.etn.login;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.util.PropsValues;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nushi on 9/20/2017.
 */
@Component(
        immediate = true,
        service = AutoLogin.class
)
public class EtnAutoLogin extends BaseAutoLogin {
    @Override
    protected String[] doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long companyId = _portal.getCompanyId(request);
        String login = request.getParameter("username");

        if (Validator.isNull(login)) {
            if (_log.isInfoEnabled()) {
                _log.info("No login found for " + login);
            }

            return null;
        }

        User user = getUser(companyId, login);
        //addRedirect(request);

        String[] credentials = new String[3];

        credentials[0] = String.valueOf(user.getUserId());
        credentials[1] = user.getPassword();
        credentials[2] = Boolean.TRUE.toString();
        credentials[3] = AutoLogin.AUTO_LOGIN_REDIRECT_AND_CONTINUE;

        return credentials;
    }

    protected User getUser( long companyId, String login ) throws PortalException {
        User user = null;

        String authType = PrefsPropsUtil.getString(
                companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
                PropsValues.COMPANY_SECURITY_AUTH_TYPE);

        if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
            user = _userLocalService.getUserByScreenName(companyId, login);
        }
        else if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
            user = _userLocalService.getUserByEmailAddress(companyId, login);
        }
        else {
            if (_log.isWarnEnabled()) {
                StringBundler sb = new StringBundler(6);

                sb.append("Incompatible setting for: ");
                sb.append(PropsKeys.COMPANY_SECURITY_AUTH_TYPE);
                sb.append(". Please configure to either: ");
                sb.append(CompanyConstants.AUTH_TYPE_EA);
                sb.append(" or ");
                sb.append(CompanyConstants.AUTH_TYPE_SN);

                _log.warn(sb.toString());
            }
        }

        return user;
    }

    @Reference(unbind = "-")
    protected void setUserLocalService(UserLocalService userLocalService) {
        _userLocalService = userLocalService;
    }

    private static final Log _log = LogFactoryUtil.getLog(EtnAutoLogin.class);

    @Reference
    private Portal _portal;
    private UserLocalService _userLocalService;
}
