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
        PermissionManager.requireManagerOrAbove("view products");

        List<Product> allProducts = productDAO.getAll();
        
        Integer branchFilter = PermissionManager.getBranchFilter();
        if (branchFilter != null) {
            return allProducts.stream()
                    .filter(p -> p.getBranchId() == branchFilter)
                    .collect(Collectors.toList());
        }
        
        return allProducts;
    }

  
    public List<Product> searchProducts(String keyword) {
        PermissionManager.requireManagerOrAbove("search products");

        List<Product> results = productDAO.searchByName(keyword);
        
        Integer branchFilter = PermissionManager.getBranchFilter();
        if (branchFilter != null) {
            return results.stream()
                    .filter(p -> p.getBranchId() == branchFilter)
                    .collect(Collectors.toList());
        }
        
        return results;
    }


    public void addProduct(Product product) {
        PermissionManager.requireManagerOrAbove("add product");

        Employee currentUser = SessionManager.getCurrentUser();
        
        // If MANAGER, enforce their branch
        if (PermissionManager.isManager()) {
            if (product.getBranchId() != currentUser.getBranchId()) {
                throw AccessDeniedException.forBranch(
                    "add products",
                    currentUser.getBranchId(),
                    product.getBranchId()
                );
            }
        }

        productDAO.insert(product);
    }


    public void updateProduct(Product product) {
        PermissionManager.requireManagerOrAbove("update product");

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (PermissionManager.isManager()) {
            if (product.getBranchId() != currentUser.getBranchId()) {
                throw AccessDeniedException.forBranch(
                    "update products",
                    currentUser.getBranchId(),
                    product.getBranchId()
                );
            }
        }

        productDAO.update(product);
    }

    public void deleteProduct(int productId) {
        PermissionManager.requireManagerOrAbove("delete product");

        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (PermissionManager.isManager()) {
            if (product.getBranchId() != currentUser.getBranchId()) {
                throw AccessDeniedException.forBranch(
                    "delete products",
                    currentUser.getBranchId(),
                    product.getBranchId()
                );
            }
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
        Employee currentUser = SessionManager.getCurrentUser();
        
        if (!PermissionManager.isAdmin()) {
            if (branchId != currentUser.getBranchId()) {
                throw AccessDeniedException.forBranch(
                    "view products",
                    currentUser.getBranchId(),
                    branchId
                );
            }
        }

        List<Product> allProducts = productDAO.getAll();
        return allProducts.stream()
                .filter(p -> p.getBranchId() == branchId)
                .collect(Collectors.toList());
    }
}