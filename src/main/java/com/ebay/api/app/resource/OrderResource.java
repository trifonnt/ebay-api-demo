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
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;

@Path("/orders")
public class OrderResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthResource.class);

	@Context
	private HttpServletRequest req;

//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/{orderId}")
//	public Response getOrder(@PathParam("orderId") String orderId) {
//	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getOrders() {
		final String token;
		if (StringUtils.isBlank(token = HttpRequestUtil.getOAuthToken(req))) {
			final Message msg = new Message("An 'Authorization: Bearer <userToken>' header must be present in the request.");
			return Response.status(Status.UNAUTHORIZED).entity(msg).build();
		} else {
			try {
				final GetOrdersCall getOrdersCall = new GetOrdersCall(new OAuthApiContext(Environment.PRODUCTION, token));

				DetailLevelCodeType[] detailLevels = new DetailLevelCodeType[] {DetailLevelCodeType.RETURN_ALL};
				getOrdersCall.setDetailLevel(detailLevels);

				getOrdersCall.setNumberOfDays( 2 );

				final OrderType orders[] = getOrdersCall.getOrders();

				return Response.ok().entity(orders).build();
			} catch (Exception unexpectedEx) {
				LOGGER.error("Failed to fetch details for orders", unexpectedEx);
				final Message msg = new Message(unexpectedEx.getMessage());
				return Response.serverError().entity(msg).build();
			}
		}
	}
}
