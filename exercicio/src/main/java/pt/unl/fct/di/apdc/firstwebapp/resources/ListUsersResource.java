package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import pt.unl.fct.di.apdc.firstwebapp.util.UserData;

@Path("/listusers")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ListUsersResource {

	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public ListUsersResource() {
	}

	@GET
	@Path("/{tokenVerifier}")
	public Response listUsers(@PathParam("tokenVerifier") String tokenVerifier) {
		Entity authToken;
		Query<Entity> tokenQuery = Query.newEntityQueryBuilder().setKind("Token")
				.setFilter(PropertyFilter.eq("verifier", tokenVerifier)).build();
		QueryResults<Entity> res = datastore.run(tokenQuery);
		if (!res.hasNext()) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		} else {
			authToken = res.next();

		}

		List<UserData> users = new ArrayList<>();

		Transaction txn = datastore.newTransaction();
		try {

			// Para users com role USER, listar apenas atributos específicos e com contas
			// públicas e ativas
			if ("USER".equals(authToken.getString("role"))) {
				Query<Entity> userQuery = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(CompositeFilter.and(PropertyFilter.eq("user_accountState", "active"),
								PropertyFilter.eq("user_profile", "public"), PropertyFilter.eq("user_role", "USER")))
						.build();

				QueryResults<Entity> queryResults = datastore.run(userQuery);
				while (queryResults.hasNext()) {
					Entity entity = queryResults.next();
					String username = entity.getString("user_username");
					String email = entity.getString("user_email");
					String name = entity.getString("user_name");
					users.add(new UserData(username, email, name));
				}
			}
			// Para usuários com role GBO, listar todos os atributos dos usuários
			else if ("GBO".equals(authToken.getString("role"))) {
				Query<Entity> userQuery = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(PropertyFilter.eq("user_role", "USER")).build();

				QueryResults<Entity> queryResults = datastore.run(userQuery);
				while (queryResults.hasNext()) {
					Entity entity = queryResults.next();
					String username = entity.getString("user_username");
					String email = entity.getString("user_email");
					String name = entity.getString("user_name");
					String role = entity.getString("user_role");
					String state = entity.getString("user_accountState");
					String profile = entity.getString("user_profile");
					String phoneNumber = entity.getString("user_phone_number");
					String mobilePhone = entity.getString("user_mobile_phone");
					String occupation = entity.getString("user_occupation");
					String workplace = entity.getString("user_occupation");
					String address = entity.getString("user_address");
					String compAddress = entity.getString("user_comp_address");
					String nif = entity.getString("user_nif");

					users.add(new UserData(username, email, name, role, state, profile, phoneNumber, mobilePhone,
							occupation, workplace, address, compAddress, nif));
				}
			}
			// Para usuários com role GS, listar todos os atributos dos usuários com role
			// USER ou GBO
			else if ("GS".equals(authToken.getString("role"))) {
				Query<Entity> userQuery = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(CompositeFilter.and(PropertyFilter.in("user_role", ListValue.of("USER", "GBO"))))
						.build();

				QueryResults<Entity> queryResults = datastore.run(userQuery);
				while (queryResults.hasNext()) {
					Entity entity = queryResults.next();
					String username = entity.getString("user_username");
					String email = entity.getString("user_email");
					String name = entity.getString("user_name");
					String role = entity.getString("user_role");
					String state = entity.getString("user_accountState");
					String profile = entity.getString("user_profile");
					String phoneNumber = entity.getString("user_phone_number");
					String mobilePhone = entity.getString("user_mobile_phone");
					String occupation = entity.getString("user_occupation");
					String workplace = entity.getString("user_occupation");
					String address = entity.getString("user_address");
					String compAddress = entity.getString("user_comp_address");
					String nif = entity.getString("user_nif");

					users.add(new UserData(username, email, name, role, state, profile, phoneNumber, mobilePhone,
							occupation, workplace, address, compAddress, nif));
				}
			}

			// Para usuários com role SU, listar todos os atributos dos usuários
			else if ("SU".equals(authToken.getString("role"))) {
				Query<Entity> userQuery = Query.newEntityQueryBuilder().setKind("User").build();

				QueryResults<Entity> queryResults = datastore.run(userQuery);
				while (queryResults.hasNext()) {
					Entity entity = queryResults.next();
					String username = entity.getString("user_username");
					String email = entity.getString("user_email");
					String name = entity.getString("user_name");
					String role = entity.getString("user_role");
					String state = entity.getString("user_accountState");
					String profile = entity.getString("user_profile");
					String phoneNumber = entity.getString("user_phone_number");
					String mobilePhone = entity.getString("user_mobile_phone");
					String occupation = entity.getString("user_occupation");
					String workplace = entity.getString("user_occupation");
					String address = entity.getString("user_address");
					String compAddress = entity.getString("user_comp_address");
					String nif = entity.getString("user_nif");

					users.add(new UserData(username, email, name, role, state, profile, phoneNumber, mobilePhone,
							occupation, workplace, address, compAddress, nif));
				}
			}

			return Response.ok(users).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}
