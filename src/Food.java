// Single file: Food.java
// Date: May 16, 2025

import java.util.NoSuchElementException; // For findMin/findMax in LazySearchTree
import java.util.Objects;              // For Item.equals/hashCode

// Non-public interface Traverser
interface Traverser<E> {
    void visit(E x);
}

// Non-public class PrintObject<E>
class PrintObject<E> implements Traverser<E> {
    protected String separator; // Made protected for PrintObjectWithStatus

    public PrintObject() {
        this.separator = " "; // Default separator
    }

    public PrintObject(String separator) {
        this.separator = separator;
    }

    @Override
    public void visit(E x) {
        System.out.print(x + separator);
    }
}

// Non-public class PrintObjectWithStatus<E>
// Specialized PrintObject for hard traversal demonstration
class PrintObjectWithStatus<E> extends PrintObject<E> {
    public PrintObjectWithStatus() {
        super();
    }

    public PrintObjectWithStatus(String separator) {
        super(separator);
    }

    // Specialized visit method to show deleted status
    public void visit(E x, boolean isDeleted) {
        System.out.print(x + (isDeleted ? " [DELETED]" : "") + super.separator);
    }

    @Override
    public void visit(E x) { // Fallback if used with a simple traverse, like from a different traverser call
        visit(x, false); // Assume not deleted if status not provided by this method call path
    }
}


// Non-public class Item
class Item implements Comparable<Item> {
    private String name;
    private int count;

