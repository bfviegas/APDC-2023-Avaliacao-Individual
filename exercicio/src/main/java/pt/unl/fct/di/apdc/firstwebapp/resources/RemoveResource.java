package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import pt.unl.fct.di.apdc.firstwebapp.util.Role;

@Path("/remove")
public class RemoveResource {

	private static final Logger LOG = Logger.getLogger(RemoveResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RemoveResource() {
	}

	@DELETE
	@Path("/{tokenVerifier}/{usernameToRemove}")
	public Response doRemove(@PathParam("tokenVerifier") String tokenVerifier ,@PathParam("usernameToRemove") String usernameToRemove) {
		LOG.fine("Attempt to remove user: " + usernameToRemove);

		Entity authToken;
		Query<Entity> tokenQuery = Query.newEntityQueryBuilder().setKind("Token").setFilter(PropertyFilter.eq("verifier", tokenVerifier)).build();
		QueryResults<Entity> res = datastore.run(tokenQuery);
		if (!res.hasNext()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}else {
			authToken = res.next();
			
		}

		String role = authToken.getString("role");
		String usernameToken = authToken.getString("user");
		

		if (role.equals(Role.USER.toString()) && !usernameToRemove.equals(usernameToken)) {
			return Response.status(Status.FORBIDDEN).entity("User can only remove their own account.").build();
		}

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(usernameToRemove);
			Entity user = txn.get(userKey);
			
			if (user == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User not found.").build();
			}

			Role userRole = Role.valueOf(user.getString("user_role"));
			if (!isAuthorizedToRemove(role, userRole)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Not authorized to remove this user.").build();
			}

			txn.delete(userKey);
			txn.delete(authToken.getKey());
			LOG.info("User removed: " + usernameToRemove);
			txn.commit();
			return Response.ok("User removed successfully.").build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	private boolean isAuthorizedToRemove(String removerRole, Role userRole) {
		if (removerRole.equals(Role.SU.toString())) {
			return true;
		} else if (removerRole.equals(Role.GS.toString())) {
			return userRole.equals(Role.USER) || userRole.equals(Role.GBO) || userRole.equals(Role.GA);
		} else if (removerRole.equals(Role.GA.toString())) {
			return userRole.equals(Role.USER) || userRole.equals(Role.GBO);
		} else if (removerRole.equals(Role.GBO.toString())) {
			return userRole.equals(Role.USER);
		}else if(removerRole.equals(Role.USER.toString())) {
			return userRole.equals(Role.USER);
		}
		return false;
	}

}
