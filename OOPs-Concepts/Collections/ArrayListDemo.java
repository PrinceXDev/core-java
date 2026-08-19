/*
 * ArrayList — a resizable array from java.util.
 *
 * - An array has a fixed size: `new String[3]` can never hold a 4th item.
 *   ArrayList grows and shrinks on its own as you add/remove.
 * - Backed by an array internally, so get(index) is instant (O(1)),
 *   but insert/remove in the middle shifts elements (O(n)).
 * - Stores objects only, never primitives — use Integer, Double, etc.
 * - Keeps insertion order and allows duplicates.
 *
 * Real-time example: an e-commerce shopping cart. You don't know how many
 * items the customer will add, they can remove items, change quantities,
 * and the order they added them in matters on the invoice.
 */

/* ArrayList are stored in heap memory
*/

import java.util.ArrayList;
import java.util.List;

public class ArrayListDemo {

    public static void main(String[] args) {
        // Declare with the interface on the left — good practice.
        List<CartItem> cart = new ArrayList<>();

        // add() — customer keeps adding, no size declared anywhere
        cart.add(new CartItem("Wireless Mouse", 799, 1));
        cart.add(new CartItem("Keyboard", 1499, 1));
        cart.add(new CartItem("USB-C Cable", 299, 2));
        cart.add(new CartItem("Laptop Stand", 1999, 1));

        System.out.println("Items in cart: " + cart.size());

        // get(index) — read the 2nd item
        System.out.println("Second item: " + cart.get(1).getName());

        // set(index, value) — customer changes quantity of the cable
        cart.set(2, new CartItem("USB-C Cable", 299, 3));

        // remove(index) — changed their mind about the laptop stand
        cart.remove(3);

        // add(index, value) — a "recommended" item pushed to the top
        cart.add(0, new CartItem("Mouse Pad", 249, 1));

        // enhanced for loop — print the invoice
        System.out.println("\n--- Cart ---");
        double total = 0;
        for (CartItem item : cart) {
            System.out.println(item);
            total += item.getSubtotal();
        }
        System.out.println("Total: Rs." + total);

        // contains() / indexOf() — is the keyboard still there?
        System.out.println("\nHas Keyboard? " + hasProduct(cart, "Keyboard"));

        // removeIf() — drop anything cheaper than Rs.300 (Java 8+)
        cart.removeIf(item -> item.getPrice() < 300);
        System.out.println("After removing items under Rs.300: " + cart.size());

        // isEmpty() / clear() — customer places the order
        cart.clear();
        System.out.println("Cart empty after checkout? " + cart.isEmpty());

        // A simple ArrayList of Strings — recently viewed products
        ArrayList<String> recentlyViewed = new ArrayList<>();
        recentlyViewed.add("Wireless Mouse");
        recentlyViewed.add("Keyboard");
        recentlyViewed.add("Wireless Mouse"); // duplicates are allowed
        System.out.println("\nRecently viewed: " + recentlyViewed);
        System.out.println("First seen 'Wireless Mouse' at index " + recentlyViewed.indexOf("Wireless Mouse"));
    }

    private static boolean hasProduct(List<CartItem> cart, String name) {
        for (CartItem item : cart) {
            if (item.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}

class CartItem {
    private final String name;
    private final double price;
    private final int quantity;

    public CartItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return name + " x" + quantity + " = Rs." + getSubtotal();
    }
}
