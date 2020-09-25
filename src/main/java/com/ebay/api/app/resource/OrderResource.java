package com.ebay.api.app.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.ebay.sdk.call.GetItemCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;

@Path("/order")
public class OrderResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthResource.class);

	@Context
	private HttpServletRequest req;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{orderId}")
	public Response getOrder(@PathParam("orderId") String orderId) {
		final String token;
		if (StringUtils.isBlank(token = HttpRequestUtil.getOAuthToken(req))) {
			final Message msg = new Message("An 'Authorization: Bearer <userToken>' header must be present in the request.");
			return Response.status(Status.UNAUTHORIZED).entity(msg).build();
		} else if (StringUtils.isBlank(orderId)) {
			final Message msg = new Message("Invalid Order id.");
			return Response.status(Status.BAD_REQUEST).entity(msg).build();
		} else {
			try {
				final GetItemCall gc = new GetItemCall(new OAuthApiContext(Environment.PRODUCTION, token));
				DetailLevelCodeType[] detailLevels = new DetailLevelCodeType[] {DetailLevelCodeType.RETURN_ALL};
				gc.setDetailLevel(detailLevels);
				final ItemType item = gc.getItem(orderId);
//				final ItemType item = null;
				return Response.ok().entity(item).build();
			} catch (Exception unexpectedEx) {
				LOGGER.error("Failed to fetch details for order {}", orderId, unexpectedEx);
				final Message msg = new Message(unexpectedEx.getMessage());
				return Response.serverError().entity(msg).build();
			}
		}
	}
}
