package org.jmc.export;

/**
 * Created by Paul on 28/06/2016.
 */
public enum Environment {

	DEV("dev", "packer-dev", "http", "8080", "92f5e6c1b6034a9ecc44" ),
	EXPERIMENTS("experiments", "packer-experiments", "http", "8080", "92f5e6c1b6034a9ecc44" ),
	LOAD("load", "packer-load", "http", "8080", "92f5e6c1b6034a9ecc44" ),
	FUSION("fusion", "packer-fusion", "http", "8080", "92f5e6c1b6034a9ecc44" ),
	DEMO("demo", "packer-demo", "https", "443", "92f5e6c1b6034a9ecc44" ),
	TEST("test", "packer-test", "https", "443", "92f5e6c1b6034a9ecc44" ),
	PREPROD("preprod", "packer-preprod", "https", "443", "b05ba6e8b4e6755b54e4" ),
	PROD("prod", "packer-prod", "https", "443", "9c773532930d6ca3974f" );

	public final String id;
	public final String packer_id;
	public final String protocol;
	public final String port;
	public final String pusher_key;

	Environment(String id, String packer_id, String protocol, String port, String pusher_key) {
		this.id = id;
		this.packer_id = packer_id;
		this.protocol = protocol;
		this.port = port;
		this.pusher_key = pusher_key;
	}
}
