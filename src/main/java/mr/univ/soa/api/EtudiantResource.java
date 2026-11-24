package mr.univ.soa.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
import mr.univ.soa.model.Etudiant;

@ApplicationScoped
@Path("/etudiants")
@Produces(MediaType.APPLICATION_JSON)
public class EtudiantResource {

    // Annotation PATCH (absente de Jakarta REST 3.1)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @HttpMethod("PATCH")
    public @interface PATCH {
    }

    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    // Stockage en mémoire (une seule instance grâce à @ApplicationScoped)
    private final ConcurrentMap<Long, Etudiant> donnees = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    // ---------- Validation et erreurs ----------

    private boolean invalide(Etudiant e) {
        return e == null
                || e.getNom() == null || e.getNom().isBlank()
                || e.getEmail() == null || !e.getEmail().matches(EMAIL_REGEX);
    }

    private boolean invalidePartiel(Etudiant e) {
        if (e == null) return true;
        if (e.getNom() == null && e.getEmail() == null) return true;
        if (e.getNom() != null && e.getNom().isBlank()) return true;
        if (e.getEmail() != null && !e.getEmail().matches(EMAIL_REGEX)) return true;
        return false;
    }

    private Response erreurDonnees() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("erreur", "DONNEES_INVALIDES",
                               "message", "Nom ou email invalide"))
                .build();
    }

    private Response erreurIntrouvable() {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("erreur", "ETUDIANT_INTROUVABLE",
                               "message", "Identifiant inconnu"))
                .build();
    }

    // ---------- READ ----------

    @GET
    public Response lister() {
        List<Etudiant> liste = new ArrayList<>(donnees.values());
        liste.sort(Comparator.comparing(Etudiant::getId));
        return Response.ok(liste).build();
    }

    @GET
    @Path("/{id}")
    public Response trouver(@PathParam("id") long id) {
        Etudiant e = donnees.get(id);
        if (e == null) return erreurIntrouvable();
        return Response.ok(e).build();
    }

    // ---------- CREATE ----------

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ajouter(Etudiant entree, @Context UriInfo uriInfo) {
        if (invalide(entree)) return erreurDonnees();
        long id = sequence.incrementAndGet();
        Etudiant cree = new Etudiant(id, entree.getNom().trim(), entree.getEmail().trim());
        donnees.put(id, cree);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(id)).build();
        return Response.created(uri).entity(cree).build();
    }

    // ---------- UPDATE (complet) ----------

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response remplacer(@PathParam("id") long id, Etudiant entree) {
        if (invalide(entree)) return erreurDonnees();
        Etudiant maj = donnees.computeIfPresent(id,
                (k, v) -> new Etudiant(id, entree.getNom().trim(), entree.getEmail().trim()));
        if (maj == null) return erreurIntrouvable();
        return Response.ok(maj).build();
    }

    // ---------- UPDATE (partiel) ----------

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifierPartiel(@PathParam("id") long id, Etudiant entree) {
        if (invalidePartiel(entree)) return erreurDonnees();
        Etudiant maj = donnees.computeIfPresent(id,
                (k, v) -> new Etudiant(id,
                        entree.getNom() != null ? entree.getNom().trim() : v.getNom(),
                        entree.getEmail() != null ? entree.getEmail().trim() : v.getEmail()));
        if (maj == null) return erreurIntrouvable();
        return Response.ok(maj).build();
    }

    // ---------- DELETE ----------

    @DELETE
    @Path("/{id}")
    public Response supprimer(@PathParam("id") long id) {
        if (donnees.remove(id) == null) return erreurIntrouvable();
        return Response.noContent().build();
    }
}