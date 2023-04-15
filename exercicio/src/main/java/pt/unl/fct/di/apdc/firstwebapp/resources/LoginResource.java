package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.gson.Gson;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public LoginResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(RegisterData data) {
		LOG.fine("Attempt to login user: " + data.username);

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);

		if (user != null) {
			String hashedPwd = user.getString("user_pwd");
			if (hashedPwd.equals(DigestUtils.sha512Hex(data.password))) {
				AuthToken token = new AuthToken(data.username, user.getString("user_role"));
				Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(token.verifier);
				Entity tokenEntity = Entity.newBuilder(tokenKey).set("user", token.username).set("role", token.role)
						.set("creationDate", token.creationDate)
						.set("expirationDate", token.expirationDate).set("verifier", token.verifier).build();
				datastore.put(tokenEntity);
				LOG.info("User ' + " + data.username + "' logged in succesfully.");
				return Response.ok("Welcome " + data.username + "!\n" + "Your role:" + user.getString("user_role") + "\n"
						+ "\n Token info:\n" + g.toJson(token)).build();
			} else {
				LOG.warning("Wrong password for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();

			}
		} else {
			// Username does not exist
			LOG.warning("Failed login attempt for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
	}

}
