package com.ebay.api.app.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ebay.api.app.context.OAuthApiContext;
import com.ebay.api.app.response.Message;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.sdk.call.GetAccountCall;
import com.ebay.soap.eBLBaseComponents.AccountEntryType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;

@Path("/account")
public class AccountResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthResource.class);

	private static final DetailLevelCodeType[] RETURN_ALL_ONLY = new DetailLevelCodeType[] {
		DetailLevelCodeType.RETURN_ALL
	};

	@Context
	private HttpServletRequest req;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getItem() {
		final String token;
		if (StringUtils.isBlank(token = HttpRequestUtil.getOAuthToken(req))) {
			final Message msg = new Message("An 'Authorization: Bearer <userToken>' header must be present in the request.");
			return Response.status(Status.UNAUTHORIZED).entity(msg).build();
		} else {
			try {
				final GetAccountCall gc = new GetAccountCall(new OAuthApiContext(Environment.PRODUCTION, token));
				gc.setDetailLevel(RETURN_ALL_ONLY);
				final AccountEntryType[] accounts = gc.getAccount();
				return Response.ok().entity(accounts).build();
			} catch (Exception unexpectedEx) {
				LOGGER.error("Failed to fetch details account", unexpectedEx);
				final Message msg = new Message(unexpectedEx.getMessage());
				return Response.serverError().entity(msg).build();
			}
		}
	}
}
