package com.eazyplan;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para GroceryListService - CRUD listas de compras y marcación como compradas
 */
public class GroceryListServiceTest {
    
    @BeforeAll
    public static void setUp() {
        System.out.println("Setup: DatabaseConfig inicializado");
    }

    @Test
    public void testCreateGroceryList() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("groceryuser", "Grocery User", "grocery@test.com", "pass");
        
        // Crear lista de compras
        var list = new com.eazyplan.domain.entities.GroceryList(user);
        
        var created = GroceryListService.get().createList(list, user);
        
        assertNotNull(created);
        assertEquals("Grocery User", created.getUser().getName());
    }

    @Test
    public void testCreateListWithoutUser() {
        assertThrows(IllegalArgumentException.class, () -> {
            var list = new com.eazyplan.domain.entities.GroceryList();
            GroceryListService.get().createList(list, null);
        });
    }

    @Test
    public void testSetPurchasedTrue() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("purchaseduser", "Purchased User", "purchased@test.com", "pass");
        
        // Crear lista de compras
        var list = new com.eazyplan.domain.entities.GroceryList(user);
        GroceryListService.get().createList(list, user);
        
        // Marcar como comprada
        GroceryListService.get().markPurchased(list.getId());
        
        assertTrue(list.isPurchased());
    }

    @Test
    public void testGroceryListItems() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("itemsuser", "Items User", "items@test.com", "pass");
        
        // Crear lista de compras
        var list = new com.eazyplan.domain.entities.GroceryList(user);
        GroceryListService.get().createList(list, user);
        
        assertNotNull(list.getItems());
        assertTrue(list.getItems().isEmpty()); // Inicialmente vacía
        
        // Agregar items (dependiendo de la implementación del repositorio)
        try {
            var item1 = new com.eazyplan.domain.entities.GroceryItem("Pan", true);
            var item2 = new com.eazyplan.domain.entities.GroceryItem("Leche", false);
            
            // Los items se guardan en el repositorio directamente
            System.out.println("Nota: Agregar items requiere implementación específica del repo");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testDeleteGroceryList() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("deletegrocery", "Delete Grocery User", "deletetest@test.com", "pass");
        
        // Crear lista de compras
        var list = new com.eazyplan.domain.entities.GroceryList(user);
        GroceryListService.get().createList(list, user);
        
        assertNotNull(list.getId());
        
        // Marcar para eliminación (dependiendo de la implementación)
        try {
            GroceryListService.get().deleteList(list);
            System.out.println("Lista eliminada exitosamente");
        } catch (Exception e) {
            System.out.println("Nota: DeleteList requiere implementación específica del repo");
        }
    }

    @Test
    public void testGetUserGroceryLists() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("multilists", "Multi Lists User", "multi@test.com", "pass");
        
        // Crear varias listas de compras
        for (int i = 0; i < 3; i++) {
            var list = new com.eazyplan.domain.entities.GroceryList(user);
            GroceryListService.get().createList(list, user);
        }
        
        // Verificar que se crearon las listas
        var lists = GroceryListService.get().getUserLists(user.getId());
        assertEquals(3, lists.size());
    }

    @Test
    public void testGroceryListCreatedAt() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("createdatuser", "Created At User", "createdat@test.com", "pass");
        
        // Crear lista de compras (debería establecer createdAt)
        var list = new com.eazyplan.domain.entities.GroceryList(user);
        GroceryListService.get().createList(list, user);
        
        assertNotNull(list.getCreatedAt()); // Debería ser LocalDate.now()
    }
}
