package mr.univ.soa.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import mr.univ.soa.model.Dep;

@ApplicationScoped
@Path("/departements")
@Produces(MediaType.APPLICATION_JSON)
public class DepResource {

    private final ConcurrentMap<Long, Dep> donnees = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    private boolean invalide(Dep d) {
        return d == null || d.getNom() == null || d.getNom().isBlank();
    }

    private Response erreurDonnees() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("erreur", "DONNEES_INVALIDES",
                               "message", "Nom invalide"))
                .build();
    }

    private Response erreurIntrouvable() {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("erreur", "DEPARTEMENT_INTROUVABLE",
                               "message", "Identifiant inconnu"))
                .build();
    }

    @GET
    public Response lister() {
        List<Dep> liste = new ArrayList<>(donnees.values());
        liste.sort(Comparator.comparing(Dep::getId));
        return Response.ok(liste).build();
    }

    @GET
    @Path("/{id}")
    public Response trouver(@PathParam("id") long id) {
        Dep d = donnees.get(id);
        if (d == null) return erreurIntrouvable();
        return Response.ok(d).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ajouter(Dep entree, @Context UriInfo uriInfo) {
        if (invalide(entree)) return erreurDonnees();
        long id = sequence.incrementAndGet();
        Dep cree = new Dep(id, entree.getNom().trim());
        donnees.put(id, cree);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(id)).build();
        return Response.created(uri).entity(cree).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifier(@PathParam("id") long id, Dep entree) {
        if (invalide(entree)) return erreurDonnees();
        Dep maj = donnees.computeIfPresent(id,
                (k, v) -> new Dep(id, entree.getNom().trim()));
        if (maj == null) return erreurIntrouvable();
        return Response.ok(maj).build();
    }

    @DELETE
    @Path("/{id}")
    public Response supprimer(@PathParam("id") long id) {
        if (donnees.remove(id) == null) return erreurIntrouvable();
        return Response.noContent().build();
    }
}