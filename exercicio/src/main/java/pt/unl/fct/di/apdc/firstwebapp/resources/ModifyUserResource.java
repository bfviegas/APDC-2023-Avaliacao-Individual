package pt.unl.fct.di.apdc.firstwebapp.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

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

@Path("/modify")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModifyUserResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public ModifyUserResource() {

	}

	@POST
	@Path("/{tokenVerifier}/{userToModify}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doModify(@PathParam("tokenVerifier") String tokenVerifier, RegisterData data, @PathParam ("userToModify")String userToModify) {
		Entity authToken;
		Query<Entity> tokenQuery = Query.newEntityQueryBuilder().setKind("Token")
				.setFilter(PropertyFilter.eq("verifier", tokenVerifier)).build();
		QueryResults<Entity> res = datastore.run(tokenQuery);
		if (!res.hasNext()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		} else {
			authToken = res.next();
		}

		String role = authToken.getString("role");
		String username = authToken.getString("user");
		
		
		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
			Entity user = txn.get(userKey);

			if (user == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User not found.").build();
			}

			Key targetUserKey = datastore.newKeyFactory().setKind("User").newKey(userToModify);
			Entity targetUser = txn.get(targetUserKey);

			if (targetUser == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Target user not found.").build();
			}
			Entity.Builder updatedUserBuilder = Entity.newBuilder(targetUser);
			String targetUserRole = targetUser.getString("user_role");

			boolean canModify = false;

			if (role.equals("USER") && userToModify.equals(username) && targetUserRole.equals("USER")) {
				canModify = true;
			} else if (role.equals("GBO") && targetUserRole.equals("USER")) {
				canModify = true;

				if (data.email != null) {
					updatedUserBuilder.set("user_email", data.email);
				}

				if (data.role != null) {
					updatedUserBuilder.set("user_role", data.role);
				}
				if (data.state != null) {
					updatedUserBuilder.set("user_accountState", data.state);
				}

			} else if (role.equals("GS") && (targetUserRole.equals("USER")|| targetUserRole.equals("GBO"))) {
				canModify = true;

				if (data.email != null) {
					updatedUserBuilder.set("user_email", data.email);
				}

				if (data.role != null) {
					updatedUserBuilder.set("user_role", data.role);
				}
				if (data.state != null) {
					updatedUserBuilder.set("user_accountState", data.state);
				}
			} else if (role.equals("SU")) {
				canModify = true;

				if (data.email != null) {
					updatedUserBuilder.set("user_email", data.email);
				}

				if (data.role != null) {
					updatedUserBuilder.set("user_role", data.role);
				}
				if (data.state != null) {
					updatedUserBuilder.set("user_account_state", data.state);
				}
			}

			if (canModify) {

				if (data.profile != null) {
					updatedUserBuilder.set("user_profile", data.profile);
				}
				if (data.phoneNumber != null) {
					updatedUserBuilder.set("user_phone_number", data.phoneNumber);
				}
				if (data.mobilePhone != null) {
					updatedUserBuilder.set("user_mobile_phone", data.mobilePhone);
				}
				if (data.occupation != null) {
					updatedUserBuilder.set("user_occupation", data.occupation);
				}
				if (data.workplace != null) {
					updatedUserBuilder.set("user_workplace", data.workplace);
				}
				if (data.address != null) {
					updatedUserBuilder.set("user_address", data.address);
				}
				if (data.compAddress != null) {
					updatedUserBuilder.set("user_comp_address", data.compAddress);
				}
				if (data.nif != null) {
					updatedUserBuilder.set("user_nif", data.nif);
				}

				Entity updatedUser = updatedUserBuilder.build();
				txn.put(updatedUser);
				txn.commit();
				return Response.ok("User modified").build();
			} else {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("You do not have permission to modify this user.")
						.build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}