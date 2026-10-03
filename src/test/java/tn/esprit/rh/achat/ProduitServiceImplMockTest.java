package tn.esprit.rh.achat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Produit;
import tn.esprit.rh.achat.repositories.CategorieProduitRepository;
import tn.esprit.rh.achat.repositories.ProduitRepository;
import tn.esprit.rh.achat.repositories.StockRepository;
import tn.esprit.rh.achat.services.ProduitServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ProduitServiceImpl AVEC Mockito.
 * Les dépendances (repositories) sont mockées : aucune base de données réelle n'est nécessaire.
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProduitServiceImplMockTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CategorieProduitRepository categorieProduitRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    // -----------------------------------------------------------------------
    // Données de test réutilisables
    // -----------------------------------------------------------------------
    private Produit buildProduit(Long id, String code, String libelle, float prix) {
        Produit p = new Produit();
        p.setIdProduit(id);
        p.setCodeProduit(code);
        p.setLibelleProduit(libelle);
        p.setPrix(prix);
        p.setDateCreation(new Date());
        p.setDateDerniereModification(new Date());
        return p;
    }

    // -----------------------------------------------------------------------
    // 1. Test addProduit (Create) avec Mockito
    // -----------------------------------------------------------------------
    @Test
    @Order(1)
    public void testAddProduit_withMockito() {
        // Arrange
        // ProduitServiceImpl.addProduit() fait : produitRepository.save(p); return p;
        // JPA/Hibernate set l'id directement sur l'objet p via reference.
        // On simule ce comportement avec doAnswer.
        Produit input = buildProduit(null, "PROD-M01", "Stylo Mock", 2.0f);

        doAnswer(invocation -> {
            Produit p = invocation.getArgument(0);
            p.setIdProduit(10L); // simule la generation d'id par JPA
            return p;
        }).when(produitRepository).save(any(Produit.class));

        // Act
        Produit result = produitService.addProduit(input);

        log.info("[MOCK] Produit ajouté : {}", result);

        // Assert
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(10L, result.getIdProduit(), "L'id mocké doit être 10");
        assertEquals("PROD-M01", result.getCodeProduit());

        // Vérifier que save() a bien été appelé une fois
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    // -----------------------------------------------------------------------
    // 2. Test retrieveAllProduits (Read - liste) avec Mockito
    // -----------------------------------------------------------------------
    @Test
    @Order(2)
    public void testRetrieveAllProduits_withMockito() {
        // Arrange
        List<Produit> mockList = Arrays.asList(
                buildProduit(1L, "PROD-M01", "Cahier Mock", 3.5f),
                buildProduit(2L, "PROD-M02", "Règle Mock", 1.8f),
                buildProduit(3L, "PROD-M03", "Crayon Mock", 0.9f)
        );
        when(produitRepository.findAll()).thenReturn(mockList);

        // Act
        List<Produit> result = produitService.retrieveAllProduits();

        log.info("[MOCK] Nombre de produits récupérés : {}", result.size());

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size(), "La liste mockée doit contenir 3 produits");
        assertEquals("Cahier Mock", result.get(0).getLibelleProduit());

        verify(produitRepository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------
    // 3. Test updateProduit (Update) avec Mockito
    // -----------------------------------------------------------------------
    @Test
    @Order(3)
    public void testUpdateProduit_withMockito() {
        // Arrange : produit avec libellé mis à jour
        Produit updated = buildProduit(5L, "PROD-M05", "Gomme Premium Mock", 1.5f);

        when(produitRepository.save(any(Produit.class))).thenReturn(updated);

        // Act
        Produit result = produitService.updateProduit(updated);

        log.info("[MOCK] Produit mis à jour : {}", result);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getIdProduit());
        assertEquals("Gomme Premium Mock", result.getLibelleProduit(),
                "Le libellé mis à jour doit correspondre");
        assertEquals(1.5f, result.getPrix(), 0.001f);

        verify(produitRepository, times(1)).save(updated);
    }

    // -----------------------------------------------------------------------
    // 4. Test retrieveProduit (Read - par id) avec Mockito
    // -----------------------------------------------------------------------
    @Test
    @Order(4)
    public void testRetrieveProduit_withMockito() {
        // Arrange
        Long produitId = 7L;
        Produit mockProduit = buildProduit(produitId, "PROD-M07", "Effaceur Mock", 3.0f);

        when(produitRepository.findById(produitId)).thenReturn(Optional.of(mockProduit));

        // Act
        Produit result = produitService.retrieveProduit(produitId);

        log.info("[MOCK] Produit récupéré par id={} : {}", produitId, result);

        // Assert
        assertNotNull(result, "Le produit récupéré ne doit pas être null");
        assertEquals(produitId, result.getIdProduit(), "L'id doit correspondre");
        assertEquals("PROD-M07", result.getCodeProduit());
        assertEquals("Effaceur Mock", result.getLibelleProduit());

        verify(produitRepository, times(1)).findById(produitId);
        // Vérifier qu'aucune autre interaction parasite n'a eu lieu
        verifyNoMoreInteractions(produitRepository);
    }
}
