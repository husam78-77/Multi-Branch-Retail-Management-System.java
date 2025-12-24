package service;

import dao.ProductDAO;
import model.Employee;
import model.Product;
import util.AccessDeniedException;
import util.PermissionManager;
import util.SessionManager;

import java.util.List;
import java.util.stream.Collectors;
public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() {
        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("view products");
        }

        List<Product> allProducts = productDAO.getAll();
        Employee currentUser = SessionManager.getCurrentUser();
        
        return allProducts.stream()
                .filter(p -> p.getBranchId() == currentUser.getBranchId())
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String keyword) {
        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("search products");
        }

        List<Product> results = productDAO.searchByName(keyword);
        Employee currentUser = SessionManager.getCurrentUser();
        
        return results.stream()
                .filter(p -> p.getBranchId() == currentUser.getBranchId())
                .collect(Collectors.toList());
    }

    public void addProduct(Product product) {
        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("add product");
        }

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (product.getBranchId() != currentUser.getBranchId()) {
            throw AccessDeniedException.forBranch(
                "add products",
                currentUser.getBranchId(),
                product.getBranchId()
            );
        }

        productDAO.insert(product);
    }

    public void updateProduct(Product product) {
        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("update product");
        }

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (product.getBranchId() != currentUser.getBranchId()) {
            throw AccessDeniedException.forBranch(
                "update products",
                currentUser.getBranchId(),
                product.getBranchId()
            );
        }

        productDAO.update(product);
    }

    public void deleteProduct(int productId) {
        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("delete product");
        }

        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (product.getBranchId() != currentUser.getBranchId()) {
            throw AccessDeniedException.forBranch(
                "delete products",
                currentUser.getBranchId(),
                product.getBranchId()
            );
        }

        productDAO.softDelete(productId);
    }

    public void updateProductQuantity(int productId, int newQuantity) {
        productDAO.updateQuantity(productId, newQuantity);
    }

    private Product getProductById(int productId) {
        List<Product> allProducts = productDAO.getAll();
        return allProducts.stream()
                .filter(p -> p.getProductId() == productId)
                .findFirst()
                .orElse(null);
    }

    public List<Product> getProductsForBranch(int branchId) {
        
        List<Product> allProducts = productDAO.getAll();
        return allProducts.stream()
                .filter(p -> p.getBranchId() == branchId)
                .collect(Collectors.toList());
    }
}