package mr.univ.soa.api;

import java.net.URI;
import java.util.Map;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import mr.univ.soa.model.Etudiant;
import mr.univ.soa.service.EtudiantService;

@Path("/etudiants")
@Produces(MediaType.APPLICATION_JSON)
public class EtudiantResource {
    @Inject
    EtudiantService service;

    @GET
    public Response lister() {
        return Response.ok(service.tous()).build();
    }

    @GET
    @Path("/{id}")
    public Response trouver(@PathParam("id") long id) {
        Etudiant e = service.trouver(id);
        if (e == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("erreur", "ETUDIANT_INTROUVABLE",
                                   "message", "Identifiant inconnu"))
                    .build();
        }
        return Response.ok(e).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ajouter(Etudiant entree, @Context UriInfo uriInfo) {
        if (entree == null || entree.getNom() == null
                || entree.getNom().isBlank()
                || entree.getEmail() == null
                || !entree.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erreur", "DONNEES_INVALIDES",
                                   "message", "Nom ou email invalide"))
                    .build();
        }
        Etudiant cree = service.creer(entree.getNom(), entree.getEmail());
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(cree.getId())).build();
        return Response.created(uri).entity(cree).build();
    }
}