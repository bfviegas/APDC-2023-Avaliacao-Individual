package pt.unl.fct.di.apdc.firstwebapp.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import pt.unl.fct.di.apdc.firstwebapp.util.ModifyPasswordData;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

@Path("/modifypwd")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModifyPasswordResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public ModifyPasswordResource() {

	}

	@POST
	@Path("/{tokenVerifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doModify(@PathParam("tokenVerifier") String tokenVerifier, ModifyPasswordData data) {

		Entity authToken;
		Query<Entity> tokenQuery = Query.newEntityQueryBuilder().setKind("Token")
				.setFilter(PropertyFilter.eq("verifier", tokenVerifier)).build();
		QueryResults<Entity> res = datastore.run(tokenQuery);
		if (!res.hasNext()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		} else {
			authToken = res.next();
		}

		String tokenUsername = authToken.getString("user");


		Key userKey = datastore.newKeyFactory().setKind("User").newKey(tokenUsername);
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);

			String password = user.getString("user_pwd");

			if (!password.equals(DigestUtils.sha512Hex(data.currentPassword))) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Old password is incorrect.").build();
			}

			if (!data.newPassword.equals(data.newPasswordConfirmation)) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("New password confirmation does not match.").build();
			}

			if (data.currentPassword.equals(data.newPassword)) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("New password must be different from old password.")
						.build();
			}

			Entity.Builder updatedUserBuilder = Entity.newBuilder(user);
			updatedUserBuilder.set("user_pwd", DigestUtils.sha512Hex(data.newPassword));
			Entity updatedUser = updatedUserBuilder.build();
			txn.put(updatedUser);
			txn.commit();
			return Response.ok("Password modified").build();

		} finally {
			if (txn.isActive())
				txn.rollback();
		}

	}
}
