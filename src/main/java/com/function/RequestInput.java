package com.function;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestInput {
	@JsonProperty("properties")
    RequestProperties properties;

	public RequestProperties getProperties() {
		return this.properties;
	}

	public void setProperties(RequestProperties properties) {
		this.properties = properties;
	}
}
