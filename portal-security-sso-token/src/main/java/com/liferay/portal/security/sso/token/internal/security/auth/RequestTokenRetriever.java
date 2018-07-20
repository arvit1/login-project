/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.security.sso.token.internal.security.auth;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.sso.token.internal.auto.login.TokenAutoLogin;
import com.liferay.portal.security.sso.token.internal.util.EncryptionUtil;
import com.liferay.portal.security.sso.token.security.auth.TokenLocation;
import com.liferay.portal.security.sso.token.security.auth.TokenRetriever;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = TokenRetriever.class)
public class RequestTokenRetriever implements TokenRetriever {

	@Override
	public String getLoginToken(
		HttpServletRequest request, String userTokenName) {
		String s = EncryptionUtil.decode(ParamUtil.getString(request, userTokenName));
		_log.debug("=====================LOGIN-AUTO=========================== " + s);
		return s;
	}

	@Override
	public TokenLocation getTokenLocation() {
		return TokenLocation.REQUEST;
	}

    private static final Log _log = LogFactoryUtil.getLog(TokenAutoLogin.class);
}