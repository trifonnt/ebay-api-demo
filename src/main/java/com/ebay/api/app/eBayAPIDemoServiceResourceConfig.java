package com.ebay.api.app;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import com.ebay.api.app.resource.AccountResource;
import com.ebay.api.app.resource.AuthResource;
import com.ebay.api.app.resource.ItemResource;
//import com.ebay.api.app.resource.ItemResource;
import com.ebay.api.app.resource.OrderResource;

@Configuration
@ApplicationPath("/v1")
public class eBayAPIDemoServiceResourceConfig extends ResourceConfig {

	@PostConstruct
	public void init() {
		super.register(AuthResource.class);
		super.register(OrderResource.class);
		super.register(ItemResource.class);
		super.register(AccountResource.class);
	}

}
