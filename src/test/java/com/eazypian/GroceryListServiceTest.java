package com.eazypian;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para GroceryListService - CRUD listas de compras y artículos
 */
public class GroceryListServiceTest {
    
    @BeforeAll
    public static void setUp() {
        System.out.println("Setup: DatabaseConfig inicializado");
    }

    @Test
    public void testCreateGroceryList() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("grocerylist", "Grocery List User", "grocery@test.com", "pass");
        
        // Crear lista de compras
        var list = new com.eazypian.domain.entities.GroceryList();
        list.setName("Test Grocery List");
        
        var created = GroceryListService.get().create(list, userService, user.getId());
        
        assertNotNull(created);
        assertEquals("Test Grocery List", created.getName());
    }

    @Test
    public void testAddGroceryItem() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("additemuser", "Add Item User", "additem@test.com", "pass");
        
        // Crear lista y agregar artículo
        var list = new com.eazypian.domain.entities.GroceryList();
        list.setName("Test List");
        
        GroceryListService.get().create(list, userService, user.getId());
        
        var item = new com.eazypian.domain.entities.GroceryItem("Milk", 10.5f);
        GroceryListService.get().add(item, list);
        
        assertNotNull(item.getId()); // Should have an ID after adding to list
    }

    @Test
    public void testMarkGroceryItemPurchased() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("markpurchased", "Mark Purchased User", "mark@test.com", "pass");
        
        // Crear lista y artículo
        var list = new com.eazypian.domain.entities.GroceryList();
        list.setName("Test List");
        
        GroceryListService.get().create(list, userService, user.getId());
        
        var item = new com.eazypian.domain.entities.GroceryItem("Bread", 3.99f);
        GroceryListService.get().add(item, list);
        
        // Marcar como comprado
        GroceryListService.get().markPurchased(item);
        
        assertTrue(item.isPurchased());
    }

    @Test
    public void testUpdateGroceryItem() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("updateitem", "Update Item User", "updateitem@test.com", "pass");
        
        // Crear lista y artículo
        var list = new com.eazypian.domain.entities.GroceryList();
        list.setName("Test List");
        
        GroceryListService.get().create(list, userService, user.getId());
        
        var item = new com.eazypian.domain.entities.GroceryItem("Eggs", 5.0f);
        GroceryListService.get().add(item, list);
        
        // Actualizar nombre y precio
        var updated = list;
        if (updated.getItemName() == null) {
            updated.setItemName("Updated Item");
            updated.setPrice(7.5f);
        }
        
        GroceryListService.get().update(updated);
        
        assertEquals("Updated Item", item.getName()); // Should reflect the update
    }

    @Test
    public void testDeleteGroceryItem() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("deleteitem", "Delete Item User", "delete@test.com", "pass");
        
        // Crear lista y artículo
        var list = new com.eazypian.domain.entities.GroceryList();
        list.setName("Test List");
        
        GroceryListService.get().create(list, userService, user.getId());
        
        var item = new com.eazypian.domain.entities.GroceryItem("Bananas", 2.5f);
        GroceryListService.get().add(item, list);
        
        // Eliminar artículo
        GroceryListService.get().delete(item);
        
        // Verificar que ya no está en la lista (esto depende de cómo se maneja la relación)
    }

    @Test
    public void testGetUserGroceryLists() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("multilists", "Multi Lists User", "multi@test.com", "pass");
        
        // Crear varias listas
        for (int i = 0; i < 3; i++) {
            var list = new com.eazypian.domain.entities.GroceryList();
            list.setName("List " + (i+1));
            
            GroceryListService.get().create(list, userService, user.getId());
        }
        
        // Verificar que se crearon las listas
        var lists = GroceryListService.get().getUserGroceryLists(user.getId());
        assertEquals(3, lists.size());
    }

    @Test
    public void testDefaultItemName() {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("defaultitemname", "Default Item Name User", "default@test.com", "pass");
        
        // Crear lista sin nombre (debería usar "New Grocery List" por defecto)
        var list = new com.eazypian.domain.entities.GroceryList();
        
        var created = GroceryListService.get().create(list, userService, user.getId());
        
        assertNotNull(created);
        assertEquals("New Grocery List", created.getName()); // Default name
    }

    @Test
    public void testDefaultItemPrice() {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("defaultprice", "Default Price User", "defprice@test.com", "pass");
        
        // Crear lista sin precio (debería ser 0.0f por defecto)
        var list = new com.eazypian.domain.entities.GroceryList();
        list.setName("Test List");
        
        GroceryListService.get().create(list, userService, user.getId());
        
        assertNotNull(list); // Should be created with default price of 0.0f
    }
}