    public Item(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public Item(String name) {
        this(name, 0); // Default count for search keys
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(Item other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name + " (Qty: " + count + ")";
    }
}

// Non-public class LazySearchTree<E extends Comparable<? super E>>
class LazySearchTree<E extends Comparable<? super E>> {

    // Inner LazySTNode class (can be protected or private)
    protected class LazySTNode {
        E data;
        LazySTNode left;
        LazySTNode right;
        boolean deleted;

        LazySTNode(E data) {
            this.data = data;
            this.left = null;
            this.right = null;
            this.deleted = false;
        }
    }

    private LazySTNode root;
    private int mSize;      // Number of active (non-deleted) nodes
    private int mSizeHard;  // Number of total physical nodes (active + deleted)

    public LazySearchTree() {
        this.root = null;
        this.mSize = 0;
        this.mSizeHard = 0;
    }

    public int size() {
        return mSize;
    }

    public int sizeHard() {
        return mSizeHard;
    }

    public boolean isEmpty() {
        return mSize == 0;
    }

    public void clear() {
        this.root = null;
        this.mSize = 0;
        this.mSizeHard = 0;
    }

    public void insert(E data) {
        root = insert(root, data);
    }

    private LazySTNode insert(LazySTNode node, E data) {
        if (node == null) {
            mSizeHard++;
            mSize++;
            return new LazySTNode(data);
        }

        int compareResult = data.compareTo(node.data);

        if (compareResult < 0) {
            node.left = insert(node.left, data);
        } else if (compareResult > 0) {
            node.right = insert(node.right, data);
        } else {
            node.data = data; // Update data (e.g., Item with new count)
            if (node.deleted) {
                node.deleted = false;
                mSize++;
            }
        }
        return node;
    }

    public boolean remove(E data) {
        int previousMSize = mSize;
        root = remove(root, data);
        return mSize < previousMSize;
    }

    private LazySTNode remove(LazySTNode node, E data) {
        if (node == null) {
            return null;
        }

        int compareResult = data.compareTo(node.data);

        if (compareResult < 0) {
            node.left = remove(node.left, data);
        } else if (compareResult > 0) {
            node.right = remove(node.right, data);
        } else {
            if (!node.deleted) {
                node.deleted = true;
                mSize--;
            }
        }
        return node;
    }

    public boolean contains(E data) {
        LazySTNode result = findNode(root, data);
        return result != null && !result.deleted;
    }

    public E find(E data) {
        LazySTNode resultNode = findNode(root, data);
        if (resultNode != null && !resultNode.deleted) {
            return resultNode.data;
        }
        return null;
    }

    private LazySTNode findNode(LazySTNode node, E data) {
        if (node == null) {
            return null;
        }
        int compareResult = data.compareTo(node.data);
        if (compareResult < 0) {
            return findNode(node.left, data);
        } else if (compareResult > 0) {
            return findNode(node.right, data);
        } else {
            return node;
        }
    }

    public E findMin() {
        if (root == null) {
            throw new NoSuchElementException("Tree is empty.");
        }
        LazySTNode minNode = findMin(root);
        if (minNode == null) {
            throw new NoSuchElementException("No active elements in the tree.");
        }
        return minNode.data;
    }

    private LazySTNode findMin(LazySTNode node) {
        if (node == null) {
            return null;
        }
        LazySTNode leftMin = findMin(node.left);
        if (leftMin != null) {
            return leftMin;
        }
        if (!node.deleted) {
            return node;
        }
        return findMin(node.right);
    }

    public E findMax() {
        if (root == null) {
            throw new NoSuchElementException("Tree is empty.");
        }
        LazySTNode maxNode = findMax(root);
        if (maxNode == null) {
            throw new NoSuchElementException("No active elements in the tree.");
        }
        return maxNode.data;
    }

    private LazySTNode findMax(LazySTNode node) {
        if (node == null) {
            return null;
        }
        LazySTNode rightMax = findMax(node.right);
        if (rightMax != null) {
            return rightMax;
        }
        if (!node.deleted) {
            return node;
        }
        return findMax(node.left);
    }

    public void traverseSoft(Traverser<? super E> func) {
        traverseSoft(root, func);
    }

    private void traverseSoft(LazySTNode node, Traverser<? super E> func) {
        if (node == null) {
            return;
        }
        traverseSoft(node.left, func);
        if (!node.deleted) {
            func.visit(node.data);
        }
        traverseSoft(node.right, func);
    }

    public void traverseHard(Traverser<? super E> func) {
        traverseHard(root, func);
    }

    private void traverseHard(LazySTNode node, Traverser<? super E> func) {
        if (node == null) {
            return;
        }
        traverseHard(node.left, func);
        if (func instanceof PrintObjectWithStatus) {
            ((PrintObjectWithStatus<E>) func).visit(node.data, node.deleted);
        } else {
            func.visit(node.data); // Standard visit if not PrintObjectWithStatus
        }
        traverseHard(node.right, func);
    }
}

// Non-public class SuperMarket
class SuperMarket {
    private LazySearchTree<Item> inventory;

    public SuperMarket() {
        inventory = new LazySearchTree<>();
    }

    public void addItem(String itemName) {
        Item searchKey = new Item(itemName);
        Item existingItem = inventory.find(searchKey);

        if (existingItem != null) {
            existingItem.setCount(existingItem.getCount() + 1);
        } else {
            Item newItem = new Item(itemName, 1);
            inventory.insert(newItem);
        }
    }

    public void removeItem(String itemName) {
        Item searchKey = new Item(itemName);
        Item currentItem = inventory.find(searchKey);

        if (currentItem != null) {
            currentItem.setCount(currentItem.getCount() - 1);
            if (currentItem.getCount() <= 0) {
                inventory.remove(searchKey);
            }
        } else {
            System.out.println("Cannot remove: Item '" + itemName + "' not found in active inventory.");
        }
    }

    public boolean searchItem(String itemName) {
        return inventory.contains(new Item(itemName));
    }

    public void displayActiveInventory() {
        System.out.println("\n----- Active Inventory -----");
        if (inventory.isEmpty()) {
            System.out.println("No active items.");
        } else {
            inventory.traverseSoft(new PrintObject<>("\n"));
        }
        System.out.println("Active items (mSize): " + inventory.size());
        System.out.println("--------------------------");
    }

    public void displayFullInventoryState() {
        System.out.println("\n----- Full Inventory State (Hard Traversal) -----");
        if (inventory.sizeHard() == 0) {
            System.out.println("No items in tree (empty hard structure).");
        } else {
            inventory.traverseHard(new PrintObjectWithStatus<>("\n"));
        }
        System.out.println("Total physical nodes (mSizeHard): " + inventory.sizeHard());
        System.out.println("Active items (mSize): " + inventory.size());
        System.out.println("---------------------------------------------");
    }

    public Item getMinActiveItem() {
        try {
            return inventory.findMin();
        } catch (NoSuchElementException e) {
            System.out.println("Min active item: " + e.getMessage());
            return null;
        }
    }

    public Item getMaxActiveItem() {
        try {
            return inventory.findMax();
        } catch (NoSuchElementException e) {
            System.out.println("Max active item: " + e.getMessage());
            return null;
        }
    }
    // Added for direct access by Main in this single-file setup
    public LazySearchTree<Item> getInventoryTree() {
        return inventory;
    }
}

// Public class Food
public class Food {
    public static void main(String[] args) {
        SuperMarket supermarket = new SuperMarket();

        System.out.println("Initial State (Date: " + java.time.LocalDate.now() + "):"); // Using current date
        supermarket.displayActiveInventory();
        supermarket.displayFullInventoryState();

        System.out.println("\n--- Adding items ---");
        supermarket.addItem("Apple");
        supermarket.addItem("Banana");
        supermarket.addItem("Apple");
        supermarket.addItem("Carrot");
        supermarket.addItem("Date");

        supermarket.displayActiveInventory();
        supermarket.displayFullInventoryState();

        System.out.println("\nMin active item: " + supermarket.getMinActiveItem());
        System.out.println("Max active item: " + supermarket.getMaxActiveItem());

        System.out.println("\n--- Removing items ---");
        supermarket.removeItem("Banana");
        System.out.println("Search for Banana (active): " + supermarket.searchItem("Banana"));

        supermarket.removeItem("Apple");
        supermarket.removeItem("NonExistent");

        supermarket.displayActiveInventory();
        supermarket.displayFullInventoryState();
        System.out.println("Min active item: " + supermarket.getMinActiveItem());
        System.out.println("Max active item: " + supermarket.getMaxActiveItem());

        System.out.println("\n--- Removing Apple completely ---");
        supermarket.removeItem("Apple");
        System.out.println("Search for Apple (active): " + supermarket.searchItem("Apple"));

        supermarket.displayActiveInventory();
        supermarket.displayFullInventoryState();
        System.out.println("Min active item: " + supermarket.getMinActiveItem());
        System.out.println("Max active item: " + supermarket.getMaxActiveItem());


        System.out.println("\n--- Re-adding a deleted item ---");
        supermarket.addItem("Banana");
        System.out.println("Search for Banana (active): " + supermarket.searchItem("Banana"));

        supermarket.displayActiveInventory();
        supermarket.displayFullInventoryState();
        System.out.println("Min active item: " + supermarket.getMinActiveItem());
        System.out.println("Max active item: " + supermarket.getMaxActiveItem());

        System.out.println("\n--- Clearing the inventory ---");
        // Accessing inventory tree directly to call clear()
        supermarket.getInventoryTree().clear();
        System.out.println("Is inventory empty (active items)? " + supermarket.getInventoryTree().isEmpty());
        supermarket.displayActiveInventory();
        supermarket.displayFullInventoryState();

        System.out.println("\nMin active item after clear: " + supermarket.getMinActiveItem());
        System.out.println("Max active item after clear: " + supermarket.getMaxActiveItem());

        System.out.println("\n--- Test with empty tree for findMin/Max from start ---");
        SuperMarket emptyMarket = new SuperMarket();
        System.out.println("Min active item in new empty market: " + emptyMarket.getMinActiveItem());
        System.out.println("Max active item in new empty market: " + emptyMarket.getMaxActiveItem());
        emptyMarket.displayActiveInventory();
        emptyMarket.displayFullInventoryState();
    }
}