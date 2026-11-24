package mr.univ.soa.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import jakarta.enterprise.context.ApplicationScoped;
import mr.univ.soa.model.Etudiant;

@ApplicationScoped
public class EtudiantService {
    private final ConcurrentMap<Long, Etudiant> donnees = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public List<Etudiant> tous() {
        List<Etudiant> resultat = new ArrayList<>(donnees.values());
        resultat.sort(Comparator.comparing(Etudiant::getId));
        return resultat;
    }

    public Etudiant trouver(long id) { return donnees.get(id); }

    public Etudiant creer(String nom, String email) {
        long id = sequence.incrementAndGet();
        Etudiant e = new Etudiant(id, nom.trim(), email.trim());
        donnees.put(id, e);
        return e;
    }
}