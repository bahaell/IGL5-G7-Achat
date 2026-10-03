package tn.esprit.rh.achat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.rh.achat.entities.Produit;
import tn.esprit.rh.achat.services.IProduitService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour ProduitServiceImpl SANS Mockito.
 * Utilise @SpringBootTest + H2 en mémoire (profil "test").
 */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProduitServiceImplTest {

    @Autowired
    private IProduitService produitService;

    // -----------------------------------------------------------------------
    // 1. Test addProduit (Create)
    // -----------------------------------------------------------------------
    @Test
    @Order(1)
    public void testAddProduit() {
        Produit p = new Produit();
        p.setCodeProduit("PROD-001");
        p.setLibelleProduit("Stylo Bic");
        p.setPrix(1.5f);
        p.setDateCreation(new Date());
        p.setDateDerniereModification(new Date());

        Produit saved = produitService.addProduit(p);

        log.info("Produit ajouté : {}", saved);

        assertNotNull(saved, "Le produit sauvegardé ne doit pas être null");
        assertNotNull(saved.getIdProduit(), "L'identifiant auto-généré doit être non null");
        assertEquals("PROD-001", saved.getCodeProduit());
        assertTrue(saved.getPrix() > 0, "Le prix doit être positif");

        // Nettoyage
        produitService.deleteProduit(saved.getIdProduit());
    }

    // -----------------------------------------------------------------------
    // 2. Test retrieveAllProduits (Read - liste)
    // -----------------------------------------------------------------------
    @Test
    @Order(2)
    public void testRetrieveAllProduits() {
        // Préparer 2 produits
        Produit p1 = new Produit();
        p1.setCodeProduit("PROD-A");
        p1.setLibelleProduit("Cahier");
        p1.setPrix(3.0f);
        p1.setDateCreation(new Date());
        p1.setDateDerniereModification(new Date());
        produitService.addProduit(p1);

        Produit p2 = new Produit();
        p2.setCodeProduit("PROD-B");
        p2.setLibelleProduit("Règle");
        p2.setPrix(2.5f);
        p2.setDateCreation(new Date());
        p2.setDateDerniereModification(new Date());
        produitService.addProduit(p2);

        List<Produit> produits = produitService.retrieveAllProduits();

        log.info("Nombre de produits en base : {}", produits.size());

        assertNotNull(produits, "La liste ne doit pas être null");
        assertFalse(produits.isEmpty(), "La liste ne doit pas être vide");
        assertTrue(produits.size() >= 2, "Il doit y avoir au moins 2 produits");

        // Nettoyage
        produits.forEach(p -> produitService.deleteProduit(p.getIdProduit()));
    }

    // -----------------------------------------------------------------------
    // 3. Test updateProduit (Update)
    // -----------------------------------------------------------------------
    @Test
    @Order(3)
    public void testUpdateProduit() {
        // Créer un produit initial
        Produit p = new Produit();
        p.setCodeProduit("PROD-002");
        p.setLibelleProduit("Gomme");
        p.setPrix(0.5f);
        p.setDateCreation(new Date());
        p.setDateDerniereModification(new Date());
        Produit saved = produitService.addProduit(p);

        // Modifier le libellé et le prix
        saved.setLibelleProduit("Gomme Premium");
        saved.setPrix(1.2f);
        saved.setDateDerniereModification(new Date());

        Produit updated = produitService.updateProduit(saved);

        log.info("Produit mis à jour : {}", updated);

        assertNotNull(updated, "Le produit mis à jour ne doit pas être null");
        assertEquals("Gomme Premium", updated.getLibelleProduit(),
                "Le libellé doit être mis à jour");
        assertEquals(1.2f, updated.getPrix(), 0.001f,
                "Le prix mis à jour doit correspondre");

        // Nettoyage
        produitService.deleteProduit(updated.getIdProduit());
    }

    // -----------------------------------------------------------------------
    // 4. Test retrieveProduit (Read - par id)
    // -----------------------------------------------------------------------
    @Test
    @Order(4)
    public void testRetrieveProduit() {
        // Créer un produit
        Produit p = new Produit();
        p.setCodeProduit("PROD-003");
        p.setLibelleProduit("Crayon HB");
        p.setPrix(0.8f);
        p.setDateCreation(new Date());
        p.setDateDerniereModification(new Date());
        Produit saved = produitService.addProduit(p);

        // Récupérer par id
        Produit found = produitService.retrieveProduit(saved.getIdProduit());

        log.info("Produit récupéré : {}", found);

        assertNotNull(found, "Le produit récupéré ne doit pas être null");
        assertEquals(saved.getIdProduit(), found.getIdProduit(),
                "L'id doit correspondre");
        assertEquals("PROD-003", found.getCodeProduit(),
                "Le code produit doit correspondre");

        // Nettoyage
        produitService.deleteProduit(found.getIdProduit());
    }
}
