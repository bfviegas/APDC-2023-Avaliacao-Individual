package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RegisterResource() {

	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegister(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);

		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		Transaction txn = datastore.newTransaction();
		try {
			// query para verificar se existem users no sistema
			Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").setLimit(1).build();
			QueryResults<Entity> results = datastore.run(query);

			// se não existir nenhum user no sistema, o primeiro user criado terá role SU
			boolean isFirstUser = !results.hasNext();
			String role = isFirstUser? "SU":"USER";
			String accState = isFirstUser? "active": "inactive";

			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey).set("user_username", data.username)
						.set("user_pwd", DigestUtils.sha512Hex(data.password)).set("user_email", data.email)
						.set("user_name", data.name).set("user_role", role).set("user_accountState", accState)
						.set("user_profile", data.profile == null ? "N/A" : data.profile)
						.set("user_phone_number", data.phoneNumber == null ? "N/A" : data.phoneNumber)
						.set("user_mobile_phone", data.mobilePhone == null ? "N/A" : data.mobilePhone)
						.set("user_occupation", data.occupation == null ? "N/A" : data.occupation)
						.set("user_workplace", data.workplace == null ? "N/A" : data.workplace)
						.set("user_address", data.address == null ? "N/A" : data.address)
						.set("user_comp_address", data.compAddress == null ? "N/A" : data.compAddress)
						.set("user_nif", data.nif == null ? "N/A" : data.nif).build();
				txn.add(user);
				LOG.info("User registered" + data.username);
				txn.commit();
				return Response.ok("User succesfully registered").build();
			}

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}
