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
        Employee user = SessionManager.getCurrentUser();

        if (PermissionManager.isAdmin()) {
            return allProducts;
        }

        // MANAGER
        return allProducts.stream()
                .filter(p -> p.getBranchId() == user.getBranchId())
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String keyword) {

        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("search products");
        }

        List<Product> results = productDAO.searchByName(keyword);
        Employee user = SessionManager.getCurrentUser();

        if (PermissionManager.isAdmin()) {
            return results;
        }

        return results.stream()
                .filter(p -> p.getBranchId() == user.getBranchId())
                .collect(Collectors.toList());
    }

    public void addProduct(Product product) {

        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("add product");
        }

        Employee user = SessionManager.getCurrentUser();

        if (PermissionManager.isManager()) {
            if (product.getBranchId() != user.getBranchId()) {
                throw AccessDeniedException.forBranch(
                        "add products",
                        user.getBranchId(),
                        product.getBranchId()
                );
            }
        }

        productDAO.insert(product);
    }

    public void updateProduct(Product product) {

        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("update product");
        }

        Employee user = SessionManager.getCurrentUser();

        if (PermissionManager.isManager()) {
            if (product.getBranchId() != user.getBranchId()) {
                throw AccessDeniedException.forBranch(
                        "update products",
                        user.getBranchId(),
                        product.getBranchId()
                );
            }
        }

        productDAO.update(product);
    }

    public void deleteProduct(int productId) {

        if (!PermissionManager.canManageProducts()) {
            throw AccessDeniedException.forPermission("delete product");
        }

        Product product = productDAO.getById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        Employee user = SessionManager.getCurrentUser();

        if (PermissionManager.isManager()) {
            if (product.getBranchId() != user.getBranchId()) {
                throw AccessDeniedException.forBranch(
                        "delete products",
                        user.getBranchId(),
                        product.getBranchId()
                );
            }
        }

        productDAO.softDelete(productId);
    }

    public List<Product> getProductsForBranch(int branchId) {

        // Used by Sales screen
        PermissionManager.requireBranchAccess(branchId, "view products");

        return productDAO.getAll().stream()
                .filter(p -> p.getBranchId() == branchId)
                .collect(Collectors.toList());
    }
}
