package com.citruspay.v2.model;

public class AerospikeConfigBean {
	private String host;
	private Integer port;
	private String namespace;
	private String set;
	private String udfFileloc;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getUdfFileloc() {
		return udfFileloc;
	}

	public void setUdfFileloc(String udfFileloc) {
		this.udfFileloc = udfFileloc;
	}
}