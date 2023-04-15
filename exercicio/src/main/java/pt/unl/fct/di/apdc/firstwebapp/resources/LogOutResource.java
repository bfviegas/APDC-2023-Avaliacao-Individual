package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;


@Path("/logout")
public class LogOutResource {

	private static final Logger LOG = Logger.getLogger(LogOutResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@POST
	@Path("/{tokenVerifier}")
	public Response doLogout(@PathParam("tokenVerifier") String tokenVerifier) {
		Query<Entity> tokenQuery = Query.newEntityQueryBuilder().setKind("Token")
				.setFilter(PropertyFilter.eq("verifier", tokenVerifier)).build();
		QueryResults<Entity> res = datastore.run(tokenQuery);
		if (!res.hasNext()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		} else {
			Entity authToken = res.next();
			authToken = Entity.newBuilder(authToken).set("verifier",UUID.randomUUID().toString() ).build();
			datastore.update(authToken);

		}
		LOG.info("Token revoked successfully.");
		return Response.status(Response.Status.OK).entity("Logout successful.").build();
	}
}
