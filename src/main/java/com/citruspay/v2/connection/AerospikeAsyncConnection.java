package com.citruspay.v2.connection;

import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.glassfish.hk2.configuration.api.Dynamicity;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.async.AsyncClient;
import com.aerospike.client.policy.WritePolicy;


@Service
@ConfiguredBy(value = "AerospikeConfigBean")
public class AerospikeAsyncConnection {

	private String aerospikeHost;

	private Integer aerospikePort;

	private String aerospikeNamespace;

	private String aerospikeSet;

	private String udfFileLocation;

	private WritePolicy policy;

	private static Logger log = LoggerFactory
			.getLogger(AerospikeAsyncConnection.class);

	public AerospikeAsyncConnection(
			@Configured(value = "host", dynamicity = Dynamicity.FULLY_DYNAMIC) final String aerospikeHost,
			@Configured(value = "port", dynamicity = Dynamicity.FULLY_DYNAMIC) final Integer aerospikePort,
			@Configured(value = "namespace", dynamicity = Dynamicity.FULLY_DYNAMIC) final String aerospikeNamespace,
			@Configured(value = "set", dynamicity = Dynamicity.FULLY_DYNAMIC) final String aerospikeSet,
			@Configured(value = "udfFileloc", dynamicity = Dynamicity.FULLY_DYNAMIC) final String udfFileLocation) {
		this.aerospikeHost = aerospikeHost;
		this.aerospikePort = aerospikePort;
		this.aerospikeNamespace = aerospikeNamespace;
		this.aerospikeSet = aerospikeSet;
		this.udfFileLocation = udfFileLocation;
	}

	public AsyncClient getAerospikeConnectionClient() {
		AsyncClient asClient = null;
			try {
				log.info("Initializing aerospike client");
				asClient = new AsyncClient(aerospikeHost,
						aerospikePort);
			} catch (Exception e) {
				log.error("Exception while creating aerospike client", e);
			}
			
			try {
	            // Write a single value.  
				policy = new WritePolicy();
		        policy.timeout = 50;
	            Key key = new Key("test", "myset", "mykey");
	            Bin bin = new Bin("mybin", "vertex");
	            System.out.println(String.format(
	                "Write: namespace=%s set=%s key=%s value=%s", 
	                key.namespace, key.setName, key.userKey, bin.value));
	            asClient.put(policy,key, bin);

	            //waitTillComplete();
	        }
	        finally {
	        	asClient.close();
	        }
		
		return asClient;
	}
}
